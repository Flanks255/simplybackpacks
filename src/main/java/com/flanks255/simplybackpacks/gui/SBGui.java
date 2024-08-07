package com.flanks255.simplybackpacks.gui;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.items.Backpack;
import com.flanks255.simplybackpacks.util.BackpackUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
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
                this.GUI = ResourceLocation.fromNamespaceAndPath(SimplyBackpacks.MODID, "textures/gui/ultimate_alt.png");
            else
                this.GUI = ResourceLocation.fromNamespaceAndPath(SimplyBackpacks.MODID, "textures/gui/ultimate_alt2.png");
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
    protected void renderBg(@Nonnull GuiGraphics gg, float partialTicks, int x, int y) {
        gg.blit(GUI, this.leftPos, this.topPos, 0,0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(@Nonnull GuiGraphics gg, int x, int y) {
        gg.drawString(font, this.title.getString(), 7,6,0x404040, false);
    }

    @Override
    public void render(@Nonnull GuiGraphics gg, int pMouseX, int pMouseY, float pPartialTicks) {
        super.render(gg,pMouseX, pMouseY, pPartialTicks);
        this.renderTooltip(gg, pMouseX, pMouseY);
    }
}
