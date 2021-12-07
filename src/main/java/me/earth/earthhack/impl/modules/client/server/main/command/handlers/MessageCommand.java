package me.earth.earthhack.impl.modules.client.server.main.command.handlers;

import me.earth.earthhack.impl.modules.client.server.api.ISender;
import me.earth.earthhack.impl.modules.client.server.main.command.CommandException;
import me.earth.earthhack.impl.modules.client.server.main.command.ICommandHandler;
import me.earth.earthhack.impl.modules.client.server.protocol.ProtocolUtil;

import java.io.IOException;

public class MessageCommand implements ICommandHandler
{
    private final ISender sender;
    private final int id;

    public MessageCommand(ISender sender, int id)
    {
        this.sender = sender;
        this.id     = id;
    }

    @Override
    public void handle(String command) throws CommandException
    {
        try
        {
            sender.send(ProtocolUtil.writeString(id, command));
        }
        catch (IOException e)
        {
            throw new CommandException(e.getMessage());
        }
    }

}
