package me.earth.earthhack.impl.commands.abstracts;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.client.ModuleManager;
import me.earth.earthhack.impl.util.text.TextColor;

// TODO: support for multiple modules? take varargs for indices
public abstract class AbstractModuleCommand extends Command
{
    protected final int index;

    public AbstractModuleCommand(String name, String[][] additionalArgs)
    {
        this(concatArray(name, additionalArgs), 1);
    }

    public AbstractModuleCommand(String[][] usage, int index)
    {
        super(usage);
        if (index < 0)
        {
            throw new IllegalArgumentException("Index is smaller than 0!");
        }

        this.index = index;
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args)
    {
        PossibleInputs inputs = super.getPossibleInputs(args);
        if (args.length > index)
        {
            Module module = getModule(args, this.index);
            if (module == null)
            {
                inputs.setCompletion("").setRest(TextColor.RED + " not found.");
            }
            else if (args.length == index + 1)
            {
                inputs.setCompletion(TextUtil.substring(module.getName(),
                                                        args[index].length()));
            }
        }

        return inputs;
    }

    protected Module getModule(String[] args, int index)
    {
        if (args.length <= index)
        {
            return null;
        }

        return CommandUtil.getNameableStartingWith(
                                            args[index],
                                            getModuleManager().getRegistered());
    }

    protected ModuleManager getModuleManager()
    {
        return Managers.MODULES;
    }

    private static String[][] concatArray(String name, String[][] args)
    {
        String[][] concat = new String[args.length + 2][];
        concat[0] = new String[]{name};
        concat[1] = new String[]{"module"};
        System.arraycopy(args, 0, concat, 2, args.length);
        return concat;
    }

}
