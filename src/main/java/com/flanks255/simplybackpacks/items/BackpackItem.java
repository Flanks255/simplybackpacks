package com.flanks255.simplybackpacks.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class BackpackItem extends Item {

    public BackpackItem() {
        super(new Item.Properties().maxStackSize(1).group(ItemGroup.TOOLS));
    }
    @Override
    public String getTranslationKey(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag();
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
        CompoundNBT nbt = stack.getOrCreateTag();
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
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (!worldIn.isRemote) {
            ItemStack item = playerIn.getHeldItem(handIn);
            if (item.getItem() instanceof BackpackItem) {
                CompoundNBT tag = item.getOrCreateTag();
                UUID uuid;
                if (!tag.contains("UUID")) {
                    uuid = UUID.randomUUID();
                    tag.putUniqueId("UUID", uuid);
                } else
                    uuid = tag.getUniqueId("UUID");
            }
        }
        return ActionResult.resultSuccess(playerIn.getHeldItem(handIn));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        if (flagIn.isAdvanced() && stack.hasTag()) {
            if(stack.getTag().contains("tier"))
                tooltip.add(new StringTextComponent("Tier: "+ stack.getTag().getInt("tier")));
            if(stack.getTag().contains("UUID"))
                tooltip.add(new StringTextComponent("UUID: "+ stack.getTag().getUniqueId("UUID")));
        }
    }

}
