package com.flanks255.simplybackpacks.crafting;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.items.BackpackItem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class CopyBackpackDataRecipe extends ShapedRecipe {
    public CopyBackpackDataRecipe(final String group, CraftingBookCategory category, ShapedRecipePattern pattern, final ItemStack recipeOutput) {
        super(group, category, pattern, recipeOutput);
    }

    public CopyBackpackDataRecipe(ShapedRecipe shapedRecipe) {
        super(shapedRecipe.getGroup(), shapedRecipe.category(), shapedRecipe.pattern, shapedRecipe.getResultItem(RegistryAccess.EMPTY));
    }

    @Override
    @Nonnull
    public ItemStack assemble(@Nonnull CraftingInput inv, @Nonnull HolderLookup.Provider provider) {
        final ItemStack craftingResult = super.assemble(inv, provider);
        //TargetNBTIngredient donorIngredient = null;
        ItemStack dataSource = ItemStack.EMPTY;
/*        NonNullList<? extends Ingredient> ingredients = getIngredients();
        for (Ingredient ingredient : ingredients) {
            if (ingredient instanceof TargetNBTIngredient target) {
                donorIngredient = target;
                break;
            }
        }
        if (donorIngredient == null) {
            SimplyBackpacks.LOGGER.info("Copy Data Recipe missing donor ingredient");
            return new ItemStack(Items.AIR);
        }*/
        if (!craftingResult.isEmpty()) {
            for (int i = 0; i < inv.size(); i++) {
                final ItemStack item = inv.getItem(i);
                if (!item.isEmpty() && item.getItem() instanceof BackpackItem) {
                    dataSource = item;
                    break;
                }
            }

            if (!dataSource.isEmpty()) {
                if (dataSource.has(SimplyBackpacks.BACKPACK_UUID.get())) {
                    craftingResult.set(SimplyBackpacks.BACKPACK_UUID.get(), dataSource.get(SimplyBackpacks.BACKPACK_UUID.get()));
                }
                else if (dataSource.has(DataComponents.CUSTOM_DATA)){ //Legacy support
                    if (dataSource.get(DataComponents.CUSTOM_DATA).contains("UUID")){
                        craftingResult.set(SimplyBackpacks.BACKPACK_UUID.get(), dataSource.get(DataComponents.CUSTOM_DATA).copyTag().getUUID("UUID"));
                    }
                }
            }
        }

        return craftingResult;
    }

    @Override
    @Nonnull
    public RecipeSerializer<?> getSerializer() {
        return SimplyBackpacks.COPYRECIPE.get();
    }

    public static class Serializer implements RecipeSerializer<CopyBackpackDataRecipe> {
        private static final MapCodec<CopyBackpackDataRecipe> CODEC = ShapedRecipe.Serializer.CODEC.xmap(CopyBackpackDataRecipe::new, $ -> $);
        private static final StreamCodec<RegistryFriendlyByteBuf, CopyBackpackDataRecipe> STREAM_CODEC = RecipeSerializer.SHAPED_RECIPE.streamCodec().map(CopyBackpackDataRecipe::new, CopyBackpackDataRecipe::new);
        @Override
        public @NotNull MapCodec<CopyBackpackDataRecipe> codec() {
            return CODEC;
        }
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, CopyBackpackDataRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

}
