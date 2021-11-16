package com.flanks255.simplybackpacks.util;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
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
        CompoundNBT tag = event.getPlayer().getPersistentData();
        if (tag.contains(modtag) && tag.getInt(modtag) >= version)
            return;

        PlayerEntity player = event.getPlayer();
        if (player instanceof ServerPlayerEntity) {
            MinecraftServer server = player.getServer();
            if (server != null) {
                List<IRecipe<?>> recipes = new ArrayList<>(server.getRecipeManager().getRecipes());
                recipes.removeIf((recipe -> !recipe.getId().getNamespace().contains(SimplyBackpacks.MODID)));
                player.awardRecipes(recipes);
                tag.putInt(modtag, version);
            }
        }
    }
}
