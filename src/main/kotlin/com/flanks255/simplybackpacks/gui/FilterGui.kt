package com.flanks255.simplybackpacks.gui

import com.flanks255.simplybackpacks.simplybackpacks
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiButtonToggle
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.resources.I18n
import net.minecraft.util.ResourceLocation
import net.minecraftforge.items.IItemHandler
import org.lwjgl.opengl.GL11

class FilterGui(val container: FilterContainer): GuiContainer(container) {

    private val background: ResourceLocation = ResourceLocation(simplybackpacks.MODID, "textures/gui/filter_gui.png")
    private var filter: Int = 0

    init {
        xSize = 176
        ySize = 166
    }

    override fun initGui() {
        super.initGui()
        var slot = 0
        for (row in 0 until 4) {
            for (col in 0 until 4) {
                val x = guiLeft + 7 + col * 18
                val y = guiTop + 7 + row * 18
                buttonList.add(slot, slotButton(slot, x + 1, y + 1, slot))
                slot++
            }
        }

        buttonList.add(16, switchButton(16,guiLeft + 80, guiTop + 8, "simplybackpacks.whitelist"))
        buttonList.add(17, switchButton(17,guiLeft + 80, guiTop + 18 + 8, "simplybackpacks.metadata"))
        buttonList.add(18, switchButton(18,guiLeft + 80, guiTop + 36 + 8, "simplybackpacks.nbtdata"))

        updateSwitches()
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

    override fun actionPerformed(button: GuiButton) {
        if (button is slotButton) {
            if (container.enchantItem(container.player, button.slot))
            mc.playerController.sendEnchantPacket(container.windowId, button.slot)
        }

        if (button is switchButton) {
            var opts = container.getFilterOpts()
            when(button.id) {
                16-> opts = opts xor 1
                17-> opts = opts xor 2
                18-> opts = opts xor 4
                else -> return
            }
            container.setFilterOpts(opts)
        }
        updateSwitches()
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        mc.textureManager.bindTexture(background)
        drawTexturedQuadFit(guiLeft, guiTop, xSize,ySize, this.zLevel)
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        this.renderHoveredToolTip(mouseX,mouseY)
    }

    override fun renderHoveredToolTip(x: Int, y: Int) {
        super.renderHoveredToolTip(x, y)

        for (button in buttonList) {
            if (button.isMouseOver && button is slotButton) {
                if (container.filterHandler?.getStackInSlot(button.slot)?.isEmpty == false) {
                    renderToolTip(container?.filterHandler?.getStackInSlot(button.slot), x, y)
                }
            }
        }
    }

    fun updateSwitches() {
        val opts = container.getFilterOpts()
        (buttonList[16] as switchButton).state = opts and 1 > 0
        (buttonList[17] as switchButton).state = opts and 2 > 0
        (buttonList[18] as switchButton).state = opts and 4 > 0
    }

    inner class slotButton(id: Int, x: Int, y: Int, val slot: Int): GuiButton(id, x, y, 16,16, "") {
        override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int, partialTicks: Float) {
            if (visible) {
                GL11.glPushMatrix()
                GlStateManager.color(1F, 1F, 1F)
                hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height

                GlStateManager.enableBlend()
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)

                if (hovered)
                    drawRect(x, y, x + width, y + height, -2130706433)

                if (container.filterHandler != null && container.filterHandler?.getStackInSlot(slot)?.isEmpty == false) {
                    zLevel = 100F
                    itemRender.zLevel = 100F
                    GlStateManager.enableDepth()
                    RenderHelper.enableGUIStandardItemLighting()
                    itemRender.renderItemAndEffectIntoGUI(mc.player, (container.filterHandler as IItemHandler).getStackInSlot(slot), x, y)
                    itemRender.renderItemOverlayIntoGUI(fontRenderer, (container.filterHandler as IItemHandler).getStackInSlot(slot), x, y, "")
                    zLevel = 0F
                    itemRender.zLevel = 0F
                }
                GL11.glPopMatrix()
                mouseDragged(mc, mouseX, mouseY)
            }
        }
    }

    inner class switchButton(id: Int, x: Int, y: Int, val textKey: String): GuiButton(id, x, y, 32, 16, "") {
        var state: Boolean = false
        private val off: ResourceLocation = ResourceLocation(simplybackpacks.MODID, "textures/gui/switch_off.png")
        private val on: ResourceLocation = ResourceLocation(simplybackpacks.MODID, "textures/gui/switch_on.png")

        fun drawTexturedQuadFit(x: Int, y: Int, width: Int, height: Int, z: Float) {
            val tessellator: Tessellator = Tessellator.getInstance()
            val buffer: BufferBuilder = tessellator.buffer
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F)
            buffer.begin(7, DefaultVertexFormats.POSITION_TEX)
            buffer.pos((x + 0).toDouble(), (y + height).toDouble(), z.toDouble()).tex(0.0,1.0).endVertex()
            buffer.pos((x + width).toDouble(), (y + height).toDouble(), z.toDouble()).tex(1.0,1.0).endVertex()
            buffer.pos((x + width).toDouble(), (y + 0).toDouble(), z.toDouble()).tex(1.0,0.0).endVertex()
            buffer.pos((x + 0).toDouble(), (y + 0).toDouble(), z.toDouble()).tex(0.0,0.0).endVertex()

            tessellator.draw()
        }

        override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int, partialTicks: Float) {

            mc.textureManager.bindTexture(if (state) on else off)
            drawTexturedQuadFit(x, y, 32, 16, 100F)
            GL11.glPushMatrix()
            GL11.glColor4f(0.25f,0.25f,0.25f,1.0f)
            fontRenderer.drawString(I18n.format(textKey), x + 34, y + 4, 0x404040)
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F)
            GL11.glPopMatrix()
            mouseDragged(mc, mouseX, mouseY)
        }
    }
}