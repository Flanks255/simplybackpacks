package com.flanks255.simplybackpacks.crafting;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.stream.Stream;

public record TargetNBTIngredient(Ingredient ingredient) implements ICustomIngredient {
    public static final MapCodec<TargetNBTIngredient> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(Ingredient.CODEC.fieldOf("value").forGetter(TargetNBTIngredient::ingredient)
            ).apply(builder, TargetNBTIngredient::new)
    );
    //public static final MapCodec<TargetNBTIngredient> CODEC = Ingredient.CODEC.xmap(TargetNBTIngredient::new, TargetNBTIngredient::new); //Wrapped in value sub-object
/*    public static final Codec<TargetNBTIngredient> CODEC =
            RecordCodecBuilder.create(builder -> builder.group(
                BuiltInRegistries.ITEM.byNameCodec().fieldOf( "item").forGetter(TargetNBTIngredient::getItem)
            ).apply(builder, TargetNBTIngredient::new));*/

    @Override
    public boolean test(@Nullable ItemStack pStack) {
        return ingredient.test(pStack);
    }

    @Override
    public Stream<ItemStack> getItems() {
        return Stream.of(ingredient.getItems());
    }

    @Override
    public boolean isSimple() {
        return ingredient.isSimple();
    }

    @Override
    public IngredientType<?> getType() {
        return SimplyBackpacks.TARGET_INGREDIENT.get();
    }

    public static Ingredient of(ItemLike itemProvider) {
        return new TargetNBTIngredient(Ingredient.of(itemProvider)).toVanilla();
    }
    public static Ingredient of(ItemStack itemStack) {
        return new TargetNBTIngredient(Ingredient.of(itemStack)).toVanilla();
    }
    public static Ingredient of(TagKey<Item> tag) {
        return new TargetNBTIngredient(Ingredient.of(tag)).toVanilla();
    }
}
