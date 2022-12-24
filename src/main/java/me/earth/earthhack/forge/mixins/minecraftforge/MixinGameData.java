package me.earth.earthhack.forge.mixins.minecraftforge;

import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.management.Management;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.GameData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(value = GameData.class, remap = false)
public abstract class MixinGameData
{
    private static final SettingCache
     <Boolean, BooleanSetting, Management> IGNORE =
     Caches.getSetting(Management.class, BooleanSetting.class, "IgnoreForgeRegistries", false);

    @Redirect(
        method = "injectSnapshot",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;size()I",
            ordinal = 0))
    private static int injectSnapshotHook(List<ResourceLocation> list)
    {
        if (IGNORE.getValue() && list.size() != 0)
        {
            Earthhack
              .getLogger()
              .info("Ignored " + list.size() + " missing forge registries.");
            return 0;
        }

        return list.size();
    }

}
