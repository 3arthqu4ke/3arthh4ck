package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import me.earth.earthhack.impl.util.helpers.command.CustomCompleterResult;

import java.util.UUID;

public class UUIDArgument extends AbstractArgument<UUID>
{
    public UUIDArgument()
    {
        super(UUID.class);
    }

    @Override
    public UUID fromString(String argument) throws ArgParseException
    {
        try
        {
            return UUID.fromString(argument);
        }
        catch (Exception e)
        {
            throw new ArgParseException(
                    "Could not parse UUID from " + argument + "!");
        }
    }

    @Override
    public PossibleInputs getPossibleInputs(String argument)
    {
        if (argument == null || argument.isEmpty())
        {
            return new PossibleInputs("", "<" + getSimpleName() + ">");
        }

        String[] split = argument.split("-");
        StringBuilder builder = new StringBuilder("*****");
        for (int i = split.length + 1; i < 5; i++)
        {
            builder.append("-*****");
        }

        String s = builder.toString();
        if (s.isEmpty())
        {
            return PossibleInputs.empty();
        }

        String compl = split.length < 5 ? "-" : "";
        return PossibleInputs.empty().setCompletion(compl).setRest(s);
    }

    @Override
    public CustomCompleterResult onTabComplete(Completer completer)
    {
        return CustomCompleterResult.PASS;
    }

}
