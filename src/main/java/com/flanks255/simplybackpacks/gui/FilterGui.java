package com.flanks255.simplybackpacks.gui;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class FilterGui extends ContainerScreen<FilterContainer> {
    public FilterGui(FilterContainer container, PlayerInventory playerInventory, ITextComponent name) {
        super(container, playerInventory, name);

        xSize = 176;
        ySize = 166;
    }

    @Override
    protected void init() {
        super.init();

        Button.IPressable slotClick = new Button.IPressable() {
            @Override
            public void onPress(Button button) {
                Minecraft.getInstance().playerController.sendEnchantPacket(container.windowId, ((slotButton)button).slot);
                container.enchantItem(playerInventory.player, ((slotButton)button).slot);
            }
        };

        int slot = 0;
        for (int row = 0; row < 4; row ++) {
            for (int col = 0; col < 4; col++) {
                int x = guiLeft + 7 + col * 18;
                int y = guiTop + 7 + row * 18;

                addButton(new slotButton(x+1, y+1,18 ,18, slot, slotClick));
                slot++;
            }
        }

        addButton(new switchButton(guiLeft + 80, guiTop + 8, "simplybackpacks.whitelist", ((container.getFilterOpts() & 1) > 0) , (button)-> ((switchButton)button).state = (container.setFilterOpts(container.getFilterOpts() ^ 1) & 1) > 0));
        addButton(new switchButton(guiLeft + 80, guiTop + 8 + 18, "simplybackpacks.nbtdata", ((container.getFilterOpts() & 2) > 0) , (button)-> ((switchButton)button).state = (container.setFilterOpts(container.getFilterOpts() ^ 2) & 2) > 0));
        addButton(new switchButton(guiLeft + 80, guiTop + 8 + 54, "simplybackpacks.autopickup", container.getPickup() , (button)-> ((switchButton)button).state = container.togglePickup()));

    }

    private ResourceLocation GUI = new ResourceLocation(SimplyBackpacks.MODID, "textures/gui/filter_gui.png");;

    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground();
        super.render(p_render_1_, p_render_2_, p_render_3_);
        this.renderHoveredToolTip(p_render_1_, p_render_2_);
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
    protected void renderHoveredToolTip(int x, int y) {
        super.renderHoveredToolTip(x, y);

        for(Widget button : buttons) {
            if (button.isMouseOver(x,y) && button instanceof slotButton)
                if (!container.itemHandler.filter.getStackInSlot(((slotButton)button).slot).isEmpty())
                    renderTooltip(container.itemHandler.filter.getStackInSlot(((slotButton)button).slot), x, y);
        }
    }

    class slotButton extends Button {
        public slotButton(int x, int y, int width, int height, int slotIn, IPressable pressable) {
            super(x,y,width,height,"", pressable);

            this.slot = slotIn;
        }
        public int slot;

        @Override
        public void renderButton(int mouseX, int mouseY, float partialTicks) {
            GlStateManager.pushMatrix();
            GlStateManager.color3f(1.0f,1.0f,1.0f);
            FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;

            boolean hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;

            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            if (container.itemHandler.filter != null && !container.itemHandler.filter.getStackInSlot(slot).isEmpty()) {
                ItemStack tmp = container.itemHandler.filter.getStackInSlot(slot);
                    itemRenderer.zLevel = 100F;
                    GlStateManager.enableDepthTest();
                    RenderHelper.enableGUIStandardItemLighting();
                    itemRenderer.renderItemAndEffectIntoGUI(tmp, x, y);
                    itemRenderer.renderItemOverlayIntoGUI(fontRenderer, tmp, x, y, "");
                    itemRenderer.zLevel = 0F;
            }

            if (hovered)
                fill(x,y,x+width, y+height, -2130706433);

            GlStateManager.popMatrix();
        }
    }


    class switchButton extends Button {
        public switchButton(int x, int y, String text,boolean initial, IPressable pressable) {
            super(x,y,32,16,"", pressable);
            textKey = text;
            state = initial;
        }

        private ResourceLocation off = new ResourceLocation(SimplyBackpacks.MODID, "textures/gui/switch_off.png");
        private ResourceLocation on = new ResourceLocation(SimplyBackpacks.MODID, "textures/gui/switch_on.png");
        public boolean state = false;
        private String textKey;
        private FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;

        @Override
        public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
            minecraft.getTextureManager().bindTexture(state?on:off);
            drawTexturedQuad(x,y,width,height,0,0,1,1, 100F);
            fontRenderer.drawString(I18n.format(textKey), x + 34, y + 4, 0x404040);
        }
    }
}
