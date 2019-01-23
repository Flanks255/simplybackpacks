package com.flanks255.simplybackpacks.gui

import com.flanks255.simplybackpacks.simplybackpacks
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation

class FilterGui(container: BackpackContainer): GuiContainer(container) {

    private val background: ResourceLocation = when(container.slotcount) {
        18 -> ResourceLocation(simplybackpacks.MODID, "textures/gui/common_gui.png")
        33 -> ResourceLocation(simplybackpacks.MODID, "textures/gui/uncommon_gui.png")
        66 -> ResourceLocation(simplybackpacks.MODID, "textures/gui/rare_gui.png")
        else -> ResourceLocation(simplybackpacks.MODID, "textures/gui/epic_gui.png")
    }
    init {
        xSize = 176
        ySize = 166
    }

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
        //drawTexturedModalRect(guiLeft, guiTop, 0,0, xSize,ySize)
        drawTexturedQuadFit(guiLeft, guiTop, xSize,ySize, this.zLevel)
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        this.renderHoveredToolTip(mouseX,mouseY)
    }
}