package com.flanks255.simplybackpacks.items

import baubles.api.BaubleType
import baubles.api.IBauble
import com.flanks255.simplybackpacks.BackpackItemStackHandler
import com.flanks255.simplybackpacks.FilterItemStackHandler
import com.flanks255.simplybackpacks.network.NetworkWrapper
import com.flanks255.simplybackpacks.network.ToggleMessageMessage
import com.flanks255.simplybackpacks.recipes.IBackpackData
import com.flanks255.simplybackpacks.simplybackpacks
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.EnumRarity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.capabilities.ICapabilitySerializable
import net.minecraftforge.event.entity.player.EntityItemPickupEvent
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.Optional
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.*
import org.lwjgl.input.Keyboard

@Optional.Interface(iface="baubles.api.IBauble", modid = "baubles")
class ItemBackpackBase(val name: String, val size: Int, private val rarity: EnumRarity): Item(), IBackpackData, IBauble {
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

        if(!filterItem(event.item.item, stack))
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
            if (playerIn.isSneaking) //Filter
                playerIn.openGui(simplybackpacks, 8, worldIn, playerIn.position.x, playerIn.position.y, playerIn.position.z )
            else //Inventory
                playerIn.openGui(simplybackpacks, 0, worldIn, playerIn.position.x, playerIn.position.y, playerIn.position.z )
        }
        return super.onItemRightClick(worldIn, playerIn, handIn)
    }

    fun filterItem(item: ItemStack, packItem: ItemStack): Boolean {
        val handler: IItemHandler? = packItem.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
        if (handler == null)
            return false
        if (handler !is BackpackItemStackHandler)
            return false

        val filterOpts: Int = if (packItem.hasTagCompound()) packItem.tagCompound?.getInteger("Filter")?:0 else 0
        val whitelist: Boolean = filterOpts and  1 > 0
        val metaMatch: Boolean = filterOpts and 2 > 0
        val nbtMatch: Boolean = filterOpts and 4 > 0

        val filter: FilterItemStackHandler = handler.filter as FilterItemStackHandler

        for (i in 0..15) {
            val fStack = filter.getStackInSlot(i)
            if (!fStack.isEmpty) {
                if (fStack.item == item.item) {
                    if (metaMatch) {
                        if (fStack.metadata == item.metadata)
                            if (nbtMatch)
                                return if (ItemStack.areItemStackTagsEqual(fStack, item)) whitelist else !whitelist
                            else
                                return whitelist
                    } else {
                        if (nbtMatch)
                            return if (ItemStack.areItemStackTagsEqual(fStack, item)) whitelist else !whitelist
                        else
                            return whitelist
                    }
                }
            }
        }

        return !whitelist
    }

    @Optional.Method(modid = "baubles")
    override fun getBaubleType(itemstack: ItemStack?): BaubleType {
        return BaubleType.BODY
    }

    fun togglePickup(player:EntityPlayer ,item: ItemStack){
        val nbt: NBTTagCompound? = if (item.hasTagCompound()) item.tagCompound else NBTTagCompound()

        if (nbt != null) {
            if (nbt.hasKey("Pickup"))
            {
                nbt.setBoolean("Pickup", !nbt.getBoolean("Pickup"))
                if (simplybackpacks?.proxy?.isClient()?:false)
                    player.sendStatusMessage(TextComponentString(I18n.format(if(nbt.getBoolean("Pickup")) "simplybackpacks.autopickupenabled" else "simplybackpacks.autopickupdisabled")), true)
                else
                    if (player is EntityPlayerMP)
                        NetworkWrapper.wrapper.sendTo(ToggleMessageMessage(nbt.getBoolean("Pickup")), player)
            }
            else {
                nbt.setBoolean("Pickup", true)
                if (simplybackpacks?.proxy?.isClient()?:false)
                    player.sendStatusMessage(TextComponentString(I18n.format("simplybackpacks.autopickupenabled")),true)
                else
                    if (player is EntityPlayerMP)
                        NetworkWrapper.wrapper.sendTo(ToggleMessageMessage(true), player)
            }
            item.tagCompound = nbt
        }

    }

    override fun initCapabilities(stack: ItemStack, nbt: NBTTagCompound?): ICapabilityProvider? {
        return BackpackCaps(size, stack)
    }

    override fun getNBTShareTag(stack: ItemStack): NBTTagCompound? {
        //if (simplybackpacks.proxy?.isClient()?:false == false) {
        if(FMLCommonHandler.instance().effectiveSide == Side.SERVER){
            val handler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
            val nbt = stack.tagCompound?:NBTTagCompound()
            if (handler is ItemStackHandler) {
                nbt.setTag("inv", handler.serializeNBT())

                if (handler is BackpackItemStackHandler) {
                    nbt.setTag("filter", handler.filter.serializeNBT())
                }

                return nbt
            }
        }
        return stack.tagCompound
    }
    override fun readNBTShareTag(stack: ItemStack, nbt: NBTTagCompound?) {
        stack.tagCompound = nbt
        if (nbt != null) {
            if (nbt.hasKey("inv")) {
                val handler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)

                if (handler is ItemStackHandler) {
                    handler.deserializeNBT(nbt.getCompoundTag("inv"))
                }
            }
            if (nbt.hasKey("filter")) {
                val handler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)

                if (handler is BackpackItemStackHandler) {
                    handler.filter.deserializeNBT(nbt.getCompoundTag("filter"))
                }
            }
        }
    }
    class BackpackCaps(val size: Int, val stack: ItemStack): ICapabilitySerializable<NBTBase> {

        val inventory: IItemHandler = BackpackItemStackHandler(size)

        override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
            return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
        }

        override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
            return if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inventory) else null
        }

        override fun serializeNBT(): NBTBase? {
            val sNBT = stack.tagCompound?: NBTTagCompound()
            sNBT.setTag("inv", (inventory as BackpackItemStackHandler).serializeNBT())
            sNBT.setTag("filter", inventory.filter.serializeNBT())
            stack.tagCompound = sNBT

            val nbt = NBTTagCompound()
            nbt.setTag("Inventory", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(inventory, null)?:NBTTagList())
            nbt.setTag("Filter", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT((inventory as BackpackItemStackHandler).filter, null)?:NBTTagList())
            return nbt
        }

        override fun deserializeNBT(nbt: NBTBase?) {
            if (nbt is NBTTagCompound) {
                CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(inventory, null, nbt.getTag("Inventory")?:NBTTagList())
                CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT((inventory as BackpackItemStackHandler).filter, null, nbt.getTag("Filter")?:NBTTagList())
            }
            else
                CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(inventory, null, nbt)
        }
    }
    private fun hasTranslation(key :String): Boolean {
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
                            tooltip.add(I18n.format("simplybackpacks.autopickupenabled"))
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