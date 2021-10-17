package com.flanks255.simplybackpacks.gui;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SBGui extends AbstractContainerScreen<SBContainer> {
    public SBGui(SBContainer container, Inventory playerInventory, Component name) {
        super(container, playerInventory, name);


        switch(container.slotcount) {
            case 18:
                GUI = new ResourceLocation(SimplyBackpacks.MODID, "textures/gui/common_gui.png");
                imageWidth = 176;
                imageHeight = 150;
                break;
            case 33:
                GUI = new ResourceLocation(SimplyBackpacks.MODID, "textures/gui/uncommon_gui.png");
                imageWidth = 212;
                imageHeight = 168;
                break;
            case 66:
                GUI = new ResourceLocation(SimplyBackpacks.MODID, "textures/gui/rare_gui.png");
                imageWidth = 212;
                imageHeight = 222;
                break;
            default:
                GUI = new ResourceLocation(SimplyBackpacks.MODID, "textures/gui/epic_gui.png");
                imageWidth = 212;
                imageHeight = 276;
                break;
        }
    }

    private ResourceLocation GUI;

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, GUI);
        blit(matrixStack, leftPos, topPos, 0,0, imageWidth, imageHeight, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int x, int y) {
       this.font.draw(matrixStack, this.title.getString(), 7,6,0x404040);
    }

    @Override
    public void render(PoseStack matrixStack, int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground(matrixStack);
        super.render(matrixStack,p_render_1_, p_render_2_, p_render_3_);
        this.renderTooltip(matrixStack, p_render_1_, p_render_2_);
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {

        return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
    }
}
