package me.earth.earthhack.impl.commands.hidden;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.text.ChatIDs;
import net.minecraft.client.gui.GuiChat;

import java.util.ArrayList;
import java.util.List;

public class HSettingCommand extends Command implements Globals
{
    public HSettingCommand()
    {
        super(new String[][]{{"hiddensetting"}, {"module"}, {"setting"}}, true);
    }

    @Override
    public void execute(String[] args)
    {
        if (args.length > 2)
        {
            Module module = Managers.MODULES.getObject(args[1]);
            if (module != null)
            {
                Setting<?> setting = module.getSetting(args[2]);
                if (setting != null)
                {
                    if (args.length == 3)
                    {
                        String command = getCommand(setting, module);
                        Scheduler.getInstance().schedule(() ->
                                mc.displayGuiScreen(new GuiChat(command)));
                    }
                    else
                    {
                        update(setting, module, args, false);
                    }
                }
            }
        }
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args)
    {
        return PossibleInputs.empty();
    }

    @Override
    public Completer onTabComplete(Completer completer)
    {
        completer.setMcComplete(true);
        return completer;
    }

    public static String getCommand(Setting<?> setting, Module module)
    {
        return Commands.getPrefix()
                + module.getName()
                + " \"" + setting.getName() + "\" ";
    }

    public static void update(Setting<?> setting,
                              Module module,
                              String[] args,
                              boolean ignoreArgs)
    {
        if (setting.getName().equals("Enabled")
                && setting instanceof BooleanSetting)
        {
            if (ignoreArgs)
            {
                return;
            }

            boolean bool = Boolean.parseBoolean(args[3]);
            if (bool)
            {
                module.enable();
            }
            else
            {
                module.disable();
            }

            return;
        }

        List<String> settingNames =
                new ArrayList<>(3 + module.getSettings().size());

        settingNames.add(module.getName() + "1");
        settingNames.add(module.getName() + "2");
        settingNames.add(module.getName() + "3");

        for (Setting<?> s : module.getSettings())
        {
            settingNames.add(s.getName() + module.getName());
        }

        if (!ignoreArgs)
        {
            setting.fromString(args[3]);
        }

        //TODO: ok this is cool and works but what if the
        // ChatGui is displayed and we add a setting etc?
        if (module.getSettings().size()
                != settingNames.size() - 3)
        {
            settingNames.forEach(n ->
                    Managers.CHAT
                            .deleteMessage(n, ChatIDs.CHAT_GUI));

            Scheduler.getInstance().schedule(() ->
                    Managers
                            .COMMANDS
                            .applyCommand(HListSettingCommand
                                    .create(module)));
        }
    }

}
