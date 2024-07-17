package com.flanks255.simplybackpacks.util;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.configuration.ConfigCache;
import com.flanks255.simplybackpacks.inventory.BackpackManager;
import com.flanks255.simplybackpacks.items.BackpackItem;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.*;

public class BackpackUtils {
    public static boolean curiosLoaded = false;

    public static ResourceLocation getRegistryName(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }

    public static boolean filterItem(ItemStack stack) {
        //check the config whitelist, overrides all checks further.
        if (ConfigCache.WHITELIST.contains(getRegistryName(stack.getItem())))
            return true;

        //check for forge:holds_items
        if (stack.is(SimplyBackpacks.HOLDS_ITEMS))
            return false;

        //vanilla method
        if (!stack.getItem().canFitInsideContainerItems())
            return false;

        // if all else fails, check the config blacklist
        return !ConfigCache.BLACKLIST.contains(getRegistryName(stack.getItem()));
    }

    public static ItemStack findBackpackForHotkeys(Player player, boolean includeHands) {
        if (includeHands && player.getMainHandItem().getItem() instanceof BackpackItem)
            return player.getMainHandItem();
        if (includeHands && player.getOffhandItem().getItem() instanceof BackpackItem)
            return player.getOffhandItem();

/*        if (curiosLoaded) { //TODO curios
            var curiosInv = CuriosApi.getCuriosInventory(player);
            if (curiosInv.isPresent()) {
                ItemStack stack = curiosInv.get().findFirstCurio(BackpackItem::isBackpack).map(slot -> {
                    if (slot.stack().getItem() instanceof BackpackItem) {
                        return slot.stack();
                    }
                    return ItemStack.EMPTY;
                }).orElse(ItemStack.EMPTY);
                if (!stack.isEmpty())
                    return stack;
            }
        }*/

        Inventory inventory = player.getInventory();
        for (int i = 0; i <= 35; i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.getItem() instanceof BackpackItem)
                return stack;
        }
        return ItemStack.EMPTY;
    }

    @SuppressWarnings("ConstantConditions")
    @Nonnull
    public static Optional<UUID> getUUID(@Nonnull ItemStack stack) {
        if (stack.has(SimplyBackpacks.BACKPACK_UUID)) {
            return Optional.ofNullable(stack.get(SimplyBackpacks.BACKPACK_UUID));
        }
        else if (stack.getItem() instanceof BackpackItem && stack.has(DataComponents.CUSTOM_DATA) && stack.get(DataComponents.CUSTOM_DATA).contains("UUID"))
            return Optional.of(stack.get(DataComponents.CUSTOM_DATA).getUnsafe().getUUID("UUID"));
        else
            return Optional.empty();
    }

    public static Set<String> getUUIDSuggestions(CommandContext<CommandSourceStack> commandSource) {
        BackpackManager backpacks = BackpackManager.get();
        Set<String> list = new HashSet<>();

        backpacks.getMap().forEach((uuid, backpackData) -> list.add(uuid.toString()));

        return list;
    }

    public static boolean increasedAltChance(UUID uuidIn) {
        if (uuidIn.compareTo(People.FLANKS255) == 0)
            return true;

        if (uuidIn.compareTo(People.ELLPECK) == 0)
            return true;

        return uuidIn.compareTo(People.LONEZTAR) == 0;
    }

    public record Confirmation(String code, UUID player, UUID backpack){}

    public static String generateCode(RandomSource random) {
        return "%08x".formatted(random.nextInt(Integer.MAX_VALUE));
    }

    private static final HashMap<String, Confirmation> confirmationMap = new HashMap<>();

    public static void addConfirmation(String code, UUID player, UUID backpack) {
        confirmationMap.put(code, new Confirmation(code, player, backpack));
    }

    public static void removeConfirmation(String code) {
        confirmationMap.remove(code);
    }

    public static Optional<Confirmation> getConfirmation(String code) {
        return Optional.ofNullable(confirmationMap.get(code));
    }

    public static boolean isValidResourceLocation(String string) {
        return ResourceLocation.tryParse(string) != null;
    }
}
