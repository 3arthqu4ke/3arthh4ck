package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import net.minecraft.util.math.BlockPos;

public class BlockPosArgument extends AbstractPositionArgument<BlockPos>
{
    public BlockPosArgument()
    {
        super("BlockPos", BlockPos.class);
    }

    @Override
    public BlockPos fromString(String argument) throws ArgParseException
    {
        if (argument.equalsIgnoreCase("ORIGIN"))
        {
            return BlockPos.ORIGIN;
        }

        String[] split = argument.split(",");
        if (split.length != 3)
        {
            throw new ArgParseException("BlockPos takes 3 arguments!");
        }

        try
        {
            int x = (int) Long.parseLong(split[0]);
            int y = (int) Long.parseLong(split[1]);
            int z = (int) Long.parseLong(split[2]);
            return new BlockPos(x, y, z);
        }
        catch (Exception e)
        {
            throw new ArgParseException(
                    "Could not parse " + argument + " to BlockPos!");
        }
    }

}
