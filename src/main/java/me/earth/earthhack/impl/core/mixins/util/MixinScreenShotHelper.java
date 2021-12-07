package me.earth.earthhack.impl.core.mixins.util;

import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.gui.chat.clickevents.RunnableClickEvent;
import me.earth.earthhack.impl.gui.chat.components.SuppliedComponent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.management.Management;
import me.earth.earthhack.impl.util.misc.FileUtil;
import me.earth.earthhack.impl.util.render.ScreenShotRunnable;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.IntBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(ScreenShotHelper.class)
public abstract class MixinScreenShotHelper
{
    private static final SettingCache<Boolean, BooleanSetting, Management> POOL
        = Caches.getSetting(Management.class, BooleanSetting.class, "Pooled-ScreenShots", false);

    @Shadow
    private static IntBuffer pixelBuffer;
    @Shadow
    private static int[] pixelValues;

    @Redirect(
        method = "saveScreenshot(Ljava/io/File;IILnet/minecraft/client/shader/Framebuffer;)Lnet/minecraft/util/text/ITextComponent;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/ScreenShotHelper;saveScreenshot(Ljava/io/File;Ljava/lang/String;IILnet/minecraft/client/shader/Framebuffer;)Lnet/minecraft/util/text/ITextComponent;"))
    private static ITextComponent saveScreenshot(File gameDirectory,
                                                 @Nullable String name,
                                                 int width,
                                                 int height,
                                                 Framebuffer buffer)
    {
        if (POOL.getValue())
        {
            try
            {
                return makeScreenShot(
                        gameDirectory, name, width, height, buffer);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return new TextComponentTranslation("screenshot.failure",
                                                    e.getMessage());
            }
        }

        return ScreenShotHelper.saveScreenshot(
                gameDirectory, null, width, height, buffer);
    }

    private static ITextComponent makeScreenShot(File gameDirectory,
                                                 @Nullable String name,
                                                 int width,
                                                 int height,
                                                 Framebuffer buffer)
            throws IOException
    {
        if (OpenGlHelper.isFramebufferEnabled())
        {
            width = buffer.framebufferTextureWidth;
            height = buffer.framebufferTextureHeight;
        }

        int i = width * height;

        if (pixelBuffer == null || pixelBuffer.capacity() < i)
        {
            pixelBuffer = BufferUtils.createIntBuffer(i);
            pixelValues = new int[i];
        }

        GlStateManager.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
        GlStateManager.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        pixelBuffer.clear();

        if (OpenGlHelper.isFramebufferEnabled())
        {
            GlStateManager.bindTexture(buffer.framebufferTexture);
            GlStateManager.glGetTexImage(
                    GL11.GL_TEXTURE_2D, 0, 32993, 33639, pixelBuffer);
        }
        else
        {
            GlStateManager.glReadPixels(
                    0, 0, width, height, 32993, 33639, pixelBuffer);
        }

        pixelBuffer.get(pixelValues);

        AtomicBoolean finished = new AtomicBoolean();
        AtomicReference<String> supplier =
                new AtomicReference<>("Creating Screenshot...");
        AtomicReference<File> file =
                new AtomicReference<>();

        Managers.THREAD.submit(new ScreenShotRunnable(supplier,
                                                      file,
                                                      finished,
                                                      width,
                                                      height,
                                                      pixelValues,
                                                      gameDirectory,
                                                      name));

        ITextComponent component = new SuppliedComponent(supplier::get);
        component.getStyle().setClickEvent(new RunnableClickEvent(() ->
        {
            File f = file.get();
            if (f != null)
            {
                URI uri = (new File(f.getAbsolutePath())).toURI();
                try
                {
                    FileUtil.openWebLink(uri);
                }
                catch (Throwable t)
                {
                    Throwable cause = t.getCause();
                    Earthhack.getLogger().error("Couldn't open link: {}",
                        cause == null ? "<UNKNOWN>" : cause.getMessage());
                }
            }
        }));

        component.getStyle().setUnderlined(true);
        return component;
    }

}
