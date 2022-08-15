package me.earth.earthhack.impl.gui.click.frame;

import me.earth.earthhack.impl.gui.click.component.Component;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.util.ArrayList;

public class Frame {
    private final String label;
    private float posX;
    private float posY;
    private float lastPosX;
    private float lastPosY;
    private float width;
    private final float height;
    private boolean extended,dragging;
    private final ArrayList<Component> components = new ArrayList<>();
    private int scrollY;

    public Frame(String label, float posX, float posY, float width, float height) {
        this.label = label;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
    }

    public void init() {
        components.forEach(Component::init);
    }

    public void moved(float posX,float posY) {
        components.forEach(component -> component.moved(posX,posY));
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        final ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        if (isDragging()) {
            setPosX(mouseX + getLastPosX());
            setPosY(mouseY + getLastPosY());
            getComponents().forEach(component -> component.moved(getPosX(),getPosY()+ getScrollY()));
        }
        if (getPosX() < 0) {
            setPosX(0);
            getComponents().forEach(component -> component.moved(getPosX(),getPosY()+ getScrollY()));
        }
        if (getPosX() + getWidth() > scaledResolution.getScaledWidth()) {
            setPosX(scaledResolution.getScaledWidth() - getWidth());
            getComponents().forEach(component -> component.moved(getPosX(),getPosY()+ getScrollY()));
        }
        if (getPosY() < 0) {
            setPosY(0);
            getComponents().forEach(component -> component.moved(getPosX(),getPosY()+ getScrollY()));
        }
        if (getPosY() + getHeight() > scaledResolution.getScaledHeight()) {
            setPosY(scaledResolution.getScaledHeight() - getHeight());
            getComponents().forEach(component -> component.moved(getPosX(),getPosY()+ getScrollY()));
        }
    }

    public void keyTyped(char character, int keyCode)  {
        if (isExtended()) getComponents().forEach(component -> component.keyTyped(character, keyCode));
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        final boolean hovered = RenderUtil.mouseWithinBounds(mouseX, mouseY, getPosX(),getPosY(),getWidth(),getHeight());
        switch (mouseButton) {
            case 0:
                if (hovered) {
                    setDragging(true);
                    setLastPosX(getPosX() - mouseX);
                    setLastPosY(getPosY() - mouseY);
                }
                break;
            case 1:
                if (hovered)
                    setExtended(!isExtended());
                break;
            default:
                break;
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && isDragging()) setDragging(false);
        if (isExtended()) getComponents().forEach(component -> component.mouseReleased(mouseX, mouseY, mouseButton));
    }

    public ArrayList<Component> getComponents() {
        return components;
    }

    public String getLabel() {
        return label;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public float getLastPosX() {
        return lastPosX;
    }

    public void setLastPosX(float lastPosX) {
        this.lastPosX = lastPosX;
    }

    public float getLastPosY() {
        return lastPosY;
    }

    public void setLastPosY(float lastPosY) {
        this.lastPosY = lastPosY;
    }

    public boolean isExtended() {
        return extended;
    }

    public void setExtended(boolean extended) {
        this.extended = extended;
    }

    public boolean isDragging() {
        return dragging;
    }

    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    public int getScrollY() {
        return scrollY;
    }

    public void setScrollY(int scrollY) {
        this.scrollY = scrollY;
    }

    public void setWidth(float width) {
        this.width = width;
    }
}
