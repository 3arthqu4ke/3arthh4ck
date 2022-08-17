package me.earth.earthhack.impl.modules.client.hud.arraylist;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Hidden;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.hud.HUD;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

/**
 * @author Gerald
 * @since 3/22/2021
 **/

public class ArrayEntry implements Globals {
    private Module module;
    private float x, startX;
    private boolean atDesired;
    private static final ModuleCache<HUD> HUD_MODULE_CACHE = Caches.getModule(HUD.class);
    private final StopWatch stopWatch = new StopWatch();
    public ArrayEntry(Module module) {
        this.module = module;
        this.x = this.startX = new ScaledResolution(mc).getScaledWidth();
        stopWatch.reset();
    }

    public void drawArrayEntry(float desiredX, float desiredY) {
        final float textWidth = HUD.RENDERER.getStringWidth(ModuleUtil.getHudName(getModule()));
        final float xSpeed = textWidth / (Minecraft.getDebugFPS() >> 2);
        GlStateManager.pushMatrix();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.scissor(desiredX - textWidth - 1, desiredY - 1, desiredX + 1, desiredY + HUD.RENDERER.getStringHeightI() + 3);
        HUD_MODULE_CACHE.get().renderText(ModuleUtil.getHudName(getModule()), getX(), desiredY);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.popMatrix();
        if (module.isEnabled() && module.isHidden() != Hidden.Hidden) {
            if (stopWatch.passed(1000)) {
                setX(desiredX - textWidth);
                setAtDesired(true);
            } else {
                if (isAtDesired()) {
                    setX(desiredX - textWidth);
                } else {
                    if (!isDone(desiredX)) {
                        if (getX() != desiredX- textWidth) {
                            setX(Math.max(getX() - xSpeed, desiredX - textWidth));
                        }
                    } else {
                        setAtDesired(true);
                    }
                }
            }
        } else {
            if (!shouldDelete()) {
                setX(getX() + xSpeed);
            } else {
                HUD_MODULE_CACHE.get().getRemoveEntries().put(module,this);
            }
            setAtDesired(false);
            stopWatch.reset();
        }
    }

    private boolean isDone(float desiredX) {
        final float textWidth = HUD.RENDERER.getStringWidth(ModuleUtil.getHudName(getModule()));
        return getX() <= desiredX - textWidth;
    }

    private boolean shouldDelete() {
        return getX() > getStartX();
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public boolean isAtDesired() {
        return atDesired;
    }

    public void setAtDesired(boolean atDesired) {
        this.atDesired = atDesired;
    }

    public float getStartX() {
        return startX;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }
}
