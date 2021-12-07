package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.util.CommandScheduler;
import me.earth.earthhack.impl.util.network.ServerUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.earthhack.impl.util.thread.ThreadUtil;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.resources.I18n;

import java.util.concurrent.ScheduledExecutorService;

public class ConnectCommand extends Command
        implements Globals, CommandScheduler
{
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

        ServerUtil.disconnectFromMC("Disconnecting.");
        SCHEDULER.submit(() -> mc.addScheduledTask(() ->
            mc.displayGuiScreen(
                new GuiConnecting(new GuiMultiplayer(new GuiMainMenu()),
                mc,
                new ServerData(I18n.format("selectServer.defaultName"),
                args[1],
                false)))),
        100);
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

        if (args.length == 2)
        {
            for (int i = 0; i < cachedServerList.countServers(); i++)
            {
                ServerData data = cachedServerList.getServerData(i);
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
