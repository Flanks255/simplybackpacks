package com.flanks255.simplybackpacks.gui;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class SBGui extends ContainerScreen<SBContainer> {
    public SBGui(SBContainer container, PlayerInventory playerInventory, ITextComponent name) {
        super(container, playerInventory, name);


        switch(container.slotcount) {
            case 18:
                GUI = new ResourceLocation(SimplyBackpacks.MODID, "textures/gui/common_gui.png");
                xSize = 176;
                ySize = 140;
                break;
            case 33:
                GUI = new ResourceLocation(SimplyBackpacks.MODID, "textures/gui/uncommon_gui.png");
                xSize = 212;
                ySize = 158;
                break;
            case 66:
                GUI = new ResourceLocation(SimplyBackpacks.MODID, "textures/gui/rare_gui.png");
                xSize = 212;
                ySize = 212;
                break;
            default:
                GUI = new ResourceLocation(SimplyBackpacks.MODID, "textures/gui/epic_gui.png");
                xSize = 212;
                ySize = 266;
                break;
        }
    }

    private ResourceLocation GUI;

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f ,1.0f);
        this.getMinecraft().textureManager.bindTexture(GUI);
        drawTexturedQuad(guiLeft, guiTop, xSize, ySize, 0D, 0D, 1D, 1D, 0);
    }
    private void drawTexturedQuad(int x, int y, int width, int height, double tx, double ty, double tw, double th, float z) {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buffer = tess.getBuffer();

        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos((double)x + 0, (double) y + height, (double) z).tex(tx,ty + th).endVertex();
        buffer.pos((double) x + width,(double) y + height, (double) z).tex(tx + tw,ty + th).endVertex();
        buffer.pos((double) x + width, (double) y + 0, (double) z).tex(tx + tw,ty).endVertex();
        buffer.pos((double) x + 0, (double) y + 0, (double) z).tex(tx,ty).endVertex();

        tess.draw();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        GlStateManager.pushMatrix();
        GlStateManager.color3f(0.25f, 0.25f, 0.25f);
        Minecraft.getInstance().fontRenderer.drawString(this.title.getString(), 7,6,0x404040);
        GlStateManager.color3f(1f, 1f, 1f);
        GlStateManager.popMatrix();
    }

    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground();
        super.render(p_render_1_, p_render_2_, p_render_3_);
        this.renderHoveredToolTip(p_render_1_, p_render_2_);
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {

        return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
    }
}
