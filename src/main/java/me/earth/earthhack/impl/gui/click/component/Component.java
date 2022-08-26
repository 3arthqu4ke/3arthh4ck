package me.earth.earthhack.impl.gui.click.component;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.gui.click.Click;
import me.earth.earthhack.impl.modules.client.clickgui.ClickGui;
import me.earth.earthhack.impl.util.render.RenderUtil;

import java.util.function.Supplier;

public class Component {
    private final String label;
    private float posX;
    private float posY;
    private float finishedX;
    private float finishedY;
    private float offsetX;
    private float offsetY;
    private float lastPosX;
    private float lastPosY;
    private float width;
    private float height;
    private boolean extended, dragging;
    private Supplier<String> description = () -> null;

    public Component(String label, float posX, float posY, float offsetX, float offsetY, float width, float height) {
        this.label = label;
        this.posX = posX;
        this.posY = posY;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.width = width;
        this.height = height;
        this.finishedX = posX + offsetX;
        this.finishedY = posY + offsetY;
    }

    public void init() {

    }

    public void moved(float posX, float posY) {
        setPosX(posX);
        setPosY(posY);
        setFinishedX(getPosX() + getOffsetX());
        setFinishedY(getPosY() + getOffsetY());
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (RenderUtil.mouseWithinBounds(mouseX, mouseY, getFinishedX() + 5, getFinishedY() + 1, getWidth() - 10, getHeight() - 2))
        {
            Click.descriptionFrame.setDescription(this.getDescription());
        }
    }

    public void keyTyped(char character, int keyCode) {
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
    }

    public float getFinishedX() {
        return finishedX;
    }

    public void setFinishedX(float finishedX) {
        this.finishedX = finishedX;
    }

    public float getFinishedY() {
        return finishedY;
    }

    public void setFinishedY(float finishedY) {
        this.finishedY = finishedY;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
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

    public static ModuleCache<ClickGui> getClickGui() {
        return Click.CLICK_GUI;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public String getDescription()
    {
        return description.get();
    }

    public void setDescription(Supplier<String> description)
    {
        this.description = description;
    }
}
