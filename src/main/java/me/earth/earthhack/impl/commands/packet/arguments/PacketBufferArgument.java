package me.earth.earthhack.impl.commands.packet.arguments;

import io.netty.buffer.Unpooled;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import net.minecraft.network.PacketBuffer;

public class PacketBufferArgument extends AbstractArgument<PacketBuffer>
{
    public PacketBufferArgument()
    {
        super(PacketBuffer.class);
    }

    @Override
    public PacketBuffer fromString(String argument) throws ArgParseException
    {
        if (argument.equalsIgnoreCase("empty"))
        {
            return new PacketBuffer(Unpooled.buffer());
        }

        String[] split = split(argument);
        byte[] b = new byte[split.length];

        for (int i = 0; i < split.length; i++)
        {
            String s = split[i];

            try
            {
                byte parsed = Byte.parseByte(s, 16);
                b[i] = parsed;
            }
            catch (Exception e)
            {
                throw new ArgParseException("Could not parse byte: " + s + "!");
            }
        }

        return new PacketBuffer(Unpooled.buffer().writeBytes(b));
    }

    @Override
    public PossibleInputs getPossibleInputs(String argument)
    {
        if (argument != null
                && !argument.isEmpty()
                && TextUtil.startsWith("empty", argument))
        {
            return new PossibleInputs(
                    TextUtil.substring("empty", argument.length()), "");
        }

        return super.getPossibleInputs(argument);
    }

    private static String[] split(String string)
    {
        String[] result = new String[(string.length() + 1) / 2];
        // saw a regex for this which apparently
        // but it doesnt work on certain machines
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < string.length(); i++)
        {
            char c = string.charAt(i);
            builder.append(c);
            if ((i + 1) % 2 == 0)
            {
                result[i / 2] = builder.toString();
                builder = new StringBuilder();
            }
        }

        String last = builder.toString();
        if (!last.isEmpty())
        {
            result[result.length - 1] = last;
        }

        return result;
    }

}
