package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.GuiChat;

public class LastCommand extends Command implements Globals
{
    public LastCommand()
    {
        super(new String[][]{{"last"}, {"execute"}});
    }

    @Override
    public void execute(String[] args)
    {
        String last = Managers.COMMANDS.getLastCommand();
        if (last == null)
        {
            ChatUtil.sendMessage(TextColor.RED + "There's no last command!");
            return;
        }

        if (args.length > 1 && "execute".equalsIgnoreCase(args[1]))
        {
            ChatUtil.sendMessage(TextColor.GREEN + "Executing last Command: "
                    + TextColor.AQUA + last + TextColor.GREEN + "!");
            Managers.COMMANDS.applyCommand(last);
            return;
        }

        Scheduler.getInstance().schedule(() ->
            mc.displayGuiScreen(new GuiChat(last)));
    }

}
