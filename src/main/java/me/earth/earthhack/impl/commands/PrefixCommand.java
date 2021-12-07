package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.text.ChatIDs;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;

public class PrefixCommand extends Command
{
    public PrefixCommand()
    {
        super(new String[][]{{"prefix"}, {"symbol"}});
        CommandDescriptions.register(this, "Manage the clients prefix.");
    }

    @Override
    public void execute(String[] args)
    {
        if (args.length > 1)
        {
            String prefix = args[1];
            Commands.setPrefix(prefix);
            Managers.CHAT.sendDeleteMessage(
                        "Prefix has been set to: "
                            + TextColor.AQUA
                            + prefix
                            + TextColor.WHITE
                            + ".",
                    "Prefix",
                    ChatIDs.COMMAND);
        }
        else
        {
            ChatUtil.sendMessage(TextColor.RED + "Please specify a prefix.");
        }
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

    @Override
    public Completer onTabComplete(Completer completer)
    {
        if (completer.getArgs().length > 1
                || completer.getArgs()[0].equalsIgnoreCase("prefix"))
        {
            return completer;
        }

        return super.onTabComplete(completer);
    }

}
