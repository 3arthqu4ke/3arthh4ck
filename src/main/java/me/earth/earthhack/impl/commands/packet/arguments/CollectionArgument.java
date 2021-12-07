package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;

import java.util.Collection;
import java.util.Collections;

@SuppressWarnings("rawtypes")
public class CollectionArgument extends AbstractArgument<Collection>
{
    public CollectionArgument()
    {
        super(Collection.class);
    }

    @Override
    public Collection fromString(String argument) throws ArgParseException
    {
        return Collections.EMPTY_LIST;
    }

}
