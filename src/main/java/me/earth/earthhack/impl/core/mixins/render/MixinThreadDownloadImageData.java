package me.earth.earthhack.impl.core.mixins.render;

import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.managers.thread.GlobalExecutor;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.extratab.ExtraTab;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ThreadDownloadImageData.class)
public abstract class MixinThreadDownloadImageData implements GlobalExecutor
{
    private static final SettingCache<Boolean, BooleanSetting, ExtraTab>
            DOWNLOAD_THREADS = Caches.getSetting(ExtraTab.class,
                                                 BooleanSetting.class,
                                                 "Download-Threads",
                                                 false);
    @Redirect(
        method = "loadTextureFromServer",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/Thread;start()V"))
    public void onStart(Thread thread)
    {
        if (DOWNLOAD_THREADS.getValue())
        {
            GlobalExecutor.FIXED_EXECUTOR.submit(thread);
        }
        else
        {
            thread.start();
        }
    }

}
