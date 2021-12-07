package me.earth.earthhack.impl.gui.hud;

import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.Render2DUtil;

import java.awt.*;

public class SnapPoint {

    protected Orientation orientation;
    protected float x;
    protected float y;
    protected float length;

    public SnapPoint(float x, float y, float length, Orientation orientation) {
        this.x = x;
        this.y = y;
        this.length = length;
        this.orientation = orientation;
    }

    public void update(int mouseX, int mouseY, float partialTicks) {
        for (HudElement element : Managers.ELEMENTS.getRegistered()) {
            if (orientation == Orientation.LEFT && (element.getX() <= x + 4 && element.getX() >= x - 4)) {
                if (!element.isDragging() && x != element.getX()) {
                    element.setX(x);
                }
            } else if (orientation == Orientation.RIGHT && (element.getX() + element.getWidth() <= x + 4 && element.getX() + element.getWidth() >= x - 4)) {
                if (!element.isDragging() && x != element.getX() + element.getWidth()) {
                    element.setX(x - element.getWidth());
                }
            } else if (orientation == Orientation.TOP && (element.getY() <= y + 4 && element.getY() >= y - 4)) {
                if (!element.isDragging() && y != element.getY()) {
                    element.setY(y);
                }
            } else if (orientation == Orientation.BOTTOM && (element.getY() + element.getHeight() <= y + 4 && element.getY() + element.getHeight() >= y - 4)) {
                if (!element.isDragging() && y != element.getY() + element.getHeight()) {
                    element.setY(y - element.getHeight());
                }
            }
        }
    }

    public void draw(int mouseX, int mouseY, float partialTicks) {
        switch (orientation) {
            case TOP:
            case BOTTOM:
                Render2DUtil.drawLine(x, y, x + length, y, 1.0f, Color.WHITE.getRGB());
                break;
            case RIGHT:
            case LEFT:
                Render2DUtil.drawLine(x, y, x, y + length, 1.0f, Color.WHITE.getRGB());
                break;
        }
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

}
