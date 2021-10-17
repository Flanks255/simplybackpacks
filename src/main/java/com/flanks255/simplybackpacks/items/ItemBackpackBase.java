package com.flanks255.simplybackpacks.items;

import com.flanks255.simplybackpacks.BackpackItemHandler;
import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.gui.FilterContainer;
import com.flanks255.simplybackpacks.gui.SBContainer;
import com.flanks255.simplybackpacks.network.ToggleMessageMessage;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemBackpackBase extends Item {
    public ItemBackpackBase(String name, Integer size, Rarity rarity) {
        super(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_TOOLS));
        this.name = name;
        this.size = size;
        this.rarity = rarity;
    }

    String name;
    Integer size;
    Rarity rarity;

    @Override
    public Rarity getRarity(ItemStack stack) {
        return rarity;
    }

    public ItemBackpackBase setName() {
        setRegistryName(SimplyBackpacks.MODID, name);

        return this;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment.is(SimplyBackpacks.SOULBOUND);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if (!worldIn.isClientSide) {
            if (playerIn.isShiftKeyDown()) {
                //filter
                playerIn.openMenu(new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return new TextComponent("Backpack Filter");
                    }

                    @Nullable
                    @Override
                    public AbstractContainerMenu createMenu(int p_createMenu_1_, Inventory p_createMenu_2_, Player p_createMenu_3_) {
                        return new FilterContainer(p_createMenu_1_, p_createMenu_3_.level, p_createMenu_3_.blockPosition(), p_createMenu_2_, p_createMenu_3_);
                    }
                });
            } else {
                //open
                playerIn.openMenu(new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return playerIn.getItemInHand(handIn).getHoverName();

                    }

                    @Nullable
                    @Override
                    public AbstractContainerMenu createMenu(int p_createMenu_1_, Inventory p_createMenu_2_, Player p_createMenu_3_) {
                        return new SBContainer(p_createMenu_1_, p_createMenu_3_.level, p_createMenu_3_.blockPosition(), p_createMenu_2_, p_createMenu_3_);
                    }
                });
            }
        }
        return InteractionResultHolder.success(playerIn.getItemInHand(handIn));
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new BackpackCaps(stack, size, nbt);
    }

    class BackpackCaps implements ICapabilitySerializable {
        public BackpackCaps(ItemStack stack, int size, CompoundTag nbtIn) {
            itemStack = stack;
            this.size = size;
            inventory = new BackpackItemHandler(itemStack, size);
            optional = LazyOptional.of(() -> inventory);
        }
        private int size;
        private ItemStack itemStack;
        private BackpackItemHandler inventory;
        private LazyOptional<IItemHandler> optional;

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                return optional.cast();
            }
            else
                return LazyOptional.empty();
        }

        @Override
        public Tag serializeNBT() {
            inventory.save();
            return new CompoundTag();
        }

        @Override
        public void deserializeNBT(Tag nbt) {
            inventory.load();
        }
    }

    public void togglePickup(Player playerEntity, ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();

        boolean Pickup = !nbt.getBoolean("Pickup");

        nbt.putBoolean("Pickup", Pickup);
        if (playerEntity instanceof ServerPlayer)
            SimplyBackpacks.NETWORK.send(PacketDistributor.PLAYER.with(()-> (ServerPlayer) playerEntity), new ToggleMessageMessage(Pickup));
        else
            playerEntity.displayClientMessage(new TextComponent(I18n.get(Pickup?"simplybackpacks.autopickupenabled":"simplybackpacks.autopickupdisabled")), true);

    }


    public boolean filterItem(ItemStack item, ItemStack packItem) {
        IItemHandler tmp = packItem.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
        if (tmp == null || !(tmp instanceof BackpackItemHandler))
            return false;

        int filterOpts = packItem.getOrCreateTag().getInt("Filter-OPT");
        boolean whitelist = (filterOpts & 1) > 0;
        boolean nbtMatch = (filterOpts & 2) > 0;

        BackpackItemHandler handler = (BackpackItemHandler) tmp;

        for (int i = 0; i < 16; i++) {
            ItemStack fStack = handler.filter.getStackInSlot(i);
            if (!fStack.isEmpty()) {
                if (fStack.sameItem(item)) {
                    if (nbtMatch)
                        return ItemStack.tagMatches(fStack, item) == whitelist;
                    else
                        return whitelist;
                }
            }
        }

        return !whitelist;
    }

    public boolean pickupEvent(EntityItemPickupEvent event, ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        if (nbt == null)
            return false;

        if (!nbt.getBoolean("Pickup"))
                return false;

        LazyOptional<IItemHandler> optional = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        if (!optional.isPresent())
            return false;

        IItemHandler handler = optional.orElse(null);
        if (handler == null || !(handler instanceof BackpackItemHandler))
            return false;
        ((BackpackItemHandler) handler).loadIfNotLoaded();

        if (!filterItem(event.getItem().getItem(), stack))
            return false;


        ItemStack pickedUp = event.getItem().getItem();
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack slot = handler.getStackInSlot(i);
            if (slot.isEmpty() || (ItemHandlerHelper.canItemStacksStack(slot, pickedUp) && slot.getCount() < slot.getMaxStackSize() && slot.getCount() < handler.getSlotLimit(i))) {
                int remainder = handler.insertItem(i, pickedUp.copy(), false).getCount();
                pickedUp.setCount(remainder);
                if (remainder == 0)
                    break;
            }
        }
        return pickedUp.isEmpty();
    }


    private boolean hasTranslation(String key) {
        return !I18n.get(key).equals(key);
    }

    private String fallbackString(String key, String fallback) {
        String tmp = I18n.get(key);
        return tmp.equals(key)?fallback:tmp;
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        String translationKey = getDescriptionId();

        boolean pickupEnabled = stack.getOrCreateTag().getBoolean("Pickup");
        if (pickupEnabled)
            tooltip.add(new TextComponent(I18n.get("simplybackpacks.autopickupenabled")));
        else
            tooltip.add(new TextComponent(I18n.get("simplybackpacks.autopickupdisabled")));

        if (Screen.hasShiftDown()) {
            tooltip.add(new TextComponent( I18n.get( translationKey + ".info") ));
            if (hasTranslation(translationKey + ".info2"))
                tooltip.add(new TextComponent( I18n.get(translationKey + ".info2")));
            if (hasTranslation(translationKey + ".info3"))
                tooltip.add(new TextComponent( I18n.get(translationKey + ".info3")));
        }
        else {
            tooltip.add(new TextComponent( fallbackString("simplybackpacks.shift", "Press <§6§oShift§r> for info.") ));
        }
    }
}
