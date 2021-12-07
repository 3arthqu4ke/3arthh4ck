package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import me.earth.earthhack.impl.util.helpers.addable.ItemAddingModule;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;

public class BlockArgument extends AbstractArgument<Block>
{
    public BlockArgument()
    {
        super(Block.class);
    }

    @Override
    public Block fromString(String argument) throws ArgParseException
    {
        Item item = ItemAddingModule.getItemStartingWith(argument,
                                                i -> i instanceof ItemBlock);
        Block block;
        if (item == null)
        {
            block = Block.getBlockFromName(argument);
            if (block == null)
            {
                try
                {
                    int id = Integer.parseInt(argument);
                    block = Block.getBlockById(id);
                }
                catch (NumberFormatException e)
                {
                    block = null;
                }

                if (block == null)
                {
                    throw new ArgParseException(
                            "Couldn't parse Block from " + argument + "!");
                }
            }
        }
        else if (item instanceof ItemBlock)
        {
            block = ((ItemBlock) item).getBlock();
        }
        else
        {
            throw new IllegalStateException("Item wasn't ItemBlock!");
        }

        return block;
    }

    @Override
    public PossibleInputs getPossibleInputs(String arg)
    {
        if (arg == null || arg.isEmpty())
        {
            return PossibleInputs.empty().setRest("<block>");
        }

        PossibleInputs inputs = PossibleInputs.empty();
        String s = ItemAddingModule.getItemStartingWithDefault(arg,
                                                i -> i instanceof ItemBlock);
        if (s != null)
        {
            return inputs.setCompletion(TextUtil.substring(s, arg.length()));
        }

        for (ResourceLocation location : Block.REGISTRY.getKeys())
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
