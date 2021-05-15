package com.flanks255.simplybackpacks.items;

import com.flanks255.simplybackpacks.gui.NeoSBContainer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BackpackItem extends Item implements INamedContainerProvider {

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
            NetworkHooks.openGui((ServerPlayerEntity) playerIn, this);
        }
        return ActionResult.resultSuccess(playerIn.getHeldItem(handIn));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        if (flagIn.isAdvanced() && stack.hasTag() && stack.getTag().contains("tier")) {
            tooltip.add(new StringTextComponent("Tier: "+ stack.getTag().getInt("tier")));
        }
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new Caps(stack);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("Backpack"); //TODO fix later...
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new NeoSBContainer(windowId, NeoSBContainer.findBackpack(playerEntity));
    }

    static class Caps implements ICapabilityProvider {
        private final ItemStack stack;

        Caps(ItemStack stack) {
            this.stack = stack;
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {

            return null;
        }
    }
}
