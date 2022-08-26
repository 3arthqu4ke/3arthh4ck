package me.earth.earthhack.impl.core.mixins.gui;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.extratab.ExtraTab;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(GuiPlayerTabOverlay.class)
public abstract class MixinGuiPlayerTabOverlay extends Gui
{
    private static final ModuleCache<ExtraTab> EXTRA_TAB =
            Caches.getModule(ExtraTab.class);
    private static final SettingCache<Boolean, BooleanSetting, ExtraTab>
        PING = Caches.getSetting(ExtraTab.class, BooleanSetting.class,
                                 "Ping", false);
    private static final SettingCache<Boolean, BooleanSetting, ExtraTab>
        BARS = Caches.getSetting(ExtraTab.class, BooleanSetting.class,
                                 "Bars", true);

    @Shadow
    @Final
    private Minecraft mc;

    @Unique
    private int maxPingOffset;

    @Redirect(
        method = "renderPlayerlist",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;subList(II)Ljava/util/List;",
            remap = false))
    public List<NetworkPlayerInfo> subListHook(List<NetworkPlayerInfo> list,
                                               int from,
                                               int to)
    {
        return list.subList(from, EXTRA_TAB.returnIfPresent(e ->
                        Math.min(e.getSize(to), list.size()), to));
    }

    @Inject(method = "renderPlayerlist", at = @At("HEAD"))
    private void renderPlayerlistHeadHook(int width, Scoreboard scoreboardIn,
                                          ScoreObjective scoreObjectiveIn,
                                          CallbackInfo ci)
    {
        if (PING.getValue())
        {
            maxPingOffset = this.mc.player.connection
                .getPlayerInfoMap()
                .stream()
                .map(NetworkPlayerInfo::getResponseTime)
                .map(String::valueOf)
                .map(this.mc.fontRenderer::getStringWidth)
                .max(Integer::compare)
                .orElse(0)
                    + 1
                    + (BARS.getValue() ? 12 : 0);
        }
    }

    @Inject(
        method = "getPlayerName",
        at = @At("HEAD"),
        cancellable = true)
    public void getPlayerNameHook(NetworkPlayerInfo playerInfo,
                                  CallbackInfoReturnable<String> info)
    {
        info.setReturnValue(EXTRA_TAB.returnIfPresent(e ->
                e.getName(playerInfo), getPlayerNameDefault(playerInfo)));
    }

    private String getPlayerNameDefault(NetworkPlayerInfo info)
    {
        return info.getDisplayName() != null
                ? info.getDisplayName().getFormattedText()
                : ScorePlayerTeam.formatPlayerName(
                        info.getPlayerTeam(),
                        info.getGameProfile().getName());
    }

    @Redirect(
        method = "renderPlayerlist",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/FontRenderer;getStringWidth(Ljava/lang/String;)I",
            ordinal = 0))
    private int getStringWidthHook(FontRenderer instance, String k)
    {
        return PING.getValue()
            ? instance.getStringWidth(k) + maxPingOffset
            : instance.getStringWidth(k);
    }

    @Inject(method = "drawPing", at = @At("HEAD"), cancellable = true)
    private void drawPingHook(int x, int xOff, int y,
                              NetworkPlayerInfo networkPlayerInfoIn,
                              CallbackInfo ci)
    {
        if (PING.getValue())
        {
            int color = networkPlayerInfoIn.getResponseTime() < 50
                ? 0xFF00FF00
                : networkPlayerInfoIn.getResponseTime() < 100
                    ? 0xFFFFFF00
                    : 0xFFFF0000;

            String toDraw = String.valueOf(networkPlayerInfoIn.getResponseTime());
            this.mc.fontRenderer.drawStringWithShadow(
                toDraw,
                x + xOff - (BARS.getValue() ? 12 : 0)
                    - this.mc.fontRenderer.getStringWidth(toDraw),
                y,
                color);
        }

        if (!BARS.getValue())
        {
            ci.cancel();
        }
    }

}
