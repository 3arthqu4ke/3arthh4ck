package me.earth.earthhack.impl.modules.client.server.main.command;

public interface ICommandHandler
{
    void handle(String command) throws CommandException;

}
