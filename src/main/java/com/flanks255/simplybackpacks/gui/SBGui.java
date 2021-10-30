package com.flanks255.simplybackpacks.gui;

import com.flanks255.simplybackpacks.items.Backpack;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
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

        this.tier = container.getTier();

        GUI = tier.texture;
        xSize = tier.xSize;
        ySize = tier.ySize;
    }

    private ResourceLocation GUI;
    private Backpack tier;

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f ,1.0f);
        this.getMinecraft().textureManager.bindTexture(GUI);
        drawTexturedQuad(guiLeft, guiTop, xSize, ySize, 0, 0, 1, 1, 0);
    }
    private void drawTexturedQuad(int x, int y, int width, int height, float tx, float ty, float tw, float th, float z) {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buffer = tess.getBuffer();

        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos((double)x + 0, (double) y + height, z).tex(tx,ty + th).endVertex();
        buffer.pos((double) x + width,(double) y + height, z).tex(tx + tw,ty + th).endVertex();
        buffer.pos((double) x + width, (double) y + 0, z).tex(tx + tw,ty).endVertex();
        buffer.pos  ((double) x + 0, (double) y + 0, z).tex(tx,ty).endVertex();

        tess.draw();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
       this.font.drawString(matrixStack, this.title.getString(), 7,6,0x404040);
    }

    @Override
    public void render(MatrixStack matrixStack, int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground(matrixStack);
        super.render(matrixStack,p_render_1_, p_render_2_, p_render_3_);
        this.renderHoveredTooltip(matrixStack, p_render_1_, p_render_2_);
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {

        return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
    }
}
