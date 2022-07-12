package com.flanks255.simplybackpacks.util;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.ArrayList;
import java.util.List;

public class RecipeUnlocker {
    private static String modtag;
    private static int version;

    public static void register(String modid, IEventBus bus, int recipeversion) {
        modtag = modid + "_unlocked";
        version = recipeversion;
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
                List<Recipe<?>> recipes = new ArrayList<>(server.getRecipeManager().getRecipes());
                recipes.removeIf((recipe -> !recipe.getId().getNamespace().contains(SimplyBackpacks.MODID)));
                player.awardRecipes(recipes);
                tag.putInt(modtag, version);
            }
        }
    }
}
