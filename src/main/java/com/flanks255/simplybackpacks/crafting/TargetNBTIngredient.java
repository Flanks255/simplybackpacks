package com.flanks255.simplybackpacks.crafting;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IIngredientSerializer;

import java.util.stream.Stream;

public class TargetNBTIngredient extends Ingredient {
    public TargetNBTIngredient(Stream<? extends IItemList> itemLists) {
        super(itemLists);
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return SERIALIZER;
    }

    public static TargetNBTIngredient of(IItemProvider itemProvider) {
        return new TargetNBTIngredient(Stream.of(new SingleItemList(new ItemStack(itemProvider))));
    }
    public static TargetNBTIngredient of(ItemStack itemStack) {
        return new TargetNBTIngredient(Stream.of(new SingleItemList(itemStack)));
    }
    public static TargetNBTIngredient of(ITag tag) {
        return new TargetNBTIngredient(Stream.of(new TagList(tag)));
    }



    @Override
    public JsonElement serialize() {
        JsonObject tmp = super.serialize().getAsJsonObject();
        tmp.addProperty("type", Serializer.NAME.toString());
        return tmp;
    }


    public static Serializer SERIALIZER = new Serializer();
    public static class Serializer implements IIngredientSerializer<TargetNBTIngredient> {
        public static ResourceLocation NAME = new ResourceLocation(SimplyBackpacks.MODID, "nbt_target");

        @Override
        public TargetNBTIngredient parse(PacketBuffer buffer) {
            return new TargetNBTIngredient(Stream.generate(() -> new SingleItemList(buffer.readItemStack())).limit(buffer.readVarInt()));
        }

        @Override
        public TargetNBTIngredient parse(JsonObject json) {
            return new TargetNBTIngredient(Stream.of(Ingredient.deserializeItemList(json)));
        }

        @Override
        public void write(PacketBuffer buffer, TargetNBTIngredient ingredient) {
            ItemStack[] items = ingredient.getMatchingStacks();
            buffer.writeVarInt(items.length);

            for (ItemStack stack : items)
                buffer.writeItemStack(stack);
        }
    }
}
