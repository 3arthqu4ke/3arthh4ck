package me.earth.earthhack.impl.modules.client.server.main.command;

import java.util.HashMap;
import java.util.Map;

public class CommandLineManager implements ICommandLineHandler
{
    private final Map<String, ICommandHandler> handlers;

    public CommandLineManager()
    {
        this.handlers = new HashMap<>();
    }

    public void handle(String line) throws CommandException
    {
        String[] command = line.split(" ", 2);
        if (command.length < 1)
        {
            throw new CommandException(
                "Your command was empty...");
        }

        ICommandHandler handler = handlers.get(command[0].toLowerCase());
        if (handler == null)
        {
            throw new CommandException("Unknown command: " + command[0] + ".");
        }

        handler.handle(command.length == 1 ? "" : command[1]);
    }

    public void add(String command, ICommandHandler handler)
    {
        handlers.put(command.toLowerCase(), handler);
    }

}
