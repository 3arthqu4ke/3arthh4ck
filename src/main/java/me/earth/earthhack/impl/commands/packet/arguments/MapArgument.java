package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;

import java.util.Collections;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class MapArgument extends AbstractArgument<Map>
{
    public MapArgument()
    {
        super(Map.class);
    }

    @Override
    public Map fromString(String argument) throws ArgParseException
    {
        return Collections.EMPTY_MAP;
    }

}
