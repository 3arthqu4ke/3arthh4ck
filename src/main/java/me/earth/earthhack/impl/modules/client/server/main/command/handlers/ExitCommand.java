package me.earth.earthhack.impl.modules.client.server.main.command.handlers;

import me.earth.earthhack.impl.modules.client.server.api.ICloseable;
import me.earth.earthhack.impl.modules.client.server.main.command.CommandException;
import me.earth.earthhack.impl.modules.client.server.main.command.ICommandHandler;

public class ExitCommand implements ICommandHandler
{
    private final ICloseable closeable;

    public ExitCommand(ICloseable closeable)
    {
        this.closeable = closeable;
    }

    @Override
    public void handle(String command) throws CommandException
    {
        System.out.println("Bye!");
        closeable.close();
        System.exit(0);
    }

}
