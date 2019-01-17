package com.flanks255.simplybackpacks.items

import baubles.api.BaubleType
import baubles.api.IBauble
import com.flanks255.simplybackpacks.BackpackItemStackHandler
import com.flanks255.simplybackpacks.recipes.IBackpackData
import com.flanks255.simplybackpacks.simplybackpacks
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.EnumRarity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.capabilities.ICapabilitySerializable
import net.minecraftforge.event.entity.player.EntityItemPickupEvent
import net.minecraftforge.fml.common.Optional
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.*
import org.lwjgl.input.Keyboard

//@Optional.Interface(iface="baubles.api.IBauble.class", modid = "baubles", striprefs = true)
class ItemBackpackBase(val name: String, val size: Int,private val rarity: EnumRarity): Item(), IBackpackData, IBauble {
    init {
        super.setUnlocalizedName(simplybackpacks.MODID+"."+name)
        setRegistryName(name)
        setMaxStackSize(1)

    }

    override fun getRarity(stack: ItemStack): EnumRarity {
        return rarity
    }

    override fun setCreativeTab(tab: CreativeTabs): ItemBackpackBase {
        super.setCreativeTab(tab)
        return this
    }

    fun pickupEvent(event: EntityItemPickupEvent, stack: ItemStack): Boolean {
        val nbt: NBTTagCompound? = if (stack.hasTagCompound()) stack.tagCompound else null
        if (nbt == null){
            return false
        }
        else
        {
            if (nbt.hasKey("Pickup")) {
             if (!nbt.getBoolean("Pickup"))
                 return false
            }
            else
                return false
        }

        val handler: IItemHandler? = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
        if (handler == null)
            return false
        val pickedup: ItemStack = event.item.item
        for (i in 0 until handler.slots) {
            val slot: ItemStack = handler.getStackInSlot(i)
            if (slot.isEmpty || (ItemHandlerHelper.canItemStacksStack(slot, pickedup) && slot.count < slot.maxStackSize && slot.count < handler.getSlotLimit(i))) {
                val count: Int = handler.insertItem(i, pickedup.copy(), false).count
                pickedup.count = count
                if(count == 0)
                    break
            }
        }
        return pickedup.isEmpty
    }

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, handIn: EnumHand): ActionResult<ItemStack> {
        if (!worldIn.isRemote) {
            playerIn.openGui(simplybackpacks, 0, worldIn, playerIn.position.x, playerIn.position.y, playerIn.position.z )
        }
        return super.onItemRightClick(worldIn, playerIn, handIn)
    }

    override fun getBaubleType(itemstack: ItemStack?): BaubleType {
        return BaubleType.BODY
    }

    fun togglePickup(player:EntityPlayer ,item: ItemStack){
        val nbt: NBTTagCompound? = if (item.hasTagCompound()) item.tagCompound else NBTTagCompound()

        if (nbt != null) {
            if (nbt.hasKey("Pickup"))
            {
                nbt.setBoolean("Pickup", !nbt.getBoolean("Pickup"))
                player.sendStatusMessage(TextComponentString("Auto-Pickup "+if (nbt.getBoolean("Pickup")) "§aEnabled§r" else "§cDisabled§r"), true)
            }
            else {
                nbt.setBoolean("Pickup", true)
                player.sendStatusMessage(TextComponentString("Auto-Pickup §aEnabled§r"),true)
            }
            item.tagCompound = nbt
        }

    }

    override fun initCapabilities(stack: ItemStack, nbt: NBTTagCompound?): ICapabilityProvider? {
        return BackpackCaps(size)
    }

    override fun getNBTShareTag(stack: ItemStack): NBTTagCompound? {
        val handler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)

        if (handler is ItemStackHandler || stack.hasTagCompound()) {
            val nbt = NBTTagCompound()

            if (stack.tagCompound != null) {
                nbt.setTag("nbt", stack.tagCompound)
            }

            if (handler is ItemStackHandler) {
                nbt.setTag("inv", handler.serializeNBT())
            }

            return nbt
        }

        return null
    }
    override fun readNBTShareTag(stack: ItemStack, nbt: NBTTagCompound?) {
        stack.tagCompound = null
        if (nbt != null) {
            if (nbt.hasKey("nbt")) {
                stack.tagCompound = nbt.getCompoundTag("nbt")
            }

            if (nbt.hasKey("inv")) {
                val handler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)

                if (handler is ItemStackHandler) {
                    handler.deserializeNBT(nbt.getCompoundTag("inv"))
                }
            }
        }
    }
    class BackpackCaps(val size: Int): ICapabilitySerializable<NBTBase> {

        val inventory: IItemHandler = BackpackItemStackHandler(size)

        override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
            return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
        }

        override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
            return if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inventory) else null
        }

        override fun serializeNBT(): NBTBase? {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(inventory, null)
        }

        override fun deserializeNBT(nbt: NBTBase?) {
            CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(inventory, null, nbt)
        }
    }
    private fun hasTranslation(key :String): Boolean{
        return I18n.format(key) != key
    }

    private fun fallbackString(key: String, fallback: String): String{
        return if(hasTranslation(key)) I18n.format(key) else fallback
    }
    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
            if (stack.hasTagCompound())
            {
                val nbt: NBTTagCompound? = stack.tagCompound
                if (nbt != null){
                    if (nbt.hasKey("Pickup")){
                        if (nbt.getBoolean("Pickup")){
                            tooltip.add("Auto-Pickup: §aEnabled§r")
                        }
                    }
                }
            }
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            tooltip.add(I18n.format(unlocalizedName + ".info"))
            if (hasTranslation(unlocalizedName + ".info2"))
                tooltip.add(I18n.format(unlocalizedName + ".info2"))
            if (hasTranslation(unlocalizedName + ".info3"))
                tooltip.add(I18n.format(unlocalizedName + ".info3"))
        } else {
            tooltip.add(fallbackString("simplybackpacks.shift", "Press <§6§oShift§r> for info."))
        }
        super.addInformation(stack, worldIn, tooltip, flagIn)
    }
}