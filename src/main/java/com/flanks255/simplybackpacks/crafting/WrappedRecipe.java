package com.flanks255.simplybackpacks.crafting;

import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class WrappedRecipe implements FinishedRecipe {
    final FinishedRecipe inner;
    RecipeSerializer<?> serializerOverride;

    public WrappedRecipe(FinishedRecipe innerIn) {
        this.inner = innerIn;
    }

    public WrappedRecipe(FinishedRecipe innerIn, RecipeSerializer<?> serializerOverrideIn) {
        this.inner = innerIn;
        this.serializerOverride = serializerOverrideIn;
    }

    public static Consumer<FinishedRecipe> Inject(Consumer<FinishedRecipe> consumer, RecipeSerializer<?> serializer) {
        return iFinishedRecipe -> consumer.accept(new WrappedRecipe(iFinishedRecipe, serializer));
    }

    @Override
    public void serializeRecipeData(@Nonnull JsonObject json) {
        this.inner.serializeRecipeData(json);
    }

    @Override
    @Nonnull
    public JsonObject serializeRecipe() {
        JsonObject jsonObject = new JsonObject();

        if (this.serializerOverride != null)
            jsonObject.addProperty("type", this.serializerOverride.getRegistryName().toString());
        else
            jsonObject.addProperty("type", this.inner.getType().getRegistryName().toString());
        serializeRecipeData(jsonObject);
        return jsonObject;
    }

    @Override
    @Nonnull
    public ResourceLocation getId() {
        return this.inner.getId();
    }

    @Override
    @Nonnull
    public RecipeSerializer<?> getType() {
        return this.serializerOverride != null? this.serializerOverride : this.inner.getType();
    }

    @Nullable
    @Override
    public JsonObject serializeAdvancement() {
        return this.inner.serializeAdvancement();
    }

    @Nullable
    @Override
    public ResourceLocation getAdvancementId() {
        return this.inner.getAdvancementId();
    }
}
