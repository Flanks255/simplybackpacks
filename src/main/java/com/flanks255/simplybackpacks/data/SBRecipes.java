package com.flanks255.simplybackpacks.data;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.crafting.TargetNBTIngredient;
import com.flanks255.simplybackpacks.crafting.WrappedRecipe;
import com.google.gson.JsonObject;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.data.*;
import net.minecraft.item.Items;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.function.Consumer;

public class SBRecipes extends RecipeProvider {
    public SBRecipes(DataGenerator generatorIn) {
        super(generatorIn);
    }
    @Override
    protected void saveRecipeAdvancement(@Nonnull DirectoryCache cache, @Nonnull JsonObject cache2, @Nonnull Path advancementJson) {
        // No thank you, good day sir.
    }

    @Override
    protected void registerRecipes(@Nonnull Consumer<IFinishedRecipe> consumer) {
        InventoryChangeTrigger.Instance lul = hasItem(Items.AIR);
        ShapedRecipeBuilder.shapedRecipe(SimplyBackpacks.COMMONBACKPACK.get())
            .patternLine("A A")
            .patternLine("DBD")
            .patternLine("BCB")
            .key('A', Tags.Items.STRING)
            .key('B', Tags.Items.LEATHER)
            .key('C', Tags.Items.CHESTS)
            .key('D', Tags.Items.DYES_WHITE)
            .addCriterion("", lul).build(consumer);

        ShapedRecipeBuilder.shapedRecipe(SimplyBackpacks.UNCOMMONBACKPACK.get())
            .patternLine("A A")
            .patternLine("EBE")
            .patternLine("CDC")
            .key('A', Tags.Items.INGOTS_GOLD)
            .key('B', TargetNBTIngredient.of(SimplyBackpacks.COMMONBACKPACK.get()))
            .key('C', Tags.Items.CHESTS)
            .key('D', Tags.Items.STORAGE_BLOCKS_GOLD)
            .key('E', Tags.Items.DYES_YELLOW)
            .addCriterion("", lul)
            .build(WrappedRecipe.Inject(consumer, SimplyBackpacks.COPYRECIPE.get()));

        ShapedRecipeBuilder.shapedRecipe(SimplyBackpacks.RAREBACKPACK.get())
            .patternLine("A A")
            .patternLine("DBD")
            .patternLine("CEC")
            .key('A', Tags.Items.GEMS_DIAMOND)
            .key('B', TargetNBTIngredient.of(SimplyBackpacks.UNCOMMONBACKPACK.get()))
            .key('C', Tags.Items.CHESTS)
            .key('D', Tags.Items.DYES_BLUE)
            .key('E', Tags.Items.STORAGE_BLOCKS_DIAMOND)
            .addCriterion("", lul)
            .build(WrappedRecipe.Inject(consumer, SimplyBackpacks.COPYRECIPE.get()));

        ShapedRecipeBuilder.shapedRecipe(SimplyBackpacks.EPICBACKPACK.get())
            .patternLine("A A")
            .patternLine("EBE")
            .patternLine("CDC")
            .key('A', Tags.Items.DYES_MAGENTA)
            .key('B', TargetNBTIngredient.of(SimplyBackpacks.RAREBACKPACK.get()))
            .key('C', Tags.Items.CHESTS)
            .key('D', Tags.Items.NETHER_STARS)
            .key('E', Items.IRON_BARS)
            .addCriterion("", lul)
            .build(WrappedRecipe.Inject(consumer, SimplyBackpacks.COPYRECIPE.get()));

        ShapedRecipeBuilder.shapedRecipe(SimplyBackpacks.ULTIMATEBACKPACK.get())
            .patternLine("A A")
            .patternLine("EBE")
            .patternLine("CDC")
            .key('A', Tags.Items.INGOTS_NETHERITE)
            .key('B', TargetNBTIngredient.of(SimplyBackpacks.RAREBACKPACK.get()))
            .key('C', Tags.Items.CHESTS)
            .key('D', Tags.Items.STORAGE_BLOCKS_NETHERITE)
            .key('E', Tags.Items.NETHER_STARS)
            .addCriterion("", lul)
            .build(WrappedRecipe.Inject(consumer, SimplyBackpacks.COPYRECIPE.get()));
    }
}
