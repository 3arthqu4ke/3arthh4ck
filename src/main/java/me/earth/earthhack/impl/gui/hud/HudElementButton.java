package me.earth.earthhack.impl.gui.hud;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.clickgui.ClickGui;
import me.earth.earthhack.impl.util.misc.GuiUtil;
import me.earth.earthhack.impl.util.render.Render2DUtil;

import java.awt.*;

public class HudElementButton extends AbstractGuiElement {

    private static final ModuleCache<ClickGui> CLICK_GUI = Caches.getModule(ClickGui.class);

    private final HudElement element;

    public HudElementButton(HudElement element) {
        super(element.getName(), Managers.TEXT.getStringWidth(element.getName()), Managers.TEXT.getStringHeight());
        this.element = element;
    }

    public void draw(int mouseX, int mouseY, float partialTicks) {
        if (element.isEnabled()) {
            Render2DUtil.drawRect(getX(), getY(), getX() + getWidth(), getY() + getHeight(), CLICK_GUI.get().color.getValue().getRGB());
            Managers.TEXT.drawString(element.getName(), getX() + getWidth() / 2 - Managers.TEXT.getStringWidth(element.getName()) / 2.0f, getY(), Color.WHITE.getRGB());
        } else {
            Managers.TEXT.drawString(element.getName(), getX() + getWidth() / 2 - Managers.TEXT.getStringWidth(element.getName()) / 2.0f, getY(), Color.GRAY.getRGB());
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (GuiUtil.isHovered(this, mouseX, mouseY) && mouseButton == 0) {
            element.toggle();
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

    }

    public void keyPressed(char eventChar, int key) {

    }

    public HudElement getElement() {
        return element;
    }

    @Override
    public float getHeight() {
        return Managers.TEXT.getStringHeight();
    }

}
