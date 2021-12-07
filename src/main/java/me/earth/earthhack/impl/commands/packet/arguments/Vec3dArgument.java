package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import net.minecraft.util.math.Vec3d;

public class Vec3dArgument extends AbstractPositionArgument<Vec3d>
{
    public Vec3dArgument()
    {
        super("Vec3d", Vec3d.class);
    }

    @Override
    public Vec3d fromString(String argument) throws ArgParseException
    {
        if (argument.equalsIgnoreCase("ORIGIN"))
        {
            return new Vec3d(0, 0, 0);
        }

        String[] split = argument.split(" ");
        if (split.length != 3)
        {
            throw new ArgParseException("Vec3d takes 3 arguments!");
        }

        try
        {
            double x = Double.parseDouble(split[0]);
            double y = Double.parseDouble(split[1]);
            double z = Double.parseDouble(split[2]);
            return new Vec3d(x, y, z);
        }
        catch (Exception e)
        {
            throw new ArgParseException(
                    "Could not parse " + argument + " to Vec3d!");
        }
    }

}
