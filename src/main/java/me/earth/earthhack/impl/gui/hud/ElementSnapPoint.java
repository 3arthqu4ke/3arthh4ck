package me.earth.earthhack.impl.gui.hud;

import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.impl.managers.Managers;

public class ElementSnapPoint extends SnapPoint {

    private final HudElement element;

    public ElementSnapPoint(HudElement element, Orientation orientation) {
        super(element.getX(), element.getY(), element.getWidth(), orientation);
        switch (orientation) {
            case TOP:
                x = element.getX();
                y = element.getY();
                length = element.getWidth();
                break;
            case BOTTOM:
                x = element.getX();
                y = element.getY() + element.getHeight();
                length = element.getWidth();
                break;
            case LEFT:
                x = element.getX();
                y = element.getY();
                length = element.getHeight();
                break;
            case RIGHT:
                x = element.getX() + element.getWidth();
                y = element.getY();
                length = element.getHeight();
                break;
        }
        this.element = element;
    }

    @Override
    public void update(int mouseX, int mouseY, float partialTicks) {
        for (HudElement hudElement : Managers.ELEMENTS.getRegistered()) {
            if (hudElement == element) continue;
            if (orientation == Orientation.LEFT && (hudElement.getX() <= x + 4 && hudElement.getX() >= x - 4)) {
                if (!hudElement.isDragging() && x != hudElement.getX()) {
                    hudElement.setX(x);
                }
            } else if (orientation == Orientation.RIGHT && (hudElement.getX() + hudElement.getWidth() <= x + 4 && hudElement.getX() + hudElement.getWidth() >= x - 4)) {
                if (!hudElement.isDragging() && x != hudElement.getX() + hudElement.getWidth()) {
                    hudElement.setX(x - hudElement.getWidth());
                }
            } else if (orientation == Orientation.TOP && (hudElement.getY() <= y + 4 && hudElement.getY() >= y - 4)) {
                if (!hudElement.isDragging() && y != hudElement.getY()) {
                    hudElement.setY(y);
                }
            } else if (orientation == Orientation.BOTTOM && (hudElement.getY() + hudElement.getHeight() <= y + 4 && hudElement.getY() + hudElement.getHeight() >= y - 4)) {
                if (!hudElement.isDragging() && y != hudElement.getY() + hudElement.getHeight()) {
                    hudElement.setY(y - hudElement.getHeight());
                }
            }
        }
    }

    // dont draw dat shit
    @Override
    public void draw(int mouseX, int mouseY, float partialTicks) { }

    public HudElement getElement() {
        return element;
    }

}
