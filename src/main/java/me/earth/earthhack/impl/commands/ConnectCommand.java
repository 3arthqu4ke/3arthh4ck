package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.util.CommandScheduler;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.impl.modules.client.pingbypass.guis.GuiConnectingPingBypass;
import me.earth.earthhack.impl.util.network.ServerUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.earthhack.pingbypass.PingBypass;
import me.earth.earthhack.pingbypass.protocol.c2s.C2SCommandPacket;
import me.earth.earthhack.pingbypass.protocol.s2c.S2CUnloadWorldPacket;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;

public class ConnectCommand extends Command
        implements Globals, CommandScheduler
{
    private static final ModuleCache<PingBypassModule> PINGBYPASS =
        Caches.getModule(PingBypassModule.class);

    private ServerList cachedServerList;
    private long lastCache;

    public ConnectCommand()
    {
        super(new String[][]{{"connect"}, {"ip"}});
    }

    @Override
    public void execute(String[] args)
    {
        if (args.length < 2)
        {
            ChatUtil.sendMessage(TextColor.RED + "Please specify an IP!");
            return;
        }

        if (PINGBYPASS.isEnabled()
            && !PINGBYPASS.get().isOld()
            && mc.player != null)
        {
            mc.player.connection.sendPacket(new C2SCommandPacket(args));
            return;
        }

        ServerAddress serveraddress = ServerAddress.fromString(args[1]);
        if (PingBypass.isConnected()) {
            try {
                PingBypass.sendPacket(new S2CUnloadWorldPacket(
                    "Pingbypass is connecting to " + args[1] + "..."));
                PingBypass.DISCONNECT_SERVICE.setAllow(true);
                ServerUtil.disconnectFromMC("Disconnecting.");
            } finally {
                PingBypass.DISCONNECT_SERVICE.setAllow(false);
            }
        }

        SCHEDULER.submit(() -> mc.addScheduledTask(() -> {
            if (PINGBYPASS.isEnabled()) {
                mc.displayGuiScreen(new GuiConnectingPingBypass(new GuiMainMenu(), mc, serveraddress.getIP(), serveraddress.getPort()));
            } else {
                mc.displayGuiScreen(new GuiConnecting(new GuiMainMenu(), mc, serveraddress.getIP(), serveraddress.getPort()));
            }
        }), 100);
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args)
    {
        if (cachedServerList == null
                || System.currentTimeMillis() - lastCache > 60000)
        {
            cachedServerList = new ServerList(this.mc);
            cachedServerList.loadServerList();
            lastCache = System.currentTimeMillis();
        }

        if (args.length >= 2)
        {
            for (int i = 0; i < cachedServerList.countServers(); i++)
            {
                ServerData data = cachedServerList.getServerData(i);
                //noinspection PointlessNullCheck
                if (data.serverIP != null
                    && TextUtil.startsWith(data.serverIP, args[1]))
                {
                    return new PossibleInputs(TextUtil.substring(
                        data.serverIP, args[1].length()), "");
                }
            }
        }

        if (args.length >= 2)
        {
            return PossibleInputs.empty();
        }

        return super.getPossibleInputs(args);
    }

}
