package com.flanks255.simplybackpacks.proxy

import com.flanks255.simplybackpacks.simplybackpacks
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.settings.KeyBinding
import net.minecraft.item.Item
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.client.registry.ClientRegistry
import org.lwjgl.input.Keyboard

class ClientProxy :IProxy{

    companion object {
        val keybindings: Array<KeyBinding> = arrayOf(
                KeyBinding("key.simplybackpacks.backpackpickup.desc", Keyboard.KEY_NONE, "key.simplybackpacks.category"),
                KeyBinding("key.simplybackpacks.backpackopen.desc", Keyboard.KEY_NONE, "key.simplybackpacks.category")
        )
    }

    override fun registerKeyBinds() {
        for (n in keybindings) {
            ClientRegistry.registerKeyBinding(n)
        }
    }

    override fun registerItemRenderer(item: Item, meta: Int, id: String){
        ModelLoader.setCustomModelResourceLocation(item, meta, ModelResourceLocation( simplybackpacks.MODID+":"+id, "inventory"))
    }
}