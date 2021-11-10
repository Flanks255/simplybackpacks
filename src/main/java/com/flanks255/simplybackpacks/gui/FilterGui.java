package com.flanks255.simplybackpacks.gui;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;

public class FilterGui extends ContainerScreen<FilterContainer> {
    public FilterGui(FilterContainer container, PlayerInventory playerInventory, ITextComponent name) {
        super(container, playerInventory, name);

        imageWidth = 176;
        imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();

        Button.IPressable slotClick = button -> {
            Minecraft.getInstance().gameMode.handleInventoryButtonClick(menu.containerId, ((SlotButton)button).slot);
            menu.clickMenuButton(inventory.player, ((SlotButton)button).slot);
        };

        int slot = 0;
        for (int row = 0; row < 4; row ++) {
            for (int col = 0; col < 4; col++) {
                int x = leftPos + 7 + col * 18;
                int y = topPos + 7 + row * 18;

                addButton(new SlotButton(x+1, y+1,18 ,18, slot, slotClick));
                slot++;
            }
        }

        addButton(new SwitchButton(leftPos + 80, topPos + 8, "simplybackpacks.whitelist", ((menu.getFilterOpts() & 1) > 0) , (button)-> ((SwitchButton)button).state = (menu.setFilterOpts(menu.getFilterOpts() ^ 1) & 1) > 0));
        addButton(new SwitchButton(leftPos + 80, topPos + 8 + 18, "simplybackpacks.nbtdata", ((menu.getFilterOpts() & 2) > 0) , (button)-> ((SwitchButton)button).state = (menu.setFilterOpts(menu.getFilterOpts() ^ 2) & 2) > 0));
        addButton(new SwitchButton(leftPos + 80, topPos + 8 + 54, "simplybackpacks.autopickup", menu.getPickup() , (button)-> ((SwitchButton)button).state = menu.togglePickup()));

    }

    private final ResourceLocation GUI = new ResourceLocation(SimplyBackpacks.MODID, "textures/gui/filter_gui.png");

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground(matrixStack);
        super.render(matrixStack,p_render_1_, p_render_2_, p_render_3_);
        this.renderTooltip(matrixStack, p_render_1_, p_render_2_);
    }

    @Override
    protected void renderBg(@Nonnull MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f ,1.0f);
        this.getMinecraft().textureManager.bind(GUI);


        blit(matrixStack, leftPos, topPos, 0,0, imageWidth, imageHeight, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(@Nonnull MatrixStack matrixStack, int x, int y) {
        // nope...
    }

    @Override
    protected void renderTooltip(@Nonnull MatrixStack matrixStack, int x, int y) {
        super.renderTooltip(matrixStack, x, y);

        for(Widget button : buttons) {
            if (button.isMouseOver(x,y) && button instanceof SlotButton)
                if (!menu.filterHandler.getStackInSlot(((SlotButton)button).slot).isEmpty())
                    renderTooltip(matrixStack, menu.filterHandler.getStackInSlot(((SlotButton)button).slot), x, y);
        }
    }

    class SlotButton extends Button {
        public SlotButton(int x, int y, int width, int height, int slotIn, IPressable pressable) {
            super(x,y,width,height,new StringTextComponent(""), pressable);

            this.slot = slotIn;
        }
        public final int slot;

        @Override
        public void renderButton(@Nonnull MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
            FontRenderer fontRenderer = Minecraft.getInstance().font;

            boolean hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;

            if (menu.filterHandler != null && !menu.filterHandler.getStackInSlot(slot).isEmpty()) {
                ItemStack tmp = menu.filterHandler.getStackInSlot(slot);
                itemRenderer.blitOffset = 100F;
                RenderSystem.enableDepthTest();
                RenderHelper.turnBackOn();
                itemRenderer.renderAndDecorateItem(tmp, x, y);
                itemRenderer.renderGuiItemDecorations(fontRenderer, tmp, x, y, "");
                itemRenderer.blitOffset = 0F;
                RenderHelper.turnOff();
                RenderSystem.disableDepthTest();
            }

            if (hovered)
                fill(stack, x,y,x + width - 1, y + height - 1, -2130706433);
        }
    }


    class SwitchButton extends Button {
        public SwitchButton(int x, int y, String text, boolean initial, IPressable pressable) {
            super(x,y,32,16,new StringTextComponent(""), pressable);
            textKey = text;
            state = initial;
        }

        private final ResourceLocation off = new ResourceLocation(SimplyBackpacks.MODID, "textures/gui/switch_off.png");
        private final ResourceLocation on = new ResourceLocation(SimplyBackpacks.MODID, "textures/gui/switch_on.png");
        public boolean state;
        private final String textKey;
        private final FontRenderer fontRenderer = Minecraft.getInstance().font;

        @Override
        public void renderButton(@Nonnull MatrixStack stack, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            getMinecraft().getTextureManager().bind(state?on:off);
            blit(stack, x,y,width,height,0,0,32,16, 32 ,16);
            fontRenderer.draw(stack, I18n.get(textKey), x + 34, y + 4, 0x404040);
            RenderSystem.disableBlend();
        }
    }
}
