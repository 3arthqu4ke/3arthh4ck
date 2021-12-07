package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class PotionEffectArgument extends AbstractArgument<PotionEffect>
{
    private static final PotionArgument POTION_ARGUMENT = new PotionArgument();

    public PotionEffectArgument()
    {
        super(PotionEffect.class);
    }

    @Override
    public PotionEffect fromString(String argument) throws ArgParseException
    {
        String[] split = argument.split(",");
        if (split.length == 0)
        {
            throw new ArgParseException("PotionEffect was empty!");
        }

        Potion potion = POTION_ARGUMENT.fromString(split[0]);
        int time = 100;
        int lvl  = 1;

        if (split.length > 1)
        {
            try
            {
                time = Integer.parseInt(split[1]);
            }
            catch (NumberFormatException e)
            {
                throw new ArgParseException(
                    "Couldn't parse PotionEffect-Time from: " + argument + "!");
            }

            if (split.length > 2)
            {
                try
                {
                    lvl = Integer.parseInt(split[1]);
                }
                catch (NumberFormatException e)
                {
                    throw new ArgParseException(
                        "Couldn't parse PotionEffect-LvL from: "
                                + argument + "!");
                }
            }
        }

        return new PotionEffect(potion, time, lvl);
    }

    @Override
    public PossibleInputs getPossibleInputs(String argument)
    {
        PossibleInputs inputs = PossibleInputs.empty();
        if (argument == null || argument.isEmpty())
        {
            return inputs.setRest("<PotionEffect:Potion,time,lvl");
        }

        String[] split = argument.split(",");
        if (split.length == 1 && !split[0].isEmpty())
        {
            inputs = POTION_ARGUMENT.getPossibleInputs(split[0]);
            inputs.setCompletion(inputs.getCompletion() + ",");
            inputs.setRest("time,lvl");
            return inputs;
        }

        if (split.length == 2)
        {
            return inputs.setCompletion(",").setRest("lvl");
        }

        return inputs;
    }

}
