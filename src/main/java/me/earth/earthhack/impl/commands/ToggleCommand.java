package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.commands.abstracts.AbstractModuleCommand;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.text.ChatIDs;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;

public class ToggleCommand extends AbstractModuleCommand
{
    public ToggleCommand()
    {
        super(new String[][]{{"toggle"}, {"module"}, {"times"}}, 1);
        CommandDescriptions.register(this, "Toggle the specified module." +
                " If you specify a number you can toggle it multiple times." +
                " This can be useful to set the FakePlayer" +
                " to another position for example (t Fakeplayer 2).");
    }

    @Override
    public void execute(String[] args)
    {
        if (args.length > 1)
        {
            Module module = Managers.MODULES.getObject(args[1]);
            if (module != null)
            {
                int times = 1;
                if (args.length > 2)
                {
                    try
                    {
                        times = Integer.parseInt(args[2]);
                    }
                    catch (NumberFormatException ignored)
                    {
                        ChatUtil.sendMessage("<ToggleCommand> "
                                                + TextColor.RED
                                                + args[2]
                                                + " is not a valid number.");
                        return;
                    }
                }

                String color = module.isEnabled() && times % 2 == 0
                                || !module.isEnabled() && times % 2 != 0
                                ? TextColor.GREEN
                                : TextColor.RED;

                Managers.CHAT.sendDeleteMessage(color
                                                    + "Toggling "
                                                    + TextColor.WHITE
                                                    + TextColor.BOLD
                                                    + module.getDisplayName()
                                                    + color
                                                    + (times > 1
                                                        ? " " + times + "x."
                                                        : "."),
                                                module.getName(),
                                                ChatIDs.MODULE);
                int finalTimes = times;
                Scheduler.getInstance().schedule(() ->
                {
                    for (int i = 0; i < finalTimes; i++)
                    {
                        module.toggle();
                    }
                });
            }
            else
            {
                ChatUtil.sendMessage("<ToggleCommand> "
                                        + TextColor.RED
                                        + "Couldn't find "
                                        + args[1]
                                        + ".");
            }

            return;
        }

        ChatUtil.sendMessage("<ToggleCommand> "
                                + TextColor.RED
                                + "Usage is: "
                                + this.getFullUsage());
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args)
    {
        PossibleInputs inputs = super.getPossibleInputs(args);
        if (args.length > 1)
        {
            Module module = this.getModule(args, 1);
            if (module != null)
            {
                String enabled = module.isEnabled()
                                    ? " <Currently: Enabled>"
                                    : " <Currently: Disabled>";
                if (args.length > 2)
                {
                    try
                    {
                        int times = Integer.parseInt(args[2]);
                        return inputs.setCompletion("").setRest(
                                enabled
                                    + " "
                                    + "<Will be:"
                                    + (module.isEnabled() && times % 2 == 0
                                    || !module.isEnabled() && times % 2 != 0
                                    ? TextColor.GREEN + " Enabled"
                                    : TextColor.RED + " Disabled")
                                    + TextColor.WHITE
                                    + ">");
                    }
                    catch (NumberFormatException e)
                    {
                        return inputs.setCompletion("").setRest(TextColor.RED
                                + " <"
                                + args[2]
                                + " is not a number>");
                    }
                }

                return inputs.setCompletion(
                                TextUtil.substring(module.getName(),
                                args[1].length()))
                             .setRest(" <times> " + enabled);
            }

            return inputs.setCompletion("").setRest(TextColor.RED
                                                    + " not found");
        }

        return inputs;
    }

    @Override
    public Completer onTabComplete(Completer completer)
    {
        if (completer.getArgs().length == 1)
        {
            completer.setResult(Commands.getPrefix() + "toggle");
            return completer;
        }
        else if (completer.getArgs().length == 2)
        {
            Module module = CommandUtil.getNameableStartingWith(
                                            completer.getArgs()[1],
                                            Managers.MODULES.getRegistered());
            if (module != null)
            {
                completer.setResult(Commands.getPrefix()
                                    + completer.getArgs()[0]
                                    + " "
                                    + module.getName());
            }
        }

        return completer;
    }

}
