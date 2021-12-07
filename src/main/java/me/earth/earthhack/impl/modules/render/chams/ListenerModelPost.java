package me.earth.earthhack.impl.modules.render.chams;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.render.ModelRenderEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.render.chams.mode.ChamsMode;
import me.earth.earthhack.impl.modules.render.esp.ESP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.GL_ALL_ATTRIB_BITS;
import static org.lwjgl.opengl.GL11.GL_ALPHA_TEST;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_POLYGON_OFFSET_LINE;
import static org.lwjgl.opengl.GL11.GL_STENCIL_TEST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glPopAttrib;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushAttrib;
import static org.lwjgl.opengl.GL11.glPushMatrix;

final class ListenerModelPost extends ModuleListener<Chams, ModelRenderEvent.Post> {

    public ListenerModelPost(Chams module) {
        super(module, ModelRenderEvent.Post.class);
    }

    @Override
    public void invoke(ModelRenderEvent.Post event) {
        if (!ESP.isRendering && module.mode.getValue() == ChamsMode.JelloTop) {
            EntityLivingBase entity = event.getEntity();
            if (module.isValid(entity)) {
                Color color = module.getVisibleColor(event.getEntity());
                glPushMatrix();
                glPushAttrib(GL_ALL_ATTRIB_BITS);
                glDisable(GL_ALPHA_TEST);
                glDisable(GL_TEXTURE_2D);
                glDisable(GL_LIGHTING);
                glEnable(GL_BLEND);
                glLineWidth(1.5f);
                glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                glEnable(GL_STENCIL_TEST);
                glEnable(GL_POLYGON_OFFSET_LINE);
                glDepthMask(false);
                glDisable(GL_DEPTH_TEST);
                glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
                render(event);
                glDepthMask(true);
                glEnable(GL_DEPTH_TEST);
                glEnable(GL_TEXTURE_2D);
                glEnable(GL_LIGHTING);
                glDisable(GL_BLEND);
                glEnable(GL_ALPHA_TEST);
                glPopAttrib();
                glPopMatrix();
                event.setCancelled(true);
                render(event);

            }
        }
    }

    private void render(ModelRenderEvent.Post event) {
        event.getModel().render(event.getEntity(),
                event.getLimbSwing(),
                event.getLimbSwingAmount(),
                event.getAgeInTicks(),
                event.getNetHeadYaw(),
                event.getHeadPitch(),
                event.getScale());
    }

}
