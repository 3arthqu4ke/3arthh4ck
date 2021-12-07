package me.earth.earthhack.impl.util.misc;

import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.impl.gui.click.frame.Frame;
import me.earth.earthhack.impl.gui.hud.AbstractGuiElement;
import me.earth.earthhack.impl.gui.hud.rewrite.SnapPoint;
import me.earth.earthhack.impl.util.render.Render2DUtil;

import java.awt.*;

public class GuiUtil {


    public static boolean isHovered(int x, int y, int width, int height, int mouseX, int mouseY) {
        return (mouseX >= x) && (mouseX <= x + width) && (mouseY >= y) && (mouseY < y + height);
    }

    public static boolean isHovered(float x, float y, float width, float height, float mouseX, float mouseY) {
        return (mouseX >= x) && (mouseX <= x + width) && (mouseY >= y) && (mouseY < y + height);
    }

    public static boolean isHovered(AbstractGuiElement element, int mouseX, int mouseY) {
        return isHovered(element.getX(), element.getY(), element.getWidth(), element.getHeight(), mouseX, mouseY);
    }

    public static boolean isHovered(HudElement element, int mouseX, int mouseY) {
        return isHovered(element.getX(), element.getY(), element.getWidth(), element.getHeight(), mouseX, mouseY);
    }

    public static boolean isHovered(Frame element, int mouseX, int mouseY) {
        return isHovered(element.getPosX(), element.getPosY(), element.getWidth(), element.getHeight() , mouseX, mouseY);
    }

    public static boolean isOverlapping(HudElement first, HudElement other) {
        double[] rec1 = new double[]{first.getX(), first.getY(), first.getX() + first.getWidth(), first.getY() + first.getHeight()};
        double[] rec2 = new double[]{other.getX(), other.getY(), other.getX() + other.getWidth(), other.getY() + other.getHeight()};
        if (rec1[0] == rec1[2] || rec1[1] == rec1[3] ||
                rec2[0] == rec2[2] || rec2[1] == rec2[3]) {
            // the line cannot have positive overlap
            return false;
        }

        return !(rec1[2] <= rec2[0] ||   // left
                rec1[3] <= rec2[1] ||   // bottom
                rec1[0] >= rec2[2] ||   // right
                rec1[1] >= rec2[3]);    // top
    }

    public static boolean isOverlapping(double[] rec1, double[] rec2) {
        /*double[] rec1 = new double[]{first.getX(), first.getY(), first.getX() + first.getWidth(), first.getY() + first.getHeight()};
        double[] rec2 = new double[]{other.getX(), other.getY(), other.getX() + other.getWidth(), other.getY() + other.getHeight()};*/
        if (rec1[0] == rec1[2] || rec1[1] == rec1[3] ||
                rec2[0] == rec2[2] || rec2[1] == rec2[3]) {
            // the line cannot have positive overlap
            return false;
        }

        return !(rec1[2] <= rec2[0] ||   // left
                rec1[3] <= rec2[1] ||   // bottom
                rec1[0] >= rec2[2] ||   // right
                rec1[1] >= rec2[3]);    // top
    }

    public static float reCheckSliderRange(float value, float min, float max) {
        return Math.min(Math.max(value, min), max);
    }

    public static double roundSliderForConfig(double val) {
        return Double.parseDouble(String.format("%.2f", val));
    }

    public static String roundSlider(float f) {
        return String.format("%.2f", f);
    }

    public static float roundSliderStep(float input, float step) {
        return (float)Math.round(input / step) * step;
    }

    public static boolean isHoveredOnEdge(int x, int y, int width, int height, int mouseX, int mouseY, int edge) {
        return isHovered(x, y, width, height, mouseX, mouseY) && (mouseX < x + edge || mouseX > x + width - edge || mouseY < y + edge || mouseY > y + height - edge);
    }

    public static Edge getHoveredEdge(int x, int y, int width, int height, int mouseX, int mouseY, int edge) {
        if (isHovered(x, y, edge, edge, mouseX, mouseY)) {
            return Edge.TOP_LEFT;
        } else if (isHovered(x, y + height - edge, edge, edge, mouseX, mouseY)) {
            return Edge.BOTTOM_LEFT;
        } else if (isHovered(x + width - edge, y, edge, edge, mouseX, mouseY)) {
            return Edge.TOP_RIGHT;
        } else if (isHovered(x + width - edge, y + height - edge, edge, edge, mouseX, mouseY)) {
            return Edge.BOTTOM_RIGHT;
        } else if (isHovered(x, y, edge, height, mouseX, mouseY)) {
            return Edge.LEFT;
        } else if (isHovered(x + width - edge, y, edge, height, mouseX, mouseY)) {
            return Edge.RIGHT;
        } else if (isHovered(x, y + edge, width, edge, mouseX, mouseY)) {
            return Edge.TOP;
        } else if (isHovered(x, y + height - edge, width, edge, mouseX, mouseY)) {
            return Edge.BOTTOM;
        }
        return null;
    }

