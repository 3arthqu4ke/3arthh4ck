package me.earth.earthhack.impl.commands.packet;

import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.util.helpers.command.CustomCompleterResult;
import me.earth.earthhack.impl.util.mcp.MappingProvider;

public abstract class AbstractArgument<T> implements PacketArgument<T>
{
    protected final Class<? super T> type;

    /**
     * @param type should be the type of this Argument.
     *        bound <? super T> is only there for generics.
     */
    public AbstractArgument(Class<? super T> type)
    {
        this.type = type;
    }

    @Override
    public PossibleInputs getPossibleInputs(String argument)
    {
        if (argument == null || argument.isEmpty())
        {
            return new PossibleInputs("", "<" + getSimpleName() + ">");
        }

        if (TextUtil.startsWith(getSimpleName(), argument))
        {
            // So ppl feel like they are doing
            // something with dont care arguments
            return PossibleInputs.empty().setCompletion(
                TextUtil.substring(getSimpleName(), argument.length()));
        }

        return PossibleInputs.empty();
    }

    @Override
    public CustomCompleterResult onTabComplete(Completer completer)
    {
        return CustomCompleterResult.RETURN;
    }

    @Override
    public Class<? super T> getType()
    {
        return type;
    }

    @Override
    public String getSimpleName()
    {
        return MappingProvider.simpleName(type);
    }

}
