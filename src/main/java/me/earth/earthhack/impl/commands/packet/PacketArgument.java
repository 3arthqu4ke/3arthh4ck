package me.earth.earthhack.impl.commands.packet;

import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import me.earth.earthhack.impl.util.helpers.command.CustomCompleterResult;

public interface PacketArgument<T>
{
    T fromString(String argument) throws ArgParseException;

    /**
     * @param argument can be null!
     * @return a completion for the argument, or the args if argument is null.
     */
    PossibleInputs getPossibleInputs(String argument);

    CustomCompleterResult onTabComplete(Completer completer);

    Class<? super T> getType();

    String getSimpleName();

}
