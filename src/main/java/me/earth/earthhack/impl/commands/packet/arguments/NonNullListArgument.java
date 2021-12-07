package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import net.minecraft.util.NonNullList;

@SuppressWarnings("rawtypes")
public class NonNullListArgument extends AbstractArgument<NonNullList>
{
    public NonNullListArgument()
    {
        super(NonNullList.class);
    }

    @Override
    public NonNullList fromString(String argument) throws ArgParseException
    {
        return NonNullList.create();
    }

}
