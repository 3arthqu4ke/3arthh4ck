package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.util.helpers.command.CustomCompleterResult;

public abstract class AbstractPositionArgument<T> extends AbstractArgument<T>
{
    protected static final String[] REST = {"y,z", ",z", "", ""};

    protected final String name;

    public AbstractPositionArgument(String name, Class<T> type)
    {
        super(type);
        this.name = name;
    }

    @Override
    public PossibleInputs getPossibleInputs(String argument)
    {
        if (argument == null || argument.isEmpty())
        {
            return new PossibleInputs("", "<" + name + ">");
        }

        PossibleInputs inputs = PossibleInputs.empty();
        if (argument.toLowerCase().startsWith("origin"))
        {
            return inputs.setCompletion(
                    TextUtil.substring("ORIGIN", argument.length()));
        }

        String[] split = argument.split(",");
        if (split.length > 3)
        {
            return inputs;
        }

        if (split.length < 3)
        {
            inputs.setCompletion(",");
        }


        return inputs.setRest(REST[split.length]);
    }

    @Override
    public CustomCompleterResult onTabComplete(Completer completer)
    {
        return CustomCompleterResult.PASS;
    }

}
