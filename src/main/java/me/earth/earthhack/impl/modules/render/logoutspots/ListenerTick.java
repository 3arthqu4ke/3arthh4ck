package me.earth.earthhack.impl.modules.render.logoutspots;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.logoutspots.util.LogoutSpot;

import java.util.Map;
import java.util.UUID;

final class ListenerTick extends ModuleListener<LogoutSpots, TickEvent> {
    public ListenerTick(LogoutSpots module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        int remove = module.remove.getValue() * 1000;
        if (remove != 0) {
            for (Map.Entry<UUID, LogoutSpot> entry : module.spots.entrySet()) {
                LogoutSpot value = entry.getValue();
                if (value != null && System.currentTimeMillis() - value.getTimeStamp() > remove) {
                    module.spots.entrySet().remove(entry);
                }
            }
        }
    }

}
