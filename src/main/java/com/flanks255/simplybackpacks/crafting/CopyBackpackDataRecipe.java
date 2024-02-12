package com.flanks255.simplybackpacks.crafting;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.items.BackpackItem;
import com.mojang.serialization.Codec;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
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
    public ItemStack assemble(@Nonnull CraftingContainer inv, RegistryAccess wat) {
        final ItemStack craftingResult = super.assemble(inv, wat);
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
            for (int i = 0; i < inv.getContainerSize(); i++) {
                final ItemStack item = inv.getItem(i);
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

    @Override
    @Nonnull
    public RecipeSerializer<?> getSerializer() {
        return SimplyBackpacks.COPYRECIPE.get();
    }

    public static class Serializer implements RecipeSerializer<CopyBackpackDataRecipe> {
/*        private static final Codec<CopyBackpackDataRecipe> CODEC = RecordCodecBuilder.create($ -> $.group( // :(
                ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter(ShapedRecipe::getGroup),
                CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(ShapedRecipe::category),
                ShapedRecipePattern.MAP_CODEC.forGetter(a -> a.pattern),
                ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("result").forGetter(c -> c.getResultItem(RegistryAccess.EMPTY)),
                ExtraCodecs.strictOptionalField(Codec.BOOL, "show_notification", false).forGetter(ShapedRecipe::showNotification)
        ).apply($, CopyBackpackDataRecipe::new));*/
        private static final Codec<CopyBackpackDataRecipe> CODEC = ShapedRecipe.Serializer.CODEC.xmap(CopyBackpackDataRecipe::new, $ -> $);
        @Override
        public @NotNull Codec<CopyBackpackDataRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull CopyBackpackDataRecipe fromNetwork(@NotNull FriendlyByteBuf pBuffer) {
            return new CopyBackpackDataRecipe(RecipeSerializer.SHAPED_RECIPE.fromNetwork(pBuffer));
        }

        @Override
        public void toNetwork(@Nonnull FriendlyByteBuf buffer, @Nonnull CopyBackpackDataRecipe recipe) {
            try {
                RecipeSerializer.SHAPED_RECIPE.toNetwork(buffer, recipe);
            }
            catch (Exception exception) {
                SimplyBackpacks.LOGGER.info("Error writing CopyBackpack Recipe to packet: ", exception);
                throw exception;
            }
        }
    }

}
