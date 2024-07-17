package com.flanks255.simplybackpacks.items;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.gui.FilterContainer;
import com.flanks255.simplybackpacks.gui.SBContainer;
import com.flanks255.simplybackpacks.inventory.BackpackData;
import com.flanks255.simplybackpacks.inventory.BackpackManager;
import com.flanks255.simplybackpacks.inventory.FilterItemHandler;
import com.flanks255.simplybackpacks.inventory.SBItemHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public class BackpackItem extends Item {
    public BackpackItem(String name, Backpack tier) {
        super(new Item.Properties().stacksTo(1).fireResistant().rarity(tier.rarity));
        this.name = name;
        this.tier = tier;
    }

    final String name;
    final Backpack tier;
    private static final Random random = new Random();

    public static Backpack getTier(ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof BackpackItem)
            return ((BackpackItem) stack.getItem()).tier;
        else
            return Backpack.COMMON;
    }

    public static BackpackData getData(ItemStack stack) {
        if (!(stack.getItem() instanceof BackpackItem))
            return null;

        UUID uuid;

        if (stack.has(SimplyBackpacks.BACKPACK_UUID)) {
            uuid = stack.get(SimplyBackpacks.BACKPACK_UUID);
        } else if (stack.has(DataComponents.CUSTOM_DATA)) {
            CompoundTag tag = stack.get(DataComponents.CUSTOM_DATA).copyTag();
            if (tag.contains("UUID")) {
                uuid = tag.getUUID("UUID");
                stack.set(SimplyBackpacks.BACKPACK_UUID, uuid);
                stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, $ -> $.update(compoundTag -> compoundTag.remove("UUID")));
            } else {
                uuid = UUID.randomUUID();
                stack.set(SimplyBackpacks.BACKPACK_UUID, uuid);
            }
        }
        else {
            uuid = UUID.randomUUID();
            stack.set(SimplyBackpacks.BACKPACK_UUID, uuid);
        }


        return BackpackManager.get().getOrCreateBackpack(uuid, ((BackpackItem) stack.getItem()).tier);
    }

    public static boolean isBackpack(ItemStack stack) {
        return stack.getItem() instanceof BackpackItem;
    }

    @Override
    @Nonnull
    public Component getDescription() {
        return Component.translatable(this.getDescriptionId()).withStyle(this.tier == Backpack.ULTIMATE? ChatFormatting.DARK_AQUA:ChatFormatting.RESET);
    }

    @Override
    public boolean isEnchantable(@Nonnull ItemStack stack) {
        return false;
    }

