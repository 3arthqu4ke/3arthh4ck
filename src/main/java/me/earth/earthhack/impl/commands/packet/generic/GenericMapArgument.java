package me.earth.earthhack.impl.commands.packet.generic;

import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.impl.commands.packet.PacketArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GenericMapArgument<K, V, M extends Map<K, V>>
        extends GenericArgument<M>
{
    protected final PacketArgument<K> key;
    protected final PacketArgument<V> value;

    public GenericMapArgument(Class<? super M> type,
                              Constructor<?> ctr,
                              int argIndex,
                              PacketArgument<K> key,
                              PacketArgument<V> value)
    {
        super(type, ctr, argIndex);
        this.key = key;
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    protected M create()
    {
        return (M) new HashMap<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public M fromString(String argument) throws ArgParseException
    {
        String[] split = argument.split("]");
        if (split.length == 0)
        {
            return (M) Collections.EMPTY_MAP;
        }

        M map = create();
        for (String entry : split)
        {
            if (entry == null)
            {
                continue;
            }

            String[] keyValue = entry.split("\\)");
            if (keyValue.length != 2)
            {
                throw new ArgParseException(
                        "Couldn't parse " + entry + " to MapEntry!");
            }

            K k = key.fromString(keyValue[0]);
            V v = value.fromString(keyValue[1]);
            map.put(k, v);
        }

        return map;
    }

    @Override
    public PossibleInputs getPossibleInputs(String argument)
    {
        PossibleInputs inputs = PossibleInputs.empty();
        if (argument == null || argument.isEmpty())
        {
            return inputs.setRest("<Map:" + key.getSimpleName() + ")"
                                    + value.getSimpleName() + "]>");
        }

        String[] split = argument.split("]");
        if (split.length == 0)
        {
            return inputs.setRest("<Map:" + key.getSimpleName() + ")"
                    + value.getSimpleName() + "]>");
        }

        String[] last = split[split.length - 1].split("\\)");
        if (last.length == 0)
        {
            return inputs.setRest(key.getSimpleName() + ")"
                    + value.getSimpleName() + "]");
        }

        if (last.length == 1)
        {
            PossibleInputs keyInputs = key.getPossibleInputs(last[0]);
            return inputs.setCompletion(keyInputs.getCompletion() + ")");
        }

        if (last.length == 2)
        {
            PossibleInputs valueInputs = key.getPossibleInputs(last[1]);
            return inputs.setCompletion(valueInputs.getCompletion() + "]");
        }

        return inputs;
    }

}
