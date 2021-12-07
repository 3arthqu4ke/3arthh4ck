package me.earth.earthhack.impl.gui.hud;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.clickgui.ClickGui;
import me.earth.earthhack.impl.util.misc.GuiUtil;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class HudPanel extends AbstractGuiElement {

    private static final ModuleCache<ClickGui> CLICK_GUI = Caches.getModule(ClickGui.class);

    private boolean dragging;
    private boolean hovered;
    private boolean stretching;
    private GuiUtil.Edge currentEdge;
    private float draggingX;
    private float draggingY;
    private float stretchingWidth;
    private float stretchingHeight;
    private float stretchingX;
    private float stretchingY;
    private float stretchingX2;
    private float stretchingY2;
    private float scrollOffset;
    private float elementOffset = 20;

    private final Set<HudElementButton> elementButtons;

    public HudPanel() {
        super("HudPanel", 200, 200, 100, 300);
        elementButtons = new HashSet<>();
        for (HudElement element : Managers.ELEMENTS.getRegistered()) {
            elementButtons.add(new HudElementButton(element));
        }
    }

    public void draw(int mouseX, int mouseY, float partialTicks) {
        hovered = GuiUtil.isHovered(this, mouseX, mouseY);
        if (dragging) {
            setX(mouseX - draggingX);
            setY(mouseY - draggingY);
        }
        if (stretching && currentEdge != null) {
            switch (currentEdge) {
                /*case TOP:
                    setY(stretchingY + (mouseY - stretchingY));
                    setHeight(stretchingY2 - getY());
                    break;*/
                case BOTTOM:
                    setHeight(stretchingHeight + (mouseY - stretchingY));
                    break;
                case LEFT:
                    setX(stretchingX + (mouseX - stretchingX));
                    setWidth(stretchingX2 - getX());
                    break;
                case RIGHT:
                    setWidth(stretchingWidth + (mouseX - stretchingX));
                    break;
                /*case TOP_LEFT:
                    setX(stretchingX + (mouseX - stretchingX));
                    setY(stretchingY + (mouseY - stretchingY));
                    setHeight(stretchingY2 - getY());
                    setWidth(stretchingX2 - getX());
                    break;
                case TOP_RIGHT:
                    setY(stretchingY + (mouseY - stretchingY));
                    setWidth(stretchingWidth + (mouseX - stretchingX));
                    setHeight(stretchingY2 - getY());
                    break;*/
                case BOTTOM_LEFT:
                    setHeight(stretchingHeight + (mouseY - stretchingY));
                    setX(stretchingX + (mouseX - stretchingX));
                    setWidth(stretchingX2 - getX());
                    break;
                case BOTTOM_RIGHT:
                    setHeight(stretchingHeight + (mouseY - stretchingY));
                    setWidth(stretchingWidth + (mouseX - stretchingX));
                    break;
            }
        }
        if (getX() <= 0) {
            setX(0);
        }
        if (getWidth() <= 100) {
            setWidth(100);
        }
        if (getHeight() <= 200) {
            setHeight(200);
        }
        if (getY() <= 0) {
            setY(0);
        }

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Render2DUtil.drawRect(getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0x92000000);
        Render2DUtil.drawRect(getX(), getY(), getX() + getWidth(), getY() + Managers.TEXT.getStringHeight(1.0f) + 10, CLICK_GUI.get().color.getValue().getRGB());
        Managers.TEXT.drawStringScaled("Hud Elements", getX() + (getWidth() / 2) - (Managers.TEXT.getStringWidthScaled("Hud Elements", 1.0f) / 2.0f), getY() + 5, Color.WHITE.getRGB(), true, 1.0f);

        float yOffset = 0;
        // GL11.glPushMatrix();
        RenderUtil.scissor(getX(), getY(),getX()+ getWidth(),getY()+ getHeight());
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        for (HudElementButton button : elementButtons) {
            button.setX(getX());
            button.setWidth(getWidth());
            button.setY(getY() + Managers.TEXT.getStringHeight() + 12 + yOffset);
            button.draw(mouseX, mouseY, partialTicks);
            yOffset += button.getHeight() + 1;
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        currentEdge = GuiUtil.getHoveredEdge(this, mouseX, mouseY, 5);
        if (GuiUtil.isHovered(this, mouseX, mouseY)) {
            if (currentEdge != null) {
                stretching = true;
                dragging = false;
                stretchingWidth = getWidth();
                stretchingHeight = getHeight();
                stretchingX = mouseX;
                stretchingY = mouseY;
                stretchingX2 = getX() + getWidth();
                stretchingY2 = getY() + getHeight();
            } else if (GuiUtil.isHovered(getX(), getY(), getWidth(), 20, mouseX, mouseY)) {
                dragging = true;
                stretching = false;
                draggingX = (mouseX - getX());
                draggingY = (mouseY - getY());
            }
        }

        for (HudElementButton button : elementButtons) {
            button.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        dragging = false;
        stretching = false;
        currentEdge = null;

        for (HudElementButton button : elementButtons) {
            button.mouseReleased(mouseX, mouseY, mouseButton);
        }
    }

    public void keyPressed(char eventChar, int key) {
        for (HudElementButton button : elementButtons) {
            button.keyPressed(eventChar, key);
        }
    }

    public void mouseScrolled() {

    }

    public Set<HudElementButton> getElementButtons() {
        return elementButtons;
    }

}
