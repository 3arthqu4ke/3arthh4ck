package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.tooltips.ToolTips;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;

public class PeekCommand extends Command implements Globals
{
    private static final ModuleCache<ToolTips> TOOLTIPS =
     Caches.getModule(ToolTips.class);
    private static final SettingCache<Boolean, BooleanSetting, ToolTips> SPY =
     Caches.getSetting(ToolTips.class, BooleanSetting.class, "ShulkerSpy", true);

    public PeekCommand()
    {
        super(new String[][]{{"peek"}, {"player"}});
        CommandDescriptions.register(this, "Type peek to view the shulker" +
                " you are currently holding. Specify a player name to view" +
                " that players last held shulker" +
                " (The Tooltips module needs to be enabled for this).");
    }

    @Override
    public void execute(String[] args)
    {
        if (!TOOLTIPS.isPresent())
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "<ToolTips> A critical error occurred!"
                    + " Please contact the dev.");
            return;
        }

        if (mc.player == null)
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "You need to be ingame to use this command!");
            return;
        }

        if (args.length == 1
                || args[1].equalsIgnoreCase(mc.getSession()
                                              .getProfile()
                                              .getName()))
        {
            ItemStack stack = mc.player.getHeldItemMainhand();
            if (!(stack.getItem() instanceof ItemShulkerBox))
            {
                stack = mc.player.getHeldItemOffhand();
                if (!(stack.getItem() instanceof ItemShulkerBox))
                {
                    stack = TOOLTIPS.get()
                            .getStack(mc.getSession().getProfile().getName());
                }
            }

            if (stack != null && stack.getItem() instanceof ItemShulkerBox)
            {
                ItemStack finalStack = stack;
                Scheduler.getInstance().schedule(() ->
                    TOOLTIPS.get().displayInventory(finalStack, null));
                return;
            }

            ChatUtil.sendMessage(TextColor.RED
                    + "You need to hold a Shulker for this.");
            return;
        }

        if (!TOOLTIPS.isEnabled()
                || !SPY.getValue())
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "Please enable ToolTips and ToolTips"
                    + " - ShulkerSpy in order to view other players.");
            return;
        }

        String name = args[1];
        ItemStack stack = TOOLTIPS.get().getStack(name);
        if (stack == null)
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "No Shulker found for "
                    + name
                    + ".");
            return;
        }

        Scheduler.getInstance().schedule(() ->
            TOOLTIPS.get().displayInventory(stack, name));
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args)
    {
        PossibleInputs inputs = super.getPossibleInputs(args);
        if (args.length == 1)
        {
            return inputs;
        }
        else if (args.length == 2)
        {
            String name = args[1];
            String full = findName(name);
            if (full == null)
            {
                return inputs.setCompletion("")
                             .setRest(TextColor.RED + " no data found.");
            }

            return inputs.setCompletion(TextUtil.substring(
                                                    full,
                                                    name.length()));
        }

        return PossibleInputs.empty();
    }

    private String findName(String input)
    {
        for (String string : TOOLTIPS.get().getPlayers())
        {
            if (string.startsWith(input.toLowerCase()))
            {
                return string;
            }
        }

        return null;
    }

}
