package me.earth.phobosmainmenu.gui.custom;

import me.earth.earthhack.impl.commands.gui.CommandGui;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.render.TextRenderer;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class GuiCustomMainScreen extends GuiScreen {
    public static final TextRenderer RENDERER = Managers.TEXT;
    private final ResourceLocation resourceLocation = new ResourceLocation("textures/background.png");

    private int x;
    private int y;
    private float xOffset;
    private float yOffset;

    public static void drawCompleteImage(float posX, float posY, float width, float height) {
        GL11.glPushMatrix();
        GL11.glTranslatef(posX, posY, 0.0f);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(0.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(0.0f, height, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(width, height, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(width, 0.0f, 0.0f);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    @Override
    public void initGui() {
        this.x = this.width / 2;
        this.y = this.height / 4 + 48;

        this.buttonList.add(new TextButton(0, this.x, this.y + 20, "Singleplayer"));
        this.buttonList.add(new TextButton(1, this.x, this.y + 44, "Multiplayer"));
        this.buttonList.add(new TextButton(2, this.x, this.y + 66, "Settings"));
        this.buttonList.add(new TextButton(3, this.x, this.y + 88, "Earthhack"));
        this.buttonList.add(new TextButton(4, this.x, this.y + 110, "Exit"));

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

    @Override
    public void confirmClicked(boolean result, int id) {
        mc.displayGuiScreen(new GuiCustomMainScreen());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (RenderUtil.mouseWithinBounds(this.x - RENDERER.getStringWidth("Singleplayer") / 2, this.y + 20, RENDERER.getStringWidth("Singleplayer"), RENDERER.getStringHeightI(), mouseX, mouseY)) {
            mc.displayGuiScreen(new GuiWorldSelection(this));
        } else if (RenderUtil.mouseWithinBounds(this.x - RENDERER.getStringWidth("Multiplayer") / 2, this.y + 44, RENDERER.getStringWidth("Multiplayer"), RENDERER.getStringHeightI(), mouseX, mouseY)) {
            mc.displayGuiScreen(new GuiMultiplayer(this));
        } else if (RenderUtil.mouseWithinBounds(this.x - RENDERER.getStringWidth("Settings") / 2, this.y + 66, RENDERER.getStringWidth("Settings"), RENDERER.getStringHeightI(), mouseX, mouseY)) {
            mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
        } else if (RenderUtil.mouseWithinBounds(this.x - RENDERER.getStringWidth("Earthhack") / 2, this.y + 88, RENDERER.getStringWidth("Earthhack"), RENDERER.getStringHeightI(), mouseX, mouseY)) {
            mc.displayGuiScreen(new CommandGui(this, 2500));
        } else if (RenderUtil.mouseWithinBounds(this.x - RENDERER.getStringWidth("Exit") / 2, this.y + 110, RENDERER.getStringWidth("Exit"), RENDERER.getStringHeightI(), mouseX, mouseY)) {
            mc.shutdown();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.xOffset = -1.0f * (((float) mouseX - (float) this.width / 2.0f) / ((float) this.width / 32.0f));
        this.yOffset = -1.0f * (((float) mouseY - (float) this.height / 2.0f) / ((float) this.height / 18.0f));
        this.x = this.width / 2;
        this.y = this.height / 4 + 48;
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        mc.getTextureManager().bindTexture(this.resourceLocation);
        drawCompleteImage(-16.0f + this.xOffset, -9.0f + this.yOffset, this.width + 32, this.height + 18);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private static class TextButton extends GuiButton {
        public TextButton(int buttonId, int x, int y, String buttonText) {
            super(buttonId, x, y, RENDERER.getStringWidth(buttonText), RENDERER.getStringHeightI(), buttonText);
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (this.visible) {
                this.enabled = true;
                this.hovered = (float) mouseX >= (float) this.x - (float) RENDERER.getStringWidth(this.displayString) / 2.0f && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
                RENDERER.drawStringWithShadow(this.displayString, (float) this.x - (float) RENDERER.getStringWidth(this.displayString) / 2.0f, this.y, Color.WHITE.getRGB());
                if (this.hovered) {
                    Render2DUtil.drawLine((float) (this.x - 1) - (float) RENDERER.getStringWidth(this.displayString) / 2.0f, this.y + 2 + RENDERER.getStringHeightI(), (float) this.x + (float) RENDERER.getStringWidth(this.displayString) / 2.0f + 1.0f, this.y + 2 + RENDERER.getStringHeightI(), 1.0f, Color.WHITE.getRGB());
                }
            }
        }

        @Override
        public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
            return this.enabled && this.visible && (float) mouseX >= (float) this.x - (float) RENDERER.getStringWidth(this.displayString) / 2.0f && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        }
    }
}
