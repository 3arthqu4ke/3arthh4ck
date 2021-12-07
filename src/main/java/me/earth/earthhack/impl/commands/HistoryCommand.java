package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.lookup.LookUp;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.text.ChatIDs;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.earthhack.impl.util.thread.LookUpUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class HistoryCommand extends Command implements Globals
{
    public HistoryCommand()
    {
        super(new String[][]{{"history"}, {"name"}});
        CommandDescriptions.register(this, "Gets the Namehistory of players.");
    }

    @Override
    public void execute(String[] args)
    {
        if (args.length == 1)
        {
            ChatUtil.sendMessage(TextColor.RED + "Please specify a name.");
        }
        else if (args.length > 1)
        {
            Managers.CHAT.sendDeleteMessage(TextColor.AQUA
                                            + "Looking up "
                                            + TextColor.WHITE
                                            + args[1]
                                            + "'s "
                                            + TextColor.AQUA
                                            + "name history"
                                            + ".",
                                            args[1],
                                            ChatIDs.COMMAND);
            Managers.LOOK_UP.doLookUp(
                new LookUp(LookUp.Type.HISTORY, args[1])
                {
                    @Override
                    public void onSuccess()
                    {
                        mc.addScheduledTask(() ->
                        {
                            boolean first = true;
                            ChatUtil.sendMessage("");

                            for (Map.Entry<Date, String> entry :
                                                            names.entrySet())
                            {
                                String dateString =
                                    entry.getKey().getTime() == 0
                                        ? ""
                                        : new SimpleDateFormat(
                                                        "dd.MM.yyyy, HH:mm:ss")
                                            .format(entry.getKey());
                                if (first)
                                {
                                    Managers.CHAT
                                        .sendDeleteMessage(
                                                TextColor.BOLD
                                                     + entry.getValue()
                                                     + TextColor.GRAY
                                                     + " - "
                                                     + TextColor.GOLD
                                                     + dateString,
                                                args[1],
                                                ChatIDs.COMMAND);

                                    first = false;
                                    continue;
                                }

                                ChatUtil.sendMessage(entry.getValue()
                                                    + TextColor.GRAY
                                                    + " - "
                                                    + TextColor.GOLD
                                                    + dateString);
                            }

                            ChatUtil.sendMessage("");
                        });
                    }

                    @Override
                    public void onFailure()
                    {
                        Managers.CHAT.sendDeleteMessage(
                                                TextColor.RED
                                                         + "Failed to lookup "
                                                         + TextColor.WHITE
                                                         + args[1],
                                                args[1],
                                                ChatIDs.COMMAND);
                    }
                });
        }
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
            String player = LookUpUtil.findNextPlayerName(args[1]);
            return inputs.setCompletion(player == null ? "" :
                            TextUtil.substring(player, args[1].length()))
                         .setRest("");
        }

        return inputs;
    }

    @Override
    public Completer onTabComplete(Completer completer)
    {
        if (completer.getArgs().length == 1)
        {
            if (completer.getArgs()[0].equalsIgnoreCase("history"))
            {
                completer.setMcComplete(true);
            }
            else
            {
                completer.setResult(Commands.getPrefix()
                                    + "history");
            }
        }
        else if (completer.getArgs().length == 2)
        {
            String player =
                    LookUpUtil.findNextPlayerName(completer.getArgs()[1]);

            if (player == null
                    || player.equalsIgnoreCase(completer.getArgs()[1]))
            {
                completer.setMcComplete(true);
            }
            else
            {
                completer.setResult(Commands.getPrefix()
                                    + "history "
                                    + player);
            }
        }

        return completer;
    }

}
