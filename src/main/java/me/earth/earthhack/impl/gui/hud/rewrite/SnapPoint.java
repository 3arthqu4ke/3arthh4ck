package me.earth.earthhack.impl.gui.hud.rewrite;

import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.impl.util.misc.GuiUtil;
import me.earth.earthhack.impl.util.render.Render2DUtil;

import java.awt.*;
import java.util.Collection;

public class SnapPoint
{

    private float size;
    private float off;
    private float location;
    private Orientation orientation;
    private boolean visible;
    private boolean shouldSnap;

    public SnapPoint(float off, float size, float location, boolean visible, Orientation orientation) {
        this.off = off;
        this.size = size;
        this.location = location;
        this.orientation = orientation;
        this.visible = visible;
        this.shouldSnap = true;
    }

    public void update(Collection<HudElement> elements) {
        if (shouldSnap) {
            for (HudElement element : elements) {
                boolean inverted = false;
                switch (this.orientation) {
                    case TOP:
                        inverted = Math.min(Math.abs(element.getY() - location), Math.abs(element.getY() + element.getHeight() - location)) != Math.abs(element.getY() - location);
                        break;
                    case BOTTOM:
                        inverted = Math.min(Math.abs(element.getY() - location), Math.abs(element.getY() + element.getHeight() - location)) == Math.abs(element.getY() - location);
                        break;
                    case LEFT:
                        inverted = Math.min(Math.abs(element.getX() - location), Math.abs(element.getX() + element.getWidth() - location)) != Math.abs(element.getX() - location);
                        break;
                    case RIGHT:
                        inverted = Math.min(Math.abs(element.getX() - location), Math.abs(element.getX() + element.getWidth() - location)) == Math.abs(element.getX() - location);
                        break;
                }
                if (GuiUtil.getDistance(this, element) < 4 && !element.isDragging()) {
                    GuiUtil.updatePosition(this, element, inverted); // TODO: find inverted
                }
            }
        }
    }

    public void draw(int mouseX, int mouseY, float partialTicks) {
        if (orientation == Orientation.BOTTOM
                || orientation == Orientation.TOP
                || orientation == Orientation.HORIZONTAL_CENTER)
        {
            Render2DUtil.drawLine((float) off, (float) location, (float) (off + size), (float) location, 1.0f, Color.WHITE.getRGB());
        }
        else if (orientation == Orientation.LEFT
                || orientation == Orientation.RIGHT
                || orientation == Orientation.VERTICAL_CENTER)
        {
            Render2DUtil.drawLine((float) location, (float) off, (float) location, (float) (off + size), 1.0f, Color.WHITE.getRGB());
        }
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public float getOff() {
        return off;
    }

    public void setOff(float off) {
        this.off = off;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public float getLocation() {
        return location;
    }

    public void setLocation(float location) {
        this.location = location;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean shouldSnap() {
        return shouldSnap;
    }

    public void setShouldSnap(boolean shouldSnap) {
        this.shouldSnap = shouldSnap;
    }

    public enum Orientation {
        TOP,
        BOTTOM,
        LEFT,
        RIGHT,
        VERTICAL_CENTER,
        HORIZONTAL_CENTER
    }

}
