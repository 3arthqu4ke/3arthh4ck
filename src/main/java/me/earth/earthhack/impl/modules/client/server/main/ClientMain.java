package me.earth.earthhack.impl.modules.client.server.main;

import me.earth.earthhack.impl.managers.thread.GlobalExecutor;
import me.earth.earthhack.impl.modules.client.server.api.*;
import me.earth.earthhack.impl.modules.client.server.client.Client;
import me.earth.earthhack.impl.modules.client.server.main.command.handlers.MessageCommand;
import me.earth.earthhack.impl.modules.client.server.protocol.Protocol;
import me.earth.earthhack.impl.modules.client.server.protocol.ProtocolUtil;
import me.earth.earthhack.impl.modules.client.server.protocol.handlers.MessageHandler;
import me.earth.earthhack.impl.modules.client.server.util.SystemLogger;

import java.io.IOException;

public class ClientMain
{
    public static void main(String[] args) throws IOException
    {
        if (args.length < 4)
        {
            throw new IllegalArgumentException(
                    "Ip and port and name are missing!");
        }

        String ip = args[1];
        int port  = Integer.parseInt(args[2]);
        ILogger log = new SystemLogger();
        log.log("Attempting to connect to: " + ip + ", " + port);
        IPacketManager manager = new SimplePacketManager();
        manager.add(Protocol.MESSAGE,   new MessageHandler(log));
        manager.add(Protocol.GLOBAL,    new MessageHandler(log,
                                                        s -> "global: "  + s));
        manager.add(Protocol.EXCEPTION, new MessageHandler(log,
                                                        s -> "error: "   + s));
        manager.add(Protocol.COMMAND,   new MessageHandler(log,
                                                        s -> "command: " + s));
        for (int id : Protocol.ids())
        {
            if (manager.getHandlerFor(id) == null)
            {
                manager.add(id, new CUnsupportedHandler(log, id));
            }
        }

        IServerList serverList = new SimpleServerList();
        Client client = new Client(manager, serverList, ip, port);
        GlobalExecutor.EXECUTOR.submit(client);
        log.log("Client connected. Enter \"exit\" or \"stop\" to exit.");
        log.log("Setting name to " + args[3] + "...");
        client.setName(args[3]);
        client.send(ProtocolUtil.writeString(Protocol.NAME, args[3]));
        BaseCommandLineHandler commands = new BaseCommandLineHandler(client);
        commands.add("msg",     new MessageCommand(client, Protocol.MESSAGE));
        commands.add("message", new MessageCommand(client, Protocol.MESSAGE));
        commands.add("name",    new MessageCommand(client, Protocol.NAME));
        commands.add("global",  new MessageCommand(client, Protocol.GLOBAL));
        commands.startListening();
    }

}
