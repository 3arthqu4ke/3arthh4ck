package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import me.earth.earthhack.impl.util.helpers.addable.ItemAddingModule;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ItemArgument extends AbstractArgument<Item>
{
    public ItemArgument()
    {
        super(Item.class);
    }

    @Override
    public Item fromString(String argument) throws ArgParseException
    {
        Item item = ItemAddingModule.getItemStartingWith(argument, i -> true);
        if (item != null)
        {
            return item;
        }

        item = Item.getByNameOrId(argument);
        if (item == null)
        {
            throw new ArgParseException(
                    "Could not parse Item from " + argument + ".");
        }

        return item;
    }

    @Override
    public PossibleInputs getPossibleInputs(String arg)
    {
        if (arg == null || arg.isEmpty())
        {
            return PossibleInputs.empty().setRest("<item>");
        }

        PossibleInputs inputs = PossibleInputs.empty();
        String s = ItemAddingModule.getItemStartingWithDefault(arg, i -> true);
        if (s != null)
        {
            return inputs.setCompletion(TextUtil.substring(s, arg.length()));
        }

        for (ResourceLocation location : Item.REGISTRY.getKeys())
        {
            if (TextUtil.startsWith(location.toString(), arg))
            {
                return inputs.setCompletion(
                        TextUtil.substring(location.toString(), arg.length()));
            }
        }

        return PossibleInputs.empty();
    }

}
