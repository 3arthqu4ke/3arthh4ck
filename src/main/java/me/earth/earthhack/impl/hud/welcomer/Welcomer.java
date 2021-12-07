package me.earth.earthhack.impl.hud.welcomer;

import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.impl.managers.Managers;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

public class Welcomer extends HudElement {

    public static String text = "welcome to phobro hack megyn";

    public Welcomer() {
        super("Welcomer", mc.displayWidth / 2.0f - Managers.TEXT.getStringWidth(text) / 2.0f, 2, Managers.TEXT.getStringWidth(text), Managers.TEXT.getStringHeight());
    }

    @Override
    public void guiDraw(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.pushMatrix();
        super.guiDraw(mouseX, mouseY, partialTicks);
        Managers.TEXT.drawString(text, getX(), getY(), Color.WHITE.getRGB(), true);
        GlStateManager.popMatrix();
    }

    @Override
    public void guiUpdate(int mouseX, int mouseY, float partialTicks) {
        super.guiUpdate(mouseX, mouseY, partialTicks);
        setWidth(Managers.TEXT.getStringWidth(text));
        setHeight(Managers.TEXT.getStringHeight());
    }

    @Override
    public void hudUpdate(float partialTicks) {
        super.hudUpdate(partialTicks);
        setWidth(Managers.TEXT.getStringWidth(text));
        setHeight(Managers.TEXT.getStringHeight());
    }

    @Override
    public void hudDraw(float partialTicks) {
        Managers.TEXT.drawString(text, getX(), getY(), Color.WHITE.getRGB(), true);
    }

    @Override
    public float getWidth() {
        return Managers.TEXT.getStringWidth(text);
    }

    @Override
    public float getHeight() {
        return Managers.TEXT.getStringHeight();
    }


}
