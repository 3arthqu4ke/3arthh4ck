package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.PacketArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import net.minecraft.world.storage.MapDecoration;

public class MapDecorationArgument extends AbstractArgument<MapDecoration>
{
    private static final PacketArgument<MapDecoration.Type> TYPE =
            new EnumArgument<>(MapDecoration.Type.class);
    private static final PacketArgument<Byte> BYTE =
            new ByteArgument();

    public MapDecorationArgument()
    {
        super(MapDecoration.class);
    }

    @Override
    public MapDecoration fromString(String argument) throws ArgParseException
    {
        String[] split = argument.split(",");
        if (split.length != 4)
        {
            throw new ArgParseException(
                "MapDecoration takes 4 arguments, but found: " + split.length);
        }

        MapDecoration.Type type = TYPE.fromString(split[0]);
        byte x = parseByte(split[0], "MapDecoration-X");
        byte y = parseByte(split[1], "MapDecoration-Y");
        byte rotation = parseByte(split[2], "MapDecoration-Rotation");

        return new MapDecoration(type, x, y, rotation);
    }

    @Override
    public PossibleInputs getPossibleInputs(String argument)
    {
        PossibleInputs inputs = PossibleInputs.empty();
        if (argument == null || argument.isEmpty())
        {
            return inputs.setRest("<MapDecoration:type,x,y,rotation");
        }

        String[] split = argument.split(",");
        switch (split.length)
        {
            case 0:
                return inputs.setRest("<MapDecoration:type,x,y,rotation");
            case 1:
                inputs = TYPE.getPossibleInputs(split[0]);
                return inputs.setCompletion(inputs.getCompletion() + ",")
                             .setRest("x,y,rotation");
            case 2:
                return inputs.setCompletion(",").setRest("y,rotation");
            case 3:
                return inputs.setCompletion(",").setRest("rotation");
            default:
        }


        return inputs;
    }

    private byte parseByte(String string, String message)
            throws ArgParseException
    {
        try
        {
            return BYTE.fromString(string);
        }
        catch (ArgParseException e)
        {
            throw new ArgParseException(
                    "Couldn't parse " + message + " from " + string + "!");
        }
    }

}
