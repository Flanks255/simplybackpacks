package com.flanks255.simplybackpacks.gui;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.capability.BackpackFilterHandler;
import com.flanks255.simplybackpacks.capability.BackpackItemHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.items.CapabilityItemHandler;

public class FilterGui extends ContainerScreen<FilterContainer> {
    public FilterGui(FilterContainer container, PlayerInventory playerInventory, ITextComponent name) {
        super(container, playerInventory, name);

        xSize = 176;
        ySize = 166;
    }

    @Override
    protected void init() {
        super.init();

        int slot = 0;
        for (int row = 0; row < 4; row ++) {
            for (int col = 0; col < 4; col++) {
                int x = guiLeft + 7 + col * 18;
                int y = guiTop + 7 + row * 18;

                addButton(new SlotButton(x+1, y+1,18 ,18, slot, button -> {
                    Minecraft.getInstance().playerController.sendEnchantPacket(container.windowId, ((SlotButton)button).slot);
                    container.enchantItem(playerInventory.player, ((SlotButton)button).slot);
                }));
                slot++;
            }
        }

        addButton(new SwitchButton(guiLeft + 80, guiTop + 8, "simplybackpacks.whitelist", ((container.getFilterOpts() & 1) > 0) , (button)-> ((SwitchButton)button).state = (container.setFilterOpts(container.getFilterOpts() ^ 1) & 1) > 0));
        addButton(new SwitchButton(guiLeft + 80, guiTop + 8 + 18, "simplybackpacks.nbtdata", ((container.getFilterOpts() & 2) > 0) , (button)-> ((SwitchButton)button).state = (container.setFilterOpts(container.getFilterOpts() ^ 2) & 2) > 0));
        addButton(new SwitchButton(guiLeft + 80, guiTop + 8 + 54, "simplybackpacks.autopickup", container.getPickup() , (button)-> ((SwitchButton)button).state = container.togglePickup()));

    }

    private final ResourceLocation GUI = new ResourceLocation(SimplyBackpacks.MODID, "textures/gui/filter_gui.png");

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.drawMouseoverTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f ,1.0f);

        this.getMinecraft().textureManager.bindTexture(GUI);
        drawTexturedQuad(guiLeft, guiTop, xSize, ySize, 0, 0, 1, 1, 0);
    }

    private void drawTexturedQuad(int x, int y, int width, int height, float tx, float ty, float tw, float th, float z) {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buffer = tess.getBuffer();

        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.vertex((double)x + 0, (double) y + height, z).texture(tx,ty + th).endVertex();
        buffer.vertex((double) x + width,(double) y + height, z).texture(tx + tw,ty + th).endVertex();
        buffer.vertex((double) x + width, (double) y + 0, z).texture(tx + tw,ty).endVertex();
        buffer.vertex((double) x + 0, (double) y + 0, z).texture(tx,ty).endVertex();

        tess.draw();
    }

    @Override
    protected void drawForeground(MatrixStack matrixStack, int p_230451_2_, int p_230451_3_) {
        //dont draw nothin...
    }

    @Override
    protected void drawMouseoverTooltip(MatrixStack matrixStack, int x, int y) {
        super.drawMouseoverTooltip(matrixStack, x, y);

        for(Widget button : buttons) {
            if (button.isMouseOver(x,y) && button instanceof SlotButton) {
                container.item.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(cap -> {
                    if (!((BackpackItemHandler) cap).getFilterHandler().getStackInSlot(((SlotButton)button).slot).isEmpty())
                        renderTooltip(matrixStack, ((BackpackItemHandler) cap).getFilterHandler().getStackInSlot(((SlotButton)button).slot), x, y);
                });
            }
        }
    }

    class SlotButton extends Button {
        public SlotButton(int x, int y, int width, int height, int slotIn, IPressable pressable) {
            super(x,y,width,height,new StringTextComponent(""), pressable);

            this.slot = slotIn;
        }

        public int slot;

        @Override
        public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
            RenderSystem.pushMatrix();
            RenderSystem.color4f(1.0f,1.0f,1.0f,1.0f);
            FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;

            boolean hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;

            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            container.item.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(cap -> {
                BackpackFilterHandler filterHandler = ((BackpackItemHandler) cap).getFilterHandler();

                ItemStack tmp = filterHandler.getStackInSlot(slot);
                itemRenderer.zLevel = 100F;
                //RenderHelper.enableGUIStandardItemLighting();
                RenderSystem.enableDepthTest();
                RenderHelper.enableGuiDepthLighting();
                itemRenderer.renderItemAndEffectIntoGUI(tmp, x, y);
                itemRenderer.renderItemOverlayIntoGUI(fontRenderer, tmp, x, y, "");
                itemRenderer.zLevel = 0F;
            });

            if (hovered)
                fill(stack, x,y,x+width, y+height, -2130706433);

            RenderSystem.popMatrix();
        }
    }


    class SwitchButton extends Button {
        public SwitchButton(int x, int y, String text, boolean initial, IPressable pressable) {
            super(x,y,32,16,new StringTextComponent(""), pressable);
            textKey = text;
            state = initial;
        }

        private final ResourceLocation OFF = new ResourceLocation(SimplyBackpacks.MODID, "textures/gui/switch_off.png");
        private final ResourceLocation ON = new ResourceLocation(SimplyBackpacks.MODID, "textures/gui/switch_on.png");

        public boolean state;
        private final String textKey;

        @Override
        public void renderButton(MatrixStack stack, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
            getMinecraft().getTextureManager().bindTexture(state ? ON : OFF);
            drawTexturedQuad(x, y, width, height,0,0,1,1, 100);
            Minecraft.getInstance().fontRenderer.draw(stack, I18n.format(textKey), x + 34, y + 4, 0x404040);
        }
    }
}
