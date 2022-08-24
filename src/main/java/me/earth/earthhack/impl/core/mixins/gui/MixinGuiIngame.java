package me.earth.earthhack.impl.core.mixins.gui;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.event.events.render.CrosshairEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.hud.HUD;
import me.earth.earthhack.impl.modules.client.hud.modes.Potions;
import me.earth.earthhack.impl.modules.player.sorter.Sorter;
import me.earth.earthhack.impl.modules.render.norender.NoRender;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public abstract class MixinGuiIngame
{
    private static final ModuleCache<Sorter> SORTER =
     Caches.getModule(Sorter.class);
    private static final SettingCache<Potions, EnumSetting<Potions>, HUD> POTS =
     Caches.getSetting(HUD.class, Setting.class, "Potions", Potions.Keep);
    private static final ModuleCache<NoRender> NO_RENDER =
     Caches.getModule(NoRender.class);

    @Inject(
        method = "renderPortal",
        at = @At("HEAD"),
        cancellable = true)
    protected void renderPortalHook(float timeInPortal,
                                    ScaledResolution scaledResolution,
                                    CallbackInfo info)
    {
        if (NO_RENDER.returnIfPresent(NoRender::noPortal, false))
        {
            info.cancel();
        }
    }

    @Inject(
        method = "renderPumpkinOverlay",
        at = @At("HEAD"),
        cancellable = true)
    protected void renderPumpkinOverlayHook(ScaledResolution scaledRes,
                                            CallbackInfo info)
    {
        if (NO_RENDER.returnIfPresent(NoRender::noPumpkin, false))
        {
            info.cancel();
        }
    }

    @Inject(
        method = "renderPotionEffects",
        at = @At("HEAD"),
        cancellable = true)
    protected void renderPotionEffectsHook(ScaledResolution scaledRes,
                                           CallbackInfo info)
    {
        if (POTS.getValue() == Potions.Hide || POTS.getValue() == Potions.Text)
        {
            info.cancel();
        }
    }

    @Inject(
            method = "renderAttackIndicator",
            at = @At("HEAD"),
            cancellable = true)
    protected void renderAttackIndicator(float partialTicks, ScaledResolution p_184045_2_, CallbackInfo ci)
    {
        final CrosshairEvent event = new CrosshairEvent();
        Bus.EVENT_BUS.post(event);
        if (event.isCancelled())
            ci.cancel();
    }

    @Inject(method = "renderHotbar", at = @At("HEAD"))
    public void onRenderHotbar(ScaledResolution sr,
                                float partialTicks,
                                CallbackInfo ci)
    {
        SORTER.computeIfPresent(Sorter::updateMapping);
    }

    @Redirect(
        method = "renderHotbar",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/player/InventoryPlayer;currentItem:I",
            opcode = Opcodes.GETFIELD))
    public int renderHotbarHook(InventoryPlayer inventoryPlayer)
    {
        int slot = inventoryPlayer.currentItem;
        return SORTER.returnIfPresent(s -> s.getReverseMapping(slot), slot);
    }

    @Redirect(
        method = "renderHotbar",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/NonNullList;get(I)Ljava/lang/Object;"))
    private Object renderHotbarHook(NonNullList<ItemStack> nonNullList, int p_get_1_)
    {
        int slot = SORTER.returnIfPresent(s -> s.getHotbarMapping(p_get_1_), p_get_1_);
        return nonNullList.get(slot);
    }

}
