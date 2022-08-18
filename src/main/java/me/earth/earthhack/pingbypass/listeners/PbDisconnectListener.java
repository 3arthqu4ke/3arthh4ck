package me.earth.earthhack.pingbypass.listeners;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.DisconnectEvent;
import me.earth.earthhack.pingbypass.PingBypass;
import me.earth.earthhack.pingbypass.nethandler.LoginHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.util.text.TextComponentString;

public class PbDisconnectListener extends EventListener<DisconnectEvent> {
    private boolean allow;

    public PbDisconnectListener() {
        super(DisconnectEvent.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(DisconnectEvent event) {
        if (event.getManager().getNetHandler() instanceof INetHandlerStatusClient) {
            return;
        }

        NetworkManager manager = PingBypass.getNetworkManager();
        if (manager != null && !isAllowingDisconnect()) {
            TextComponentString component = new TextComponentString(
                "PingBypass disconnected from server:\n");
            component.appendSibling(event.getComponent());
            if (manager.getNetHandler() instanceof LoginHandler) {
                manager.sendPacket(
                    new net.minecraft.network.login.server.SPacketDisconnect(
                        component));
                manager.closeChannel(component);
            } else {
                manager.sendPacket(new SPacketDisconnect(component),
                                   o -> manager.closeChannel(component));
            }
        }
    }

    public boolean isAllowingDisconnect() {
        return allow;
    }

    public void setAllow(boolean allow) {
        this.allow = allow;
    }
    
}
