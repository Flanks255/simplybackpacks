package com.flanks255.simplybackpacks.data;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.crafting.TargetNBTIngredient;
import com.flanks255.simplybackpacks.crafting.WrappedRecipe;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.function.Consumer;

public class SBRecipes extends RecipeProvider {
    public SBRecipes(DataGenerator generatorIn) {
        super(generatorIn);
    }
    @Override
    protected void saveAdvancement(@Nonnull HashCache cache, @Nonnull JsonObject cache2, @Nonnull Path advancementJson) {
        // No thank you, good day sir.
    }

    @Override
    protected void buildCraftingRecipes(@Nonnull Consumer<FinishedRecipe> consumer) {
        InventoryChangeTrigger.TriggerInstance lul = has(Items.AIR);
        ShapedRecipeBuilder.shaped(SimplyBackpacks.COMMONBACKPACK.get())
            .pattern("A A")
            .pattern("DBD")
            .pattern("BCB")
            .define('A', Tags.Items.STRING)
            .define('B', Tags.Items.LEATHER)
            .define('C', Tags.Items.CHESTS)
            .define('D', Tags.Items.DYES_WHITE)
            .unlockedBy("", lul).save(consumer);

        ShapedRecipeBuilder.shaped(SimplyBackpacks.UNCOMMONBACKPACK.get())
            .pattern("A A")
            .pattern("EBE")
            .pattern("CDC")
            .define('A', Tags.Items.INGOTS_GOLD)
            .define('B', TargetNBTIngredient.of(SimplyBackpacks.COMMONBACKPACK.get()))
            .define('C', Tags.Items.CHESTS)
            .define('D', Tags.Items.STORAGE_BLOCKS_GOLD)
            .define('E', Tags.Items.DYES_YELLOW)
            .unlockedBy("", lul)
            .save(WrappedRecipe.Inject(consumer, SimplyBackpacks.COPYRECIPE.get()));

        ShapedRecipeBuilder.shaped(SimplyBackpacks.RAREBACKPACK.get())
            .pattern("A A")
            .pattern("DBD")
            .pattern("CEC")
            .define('A', Tags.Items.GEMS_DIAMOND)
            .define('B', TargetNBTIngredient.of(SimplyBackpacks.UNCOMMONBACKPACK.get()))
            .define('C', Tags.Items.CHESTS)
            .define('D', Tags.Items.DYES_BLUE)
            .define('E', Tags.Items.STORAGE_BLOCKS_DIAMOND)
            .unlockedBy("", lul)
            .save(WrappedRecipe.Inject(consumer, SimplyBackpacks.COPYRECIPE.get()));

        ShapedRecipeBuilder.shaped(SimplyBackpacks.EPICBACKPACK.get())
            .pattern("A A")
            .pattern("EBE")
            .pattern("CDC")
            .define('A', Tags.Items.DYES_MAGENTA)
            .define('B', TargetNBTIngredient.of(SimplyBackpacks.RAREBACKPACK.get()))
            .define('C', Tags.Items.CHESTS)
            .define('D', Tags.Items.NETHER_STARS)
            .define('E', Items.IRON_BARS)
            .unlockedBy("", lul)
            .save(WrappedRecipe.Inject(consumer, SimplyBackpacks.COPYRECIPE.get()));

        ShapedRecipeBuilder.shaped(SimplyBackpacks.ULTIMATEBACKPACK.get())
            .pattern("A A")
            .pattern("EBE")
            .pattern("CDC")
            .define('A', Tags.Items.INGOTS_NETHERITE)
            .define('B', TargetNBTIngredient.of(SimplyBackpacks.EPICBACKPACK.get()))
            .define('C', Tags.Items.CHESTS)
            .define('D', Tags.Items.STORAGE_BLOCKS_NETHERITE)
            .define('E', Tags.Items.NETHER_STARS)
            .unlockedBy("", lul)
            .save(WrappedRecipe.Inject(consumer, SimplyBackpacks.COPYRECIPE.get()));
    }
}
