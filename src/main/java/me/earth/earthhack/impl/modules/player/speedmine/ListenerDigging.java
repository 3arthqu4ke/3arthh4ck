package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.core.ducks.network.ICPacketPlayerDigging;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.antisurround.AntiSurround;
import me.earth.earthhack.impl.modules.misc.nuker.Nuker;
import me.earth.earthhack.impl.modules.player.speedmine.mode.MineMode;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.mine.MineUtil;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.math.BlockPos;

final class ListenerDigging extends
        ModuleListener<Speedmine, PacketEvent.Send<CPacketPlayerDigging>>
{
    private static final ModuleCache<Nuker> NUKER =
        Caches.getModule(Nuker.class);
    private static final SettingCache<Boolean, BooleanSetting, Nuker> NUKE =
        Caches.getSetting(Nuker.class, BooleanSetting.class, "Nuke", false);
    private static final ModuleCache<AntiSurround> ANTISURROUND =
        Caches.getModule(AntiSurround.class);

    public ListenerDigging(Speedmine module)
    {
        super(module, PacketEvent.Send.class, CPacketPlayerDigging.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketPlayerDigging> event)
    {
        if (module.cancelNormalPackets.getValue()
            && ((ICPacketPlayerDigging) event.getPacket()).isNormalDigging()
            && (event.getPacket().getAction() ==
                    CPacketPlayerDigging.Action.START_DESTROY_BLOCK
                || event.getPacket().getAction() ==
                    CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK
                || event.getPacket().getAction() ==
                    CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK))
        {
            event.setCancelled(true);
            return;
        }

        if (!PlayerUtil.isCreative(mc.player)
            && !ANTISURROUND.returnIfPresent(AntiSurround::isActive, false)
            && (!NUKER.isEnabled() || !NUKE.getValue())
            && (module.mode.getValue() == MineMode.Packet
                    || module.mode.getValue() == MineMode.Smart
                    || module.mode.getValue() == MineMode.Instant))
        {
            CPacketPlayerDigging packet = event.getPacket();
            if (packet.getAction() ==
                        CPacketPlayerDigging.Action.START_DESTROY_BLOCK
                    || packet.getAction() ==
                            CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK)
            {
                BlockPos pos = packet.getPosition();
                if (!MineUtil.canBreak(pos))
                {
                    event.setCancelled(true);
                }
            }
        }
    }

}
