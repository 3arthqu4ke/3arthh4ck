package me.earth.earthhack.pingbypass.listeners;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.pingbypass.PingBypass;
import me.earth.earthhack.pingbypass.protocol.c2s.C2SSpeedPacket;

public class ClientSpeedService extends EventListener<MotionUpdateEvent>
    implements Globals {
    private static final ModuleCache<PingBypassModule> MODULE =
        Caches.getModule(PingBypassModule.class);
   
    public ClientSpeedService() {
        super(MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (event.getStage() == Stage.PRE 
            && MODULE.isEnabled()
            && !MODULE.get().isOld()
            && mc.player != null
            && !PingBypass.isServer()) {
            mc.player.connection.sendPacket(new C2SSpeedPacket(
                mc.player.collidedHorizontally,
                mc.player.collidedVertically,
                mc.player.motionX,
                mc.player.motionY,
                mc.player.motionZ
            ));
        }
    }
    
}
