package com.flanks255.simplybackpacks.data;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.crafting.CopyBackpackDataRecipe;
import com.flanks255.simplybackpacks.crafting.TargetNBTIngredient;
import com.flanks255.simplybackpacks.util.NoAdvRecipeOutput;
import com.flanks255.simplybackpacks.util.RecipeInjector;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

public class SBRecipes extends RecipeProvider {
    public SBRecipes(DataGenerator generatorIn) {
        super(generatorIn.getPackOutput());
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput output) {
        var lul = has(Items.AIR);
        var consumer = new NoAdvRecipeOutput(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SimplyBackpacks.COMMONBACKPACK.get())
            .pattern("A A")
            .pattern("DBD")
            .pattern("BCB")
            .define('A', Tags.Items.STRING)
            .define('B', Tags.Items.LEATHER)
            .define('C', Tags.Items.CHESTS)
            .define('D', Tags.Items.DYES_WHITE)
            .showNotification(false)
            .unlockedBy("", lul).save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SimplyBackpacks.UNCOMMONBACKPACK.get())
            .pattern("A A")
            .pattern("EBE")
            .pattern("CDC")
            .define('A', Tags.Items.INGOTS_GOLD)
            .define('B', TargetNBTIngredient.of(SimplyBackpacks.COMMONBACKPACK.get()))
            .define('C', Tags.Items.CHESTS)
            .define('D', Tags.Items.STORAGE_BLOCKS_GOLD)
            .define('E', Tags.Items.DYES_YELLOW)
            .showNotification(false)
            .unlockedBy("", lul)
            .save(backpackUpgrade(consumer));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SimplyBackpacks.RAREBACKPACK.get())
            .pattern("A A")
            .pattern("DBD")
            .pattern("CEC")
            .define('A', Tags.Items.GEMS_DIAMOND)
            .define('B', TargetNBTIngredient.of(SimplyBackpacks.UNCOMMONBACKPACK.get()))
            .define('C', Tags.Items.CHESTS)
            .define('D', Tags.Items.DYES_BLUE)
            .define('E', Tags.Items.STORAGE_BLOCKS_DIAMOND)
            .showNotification(false)
            .unlockedBy("", lul)
            .save(backpackUpgrade(consumer));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SimplyBackpacks.EPICBACKPACK.get())
            .pattern("A A")
            .pattern("EBE")
            .pattern("CDC")
            .define('A', Tags.Items.DYES_MAGENTA)
            .define('B', TargetNBTIngredient.of(SimplyBackpacks.RAREBACKPACK.get()))
            .define('C', Tags.Items.CHESTS)
            .define('D', Tags.Items.NETHER_STARS)
            .define('E', Items.IRON_BARS)
            .showNotification(false)
            .unlockedBy("", lul)
            .save(backpackUpgrade(consumer));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SimplyBackpacks.ULTIMATEBACKPACK.get())
            .pattern("A A")
            .pattern("EBE")
            .pattern("CDC")
            .define('A', Tags.Items.INGOTS_NETHERITE)
            .define('B', TargetNBTIngredient.of(SimplyBackpacks.EPICBACKPACK.get()))
            .define('C', Tags.Items.CHESTS)
            .define('D', Tags.Items.STORAGE_BLOCKS_NETHERITE)
            .define('E', Tags.Items.NETHER_STARS)
            .showNotification(false)
            .unlockedBy("", lul)
            .save(backpackUpgrade(consumer));
    }

    @NotNull
    private static RecipeInjector<ShapedRecipe> backpackUpgrade(NoAdvRecipeOutput consumer) {
        return new RecipeInjector<>(consumer, CopyBackpackDataRecipe::new);
    }
}
