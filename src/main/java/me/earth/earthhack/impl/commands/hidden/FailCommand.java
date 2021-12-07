package me.earth.earthhack.impl.commands.hidden;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.register.Registrable;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;

import java.util.Arrays;

/**
 * Command returned by the CommandManager
 * when no applicable Command was found.
 */
public class FailCommand extends Command implements Registrable, Globals
{
    private final StopWatch indexTimer = new StopWatch();
    private int index;

    public FailCommand()
    {
        super(new String[][]{{"fail"}}, true);
    }

    @Override
    public void execute(String[] args)
    {
        if (args != null && args.length != 0)
        {
            Command closest = null;
            int closestDistance = Integer.MAX_VALUE;
            for (Command command : Managers.COMMANDS.getRegistered())
            {
                int levenshtein = CommandUtil.levenshtein(command.getName(),
                                                          args[0]);
                if (levenshtein < closestDistance)
                {
                    closest = command;
                    closestDistance = levenshtein;
                }
            }

            if (closest != null)
            {
                ChatUtil.sendMessage(TextColor.RED
                        + "Command not found, did you mean "
                        + closest.getName() + "?. Type "
                        + Commands.getPrefix()
                        + "help to get a list of commands.");

                Earthhack.getLogger().info("FailCommand for args: "
                        + Arrays.toString(args));
                return;
            }
        }

        ChatUtil.sendMessage(TextColor.RED
                + "Command not found. Type "
                + Commands.getPrefix()
                + "help to get a list of commands.");

        Earthhack.getLogger().info("FailCommand for args: "
                + Arrays.toString(args));
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args)
    {
        // TODO: this kinda ugly tho
        String conc = Managers.COMMANDS.getConcatenatedCommands();
        if (conc == null || conc.isEmpty())
        {
            return PossibleInputs.empty().setRest(TextColor.RED + "error");
        }

        if (indexTimer.passed(750))
        {
            index += 10;
            indexTimer.reset();
        }

        if (index >= conc.length())
        {
            index = 0;
        }

        return PossibleInputs.empty().setRest(
                TextColor.RED + conc.substring(index) + ", " + conc);
    }

    @Override
    public Completer onTabComplete(Completer completer)
    {
        completer.setMcComplete(true);
        return completer;
    }

}
