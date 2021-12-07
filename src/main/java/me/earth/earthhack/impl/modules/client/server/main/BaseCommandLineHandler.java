package me.earth.earthhack.impl.modules.client.server.main;

import me.earth.earthhack.impl.modules.client.server.api.ICloseable;
import me.earth.earthhack.impl.modules.client.server.main.command.CommandException;
import me.earth.earthhack.impl.modules.client.server.main.command.CommandLineManager;
import me.earth.earthhack.impl.modules.client.server.main.command.handlers.ExitCommand;

import java.util.Scanner;

public class BaseCommandLineHandler extends CommandLineManager
{
    private final ICloseable closeable;

    public BaseCommandLineHandler(ICloseable closeable)
    {
        this.closeable = closeable;
        this.add("exit", new ExitCommand(closeable));
        this.add("stop", new ExitCommand(closeable));
        this.add("bye",  new ExitCommand(closeable));
    }

    public void startListening()
    {
        Thread thread = Thread.currentThread();
        try (Scanner scanner = new Scanner(System. in))
        {
            while (!thread.isInterrupted() && closeable.isOpen())
            {
                String input = scanner.nextLine();
                try
                {
                    this.handle(input);
                }
                catch (CommandException e)
                {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

}
