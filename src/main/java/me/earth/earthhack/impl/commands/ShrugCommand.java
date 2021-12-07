package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;

public class ShrugCommand extends Command implements Globals
{
    public static final String SHRUG = "\u00AF\\_(\u30C4)_/\u00AF";

    public ShrugCommand()
    {
        super(new String[][]{{"shrug"}, {"message"}});
    }

    @Override
    public void execute(String[] args)
    {
        if (args.length == 1)
        {
            sendMessage(SHRUG);
            return;
        }

        String message = CommandUtil.concatenate(args, 1);
        if (!message.contains(":shrug:"))
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "Use :shrug: to specify parts of the message"
                    + " that should be replaced with the shrug emoji!");
            return;
        }

        sendMessage(message.replace(":shrug:", SHRUG));
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args)
    {
        if (args.length > 1)
        {
            return PossibleInputs.empty();
        }

        return super.getPossibleInputs(args);
    }

    private void sendMessage(String message)
    {
        if (mc.player == null)
        {
            ChatUtil.sendMessage(message);
        }
        else
        {
            mc.player.sendChatMessage(message);
        }
    }

}
