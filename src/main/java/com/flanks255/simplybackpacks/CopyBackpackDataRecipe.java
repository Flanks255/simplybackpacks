package com.flanks255.simplybackpacks;

import com.flanks255.simplybackpacks.items.BackpackItem;
import com.google.gson.JsonObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.Map;

public class CopyBackpackDataRecipe extends ShapedRecipe {
    public CopyBackpackDataRecipe(final ResourceLocation id, final String group, final int recipeWidth, final int recipeHeight, final NonNullList<Ingredient> ingredients, final ItemStack recipeOutput) {
        super(id, group, recipeWidth, recipeHeight, ingredients, recipeOutput);
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        final ItemStack craftingResult = super.getCraftingResult(inv);
        ItemStack dataSource = ItemStack.EMPTY;

        if (!craftingResult.isEmpty()) {
            for (int i = 0; i < inv.getSizeInventory(); i++) {
                final ItemStack item = inv.getStackInSlot(i);
                if (!item.isEmpty() && item.getItem() instanceof BackpackItem) {
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


    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CopyBackpackDataRecipe> {
        @Nullable
        @Override
        public CopyBackpackDataRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            int width = buffer.readInt();
            int height = buffer.readInt();
            String group = buffer.readString();

            NonNullList<Ingredient> ingredients = NonNullList.withSize(height * width, Ingredient.EMPTY);
            for (int i = 0; i < ingredients.size(); i++) {
                ingredients.set(i, Ingredient.read(buffer));

            }

            ItemStack craftingResult = buffer.readItemStack();
            return new CopyBackpackDataRecipe(recipeId,group,width,height,ingredients,craftingResult);
        }

        @Override
        public CopyBackpackDataRecipe read(ResourceLocation recipeId, JsonObject json) {
                String group = JSONUtils.getString(json, "group", "");
                Map<String, Ingredient> map = ShapedRecipe.deserializeKey(JSONUtils.getJsonObject(json, "key"));
                String[] pattern = ShapedRecipe.shrink(ShapedRecipe.patternFromJson(JSONUtils.getJsonArray(json, "pattern")));
                int width = pattern[0].length();
                int height = pattern.length;
                NonNullList<Ingredient> ingredients = ShapedRecipe.deserializeIngredients(pattern, map, width, height);
                ItemStack result = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));

                return new CopyBackpackDataRecipe(recipeId, group,width, height,ingredients,result);
        }

        @Override
        public void write(PacketBuffer buffer, CopyBackpackDataRecipe recipe) {
            buffer.writeVarInt(recipe.getRecipeWidth());
            buffer.writeVarInt(recipe.getRecipeHeight());
            buffer.writeString(recipe.getGroup());
            for (Ingredient ingredient: recipe.getIngredients()) {
                ingredient.write(buffer);
            }
            buffer.writeItemStack(recipe.getRecipeOutput());
        }
    }

}
