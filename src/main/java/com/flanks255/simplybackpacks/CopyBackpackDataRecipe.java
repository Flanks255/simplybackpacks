package com.flanks255.simplybackpacks;

import com.flanks255.simplybackpacks.items.ItemBackpackBase;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
                if (!item.isEmpty() && item.getItem() instanceof ItemBackpackBase) {
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

        private static final Method deserializeKey = ObfuscationReflectionHelper.findMethod(ShapedRecipe.class, "func_192408_a", JsonObject.class);
        private static final Method shrink = ObfuscationReflectionHelper.findMethod(ShapedRecipe.class, "func_194134_a", String[].class);
        private static final Method patternFromJson = ObfuscationReflectionHelper.findMethod(ShapedRecipe.class, "func_192407_a", JsonArray.class);
        private static final Method deserializeIngredients = ObfuscationReflectionHelper.findMethod(ShapedRecipe.class, "func_192402_a", String[].class, Map.class, int.class, int.class);
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
            try {
                String group = JSONUtils.getString(json, "group", "");
                Map<String, Ingredient> map = (Map<String, Ingredient>)deserializeKey.invoke(null, JSONUtils.getJsonObject(json, "key"));
                String[] pattern = (String[])shrink.invoke(null, patternFromJson.invoke(null, JSONUtils.getJsonArray(json, "pattern")));
                int width = pattern[0].length();
                int height = pattern.length;
                NonNullList<Ingredient> ingredients = (NonNullList<Ingredient>) deserializeIngredients.invoke(null, pattern, map, width, height);
                ItemStack result = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));

                return new CopyBackpackDataRecipe(recipeId, group,width, height,ingredients,result);
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("Failed to parse backpack upgrade recipe");
            }
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
