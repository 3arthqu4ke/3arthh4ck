package me.earth.earthhack.pingbypass.listeners;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.misc.ClickBlockEvent;
import me.earth.earthhack.impl.event.events.misc.DamageBlockEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.pingbypass.protocol.c2s.C2SClickBlockPacket;
import me.earth.earthhack.pingbypass.protocol.c2s.C2SDamageBlockPacket;

public class PbBlockBreakService extends SubscriberImpl implements Globals {
    private static final ModuleCache<PingBypassModule> PINGBYPASS =
        Caches.getModule(PingBypassModule.class);

    public PbBlockBreakService() {
        this.listeners.add(new LambdaListener<>(DamageBlockEvent.class, Integer.MIN_VALUE, e -> {
            if (!e.isCancelled() && PINGBYPASS.isEnabled() && !PINGBYPASS.get().isOld() && mc.player != null) {
                mc.player.connection.sendPacket(new C2SDamageBlockPacket(e.getPos(), e.getFacing(), e.getDamage(), e.getDelay()));
            }
        }));
        this.listeners.add(new LambdaListener<>(ClickBlockEvent.class, Integer.MIN_VALUE, e -> {
            if (!e.isCancelled() && PINGBYPASS.isEnabled() && !PINGBYPASS.get().isOld() && mc.player != null) {
                mc.player.connection.sendPacket(new C2SClickBlockPacket(e.getPos(), e.getFacing()));
            }
        }));


    }

}
