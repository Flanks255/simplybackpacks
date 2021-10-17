package com.flanks255.simplybackpacks.crafting;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.IIngredientSerializer;

import java.util.stream.Stream;

public class TargetNBTIngredient extends Ingredient {
    public TargetNBTIngredient(Stream<? extends Value> itemLists) {
        super(itemLists);
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return SERIALIZER;
    }

    public static TargetNBTIngredient of(ItemLike itemProvider) {
        return new TargetNBTIngredient(Stream.of(new ItemValue(new ItemStack(itemProvider))));
    }
    public static TargetNBTIngredient of(ItemStack itemStack) {
        return new TargetNBTIngredient(Stream.of(new ItemValue(itemStack)));
    }
    public static TargetNBTIngredient of(Tag tag) {
        return new TargetNBTIngredient(Stream.of(new TagValue(tag)));
    }



    @Override
    public JsonElement toJson() {
        JsonObject tmp = super.toJson().getAsJsonObject();
        tmp.addProperty("type", Serializer.NAME.toString());
        return tmp;
    }


    public static Serializer SERIALIZER = new Serializer();
    public static class Serializer implements IIngredientSerializer<TargetNBTIngredient> {
        public static ResourceLocation NAME = new ResourceLocation(SimplyBackpacks.MODID, "nbt_target");

        @Override
        public TargetNBTIngredient parse(FriendlyByteBuf buffer) {
            return new TargetNBTIngredient(Stream.generate(() -> new ItemValue(buffer.readItem())).limit(buffer.readVarInt()));
        }

        @Override
        public TargetNBTIngredient parse(JsonObject json) {
            return new TargetNBTIngredient(Stream.of(Ingredient.valueFromJson(json)));
        }

        @Override
        public void write(FriendlyByteBuf buffer, TargetNBTIngredient ingredient) {
            ItemStack[] items = ingredient.getItems();
            buffer.writeVarInt(items.length);

            for (ItemStack stack : items)
                buffer.writeItem(stack);
        }
    }
}
