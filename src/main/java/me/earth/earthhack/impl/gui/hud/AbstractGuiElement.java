package me.earth.earthhack.impl.gui.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public abstract class AbstractGuiElement implements ICoordinate {

    private float x;
    private float y;
    private float width;
    private float height;
    private String name;

    protected Minecraft mc = Minecraft.getMinecraft();

    public AbstractGuiElement(String name) {
        this.name = name;
        this.x = 0;
        this.y = 0;
        this.width = 300;
        this.height = new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight();
    }

    public AbstractGuiElement(String name, float width, float height) {
        this.name = name;
        this.width = width;
        this.height = height;
    }

    public AbstractGuiElement(String name, float x, float y, float width, float height) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDisplayName(String displayName) {
        this.name = displayName;
    }

    @Override
    public float getX() {
        return this.x;
    }

    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public float getY() {
        return this.y;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }

    @Override
    public float getWidth() {
        return this.width;
    }

    @Override
    public void setWidth(float width) {
        this.width = width;
    }

    @Override
    public float getHeight() {
        return this.height;
    }

    @Override
    public void setHeight(float height) {
        this.height = height;
    }

}
