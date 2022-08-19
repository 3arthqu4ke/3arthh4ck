package me.earth.earthhack.impl.modules.client.server.main;

import me.earth.earthhack.impl.managers.thread.GlobalExecutor;
import me.earth.earthhack.impl.modules.client.server.api.*;
import me.earth.earthhack.impl.modules.client.server.host.Host;
import me.earth.earthhack.impl.modules.client.server.protocol.Protocol;
import me.earth.earthhack.impl.modules.client.server.protocol.handlers.GlobalMessageHandler;
import me.earth.earthhack.impl.modules.client.server.protocol.handlers.MessageHandler;
import me.earth.earthhack.impl.modules.client.server.protocol.handlers.NameHandler;
import me.earth.earthhack.impl.modules.client.server.util.SystemLogger;

import java.io.IOException;

public class ServerMain
{
    public static void main(String[] args) throws IOException
    {
        int port = 0;
        if (args.length > 1)
        {
            port = Integer.parseInt(args[1]);
        }

        int t = (int) (Runtime.getRuntime().availableProcessors() / 1.5) - 1;
        ILogger logger              = new SystemLogger();
        IPacketManager pManager     = new SimplePacketManager();
        IConnectionManager cManager = new SimpleConnectionManager(pManager, t);

        // ------------- protocol handlers -------------------

        pManager.add(Protocol.NAME, new NameHandler(logger));

        pManager.add(Protocol.MESSAGE, new MessageHandler(logger));

        pManager.add(Protocol.GLOBAL, new GlobalMessageHandler(
                logger, cManager));

        for (int id : Protocol.ids())
        {
            if (pManager.getHandlerFor(id) == null)
            {
                pManager.add(id, new SUnsupportedHandler(
                    "This is a command-line server. " +
                        "This type of packet is not supported!"));
            }
        }

        // ------------- protocol handlers -------------------


        Host host = Host.createAndStart(GlobalExecutor.FIXED_EXECUTOR,
                                        cManager,
                                        new SystemShutdownHandler(),
                                        port,
                                        true);

        System.out.println("Listening on port: " + host.getPort()
                + ". Enter \"exit\" or \"stop\" to exit.");

        BaseCommandLineHandler commandLine = new BaseCommandLineHandler(host);
        commandLine.startListening();
    }

}