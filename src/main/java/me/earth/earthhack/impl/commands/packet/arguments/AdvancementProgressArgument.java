package me.earth.earthhack.impl.commands.packet.arguments;

import com.google.gson.JsonElement;
import me.earth.earthhack.api.config.Jsonable;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import net.minecraft.advancements.AdvancementProgress;

public class AdvancementProgressArgument
        extends AbstractArgument<AdvancementProgress>
{
    private static final AdvancementProgress.Serializer SERIALIZER =
            new AdvancementProgress.Serializer();


    public AdvancementProgressArgument()
    {
        super(AdvancementProgress.class);
    }

    @Override
    public AdvancementProgress fromString(String argument)
            throws ArgParseException
    {
        JsonElement element = Jsonable.parse(argument);
        if (element == null)
        {
            throw new ArgParseException("Couldn't parse " + argument
                    + " to Json! Check the log.");
        }

        try
        {
            // type and context can be null
            //noinspection ConstantConditions
            return SERIALIZER.deserialize(element, null, null);
        }
        catch (Exception e)
        {
            throw new ArgParseException(
                    "Couldn't read AdvancementProgress from Json: "
                            + element + ": " + e.getMessage());
        }
    }

}
