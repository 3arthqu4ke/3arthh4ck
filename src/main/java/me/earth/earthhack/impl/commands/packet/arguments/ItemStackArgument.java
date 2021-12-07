package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;

public class ItemStackArgument extends AbstractArgument<ItemStack>
{
    private static final ItemArgument ITEM_ARGUMENT = new ItemArgument();

    public ItemStackArgument()
    {
        super(ItemStack.class);
    }

    @Override
    public ItemStack fromString(String argument) throws ArgParseException
    {
        String[] args = argument.split(",");
        if (args.length == 0)
        {
            throw new ArgParseException("ItemStack was empty?");
        }

        Item item = ITEM_ARGUMENT.fromString(args[0]);
        int size = new ItemStack(item).getMaxStackSize();
        if (args.length > 1)
        {
            try
            {
                size = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException e)
            {
                throw new ArgParseException(
                        "Could not parse " + argument + " as ItemStack size!");
            }
        }

        int meta = 0;
        if (args.length > 2)
        {
            try
            {
                meta = Integer.parseInt(args[2]);
            }
            catch (NumberFormatException e)
            {
                throw new ArgParseException(
                        "Could not parse " + argument + " as ItemStack meta!");
            }
        }

        ItemStack stack = new ItemStack(item, size, meta);
        if (args.length > 3)
        {
            String conc = CommandUtil.concatenate(args, 3);

            try
            {
                stack.setTagCompound(JsonToNBT.getTagFromJson(conc));
            }
            catch (NBTException nbtexception)
            {
                throw new ArgParseException(
                        "Could not parse ItemStack NBT from " + conc + "!");
            }
        }

        return stack;
    }

    @Override
    public PossibleInputs getPossibleInputs(String argument)
    {
        PossibleInputs inputs = PossibleInputs.empty();
        if (argument == null || argument.isEmpty())
        {
            return inputs.setRest("<ItemStack:Item,Size,Meta,NBT>");
        }

        String[] args = argument.split(",");
        if (args.length == 1)
        {
            if (args[0].isEmpty())
            {
                return inputs.setRest("Item,Size,Meta,NBT");
            }

            inputs = ITEM_ARGUMENT.getPossibleInputs(args[0]);
            inputs.setCompletion(inputs.getCompletion() + ",");
            inputs.setRest(",Size,Meta,NBT");
        }

        if (args.length == 2)
        {
            return inputs.setCompletion(",").setRest(",Meta,NBT");
        }

        if (args.length == 3)
        {
            return inputs.setCompletion(",").setRest("NBT");
        }

        return inputs;
    }

}
