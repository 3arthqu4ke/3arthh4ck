package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.register.Registrable;
import me.earth.earthhack.api.register.exception.CantUnregisterException;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.event.SettingResult;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.commands.hidden.HListSettingCommand;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.client.ModuleManager;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.helpers.command.CustomCommandModule;
import me.earth.earthhack.impl.util.text.ChatIDs;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;

import java.util.Optional;

public class ModuleCommand extends Command implements Registrable
{
    private final ModuleManager moduleManager;

    public ModuleCommand()
    {
        this(Managers.MODULES);
        CommandDescriptions.register(this, "Type only the name of the module" +
            " to open the chatgui with its settings. You can also" +
            " specify one of the modules settings and set it to a value.");
    }

    public ModuleCommand(ModuleManager moduleManager)
    {
        super(new String[][]{{"module"}, {"setting"}, {"value"}});
        this.moduleManager = moduleManager;
    }

    @Override
    public void onUnRegister() throws CantUnregisterException
    {
        throw new CantUnregisterException(this);
    }

    @Override
    public boolean fits(String[] args)
    {
        return args[0].length() > 0 && getModule(args[0]) != null;
    }

    @Override
    public void execute(String[] args)
    {
        if (args == null || args.length < 1)
        {
            return;
        }

        Module module = moduleManager.getObject(args[0]);
        if (module == null)
        {
            module = getModule(args[0].toLowerCase());
            if (module == null)
            {
                ChatUtil.sendMessage(TextColor.RED
                        + "Could not find "
                        + TextColor.WHITE
                        + args[0]
                        + TextColor.RED
                        + ". Try "
                        + Commands.getPrefix()
                        + "modules.");
            }
            else
            {
                ChatUtil.sendMessage(TextColor.RED
                        + "Did you mean "
                        + TextColor.WHITE
                        + module.getName()
                        + TextColor.RED
                        + "?");
            }

            return;
        }

        if (module instanceof CustomCommandModule
                && ((CustomCommandModule) module).execute(args))
        {
            return;
        }

        if (args.length == 1)
        {
            Module finalModule = module;
            Scheduler.getInstance().schedule(() ->
                    Managers.COMMANDS.applyCommand(HListSettingCommand
                                                        .create(finalModule)));
            return;
        }

        Setting<?> setting = module.getSetting(args[1]);
        if (setting == null)
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "Couldn't find setting "
                    + TextColor.AQUA
                    + args[1]
                    + TextColor.RED
                    + " in "
                    + TextColor.WHITE
                    + module.getName()
                    + TextColor.RED
                    + ".");
            return;
        }

        if (args.length == 2)
        {
            ChatUtil.sendMessage(TextColor.RED
                                    + "Please specify a value for "
                                    + TextColor.WHITE
                                    + args[1]
                                    + TextColor.RED
                                    + " in "
                                    + TextColor.WHITE
                                    + module.getName()
                                    + TextColor.RED
                                    + ".");
        }
        else
        {
            // Not really required anymore since the new enable system,
            //  but I'll keep cause lazy and enable message.
            if (setting.getName().equals("Enabled"))
            {
                if (args[2].equalsIgnoreCase("true"))
                {
                    module.enable();
                }
                else if (args[2].equalsIgnoreCase("false"))
                {
                    module.disable();
                }
                else
                {
                    ChatUtil.sendMessage(TextColor.RED
                            + "Possible values: true or false!");
                    return;
                }

                Managers.CHAT.sendDeleteMessage(TextColor.BOLD
                                + module.getName()
                                + (module.isEnabled()
                                    ? TextColor.GREEN + " enabled."
                                    : TextColor.RED + " disabled."),
                                module.getName(),
                                ChatIDs.MODULE);
                return;
            }

            SettingResult result = setting.fromString(args[2]);
            if (!result.wasSuccessful())
            {
                ChatUtil.sendMessage(TextColor.RED + result.getMessage());
            }
            else
            {
                sendSettingMessage(module, setting);
            }
        }
    }

    public static void sendSettingMessage(Module module, Setting<?> setting) {
        sendSettingMessage(module, setting, "");
    }

    public static void sendSettingMessage(Module module, Setting<?> setting,
                                          String idAppend) {
        String message = "<"
            + module.getDisplayName()
            + "> "
            + TextColor.AQUA
            + setting.getName()
            + TextColor.WHITE
            + " set to "
            + (setting.getValue() instanceof Boolean
            ? ((Boolean) setting.getValue()
            ? TextColor.GREEN
            : TextColor.RED)
            : TextColor.AQUA)
            + setting.toJson()
            + TextColor.WHITE
            + ".";

        Managers.CHAT.sendDeleteMessage(message,
                                        setting.getName() + idAppend,
                                        ChatIDs.COMMAND);
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args)
    {
        Module module = getModule(args[0]);
        if (module == null)
        {
            return new PossibleInputs("", TextColor.RED + " not found");
        }

        if (module instanceof CustomCommandModule)
        {
            PossibleInputs inputs = PossibleInputs.empty();
            if (((CustomCommandModule) module).getInput(args, inputs))
            {
                return inputs;
            }
        }

        if (args.length == 1)
        {
            return new PossibleInputs(
                TextUtil.substring(module.getName(), args[0].length()),
                " <setting> <value>");
        }

        if (!args[0].equalsIgnoreCase(module.getName()))
        {
            return new PossibleInputs("", TextColor.RED + " not found");
        }

        Setting<?> setting = CommandUtil.getNameableStartingWith(args[1],
                                                        module.getSettings());
        if (setting == null)
        {
            return new PossibleInputs("", TextColor.RED + " no such setting!");
        }

        if (args.length == 2)
        {
            return new PossibleInputs(
                    TextUtil.substring(setting.getName(), args[1].length()),
                    " " + setting.getInputs(null)
                        + " <Current: " + setting.toJson() + ">"
                        + (setting.getName().contains(" ")
                        ? " <Surround setting with \"...\">"
                        : ""));
        }

        if (args.length != 3)
        {
            return PossibleInputs.empty();
        }

        return new PossibleInputs(
            TextUtil.substring(setting.getInputs(args[2]), args[2].length()),
                " <Current: " + setting.toJson() + ">"
                        + (setting.getName().contains(" ")
                            ? " <Surround setting with \"...\">"
                            : ""));
    }

    @Override
    public Completer onTabComplete(Completer completer)
    {
        String[] args = completer.getArgs();
        if (args.length > 0)
        {
            Module module = getModule(args[0]);
            if (module instanceof CustomCommandModule)
            {
                switch (((CustomCommandModule) module).complete(completer))
                {
                    case RETURN:
                        return completer;
                    case SUPER:
                        return super.onTabComplete(completer);
                    default:
                }
            }
        }

        if (completer.isSame())
        {
            Module module = getModule(args[0]);
            if (module == null)
            {
                return super.onTabComplete(completer);
            }

            if (args.length == 1)
            {
                return completer;
            }
            else if (args.length == 2)
            {
                Optional<Setting<?>> first = module.getSettings()
                        .stream()
                        .findFirst();

                if (!first.isPresent())
                {
                    return completer;
                }

                if (module instanceof CustomCommandModule)
                {
                    String[] custom = ((CustomCommandModule) module).getArgs();
                    if (custom != null && custom.length > 0)
                    {
                        boolean found = false;
                        for (String s : custom)
                        {
                            if (found)
                            {
                                completer.setResult(Commands.getPrefix()
                                        + args[0]
                                        + " "
                                        + s);
                                return completer;
                            }

                            if (args[1].equalsIgnoreCase(s))
                            {
                                found = true;
                            }
                        }
                    }
                }

                Setting<?> setting = module.getSetting(args[1]);
                if (setting == null)
                {
                    completer.setResult(Commands.getPrefix()
                                        + args[0]
                                        + " "
                                        + getEscapedName(first.get()
                                                              .getName()));
                    return completer;
                }

                boolean found = false;
                for (Setting<?> s : module.getSettings())
                {
                    if (found)
                    {
                        completer.setResult(Commands.getPrefix()
                                            + args[0] + " "
                                            + getEscapedName(s.getName()));
                        return completer;
                    }

                    if (s.equals(setting))
                    {
                        found = true;
                    }
                }

                if (module instanceof CustomCommandModule)
                {
                    String[] custom = ((CustomCommandModule) module).getArgs();
                    if (custom != null && custom.length > 0)
                    {
                        completer.setResult(Commands.getPrefix()
                                            + args[0]
                                            + " "
                                            + custom[0]);
                        return completer;
                    }
                }

                completer.setResult(Commands.getPrefix() + args[0] + " "
                                    + getEscapedName(first.get().getName()));
            }
            else
            {
                if (module instanceof CustomCommandModule)
                {
                    String[] custom = ((CustomCommandModule) module).getArgs();
                    if (custom != null && custom.length > 0)
                    {
                        for (String s : custom)
                        {
                            if (args[1].equalsIgnoreCase(s))
                            {
                                return completer;
                            }
                        }
                    }
                }

                Setting<?> setting = module.getSetting(args[2]);
                if (setting != null)
                {
                    completer.setResult(Commands.getPrefix() + args[0] + " "
                            + Completer.nextValueInSetting(setting,
                                                    args[args.length - 1]));
                }
            }

            return completer;
        }

        if (args.length == 2)
        {
            Module module = getModule(args[0]);
            if (module == null)
            {
                return completer;
            }

            Setting<?> setting = CommandUtil.getNameableStartingWith(args[1],
                                                        module.getSettings());
            if (setting == null)
            {
                return completer;
            }

            return completer.setResult(Commands.getPrefix()
                                        + args[0]
                                        + " "
                                        + getEscapedName(setting.getName()));
        }

        return super.onTabComplete(completer);
    }

    private String getEscapedName(String name)
    {
        return name.contains(" ") ? "\"" + name + "\"" : name;
    }

    private Module getModule(String name)
    {
        return CommandUtil.getNameableStartingWith(name, moduleManager);
    }

}
