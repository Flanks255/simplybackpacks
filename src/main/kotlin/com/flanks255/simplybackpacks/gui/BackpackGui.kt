package com.flanks255.simplybackpacks.gui

import com.flanks255.simplybackpacks.proxy.ClientProxy
import com.flanks255.simplybackpacks.simplybackpacks
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.resources.I18n
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11

class BackpackGui(val container: BackpackContainer): GuiContainer(container) {

    private val background: ResourceLocation = when(container.slotcount) {
        18 -> ResourceLocation(simplybackpacks.MODID, "textures/gui/common_gui.png")
        33 -> ResourceLocation(simplybackpacks.MODID, "textures/gui/uncommon_gui.png")
        66 -> ResourceLocation(simplybackpacks.MODID, "textures/gui/rare_gui.png")
        else -> ResourceLocation(simplybackpacks.MODID, "textures/gui/epic_gui.png")
    }
    init {
        val size: Size = when(container.slotcount) {
            18 -> Size(176, 150)
            33 -> Size(212, 168)
            66 -> Size(212, 222)
            else -> Size(212, 276)
        }
        xSize = size.width
        ySize = size.height
    }

    private data class Size(val width: Int, val height: Int)

    fun drawTexturedQuadFit(x: Int, y: Int, width: Int, height: Int, z: Float) {
        val tessellator: Tessellator = Tessellator.getInstance()
        val buffer: BufferBuilder = tessellator.buffer

        buffer.begin(7, DefaultVertexFormats.POSITION_TEX)
        buffer.pos((x + 0).toDouble(), (y + height).toDouble(), z.toDouble()).tex(0.0,1.0).endVertex()
        buffer.pos((x + width).toDouble(), (y + height).toDouble(), z.toDouble()).tex(1.0,1.0).endVertex()
        buffer.pos((x + width).toDouble(), (y + 0).toDouble(), z.toDouble()).tex(1.0,0.0).endVertex()
        buffer.pos((x + 0).toDouble(), (y + 0).toDouble(), z.toDouble()).tex(0.0,0.0).endVertex()

        tessellator.draw()
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        mc.textureManager.bindTexture(background)
        drawTexturedQuadFit(guiLeft, guiTop, xSize,ySize, this.zLevel)
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        GL11.glPushMatrix()
        GL11.glColor4f(0.25f,0.25f,0.25f,1.0f)
        fontRenderer.drawString(I18n.format(container.itemKey), 7, 6, 0x404040)
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F)
        GL11.glPopMatrix()

        super.drawGuiContainerForegroundLayer(mouseX, mouseY)
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        this.renderHoveredToolTip(mouseX,mouseY)
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        super.keyTyped(typedChar, keyCode)
        val key = simplybackpacks.proxy?.getKeyBindCode("key.simplybackpacks.backpackopen.desc")
        if (simplybackpacks.proxy is ClientProxy && keyCode == key)
            this.mc.player.closeScreen()
    }
}