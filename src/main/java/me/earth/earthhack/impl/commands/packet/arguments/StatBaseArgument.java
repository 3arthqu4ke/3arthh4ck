package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import net.minecraft.stats.StatBase;
import net.minecraft.util.text.TextComponentString;

public class StatBaseArgument extends AbstractArgument<StatBase>
{
    public StatBaseArgument()
    {
        super(StatBase.class);
    }

    @Override
    public StatBase fromString(String argument) throws ArgParseException
    {
        return new StatBase(argument,
                new TextComponentString("dummy-statbase-component"));
    }

}
