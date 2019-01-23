package com.flanks255.simplybackpacks

import baubles.api.cap.BaublesCapabilities
import baubles.api.cap.IBaublesItemHandler
import com.flanks255.simplybackpacks.gui.BackpackContainer
import com.flanks255.simplybackpacks.gui.GuiHandler
import com.flanks255.simplybackpacks.items.ItemBackpackBase
import com.flanks255.simplybackpacks.network.NetworkWrapper
import com.flanks255.simplybackpacks.network.OpenMessage
import com.flanks255.simplybackpacks.network.ToggleMessage
import com.flanks255.simplybackpacks.proxy.ClientProxy
import com.flanks255.simplybackpacks.proxy.IProxy
import net.minecraft.client.settings.KeyBinding
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.item.EnumRarity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.event.entity.player.EntityItemPickupEvent
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.Event
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@Suppress("OverridingDeprecatedMember")
@Mod(modid=simplybackpacks.MODID, name=simplybackpacks.NAME, version=simplybackpacks.VERSION, modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter", dependencies = "")
object simplybackpacks {
    const val MODID = "simplybackpacks"
    const val NAME = "Simply Backpacks"
    const val VERSION = "@VERSION@"

    val commonBackpack: ItemBackpackBase = ItemBackpackBase("commonbackpack", 18, EnumRarity.COMMON).setCreativeTab(CreativeTabs.TOOLS)
    val uncommonBackpack: ItemBackpackBase = ItemBackpackBase("uncommonbackpack", 33, EnumRarity.UNCOMMON).setCreativeTab(CreativeTabs.TOOLS)
    val rareBackpack: ItemBackpackBase = ItemBackpackBase("rarebackpack", 66, EnumRarity.RARE).setCreativeTab(CreativeTabs.TOOLS)
    val epicBackpack: ItemBackpackBase = ItemBackpackBase("epicbackpack", 99, EnumRarity.EPIC).setCreativeTab(CreativeTabs.TOOLS)

    @SidedProxy(serverSide = "com.flanks255.simplybackpacks.proxy.ServerProxy", clientSide = "com.flanks255.simplybackpacks.proxy.ClientProxy")
    var proxy: IProxy? = null
    var isBaubles: Boolean = false

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent){
        NetworkWrapper.registerPackets()
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent){
        NetworkRegistry.INSTANCE.registerGuiHandler(this, GuiHandler)
        proxy!!.registerKeyBinds()
        isBaubles = Loader.isModLoaded("baubles")
    }

    fun toggleBauble(player: EntityPlayer){
        val stack: ItemStack? = findBackpackBauble(player)
        if (stack != null)
            (stack.item as ItemBackpackBase).togglePickup(player, stack)
    }

    //finds the first backpack in the hotbar.
    data class Result (val stack: ItemStack?, val slotID: Int)
    fun findBackpackHotbar(player: EntityPlayer): Result {
        val playerInv: InventoryPlayer = player.inventory
        for (i in 0..8){
            val stack: ItemStack = playerInv.getStackInSlot(i)
            if (stack.item is ItemBackpackBase)
                return Result(stack, i)
        }
        return Result(null, -1)
    }

    //finds the backpack in bauble slots
    fun findBackpackBauble(player: EntityPlayer): ItemStack? {
        if (player.hasCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null)) {
            val baubleInv:IBaublesItemHandler? = player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null)
            if (baubleInv != null) {
                for (i in 0 until baubleInv.slots){
                    val stack: ItemStack = baubleInv.getStackInSlot(i)
                    if (stack.item is ItemBackpackBase){
                        return stack
                    }
                }
            }
        }
        return null
    }
    private fun checkBaubles(player: EntityPlayer, event: EntityItemPickupEvent) {
        val stack: ItemStack? = findBackpackBauble(player)
        if (stack != null) {
            if ((stack.item as ItemBackpackBase).pickupEvent(event, stack)) {
                event.result = Event.Result.ALLOW
                return
            }
        }
    }

    @Mod.EventBusSubscriber(modid=simplybackpacks.MODID)
    object EventHandler {
        @JvmStatic
        @SubscribeEvent
        fun registerItems(event: RegistryEvent.Register<Item>) {
            event.registry.registerAll(
                    commonBackpack,
                    uncommonBackpack,
                    rareBackpack,
                    epicBackpack
            )
        }

        @JvmStatic
        @SubscribeEvent
        fun registerItems(event: ModelRegistryEvent) {
            registerItemModel(commonBackpack)
            registerItemModel(uncommonBackpack)
            registerItemModel(rareBackpack)
            registerItemModel(epicBackpack)
        }

        @JvmStatic
        @SubscribeEvent
        fun itemPickupEvent(event: EntityItemPickupEvent) {
            if (event.entityPlayer.openContainer is BackpackContainer || event.entityPlayer.isSneaking)
                return
            val playerinv: InventoryPlayer = event.entityPlayer.inventory

            for (i in 0..8) {
                val stack: ItemStack = playerinv.getStackInSlot(i)
                if (stack.item is ItemBackpackBase && (stack.item as ItemBackpackBase).pickupEvent(event, stack)) {
                    event.result = Event.Result.ALLOW
                    return
                }
            }

            if (isBaubles){
                checkBaubles(event.entityPlayer, event)
            }
        }

        @SideOnly(Side.CLIENT)
        @JvmStatic
        @SubscribeEvent
        fun keyEvent(event: InputEvent.KeyInputEvent) {
            val binds: Array<KeyBinding> = ClientProxy.keybindings

            if (binds[0].isPressed) {
                NetworkWrapper.wrapper.sendToServer(ToggleMessage())
            }
            if (binds[1].isPressed)
                NetworkWrapper.wrapper.sendToServer(OpenMessage())
        }
    }

    fun registerItemModel(item: ItemBackpackBase) {
        proxy?.registerItemRenderer(item, 0, item.name)
    }
}