package com.flanks255.simplybackpacks.gui;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class FilterGui extends AbstractContainerScreen<FilterContainer> {
    public FilterGui(FilterContainer container, Inventory playerInventory, Component name) {
        super(container, playerInventory, name);

        inventory = playerInventory;
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    private final Inventory inventory;

    @Override
    protected void init() {
        super.init();

        Button.OnPress slotClick = button -> {
            Minecraft.getInstance().gameMode.handleInventoryButtonClick(menu.containerId, ((SlotButton)button).slot);
            menu.clickMenuButton(inventory.player, ((SlotButton)button).slot);
        };

        int slot = 0;
        for (int row = 0; row < 4; row ++) {
            for (int col = 0; col < 4; col++) {
                int x = this.leftPos + 7 + col * 18;
                int y = this.topPos + 7 + row * 18;

                addRenderableWidget(new SlotButton(x+1, y+1,18 ,18, slot, slotClick));
                slot++;
            }
        }

        addRenderableWidget(new SwitchButton(this.leftPos + 80, this.topPos + 8, "simplybackpacks.whitelist", ((this.menu.getFilterOpts() & 1) > 0) , (button)-> ((SwitchButton)button).state = (this.menu.saveFilterClient(this.menu.getFilterOpts() ^ 1) & 1) > 0));
        addRenderableWidget(new SwitchButton(this.leftPos + 80, this.topPos + 8 + 18, "simplybackpacks.nbtdata", ((this.menu.getFilterOpts() & 2) > 0) , (button)-> ((SwitchButton)button).state = (this.menu.saveFilterClient(this.menu.getFilterOpts() ^ 2) & 2) > 0));
        addRenderableWidget(new SwitchButton(this.leftPos + 80, this.topPos + 8 + 54, "simplybackpacks.autopickup", this.menu.getPickup() , (button)-> ((SwitchButton)button).state = this.menu.togglePickup()));

    }

    private final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(SimplyBackpacks.MODID, "textures/gui/filter_gui.png");

    @Override
    public void render(@Nonnull GuiGraphics gg, int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground(gg, p_render_1_, p_render_2_, p_render_3_);
        super.render(gg,p_render_1_, p_render_2_, p_render_3_);
        this.renderTooltip(gg, p_render_1_, p_render_2_);
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics gui, float partialTicks, int x, int y) {
        gui.blit(GUI, this.leftPos, this.topPos, 0,0, 176,166, 176, 166);
    }

    @Override
    protected void renderLabels(@Nonnull GuiGraphics gg, int x, int y) {
        // nope...
    }

    @Override
    protected void renderTooltip(@Nonnull GuiGraphics gg, int x, int y) {
        super.renderTooltip(gg, x, y);

        children().forEach((child) -> {
            if (child.isMouseOver(x,y) && child instanceof SlotButton button)
                if (!this.menu.filterHandler.getStackInSlot(button.slot).isEmpty())
                    gg.renderTooltip(font, this.menu.filterHandler.getStackInSlot(button.slot), x, y);
        });
    }

    class SlotButton extends Button {
        public SlotButton(int x, int y, int width, int height, int slotIn, OnPress pressable) {
            super(x,y,width,height,Component.empty(), pressable, Button.DEFAULT_NARRATION);

            this.slot = slotIn;
        }
        public final int slot;

        @Override
        public void renderWidget(@Nonnull GuiGraphics gg, int mouseX, int mouseY, float partialTicks) {
            boolean hovered = mouseX >= this.getX() && mouseX < this.getX() + this.width && mouseY >= this.getY() && mouseY < this.getY() + this.height;

            if (menu.filterHandler != null && !menu.filterHandler.getStackInSlot(this.slot).isEmpty()) {
                ItemStack tmp = menu.filterHandler.getStackInSlot(this.slot);
                RenderSystem.enableDepthTest();
                Lighting.setupForFlatItems();

                gg.renderItem(tmp, this.getX(), this.getY());

                Lighting.setupFor3DItems();
                RenderSystem.disableDepthTest();
            }

            if (hovered)
                gg.fill(this.getX(), this.getY(), this.getX() + this.width-1, this.getY() + this.height-1, -2130706433);
        }
    }


    class SwitchButton extends Button {
        public SwitchButton(int x, int y, String text, boolean initial, OnPress pressable) {
            super(x,y,32,16, Component.empty(), pressable, Button.DEFAULT_NARRATION);
            this.textKey = text;
            this.state = initial;
        }

        private static final ResourceLocation off = ResourceLocation.fromNamespaceAndPath(SimplyBackpacks.MODID, "textures/gui/switch_off.png");
        private static final ResourceLocation on = ResourceLocation.fromNamespaceAndPath(SimplyBackpacks.MODID, "textures/gui/switch_on.png");
        public boolean state;
        private final String textKey;

        @Override
        public void renderWidget(@Nonnull GuiGraphics gg, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
            gg.blit(this.state ? on:off, this.getX(), this.getY(), this.width, this.height,0,0,32,16, 32 ,16);
            gg.drawString(font, I18n.get(this.textKey), this.getX() + 34, this.getY() + 4, 0x404040, false);
        }
    }
}
