package com.flanks255.simplybackpacks.util;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.configuration.ConfigCache;
import com.flanks255.simplybackpacks.items.BackpackItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.UUID;

public class BackpackUtils {
    public static boolean curiosLoaded = false;

    public static boolean filterItem(ItemStack stack) {
        //check the config whitelist, overrides all checks further.
        if (ConfigCache.WHITELIST.contains(stack.getItem().getRegistryName()))
            return true;

        //check for forge:holds_items
        if (stack.getItem().isIn(SimplyBackpacks.HOLDS_ITEMS))
            return false;

        // if all else fails, check the config blacklist
        return !ConfigCache.BLACKLIST.contains(stack.getItem().getRegistryName());
    }

    public static ItemStack findBackpackForHotkeys(PlayerEntity player) {
        if (player.getHeldItemMainhand().getItem() instanceof BackpackItem)
            return player.getHeldItemMainhand();
        if (player.getHeldItemOffhand().getItem() instanceof BackpackItem)
            return player.getHeldItemOffhand();

        if (curiosLoaded) {
            ItemStack stack = CuriosApi.getCuriosHelper().findEquippedCurio(BackpackItem::isBackpack, player).map(data -> {
                if (data.getRight().getItem() instanceof BackpackItem) {
                    return data.getRight();
                }
                return ItemStack.EMPTY;
            }).orElse(ItemStack.EMPTY);
            if (!stack.isEmpty())
                return stack;
        }

        PlayerInventory inventory = player.inventory;
        for (int i = 0; i <= 35; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack.getItem() instanceof BackpackItem)
                return stack;
        }
        return ItemStack.EMPTY;
    }

    public static boolean increasedAltChance(UUID uuidIn) {
        if (uuidIn.compareTo(People.FLANKS255) == 0)
            return true;

        return uuidIn.compareTo(People.LONEZTAR) == 0;
    }
}
