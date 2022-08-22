package me.earth.earthhack.impl.core.mixins.block;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.core.ducks.entity.ITileEntityShulkerBox;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.velocity.Velocity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityShulkerBox.class)
public abstract class MixinTileEntityShulkerBox implements ITileEntityShulkerBox
{
    private static final ModuleCache<Velocity>
        VELOCITY = Caches.getModule(Velocity.class);
    private static final SettingCache<Boolean, BooleanSetting, Velocity>
        SHULKERS = Caches.getSetting
            (Velocity.class, BooleanSetting.class, "Shulkers", false);

    @Override
    @Accessor(value = "items")
    public abstract NonNullList<ItemStack> getItems();

    @Inject(
        method = "moveCollidedEntities",
        at = @At("HEAD"),
        cancellable = true)
    public void moveCollidedEntitiesHook(CallbackInfo info)
    {
        if (VELOCITY.isEnabled() && SHULKERS.getValue())
        {
            info.cancel();
        }
    }

}
