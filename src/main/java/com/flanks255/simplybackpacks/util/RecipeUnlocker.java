package com.flanks255.simplybackpacks.util;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.ArrayList;

public class RecipeUnlocker {
    private static String modtag;
    private static int version;

    public static void register(String modId, IEventBus bus, int recipeVersion) {
        modtag = modId + "_unlocked";
        version = recipeVersion;
        bus.addListener(RecipeUnlocker::onPlayerLoggedIn);
    }

    private static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();

        CompoundTag tag = player.getPersistentData();
        if (tag.contains(modtag) && tag.getInt(modtag) >= version) {
            return;
        }

        if (player instanceof ServerPlayer) {
            MinecraftServer server = player.getServer();
            if (server != null) {
                var recipes = new ArrayList<>(server.getRecipeManager().getRecipes());
                recipes.removeIf((recipe -> !recipe.id().getNamespace().contains(SimplyBackpacks.MODID)));
                player.awardRecipes(recipes);
                tag.putInt(modtag, version);
            }
        }
    }
}
