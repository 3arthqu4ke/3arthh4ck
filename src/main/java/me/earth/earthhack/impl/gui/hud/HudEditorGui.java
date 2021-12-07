package me.earth.earthhack.impl.gui.hud;

import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.render.chams.Chams;
import me.earth.earthhack.impl.util.misc.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static me.earth.earthhack.impl.gui.hud.Orientation.BOTTOM;
import static me.earth.earthhack.impl.gui.hud.Orientation.LEFT;
import static me.earth.earthhack.impl.gui.hud.Orientation.RIGHT;
import static me.earth.earthhack.impl.gui.hud.Orientation.TOP;

public class HudEditorGui extends GuiScreen {

    private static HudEditorGui INSTANCE;
    private final static Minecraft mc = Minecraft.getMinecraft();

    private HudPanel panel;

    private Set<SnapPoint> snapPoints;
    private Map<HudElement, SnapPoint> moduleSnapPoints;

    public HudEditorGui() {
        INSTANCE = this;
        panel = new HudPanel();
        snapPoints = new HashSet<>();
        moduleSnapPoints = new HashMap<>();
        for (HudElement element : Managers.ELEMENTS.getRegistered()) {
            if (element.isEnabled()) {
                moduleSnapPoints.put(element, new ElementSnapPoint(element, TOP));
                moduleSnapPoints.put(element, new ElementSnapPoint(element, BOTTOM));
                moduleSnapPoints.put(element, new ElementSnapPoint(element, LEFT));
                moduleSnapPoints.put(element, new ElementSnapPoint(element, RIGHT));
            }
        }
        snapPoints.add(new SnapPoint(2, -2, mc.displayHeight + 4, LEFT));
        snapPoints.add(new SnapPoint(width - 2, -2, mc.displayHeight + 4, RIGHT));
        snapPoints.add(new SnapPoint(-2, 2, mc.displayWidth + 4, TOP));
        snapPoints.add(new SnapPoint(-2, height - 2, mc.displayWidth + 4, BOTTOM));
    }

    public static HudEditorGui getInstance() {
        return INSTANCE;
    }

    public void onToggle() {
        snapPoints.clear();
        moduleSnapPoints.clear();
        for (HudElement element : Managers.ELEMENTS.getRegistered()) {
            if (element.isEnabled()) {
                moduleSnapPoints.put(element, new ElementSnapPoint(element, TOP));
                moduleSnapPoints.put(element, new ElementSnapPoint(element, BOTTOM));
                moduleSnapPoints.put(element, new ElementSnapPoint(element, LEFT));
                moduleSnapPoints.put(element, new ElementSnapPoint(element, RIGHT));
            }
        }
        snapPoints.add(new SnapPoint(2, -2, height + 4, LEFT));
        snapPoints.add(new SnapPoint(width - 2, -2, height + 4, RIGHT));
        snapPoints.add(new SnapPoint(-2, 2, width + 4, TOP));
        snapPoints.add(new SnapPoint(-2, height - 2, width + 4, BOTTOM));
    }

    public HudPanel getPanel() {
        return panel;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        List<HudElement> clicked = new ArrayList<>();
        for (HudElement element : Managers.ELEMENTS.getRegistered())
        {
            if (element.isEnabled() && GuiUtil.isHovered(element, mouseX, mouseY)) {
                clicked.add(element);
                // element.guiMouseClicked(mouseX, mouseY, mouseButton);
            }
        }
        clicked.sort(Comparator.comparing(HudElement::getZ));
        if (!clicked.isEmpty()) {
            clicked.get(0).guiMouseClicked(mouseX, mouseY, mouseButton);
        }
        panel.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        for (HudElement element : Managers.ELEMENTS.getRegistered())
        {
            if (element.isEnabled()) {
                element.guiMouseReleased(mouseX, mouseY, state);
            }
        }
        panel.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        for (HudElement element : Managers.ELEMENTS.getRegistered())
        {
            if (element.isEnabled()) {
                element.guiKeyPressed(typedChar, keyCode);
            }
        }
        panel.keyPressed(typedChar, keyCode);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // super.drawScreen(mouseX, mouseY, partialTicks);
        for (SnapPoint point : snapPoints) {
            point.update(mouseX, mouseY, partialTicks);
            point.draw(mouseX, mouseY, partialTicks);
            Earthhack.getLogger().info(point.orientation.name() + " x: " + point.getX() + " y: " + point.getY() + " length: " + point.getLength());
        }
        for (SnapPoint point : moduleSnapPoints.values()) {
            point.update(mouseX, mouseY, partialTicks);
        }
        for (HudElement element : Managers.ELEMENTS.getRegistered())
        {
            if (element.isEnabled()) {
                element.guiUpdate(mouseX, mouseY, partialTicks);
                element.guiDraw(mouseX, mouseY, partialTicks);
            }
        }
        panel.draw(mouseX, mouseY, partialTicks);
        // animation.play();
        // animation.add(partialTicks);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public Set<SnapPoint> getSnapPoints() {
        return snapPoints;
    }
}
