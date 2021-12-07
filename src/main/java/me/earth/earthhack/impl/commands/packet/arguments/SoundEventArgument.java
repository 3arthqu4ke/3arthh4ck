package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import net.minecraft.util.SoundEvent;

public class SoundEventArgument extends AbstractArgument<SoundEvent>
{
    public SoundEventArgument()
    {
        super(SoundEvent.class);
    }

    @Override
    public SoundEvent fromString(String argument) throws ArgParseException
    {
        SoundEvent event = getSoundStartingWith(argument);
        if (event == null)
        {
            try
            {
                int id = Integer.parseInt(argument);
                event = SoundEvent.REGISTRY.getObjectById(id);
            }
            catch (NumberFormatException e)
            {
                event = null;
            }

            if (event == null)
            {
                throw new ArgParseException("Could not parse SoundEvent from" +
                        " name or id: " + argument + "!");
            }
        }

        return event;
    }

    @Override
    public PossibleInputs getPossibleInputs(String arg)
    {
        if (arg == null || arg.isEmpty())
        {
            return PossibleInputs.empty().setRest("<SoundEvent>");
        }

        PossibleInputs inputs = PossibleInputs.empty();
        SoundEvent event = getSoundStartingWith(arg);
        if (event != null)
        {
            return inputs.setCompletion(TextUtil.substring(
                    event.getSoundName().toString(),
                    arg.length()));
        }

        return inputs;
    }

    public static SoundEvent getSoundStartingWith(String argument)
    {
        for (SoundEvent event : SoundEvent.REGISTRY)
        {
            String name = event.getSoundName().toString();
            if (TextUtil.startsWith(name, argument))
            {
                return event;
            }
        }

        return null;
    }

}
