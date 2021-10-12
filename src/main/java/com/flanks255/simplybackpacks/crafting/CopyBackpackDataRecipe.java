package com.flanks255.simplybackpacks.crafting;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.google.gson.JsonObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class CopyBackpackDataRecipe extends ShapedRecipe {
    public CopyBackpackDataRecipe(final ResourceLocation id, final String group, final int recipeWidth, final int recipeHeight, final NonNullList<Ingredient> ingredients, final ItemStack recipeOutput) {
        super(id, group, recipeWidth, recipeHeight, ingredients, recipeOutput);
    }

    public CopyBackpackDataRecipe(ShapedRecipe shapedRecipe) {
        super(shapedRecipe.getId(), shapedRecipe.getGroup(), shapedRecipe.getRecipeWidth(), shapedRecipe.getRecipeHeight(), shapedRecipe.getIngredients(), shapedRecipe.getRecipeOutput());
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        final ItemStack craftingResult = super.getCraftingResult(inv);
        TargetNBTIngredient donorIngredient = null;
        ItemStack dataSource = ItemStack.EMPTY;
        NonNullList<Ingredient> ingredients = getIngredients();
        for (Ingredient ingredient : ingredients) {
            if (ingredient instanceof TargetNBTIngredient) {
                donorIngredient = (TargetNBTIngredient) ingredient;
                break;
            }
        }
        if (!craftingResult.isEmpty()) {
            for (int i = 0; i < inv.getSizeInventory(); i++) {
                final ItemStack item = inv.getStackInSlot(i);
                if (!item.isEmpty() && donorIngredient.test(item)) {
                    dataSource = item;
                    break;
                }
            }

            if (!dataSource.isEmpty() && dataSource.hasTag()) {
                craftingResult.setTag(dataSource.getTag().copy());
            }
        }

        return craftingResult;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SimplyBackpacks.COPYRECIPE.get();
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CopyBackpackDataRecipe> {
        @Nullable
        @Override
        public CopyBackpackDataRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            return new CopyBackpackDataRecipe(IRecipeSerializer.CRAFTING_SHAPED.read(recipeId, buffer));
        }

        @Override
        public CopyBackpackDataRecipe read(ResourceLocation recipeId, JsonObject json) {
            try {
                return new CopyBackpackDataRecipe(IRecipeSerializer.CRAFTING_SHAPED.read(recipeId, json));
            }
            catch (Exception exception) {
                SimplyBackpacks.LOGGER.info("Error reading CopyBackpack Recipe from packet: ", exception);
                throw exception;
            }
        }

        @Override
        public void write(PacketBuffer buffer, CopyBackpackDataRecipe recipe) {
            try {
                IRecipeSerializer.CRAFTING_SHAPED.write(buffer, recipe);
            }
            catch (Exception exception) {
                SimplyBackpacks.LOGGER.info("Error writing CopyBackpack Recipe to packet: ", exception);
            throw exception;
        }
        }
    }

}
