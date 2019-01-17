package com.flanks255.simplybackpacks.recipes

import com.flanks255.simplybackpacks.simplybackpacks
import com.google.gson.JsonObject
import net.minecraft.block.Block
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.JsonUtils
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.common.crafting.IRecipeFactory
import net.minecraftforge.common.crafting.JsonContext
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.ItemStackHandler
import net.minecraftforge.oredict.ShapedOreRecipe

class CopyBackpackRecipeFactory: IRecipeFactory {
    override fun parse(context: JsonContext?, json: JsonObject): IRecipe {
        val recipe: ShapedOreRecipe = ShapedOreRecipe.factory(context, json)
        val primer: CraftingHelper.ShapedPrimer = CraftingHelper.ShapedPrimer()

        primer.height = recipe.recipeHeight
        primer.width = recipe.recipeWidth
        primer.mirrored = JsonUtils.getBoolean(json, "mirrored", true)
        primer.input = recipe.ingredients

        return CopyBackpackRecipe(ResourceLocation(simplybackpacks.MODID, "copyBackpack"), recipe.recipeOutput, primer)
    }

    class CopyBackpackRecipe(group: ResourceLocation, result: ItemStack, primer: CraftingHelper.ShapedPrimer): ShapedOreRecipe(group, result, primer) {
        override fun getCraftingResult(var1: InventoryCrafting): ItemStack {
            val newOutput: ItemStack = this.output.copy()
            var itemstack: ItemStack = ItemStack.EMPTY

            for (i in 0 until var1.sizeInventory) {
                val stack: ItemStack = var1.getStackInSlot(i)
                if (!stack.isEmpty) {
                    if (stack.item is IBackpackData) {
                        itemstack = stack
                        break
                    } else if (Block.getBlockFromItem(stack.item) is IBackpackData) {
                        itemstack = stack
                        break

                    }
                }
            }

            if (itemstack.hasTagCompound())
                newOutput.tagCompound = itemstack.tagCompound?.copy()

            if (itemstack.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) && newOutput.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
                val old = itemstack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
                val new = newOutput.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
                if (old is ItemStackHandler && new is ItemStackHandler)
                    new.deserializeNBT(old.serializeNBT())
            }
            return newOutput
        }
    }
}