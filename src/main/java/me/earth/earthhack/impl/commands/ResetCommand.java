package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.abstracts.AbstractModuleCommand;
import me.earth.earthhack.impl.commands.gui.YesNoNonPausing;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.GuiScreen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ResetCommand extends AbstractModuleCommand implements Globals
{
    public ResetCommand()
    {
        super(new String[][]{{"reset"}, {"module"}, {"setting"}}, 1);
        CommandDescriptions.register(this,
                "Resets all settings of the module to their default values.");
    }

    @Override
    public void execute(String[] args)
    {
        if (args.length < 2)
        {
            ChatUtil.sendMessage(
                "Use this command to reset Modules and Settings.");
            return;
        }

        Module module = Managers.MODULES.getObject(args[1]);
        if (module == null)
        {
            ChatUtil.sendMessage(TextColor.RED + "Module " + TextColor.WHITE
                    + args[1] + TextColor.RED + " not found!");
            return;
        }

        if (args.length == 2)
        {
            Scheduler.getInstance().schedule(() ->
            {
                GuiScreen previous = mc.currentScreen;
                mc.displayGuiScreen(new YesNoNonPausing(
                        (result, id) ->
                        {
                            mc.displayGuiScreen(previous);
                            if (!result)
                            {
                                return;
                            }

                            for (Setting<?> setting : module.getSettings())
                            {
                                setting.reset();
                            }

                            ChatUtil.sendMessage(TextColor.GREEN + "Module "
                                    + TextColor.WHITE + module.getName()
                                    + TextColor.GREEN + " has been reset.");
                        },
                        "Do you really want to reset the Module "
                                + module.getName()
                                + "?",
                        "",
                        1337));
            });
        }
        else
        {
            List<Setting<?>> settings = new ArrayList<>(args.length - 2);
            for (int i = 2; i < args.length; i++)
            {
                Setting<?> setting = module.getSetting(args[i]);
                if (setting != null)
                {
                    settings.add(setting);
                }
                else
                {
                    ChatUtil.sendMessage(TextColor.RED
                            + "Could not find Setting "
                            + TextColor.WHITE
                            + args[i]
                            + TextColor.RED
                            + " in module "
                            + TextColor.WHITE
                            + module.getName()
                            + TextColor.RED
                            + ".");
                }
            }

            if (settings.isEmpty())
            {
                return;
            }

            StringBuilder settingString = new StringBuilder(TextColor.RED);
            settingString.append("Do you really want to reset the Setting");
            if (settings.size() > 1)
            {
                settingString.append("s ");
            }
            else
            {
                settingString.append(" ");
            }

            settingString.append(TextColor.WHITE);
            Iterator<Setting<?>> itr = settings.iterator();
            while (itr.hasNext())
            {
                Setting<?> s = itr.next();
                settingString.append(s.getName());
                if (itr.hasNext())
                {
                    settingString.append(TextColor.RED)
                                 .append(", ")
                                 .append(TextColor.WHITE);
                }
            }

            settingString.append(TextColor.RED)
                         .append(" in the module ")
                         .append(TextColor.WHITE)
                         .append(module.getName())
                         .append(TextColor.RED)
                         .append("?");

            Scheduler.getInstance().schedule(() ->
            {
                GuiScreen previous = mc.currentScreen;
                mc.displayGuiScreen(new YesNoNonPausing(
                    (result, id) ->
                    {
                        mc.displayGuiScreen(previous);
                        if (!result)
                        {
                            return;
                        }

                        for (Setting<?> setting : settings)
                        {
                            setting.reset();
                            ChatUtil.sendMessage(TextColor.RED
                                    + "Reset "
                                    + module.getName()
                                    + TextColor.RED
                                    + " - "
                                    + TextColor.WHITE
                                    + setting.getName()
                                    + TextColor.RED
                                    + ".");
                        }
                    },
                    settingString.toString(),
                    "",
                    1337));
            });
        }
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args)
    {
        PossibleInputs inputs = super.getPossibleInputs(args);
        if (args.length > 2)
        {
            Module module = Managers.MODULES.getObject(args[1]);
            if (module == null)
            {
                return inputs.setCompletion("")
                             .setRest(TextColor.RED + " not found.");
            }

            Setting<?> s = CommandUtil.getNameableStartingWith(
                                args[args.length - 1], module.getSettings());
            if (s == null)
            {
                return inputs.setCompletion("")
                             .setRest(TextColor.RED + " not found.");
            }
            else
            {
                return inputs.setCompletion(TextUtil.substring(s.getName(),
                                            args[args.length - 1].length()));
            }
        }

        return inputs;
    }

}
