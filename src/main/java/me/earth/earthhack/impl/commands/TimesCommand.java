package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.util.CommandScheduler;
import me.earth.earthhack.impl.commands.util.TimesProcess;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class TimesCommand extends Command implements CommandScheduler, Globals
{
    private final Map<String, TimesProcess> ids = new ConcurrentHashMap<>();
    private final AtomicLong id = new AtomicLong();

    public TimesCommand()
    {
        super(new String[][]{
            {"times"}, {"amount", "cancel"}, {"delay", "id"}, {"command"}});
    }

    @Override
    public void execute(String[] args)
    {
        if (args.length == 1)
        {
            ChatUtil.sendMessage("Use this command to execute a " +
                    "command x times with a given delay.");
            return;
        }

        if (args.length == 2)
        {
            if (args[1].equalsIgnoreCase("cancel"))
            {
                ChatUtil.sendMessage(TextColor.RED
                    + "No id specified, available: " + ids.keySet() + ".");
            }
            else
            {
                ChatUtil.sendMessage(TextColor.RED
                        + "Please specify a command.");
            }

            return;
        }

        if (args[1].equalsIgnoreCase("cancel"))
        {
            TimesProcess process = ids.get(args[2]);
            if (process == null)
            {
                ChatUtil.sendMessage(TextColor.RED
                        + "No process found for id "
                        + TextColor.WHITE + args[2]
                        + TextColor.RED + "!");
                return;
            }

            ChatUtil.sendMessage(TextColor.AQUA + "Cancelling process "
                    + TextColor.WHITE + args[2] + TextColor.AQUA + "...");

            process.setValid(false);
            process.clear();
            return;
        }

        if (args.length < 4)
        {
            ChatUtil.sendMessage(TextColor.RED + "Please specify a command.");
            return;
        }

        int amount;
        try
        {
            amount = (int) Long.parseLong(args[1]);
            if (amount <= 0)
            {
                ChatUtil.sendMessage(TextColor.RED + "Amount "
                        + TextColor.WHITE + args[1]
                        + TextColor.RED + " was smaller than or equal to 0!");
                return;
            }
        }
        catch (NumberFormatException e)
        {
            ChatUtil.sendMessage(TextColor.RED + "Couldn't parse "
                + TextColor.WHITE + args[1] + TextColor.RED + " to amount.");
            return;
        }

        long delay;
        try
        {
            delay = Long.parseLong(args[2]);
            if (delay < 0)
            {
                ChatUtil.sendMessage(TextColor.RED + "Delay "
                        + TextColor.WHITE + args[2]
                        + TextColor.RED + " was smaller than 0!");
                return;
            }
        }
        catch (NumberFormatException e)
        {
            ChatUtil.sendMessage(TextColor.RED + "Couldn't parse "
                + TextColor.WHITE + args[2] + TextColor.RED + " to delay.");
            return;
        }

        String[] arguments = Arrays.copyOfRange(args, 3, args.length);
        Command command = Managers.COMMANDS.getCommandForMessage(arguments);

        if (delay == 0)
        {
            mc.addScheduledTask(() ->
            {
                try
                {
                    for (int i = 0; i < amount; i++)
                    {
                        command.execute(arguments);
                    }
                }
                catch (Throwable t)
                {
                    ChatUtil.sendMessage(TextColor.RED
                            + "An error occurred while executing command "
                            + TextColor.WHITE + arguments[0]
                            + TextColor.RED + ": " + t.getMessage());
                    t.printStackTrace();
                }
            });
        }
        else
        {
            Runnable last = null;
            String processId = id.incrementAndGet() + "";
            TimesProcess process = new TimesProcess(amount);
            for (int i = 0; i < amount; i++)
            {
                if (last != null)
                {
                    process.addFuture(
                        SCHEDULER.schedule(last,
                                delay * (i - 1),
                                TimeUnit.MILLISECONDS));
                }

                long time = delay * i;
                if (time < 0)
                {
                    ChatUtil.sendMessage(
                        TextColor.RED + "Your delay * amount overflowed!");

                    process.setValid(false);
                    process.clear();
                    return;
                }

                last = () -> mc.addScheduledTask(() ->
                {
                    if (!process.isValid())
                    {
                        return;
                    }

                    try
                    {
                        command.execute(arguments);
                    }
                    catch (Throwable t)
                    {
                        ChatUtil.sendMessage(TextColor.RED
                                + "An error occurred while executing command "
                                + TextColor.WHITE + arguments[0]
                                + TextColor.RED + ": " + t.getMessage());
                        t.printStackTrace();
                    }
                });
            }

            Runnable finalLast = last;
            ids.put(processId, process);
            process.addFuture(SCHEDULER.schedule(() ->
            {
                finalLast.run();
                ids.remove(processId);
            }, delay * (amount - 1), TimeUnit.MILLISECONDS));

            ChatUtil.sendMessage(TextColor.GREEN
                    + "Started process with id "
                    + TextColor.AQUA + processId + TextColor.GREEN + ".");
        }
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args)
    {
        if (args.length == 1)
        {
            return super.getPossibleInputs(args);
        }

        if (args.length == 2)
        {
            if (TextUtil.startsWith("cancel", args[1]))
            {
                return super.getPossibleInputs(args);
            }
            else
            {
                return new PossibleInputs("", " <delay> <command>");
            }
        }

        if (TextUtil.startsWith("cancel", args[1]))
        {
            return PossibleInputs.empty();
        }

        if (args.length == 3)
        {
            return new PossibleInputs("", " <command>");
        }

        String[] arguments = Arrays.copyOfRange(args, 3, args.length);
        Command command = Managers.COMMANDS.getCommandForMessage(arguments);
        return command.getPossibleInputs(arguments);
    }

}
