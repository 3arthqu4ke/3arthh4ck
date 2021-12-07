package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import net.minecraft.world.WorldType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class WorldTypeArgument extends AbstractArgument<WorldType>
{
    public WorldTypeArgument()
    {
        super(WorldType.class);
    }

    @Override
    public WorldType fromString(String argument) throws ArgParseException
    {
        String[] split = argument.split(",");
        if (split.length < 2 || split.length > 3)
        {
            throw new ArgParseException(
                    "Expected 2-3 arguments for WorldType, but found "
                            + split.length
                            + "!");
        }

        int id = (int) ArgParseException.tryLong(split[0], "WorldType-ID");
        int version = 0;
        if (split.length == 3)
        {
            version = (int) ArgParseException.tryLong(split[2], "Version");
        }

        try
        {
            Constructor<WorldType> ctr = WorldType.class.getDeclaredConstructor(
                                            int.class, String.class, int.class);
            return ctr.newInstance(id, split[1], version);
        }
        catch (NoSuchMethodException
                | InvocationTargetException
                | InstantiationException
                | IllegalAccessException e)
        {
            e.printStackTrace();
            throw new ArgParseException(
                    "This definitely shouldn't happen, contact the dev!");
        }
    }

    @Override
    public PossibleInputs getPossibleInputs(String argument)
    {
        PossibleInputs inputs = PossibleInputs.empty();
        if (argument == null || argument.isEmpty())
        {
            return inputs.setRest("<WorldType:id,name,version>");
        }

        String[] split = argument.split(",");
        switch (split.length)
        {
            case 0:
                return inputs.setRest("<WorldType:id,name,version>");
            case 1:
                return inputs.setCompletion(",").setRest("name,version");
            case 2:
                return inputs.setCompletion(",").setRest("version");
            default:
        }

        return inputs;
    }

}
