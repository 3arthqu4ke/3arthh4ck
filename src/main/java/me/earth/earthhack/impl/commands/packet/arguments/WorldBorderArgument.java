package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import net.minecraft.world.border.WorldBorder;

public class WorldBorderArgument extends AbstractArgument<WorldBorder>
{
    public WorldBorderArgument()
    {
        super(WorldBorder.class);
    }

    @Override
    public WorldBorder fromString(String argument) throws ArgParseException
    {
        return new WorldBorder();
    }

}