    public static Edge getHoveredEdgeNoTop(int x, int y, int width, int height, int mouseX, int mouseY, int edge) {
        if (isHovered(x, y, edge, edge, mouseX, mouseY)) {
            // return Edge.TOP_LEFT;
        } else if (isHovered(x, y + height - edge, edge, edge, mouseX, mouseY)) {
            return Edge.BOTTOM_LEFT;
        } else if (isHovered(x + width - edge, y, edge, edge, mouseX, mouseY)) {
            // return Edge.TOP_RIGHT;
        } else if (isHovered(x + width - edge, y + height - edge, edge, edge, mouseX, mouseY)) {
            return Edge.BOTTOM_RIGHT;
        } else if (isHovered(x, y, edge, height, mouseX, mouseY)) {
            return Edge.LEFT;
        } else if (isHovered(x + width - edge, y, edge, height, mouseX, mouseY)) {
            return Edge.RIGHT;
        } else if (isHovered(x, y + edge, width, edge, mouseX, mouseY)) {
            // return Edge.TOP;
        } else if (isHovered(x, y + height - edge, width, edge, mouseX, mouseY)) {
            return Edge.BOTTOM;
        }
        return null;
    }

    public static Edge getHoveredEdgeNoTop(AbstractGuiElement element, int mouseX, int mouseY, int edge) {
        return getHoveredEdgeNoTop((int) element.getX(), (int) element.getY(), (int) element.getWidth(), (int) element.getHeight(), mouseX, mouseY, edge);
    }

    public static Edge getHoveredEdge(AbstractGuiElement element, int mouseX, int mouseY, int edge) {
        return getHoveredEdge((int) element.getX(), (int) element.getY(), (int) element.getWidth(), (int) element.getHeight(), mouseX, mouseY, edge);
    }

    public static Edge getHoveredEdge(HudElement element, int mouseX, int mouseY, int edge) {
        return getHoveredEdge((int) element.getX(), (int) element.getY(), (int) element.getWidth(), (int) element.getHeight(), mouseX, mouseY, edge);
    }

    public static double getDistance(SnapPoint point, HudElement element) {
        me.earth.earthhack.impl.gui.hud.rewrite.SnapPoint.Orientation orientation = point.getOrientation();
        switch (orientation) {
            case TOP:
                return Math.min(Math.abs(point.getLocation() - element.getY()), Math.abs(point.getLocation() - (element.getY() + element.getHeight())));
            case BOTTOM:
                return Math.min(Math.abs(point.getLocation() - (element.getY() + element.getHeight())), Math.abs(point.getLocation() - element.getY()));
            case LEFT:
                return Math.min(Math.abs(point.getLocation() - element.getX()), Math.abs(point.getLocation() - (element.getX() + element.getWidth())));
            case RIGHT:
                return Math.min(Math.abs(point.getLocation() - (element.getX() + element.getWidth())), Math.abs(point.getLocation() - element.getX()));
            case VERTICAL_CENTER:
                return Math.abs(point.getLocation() - (element.getX() + (element.getWidth() / 2)));
            case HORIZONTAL_CENTER:
                return Math.abs(point.getLocation() - (element.getY() + (element.getHeight() / 2)));
        }
        return -1;
    }

    public static void updatePosition(SnapPoint point, HudElement element, boolean inverted) {
        me.earth.earthhack.impl.gui.hud.rewrite.SnapPoint.Orientation orientation = point.getOrientation();
        switch (orientation) {
            case TOP:
                element.setY(inverted ? (point.getLocation() - element.getHeight()) : point.getLocation());
                break;
            case BOTTOM:
                element.setY(inverted ? point.getLocation() : point.getLocation() - element.getHeight());
                break;
            case LEFT:
                element.setX(inverted ? (point.getLocation() - element.getWidth()) : point.getLocation());
                break;
            case RIGHT:
                element.setX(inverted ? point.getLocation() : point.getLocation() - element.getWidth());
                break;
            case VERTICAL_CENTER:
                element.setX(point.getLocation() - (element.getWidth() / 2));
                break;
            case HORIZONTAL_CENTER:
                element.setY(point.getLocation() - (element.getHeight() / 2));
        }
    }

    public enum Edge {
        RIGHT,
        TOP,
        LEFT,
        BOTTOM,
        TOP_RIGHT,
        BOTTOM_RIGHT,
        TOP_LEFT,
        BOTTOM_LEFT
    }

}
