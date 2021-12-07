package me.earth.earthhack.impl.modules.client.server.main.command;

public interface ICommandLineHandler
{
    void handle(String line) throws CommandException;

    void add(String command, ICommandHandler handler);
}
