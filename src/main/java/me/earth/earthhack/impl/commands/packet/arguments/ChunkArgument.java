package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import me.earth.earthhack.impl.util.helpers.command.CustomCompleterResult;
import net.minecraft.world.chunk.Chunk;

public class ChunkArgument extends AbstractArgument<Chunk> implements Globals
{
    protected static final String[] REST = {"z", "z", ""};

    public ChunkArgument()
    {
        super(Chunk.class);
    }

    @Override
    public Chunk fromString(String argument) throws ArgParseException
    {
        if (mc.world == null)
        {
            throw new ArgParseException("Minecraft.world is null!");
        }

        if (argument.equalsIgnoreCase("ORIGIN"))
        {
            return new Chunk(mc.world, 0, 0);
        }

        String[] split = argument.split(",");
        if (split.length != 2)
        {
            throw new ArgParseException("Chunk takes 2 arguments!");
        }

        try
        {
            int x = (int) Long.parseLong(split[0]);
            int z = (int) Long.parseLong(split[1]);
            return new Chunk(mc.world, x, z);
        }
        catch (Exception e)
        {
            throw new ArgParseException(
                    "Could not parse " + argument + " to Chunk!");
        }
    }

    @Override
    public PossibleInputs getPossibleInputs(String argument)
    {
        if (argument == null || argument.isEmpty())
        {
            return new PossibleInputs("", "<Chunk>");
        }

        PossibleInputs inputs = PossibleInputs.empty();
        if (argument.toLowerCase().startsWith("origin"))
        {
            return inputs.setCompletion(
                    TextUtil.substring("ORIGIN", argument.length()));
        }

        String[] split = argument.split(",");
        if (split.length > 2)
        {
            return inputs;
        }

        if (split.length < 2)
        {
            inputs.setCompletion(",");
        }


        return inputs.setRest(REST[split.length]);
    }

    @Override
    public CustomCompleterResult onTabComplete(Completer completer)
    {
        return CustomCompleterResult.PASS;
    }

}