/*    @Override //TODO someday
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment.builtInRegistryHolder().is(SimplyBackpacks.SOULBOUND);
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {

    }*/

    @Override
    @Nonnull
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, @Nonnull InteractionHand handIn) {
        ItemStack backpack = playerIn.getItemInHand(handIn);
        if (!worldIn.isClientSide && playerIn instanceof ServerPlayer && backpack.getItem() instanceof BackpackItem) {
            BackpackData data = BackpackItem.getData(backpack);

            Backpack itemTier = ((BackpackItem) backpack.getItem()).tier;
            UUID uuid = data.getUuid();

            data.updateAccessRecords(playerIn.getName().getString(), System.currentTimeMillis());

            if (data.getTier().ordinal() < itemTier.ordinal()) {
                data.upgrade(itemTier);
                playerIn.sendSystemMessage(Component.literal("Backpack upgraded to " + itemTier.name));
            }

            if (playerIn.isShiftKeyDown()) {
                //filter
                playerIn.openMenu(new SimpleMenuProvider( (windowId, playerInventory, playerEntity) -> new FilterContainer(windowId, playerInventory, data.getFilter()), backpack.getHoverName()), (buffer -> buffer.writeNbt(data.getFilter().serializeNBT(worldIn.registryAccess()))));
            } else {
                //open
                playerIn.openMenu(new SimpleMenuProvider( (windowId, playerInventory, playerEntity) -> new SBContainer(windowId, playerInventory, uuid, data.getTier(), data.getHandler()), backpack.getHoverName()), (buffer -> buffer.writeUUID(uuid).writeInt(data.getTier().ordinal())));
            }
        }
        return InteractionResultHolder.success(playerIn.getItemInHand(handIn));
    }

    public static void togglePickup(Player playerEntity, ItemStack stack) {
        boolean Pickup = !stack.getOrDefault(SimplyBackpacks.BACKPACK_PICKUP, false);

        stack.set(SimplyBackpacks.BACKPACK_PICKUP, Pickup);
        if (playerEntity instanceof ServerPlayer serverPlayer)
            serverPlayer.displayClientMessage(Component.translatable(Pickup?"simplybackpacks.autopickupenabled":"simplybackpacks.autopickupdisabled"), true);
        else
            playerEntity.displayClientMessage(Component.translatable(Pickup?"simplybackpacks.autopickupenabled":"simplybackpacks.autopickupdisabled"), true);

    }


    public static boolean applyFilter(ItemStack item, ItemStack packItem) {
        Optional<IItemHandler> handler = Optional.ofNullable(packItem.getCapability(Capabilities.ItemHandler.ITEM));

        if (handler.isPresent() && handler.get() instanceof SBItemHandler) {
            BackpackData data = BackpackItem.getData(packItem);
            if (data == null)
                return false;

            FilterItemHandler filterHandler = data.getFilter();

            int filterOpts = packItem.getOrDefault(SimplyBackpacks.BACKPACK_FILTER, 0);
            boolean whitelist = (filterOpts & 1) > 0;
            boolean nbtMatch = (filterOpts & 2) > 0;

            for (int i = 0; i < 16; i++) {
                ItemStack fStack = filterHandler.getStackInSlot(i);
                if (!fStack.isEmpty()) {
                    if (ItemStack.isSameItem(fStack, item)) {
                        if (nbtMatch)
                            return ItemStack.isSameItemSameComponents(fStack, item) == whitelist;
                        else
                            return whitelist;
                    }
                }
            }

            return !whitelist;
        }
        return false;
    }

    public static boolean pickupEvent(ItemEntityPickupEvent.Pre event, ItemStack stack) {
        if (!stack.getOrDefault(SimplyBackpacks.BACKPACK_PICKUP, false))
            return false;

        Optional<IItemHandler> optional = Optional.ofNullable(stack.getCapability(Capabilities.ItemHandler.ITEM));
        if (optional.isPresent()) {
            IItemHandler handler = optional.get();

            if (!(handler instanceof SBItemHandler))
                return false;

            if (!applyFilter(event.getItemEntity().getItem(), stack))
                return false;

            ItemStack pickedUp = event.getItemEntity().getItem();
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack slot = handler.getStackInSlot(i);
                if (slot.isEmpty() || (ItemStack.isSameItemSameComponents(slot, pickedUp) && slot.getCount() < slot.getMaxStackSize() && slot.getCount() < handler.getSlotLimit(i))) {
                    int remainder = handler.insertItem(i, pickedUp.copy(), false).getCount();
                    pickedUp.setCount(remainder);
                    if (remainder == 0)
                        break;
                }
            }
            if (pickedUp.isEmpty())
                event.getPlayer().level().playSound(null, event.getPlayer().blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((random.nextFloat() - random.nextFloat()) * 0.7F + 1.0F) * 2.0F);

            return pickedUp.isEmpty();
        }
        else
            return false;
    }


    private boolean hasTranslation(String key) {
        return !I18n.get(key).equals(key);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull Item.TooltipContext context, @Nonnull List<Component> tooltip,@Nonnull TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        String translationKey = getDescriptionId();

        if (!stack.has(SimplyBackpacks.BACKPACK_UUID)) {
            tooltip.add(Component.translatable("simplybackpacks.notsetup").withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.GOLD));
        }

        if (stack.has(SimplyBackpacks.BACKPACK_PICKUP)) {
            boolean pickupEnabled = stack.get(SimplyBackpacks.BACKPACK_PICKUP);
            if (pickupEnabled)
                tooltip.add(Component.translatable("simplybackpacks.autopickupenabled"));
            else
                tooltip.add(Component.translatable("simplybackpacks.autopickupdisabled"));
        }

        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable( translationKey + ".info"));
            if (hasTranslation(translationKey + ".info2"))
                tooltip.add(Component.translatable( translationKey + ".info2"));
            if (hasTranslation(translationKey + ".info3"))
                tooltip.add(Component.translatable( translationKey + ".info3"));
        }
        else {
            tooltip.add(Component.translatable( "simplybackpacks.shift" ));
        }

        if (flagIn.isAdvanced() && stack.has(SimplyBackpacks.BACKPACK_UUID)) {
            UUID uuid = stack.get(SimplyBackpacks.BACKPACK_UUID);
            tooltip.add(Component.literal("ID: " + uuid.toString().substring(0,8)).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        } else if (flagIn.isAdvanced() && stack.has(DataComponents.CUSTOM_DATA) && stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).contains("UUID")) {
            UUID uuid = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).getUnsafe().getUUID("UUID");
            tooltip.add(Component.literal("ID: " + uuid.toString().substring(0,8)).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
    }
}
