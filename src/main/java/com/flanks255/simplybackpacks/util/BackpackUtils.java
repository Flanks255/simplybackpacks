package com.flanks255.simplybackpacks.util;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.configuration.ConfigCache;
import com.flanks255.simplybackpacks.inventory.BackpackManager;
import com.flanks255.simplybackpacks.items.BackpackItem;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

import javax.annotation.Nonnull;
import java.util.*;

public class BackpackUtils {
    public static boolean curiosLoaded = false;

    public static boolean filterItem(ItemStack stack) {
        //check the config whitelist, overrides all checks further.
        if (ConfigCache.WHITELIST.contains(stack.getItem().getRegistryName()))
            return true;

        //check for forge:holds_items
        if (stack.getItem().is(SimplyBackpacks.HOLDS_ITEMS))
            return false;

        // if all else fails, check the config blacklist
        return !ConfigCache.BLACKLIST.contains(stack.getItem().getRegistryName());
    }

    public static ItemStack findBackpackForHotkeys(PlayerEntity player, boolean includeHands) {
        if (includeHands && player.getMainHandItem().getItem() instanceof BackpackItem)
            return player.getMainHandItem();
        if (includeHands && player.getOffhandItem().getItem() instanceof BackpackItem)
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

        PlayerInventory inventory = player.inventory;
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

        return uuidIn.compareTo(People.LONEZTAR) == 0;
    }

    public static Set<String> getUUIDSuggestions(CommandContext<CommandSource> commandSource) {
        BackpackManager backpacks = BackpackManager.get();
        Set<String> list = new HashSet<>();

        backpacks.getMap().forEach((uuid, backpackData) -> list.add(uuid.toString()));

        return list;
    }

    public static class Confirmation{
        private final String code;
        private final UUID player;
        private final UUID backpack;
        public Confirmation(String code, UUID player, UUID backpack) {
            this.code = code;
            this.player = player;
            this.backpack = backpack;
        }

        public String getCode() {
            return code;
        }

        public UUID getPlayer() {
            return player;
        }

        public UUID getBackpack() {
            return backpack;
        }
    }

    public static String generateCode(Random random) {
        return String.format("%08x", random.nextInt(Integer.MAX_VALUE));
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
}
