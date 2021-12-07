package me.earth.earthhack.impl.commands.abstracts;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;

public abstract class AbstractTextCommand extends Command
{
    public AbstractTextCommand(String name)
    {
        super(new String[][]{{name}, {"message"}});
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args)
    {
        if (args.length > 1)
        {
            return PossibleInputs.empty();
        }

        return super.getPossibleInputs(args);
    }

    @Override
    public Completer onTabComplete(Completer completer)
    {
        if (completer.getArgs().length > 1
                || completer.getArgs()[0].equalsIgnoreCase(this.getName()))
        {
            completer.setMcComplete(true);
            return completer;
        }

        return super.onTabComplete(completer);
    }

}
