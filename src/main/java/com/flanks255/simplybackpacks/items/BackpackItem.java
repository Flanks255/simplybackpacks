package com.flanks255.simplybackpacks.items;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class BackpackItem extends Item {

    public BackpackItem() {
        super(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_TOOLS));
    }
    @Override
    public String getDescriptionId(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        if (nbt.contains("tier"))
            switch(nbt.getInt("tier")) {
                case 1:
                    return "item.simplybackpacks.uncommonbackpack";
                case 2:
                    return "item.simplybackpacks.rarebackpack";
                case 3:
                    return "item.simplybackpacks.epicbackpack";
                default:
                return "item.simplybackpacks.commonbackpack";
            }
        else
            return "item.simplybackpacks.commonbackpack";
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        if (nbt.contains("tier"))
            switch(nbt.getInt("tier")) {
                case 1:
                    return Rarity.UNCOMMON;
                case 2:
                    return Rarity.RARE;
                case 3:
                    return Rarity.EPIC;
                default:
                    return Rarity.COMMON;
            }
        else
            return Rarity.COMMON;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if (!worldIn.isClientSide) {
            ItemStack item = playerIn.getItemInHand(handIn);
            if (item.getItem() instanceof BackpackItem) {
                CompoundTag tag = item.getOrCreateTag();
                UUID uuid;
                if (!tag.contains("UUID")) {
                    uuid = UUID.randomUUID();
                    tag.putUUID("UUID", uuid);
                } else
                    uuid = tag.getUUID("UUID");
            }
        }
        return InteractionResultHolder.success(playerIn.getItemInHand(handIn));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        if (flagIn.isAdvanced() && stack.hasTag()) {
            if(stack.getTag().contains("tier"))
                tooltip.add(new TextComponent("Tier: "+ stack.getTag().getInt("tier")));
            if(stack.getTag().contains("UUID"))
                tooltip.add(new TextComponent("UUID: "+ stack.getTag().getUUID("UUID")));
        }
    }

}
