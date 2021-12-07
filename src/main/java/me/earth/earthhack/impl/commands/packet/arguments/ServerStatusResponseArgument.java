package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import me.earth.earthhack.impl.commands.packet.util.DummyServerStatusResponse;
import net.minecraft.network.ServerStatusResponse;

public class ServerStatusResponseArgument
        extends AbstractArgument<ServerStatusResponse>
{
    public ServerStatusResponseArgument()
    {
        super(ServerStatusResponse.class);
    }

    @Override
    public ServerStatusResponse fromString(String argument)
            throws ArgParseException
    {
        return new DummyServerStatusResponse();
    }

}
