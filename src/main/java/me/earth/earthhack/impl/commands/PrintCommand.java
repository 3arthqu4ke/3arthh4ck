package me.earth.earthhack.impl.commands;

import me.earth.earthhack.impl.commands.abstracts.AbstractTextCommand;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;

public class PrintCommand extends AbstractTextCommand
{
    public PrintCommand()
    {
        super("print");
        CommandDescriptions.register(this,
                "Prints a message in chat, without sending it.");
    }

    @Override
    public void execute(String[] args)
    {
        if (args.length == 1)
        {
            ChatUtil.sendMessage("");
            return;
        }

        ChatUtil.sendMessage(CommandUtil.concatenate(args, 1));
    }

}
