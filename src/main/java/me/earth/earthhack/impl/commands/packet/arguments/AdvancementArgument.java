package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;

public class AdvancementArgument extends AbstractArgument<Advancement>
{
    public static final AdvancementManager MANAGER =
            new AdvancementManager(null);

    public AdvancementArgument()
    {
        super(Advancement.class);
    }

    @Override
    public Advancement fromString(String argument) throws ArgParseException
    {
        Advancement advancement = getAdvancementStartingWith(argument);
        if (advancement == null)
        {
            throw new ArgParseException(
                    "Couldn't parse Advancement from " + argument + "!");
        }

        return advancement;
    }

    @Override
    public PossibleInputs getPossibleInputs(String argument)
    {
        PossibleInputs inputs = PossibleInputs.empty();
        if (argument == null || argument.isEmpty())
        {
            return inputs.setRest("<Advancement>");
        }

        Advancement advancement = getAdvancementStartingWith(argument);
        if (advancement != null)
        {
            inputs.setCompletion(TextUtil.substring(
                    advancement.getId().toString(), argument.length()));
        }

        return inputs;
    }

    private Advancement getAdvancementStartingWith(String name)
    {
        for (Advancement advancement : MANAGER.getAdvancements())
        {
            if (TextUtil.startsWith(advancement.getId().toString(), name))
            {
                return advancement;
            }
        }

        return null;
    }

}
