package me.earth.earthhack.impl.util.helpers.command;

import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;

public interface CustomCommandModule
{
    String[] DEFAULT_ARGS = {};
    
    default boolean execute(String[] args)
    {
        return false;
    }

    default boolean getInput(String[] args, PossibleInputs inputs)
    {
        return false;
    }

    default CustomCompleterResult complete(Completer completer)
    {
        return CustomCompleterResult.PASS;
    }

    default String[] getArgs()
    {
        return DEFAULT_ARGS;
    }

}
