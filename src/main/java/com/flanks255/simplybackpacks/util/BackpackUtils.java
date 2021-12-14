package com.flanks255.simplybackpacks.util;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.configuration.ConfigCache;
import com.flanks255.simplybackpacks.inventory.BackpackManager;
import com.flanks255.simplybackpacks.items.BackpackItem;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class BackpackUtils {
    public static boolean curiosLoaded = false;

    public static boolean filterItem(ItemStack stack) {
        //check the config whitelist, overrides all checks further.
        if (ConfigCache.WHITELIST.contains(stack.getItem().getRegistryName()))
            return true;

        //check for forge:holds_items
        if (stack.is(SimplyBackpacks.HOLDS_ITEMS))
            return false;

        // if all else fails, check the config blacklist
        return !ConfigCache.BLACKLIST.contains(stack.getItem().getRegistryName());
    }

    public static ItemStack findBackpackForHotkeys(Player player) {
        if (player.getMainHandItem().getItem() instanceof BackpackItem)
            return player.getMainHandItem();
        if (player.getOffhandItem().getItem() instanceof BackpackItem)
            return player.getOffhandItem();

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

        Inventory inventory = player.getInventory();
        for (int i = 0; i <= 35; i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.getItem() instanceof BackpackItem)
                return stack;
        }
        return ItemStack.EMPTY;
    }

    @Nonnull
    public static Optional<UUID> getUUID(@Nonnull ItemStack stack) {
        if (stack.getItem() instanceof BackpackItem && stack.hasTag() && stack.getTag().contains("UUID"))
            return Optional.of(stack.getTag().getUUID("UUID"));
        else
            return Optional.empty();
    }

    public static boolean increasedAltChance(UUID uuidIn) {
        if (uuidIn.compareTo(People.FLANKS255) == 0)
            return true;

        if (uuidIn.compareTo(People.ELLPECK) == 0)
            return true;

        return uuidIn.compareTo(People.LONEZTAR) == 0;
    }

    public static Set<String> getUUIDSuggestions(CommandContext<CommandSourceStack> commandSource) {
        BackpackManager backpacks = BackpackManager.get();
        Set<String> list = new HashSet<>();

        backpacks.getMap().forEach((uuid, backpackData) -> list.add(uuid.toString()));

        return list;
    }
}
