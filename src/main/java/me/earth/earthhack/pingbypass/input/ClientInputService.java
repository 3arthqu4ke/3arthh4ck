package me.earth.earthhack.pingbypass.input;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import me.earth.earthhack.impl.event.events.keyboard.MouseEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.pingbypass.PingBypass;
import me.earth.earthhack.pingbypass.protocol.c2s.C2SKeyboardPacket;
import me.earth.earthhack.pingbypass.protocol.c2s.C2SMousePacket;
import me.earth.earthhack.pingbypass.protocol.c2s.C2SPostKeyPacket;
import net.minecraft.client.entity.EntityPlayerSP;

public class ClientInputService extends SubscriberImpl implements Globals {
    private static final ModuleCache<PingBypassModule> PING_BYPASS =
        Caches.getModule(PingBypassModule.class);

    public ClientInputService() {
        this.listeners.add(new LambdaListener<>(MouseEvent.class, e -> {
            EntityPlayerSP player = mc.player;
            if (PING_BYPASS.isEnabled()
                && e.getButton() != -1
                && !PING_BYPASS.get().isOld()
                && !PingBypass.isServer()
                && player != null) {
                player.connection.sendPacket(new C2SMousePacket(e));
            }
        }));
        this.listeners.add(new LambdaListener<>(KeyboardEvent.class, e -> {
            if (PING_BYPASS.isEnabled()
                && !PingBypass.isServer()
                && !PING_BYPASS.get().isOld()
                && mc.player != null) {
                mc.player.connection.sendPacket(new C2SKeyboardPacket(e));
            }
        }));
        this.listeners.add(new LambdaListener<>(KeyboardEvent.Post.class, e -> {
            if (PING_BYPASS.isEnabled()
                && !PING_BYPASS.get().isOld()
                && !PingBypass.isServer()
                && mc.player != null) {
                mc.player.connection.sendPacket(new C2SPostKeyPacket());
            }
        }));
    }

}
