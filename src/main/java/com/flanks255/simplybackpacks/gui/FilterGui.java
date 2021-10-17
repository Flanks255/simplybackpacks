package com.flanks255.simplybackpacks.gui;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class FilterGui extends AbstractContainerScreen<FilterContainer> {
    private Inventory inventory;
    public FilterGui(FilterContainer container, Inventory playerInventory, Component name) {
        super(container, playerInventory, name);
        inventory = playerInventory;
        imageWidth = 176;
        imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();

        Button.OnPress slotClick = new Button.OnPress() {
            @Override
            public void onPress(Button button) {
                Minecraft.getInstance().gameMode.handleInventoryButtonClick(menu.containerId, ((SlotButton)button).slot);
                menu.clickMenuButton(inventory.player, ((SlotButton)button).slot);
            }
        };

        int slot = 0;
        for (int row = 0; row < 4; row ++) {
            for (int col = 0; col < 4; col++) {
                int x = leftPos + 7 + col * 18;
                int y = topPos + 7 + row * 18;

                addRenderableWidget(new SlotButton(x+1, y+1,18 ,18, slot, slotClick));
                slot++;
            }
        }

        addRenderableWidget(new SwitchButton(leftPos + 80, topPos + 8, "simplybackpacks.whitelist", ((menu.getFilterOpts() & 1) > 0) , (button)-> ((SwitchButton)button).state = (menu.setFilterOpts(menu.getFilterOpts() ^ 1) & 1) > 0));
        addRenderableWidget(new SwitchButton(leftPos + 80, topPos + 8 + 18, "simplybackpacks.nbtdata", ((menu.getFilterOpts() & 2) > 0) , (button)-> ((SwitchButton)button).state = (menu.setFilterOpts(menu.getFilterOpts() ^ 2) & 2) > 0));
        addRenderableWidget(new SwitchButton(leftPos + 80, topPos + 8 + 54, "simplybackpacks.autopickup", menu.getPickup() , (button)-> ((SwitchButton)button).state = menu.togglePickup()));

    }

    private ResourceLocation GUI = new ResourceLocation(SimplyBackpacks.MODID, "textures/gui/filter_gui.png");

    @Override
    public void render(PoseStack matrixStack, int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground(matrixStack);
        super.render(matrixStack,p_render_1_, p_render_2_, p_render_3_);
        this.renderTooltip(matrixStack, p_render_1_, p_render_2_);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, GUI);
        blit(matrixStack, leftPos, topPos, 0,0, 176,166, 176, 166);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int x, int y) {
        // nope...
    }

    @Override
    protected void renderTooltip(PoseStack matrixStack, int x, int y) {
        super.renderTooltip(matrixStack, x, y);

        children().forEach((child) -> {
            if (child.isMouseOver(x,y) && child instanceof SlotButton button)
                if (!menu.itemHandler.filter.getStackInSlot(button.slot).isEmpty())
                    renderTooltip(matrixStack, menu.itemHandler.filter.getStackInSlot(button.slot), x, y);
        });
    }

    class SlotButton extends Button {
        public SlotButton(int x, int y, int width, int height, int slotIn, OnPress pressable) {
            super(x,y,width,height,new TextComponent(""), pressable);

            this.slot = slotIn;
        }
        public int slot;

        @Override
        public void renderButton(PoseStack pMatrixStack, int mouseX, int mouseY, float partialTicks) {
            pMatrixStack.pushPose();
            RenderSystem.setShaderColor(1.0f,1.0f,1.0f,1.0f);
            Font fontRenderer = Minecraft.getInstance().font;

            boolean hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;

            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            if (menu.itemHandler.filter != null && menu.itemHandler.filter.getStackInSlot(slot) != null && !menu.itemHandler.filter.getStackInSlot(slot).isEmpty()) {
                ItemStack tmp = menu.itemHandler.filter.getStackInSlot(slot);
                    itemRenderer.blitOffset = 100F;
                    //RenderHelper.enableGUIStandardItemLighting();
                    RenderSystem.enableDepthTest();
                    //Lighting.turnBackOn();
                    itemRenderer.renderAndDecorateItem(tmp, x, y);
                    itemRenderer.renderGuiItemDecorations(fontRenderer, tmp, x, y, "");
                    itemRenderer.blitOffset = 0F;
            }

            if (hovered)
                fill(pMatrixStack, x,y,x+width, y+height, -2130706433);

            pMatrixStack.popPose();
        }
    }


    class SwitchButton extends Button {
        public SwitchButton(int x, int y, String text, boolean initial, OnPress pressable) {
            super(x,y,32,16,new TextComponent(""), pressable);
            textKey = text;
            state = initial;
        }

        private ResourceLocation off = new ResourceLocation(SimplyBackpacks.MODID, "textures/gui/switch_off.png");
        private ResourceLocation on = new ResourceLocation(SimplyBackpacks.MODID, "textures/gui/switch_on.png");
        public boolean state = false;
        private String textKey;
        private Font fontRenderer = Minecraft.getInstance().font;

        @Override
        public void renderButton(PoseStack stack, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
            RenderSystem.setShaderTexture(0, state?on:off);
            blit(stack, x,y,width,height,0,0,32,16, 32 ,16);
            fontRenderer.draw(stack, I18n.get(textKey), x + 34, y + 4, 0x404040);
        }
    }
}
