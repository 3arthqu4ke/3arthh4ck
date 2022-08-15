package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.commands.abstracts.AbstractStackCommand;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.helpers.addable.ItemAddingModule;
import me.earth.earthhack.impl.util.text.ChatIDs;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;

public class GiveCommand extends AbstractStackCommand
{
    private boolean local = true;
    private int amount;
    private ItemStack stack;
    private Item item;

    public GiveCommand()
    {
        super(new String[][]{{"give"},
                             {"amount", "local"},
                             {"item/block"}}, "");
        CommandDescriptions.register(this, "Gives you an Item.");
    }

    @Override
    public void execute(String[] args)
    {
        if (args.length == 1)
        {
            ChatUtil.sendMessage("Use this command to give yourself an item.");
            return;
        }
        else if (args.length == 2)
        {
            if (args[1].equalsIgnoreCase("local"))
            {
                local = !local;
                if (local)
                {
                    Managers.CHAT.sendDeleteMessage(TextColor.GREEN
                            + "The Give command now uses localized names,"
                            + " that means that you can use normal names like "
                            + TextColor.AQUA
                            + "Ender Chest"
                            + TextColor.GREEN
                            + " now.",
                        "giveCommand",
                        ChatIDs.COMMAND);
                }
                else
                {
                    Managers.CHAT.sendDeleteMessage(TextColor.GREEN
                            + "The Give command now uses ids,"
                            + " that means that you need to use names like "
                            + TextColor.AQUA
                            + "minecraft:apple"
                            + TextColor.GREEN
                            + " or ids now.",
                        "giveCommand",
                        ChatIDs.COMMAND);
                }

                return;
            }

            ChatUtil.sendMessage("Please specify an item.");
            return;
        }

        int amount;
        try
        {
            amount = Integer.parseInt(args[1]);
        }
        catch (Exception e)
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "Could not parse "
                    + TextColor.WHITE
                    + args[1]
                    + TextColor.RED
                    + " to a number!");
            return;
        }

        String conc = CommandUtil.concatenate(args, 2);

        stack = null;
        if (Arrays.stream(args).anyMatch("-current"::equalsIgnoreCase))
        {
            stack = mc.player.inventory.getStackInSlot(
                mc.player.inventory.currentItem);
        }
        else if (local)
        {
            item = ItemAddingModule.getItemStartingWith(conc, i -> true);
        }
        else
        {
            item = Item.getByNameOrId(conc);
        }

        if (item == null && stack == null)
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "Could not find item "
                    + TextColor.WHITE
                    + conc
                    + TextColor.RED
                    + "! Give command currently uses "
                    + (local ? "localized names." : "ids."));
            return;
        }

        this.amount = amount;
        this.stackName = conc;
        super.execute(args);
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args)
    {
        if (args.length <= 1)
        {
            return super.getPossibleInputs(args);
        }

        PossibleInputs inputs = PossibleInputs.empty();
        if ("local".startsWith(args[1].toLowerCase()))
        {
            if (args.length > 2)
            {
                return inputs;
            }

            return inputs.setCompletion(
                    TextUtil.substring("local", args[1].length()));
        }

        if (args.length == 2)
        {
            if (args[1].isEmpty())
            {
                return inputs.setRest("<amount/local> <item/block>");
            }

            return inputs.setRest(" <item/block>");
        }

        String conc = CommandUtil.concatenate(args, 2);
        if (conc.isEmpty())
        {
            return inputs.setRest(" <item/block>");
        }

        if (local)
        {
            String s = ItemAddingModule.getItemStartingWithDefault(conc,
                                                                   i -> true);
            if (s != null)
            {
                inputs.setCompletion(TextUtil.substring(s, conc.length()));
            }
        }
        else
        {
            if (args.length == 3 && Character.isDigit(conc.charAt(0)))
            {
                try
                {
                    int id = Integer.parseInt(args[2]);
                    Item item = Item.getItemById(id);
                    //noinspection ConstantConditions
                    if (item != null)
                    {
                        return inputs.setRest(" <"
                            + item.getItemStackDisplayName(new ItemStack(item))
                            + ">");
                    }
                }
                catch (Exception ignored) { }
            }

            for (ResourceLocation location : Item.REGISTRY.getKeys())
            {
                if (TextUtil.startsWith(location.toString(), conc))
                {
                    return inputs.setCompletion(
                        TextUtil.substring(location.toString(), conc.length()));
                }
            }
        }

        return inputs;
    }

    @Override
    protected ItemStack getStack(String[] args)
    {
        if (this.stack != null)
        {
            ItemStack stack = this.stack.copy();
            stack.setCount(amount);
            return stack;
        }

        if (item == null)
        {
            ItemStack stack = new ItemStack(Items.WRITTEN_BOOK);
            stack.setStackDisplayName(TextColor.RED + "ERROR");
            return stack;
        }

        ItemStack stack = new ItemStack(item);
        stack.setCount(amount);
        return stack;
    }

}
