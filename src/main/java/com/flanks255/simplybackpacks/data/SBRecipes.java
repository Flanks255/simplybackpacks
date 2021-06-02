package com.flanks255.simplybackpacks.data;

import com.google.gson.JsonObject;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import java.nio.file.Path;
import java.util.function.Consumer;

public class SBRecipes extends RecipeProvider {
    @Override
    protected void saveRecipeAdvancement(DirectoryCache cache, JsonObject cache2, Path advancementJson) { }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {

    }

    public SBRecipes(DataGenerator generatorIn) {
        super(generatorIn);
    }

}
