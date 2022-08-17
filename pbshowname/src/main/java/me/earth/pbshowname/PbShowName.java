package me.earth.pbshowname;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.plugin.Plugin;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.managers.client.PluginDescriptions;
import me.earth.earthhack.pingbypass.PingBypass;
import me.earth.earthhack.pingbypass.event.PbPacketEvent;
import net.minecraft.network.login.client.CPacketLoginStart;

@SuppressWarnings("unused")
public class PbShowName implements Plugin {
    private static String name;

    @Override
    public void load() {
        PluginDescriptions.register(this, "Shows the name of the currently" +
            " connected player in the server info.");
        Bus.EVENT_BUS.register(
            new LambdaListener<PbPacketEvent.C2S<CPacketLoginStart>>(
                PbPacketEvent.C2S.class, CPacketLoginStart.class,
                e -> {
                    name = e.getPacket().getProfile().getName();
                    Earthhack.getLogger().info("PbShowName: " + name);
                }));
    }

    public static String getName() {
        return PingBypass.isConnected() ? name : null;
    }

}
