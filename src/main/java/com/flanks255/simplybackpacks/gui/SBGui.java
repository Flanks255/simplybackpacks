package com.flanks255.simplybackpacks.gui;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.items.Backpack;
import com.flanks255.simplybackpacks.util.BackpackUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;

public class SBGui extends AbstractContainerScreen<SBContainer> {
    public SBGui(SBContainer container, Inventory playerInventory, Component name) {
        super(container, playerInventory, name);

        Backpack tier = container.getTier();

        //1 in 100, vs 1 in 10k
        double chance = BackpackUtils.increasedAltChance(playerInventory.player.getUUID())? 0.01:0.0001;

        if (tier == Backpack.ULTIMATE && Math.random() < chance) {
            playerInventory.player.playSound(SoundEvents.COW_HURT, 0.5f, 1f);
            if (Math.random() < 0.5)
                this.GUI = new ResourceLocation(SimplyBackpacks.MODID, "textures/gui/ultimate_alt.png");
            else
                this.GUI = new ResourceLocation(SimplyBackpacks.MODID, "textures/gui/ultimate_alt2.png");
        }
        else
            this.GUI = tier.texture;
        this.imageWidth = tier.xSize;
        this.imageHeight = tier.ySize;


    }

    private final ResourceLocation GUI;

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void renderBg(@Nonnull PoseStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.GUI);
        blit(matrixStack, this.leftPos, this.topPos, 0,0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(@Nonnull PoseStack matrixStack, int x, int y) {
        this.font.draw(matrixStack, this.title.getString(), 7,6,0x404040);
    }

    @Override
    public void render(@Nonnull PoseStack matrixStack, int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground(matrixStack);
        super.render(matrixStack,p_render_1_, p_render_2_, p_render_3_);
        this.renderTooltip(matrixStack, p_render_1_, p_render_2_);
    }
}
