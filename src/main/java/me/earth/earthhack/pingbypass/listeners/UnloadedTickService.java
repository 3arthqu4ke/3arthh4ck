package me.earth.earthhack.pingbypass.listeners;

import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import net.minecraft.network.NetworkManager;

/**
 * When {@link me.earth.earthhack.pingbypass.protocol.s2c.S2CUnloadWorldPacket}
 * is received we need to tick the NetworkManager manually.
 */
public class UnloadedTickService extends SubscriberImpl implements Globals {
    private NetworkManager networkManager;

    public UnloadedTickService() {
        this.listeners.add(new LambdaListener<>(TickEvent.class, e -> {
            NetworkManager manager;
            if (mc.playerController == null
                && (manager = networkManager) != null) {
                if (!manager.isChannelOpen()) {
                    manager.handleDisconnection();
                    setNetworkManager(null);
                }
            }
        }));
    }

    public void setNetworkManager(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

}
