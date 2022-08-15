package me.earth.earthhack.pingbypass.listeners;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.core.ducks.network.ICPacketPlayerDigging;
import me.earth.earthhack.impl.event.listeners.SendListener;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.impl.modules.player.speedmine.mode.MineMode;
import me.earth.earthhack.pingbypass.PingBypass;
import net.minecraft.network.play.client.CPacketPlayerDigging;

public class ClientDiggingService extends SubscriberImpl {
    private static final ModuleCache<?> SPEED_MINE = new ModuleCache<>(
        () -> PingBypass.MODULES.getObject("Speedmine"), Module.class);
    private static final SettingCache<MineMode, Setting<MineMode>, ?> MINE_MODE =
        SettingCache.newModuleSettingCache("Mode", MineMode.class, SPEED_MINE, MineMode.Reset);

    public ClientDiggingService() {
        this.listeners.add(new SendListener<>(CPacketPlayerDigging.class, e -> {
            if (SPEED_MINE.isEnabled()
                && MINE_MODE.getValue() != MineMode.Reset
                && PingBypassModule.CACHE.isEnabled()
                && !PingBypassModule.CACHE.get().isOld()
                && (e.getPacket().getAction() == CPacketPlayerDigging.Action.START_DESTROY_BLOCK
                || e.getPacket().getAction() == CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK
                || e.getPacket().getAction() == CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK)
                && ((ICPacketPlayerDigging) e.getPacket()).isNormalDigging()) {
                e.setCancelled(true);
            }
        }));
    }

}
