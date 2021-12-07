package me.earth.earthhack.impl.core.mixins.gui;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.extratab.ExtraTab;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(GuiPlayerTabOverlay.class)
public abstract class MixinGuiPlayerTabOverlay
{
    private static final ModuleCache<ExtraTab> EXTRA_TAB =
            Caches.getModule(ExtraTab.class);

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

}
