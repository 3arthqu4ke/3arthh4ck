package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import me.earth.earthhack.impl.util.helpers.command.CustomCompleterResult;

public class BooleanArgument extends AbstractArgument<Boolean>
{
    public BooleanArgument()
    {
        super(Boolean.class);
    }

    @Override
    public Boolean fromString(String argument) throws ArgParseException
    {
        return Boolean.parseBoolean(argument);
    }

    @Override
    public PossibleInputs getPossibleInputs(String arg)
    {
        PossibleInputs inputs = PossibleInputs.empty();
        if (arg == null || arg.isEmpty())
        {
            return inputs.setRest("<Boolean>");
        }

        arg = arg.toLowerCase();
        if ("true".startsWith(arg))
        {
            inputs.setCompletion(TextUtil.substring("true", arg.length()));
        }
        else if ("false".startsWith(arg))
        {
            inputs.setCompletion(TextUtil.substring("false", arg.length()));
        }

        return inputs;
    }

    @Override
    public CustomCompleterResult onTabComplete(Completer completer)
    {
        return CustomCompleterResult.PASS;
    }

}
