package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.register.exception.AlreadyRegisteredException;
import me.earth.earthhack.api.register.exception.CantUnregisterException;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.commands.abstracts.AbstractMultiMacroCommand;
import me.earth.earthhack.impl.commands.hidden.HMacroCombineCommand;
import me.earth.earthhack.impl.commands.hidden.HMacroFlowCommand;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.gui.chat.util.ChatComponentUtil;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.client.macro.Macro;
import me.earth.earthhack.impl.managers.client.macro.MacroType;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.text.ChatIDs;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.HoverEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class MacroCommand extends Command
{
    private static final BindSetting BIND_INSTANCE = new BindSetting("Bind");
    private final List<AbstractMultiMacroCommand<?>> custom = new ArrayList<>();

    public MacroCommand()
    {
        super(new String[][]{{"macro"},
                             {"add", "del", "release", "use"},
                             {"name"},
                             {"bind", "release"},
                             {"flow", "combine", "command"}});
        CommandDescriptions.register(this, "Manage your Macros. Use "
            + TextColor.BOLD
            + "flow"
            + TextColor.RESET
            + " to create a macro that switches between "
            + "the given macros everytime its used. Use "
            + TextColor.BOLD
            + "combine" + TextColor.RESET
            + " to combine multiple macros into one. You can also "
            + "use these features to combine or flow macros even further"
            + " customizing your macros to the maximum."
            + TextColor.BOLD + " Release" + TextColor.RESET
            + " <true/false> allows you to make macros that toggle"
            + " when you release a key.");
        custom.add(new HMacroCombineCommand());
        custom.add(new HMacroFlowCommand());
    }

    @Override
    public void execute(String[] args)
    {
        if (args.length == 1)
        {
            ITextComponent component = new TextComponentString("Macros: ");
            Iterator<Macro> iterator = Managers.MACRO
                               .getRegistered()
                               .stream()
                               .filter(m -> m.getType() != MacroType.DELEGATE)
                               .collect(Collectors.toList())
                               .iterator();
            while (iterator.hasNext())
            {
                Macro macro = iterator.next();
                ITextComponent macroComp = new TextComponentString(
                        TextColor.AQUA + macro.getName());

                macroComp.setStyle(
                    new Style().setHoverEvent(
                        ChatComponentUtil.setOffset(
                            new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                               new TextComponentString("Bind: "
                                    + TextColor.AQUA
                                    + macro.getBind().toString()
                                    + TextColor.WHITE
                                    + ", Command: "
                                    + TextColor.RED
                                    + Arrays.toString(
                                        macro.getCommands())
                               )))));

                component.appendSibling(macroComp);
                if (iterator.hasNext())
                {
                    component.appendSibling(new TextComponentString(
                            TextColor.WHITE + ", "));
                }
            }

            Managers.CHAT.sendDeleteComponent(component,
                                              "Macros",
                                              ChatIDs.COMMAND);
            return;
        }
        else if (args.length == 2)
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "Please Specify a Macro");
            return;
        }

        if (args.length >= 3)
        {
            if (args[1].equalsIgnoreCase("use"))
            {
                executeMacro(args[2]);
                return;
            }
            else if (args[1].equalsIgnoreCase("release"))
            {
                Macro m = Managers.MACRO.getObject(args[2]);
                if (m == null)
                {
                    ChatUtil.sendMessage(TextColor.RED
                            + "Macro "
                            + TextColor.WHITE
                            + args[2]
                            + TextColor.RED
                            + " doesn't exist.");
                }
                else
                {
                    if (args.length == 3)
                    {
                        boolean r = m.isRelease();
                        ChatUtil.sendMessage(TextColor.GREEN
                                                + "Macro "
                                                + TextColor.AQUA
                                                + args[2]
                                                + TextColor.GREEN
                                                + (r ? " toggles"
                                                     : " doesn't toggle")
                                                + " on release.");
                    }
                    else
                    {
                        boolean r = Boolean.parseBoolean(args[3]);
                        m.setRelease(r);
                        ChatUtil.sendMessage(TextColor.GREEN
                                + "Macro "
                                + TextColor.AQUA
                                + args[2]
                                + TextColor.GREEN
                                + " now"
                                + (r ? " toggles " : " doesn't toggle ")
                                + "on releasing the key.");
                    }
                }

                return;
            }
        }

        if (args.length == 3)
        {
            if (args[1].equalsIgnoreCase("del"))
            {
                delMacro(args);
            }
            else if (args[1].equalsIgnoreCase("add"))
            {
                ChatUtil.sendMessage(TextColor.RED
                                     + "Please specify a bind "
                                     + "and command.");
            }
            else
            {
                onInvalidInput(args);
            }
        }
        else if (args.length == 4)
        {
            if (args[1].equalsIgnoreCase("del"))
            {
                delMacro(args);
            }
            else if (args[1].equalsIgnoreCase("add"))
            {
                ChatUtil.sendMessage(TextColor.RED
                                     + "Please specify a command.");
            }
            else
            {
                onInvalidInput(args);
            }
        }
        else
        {
            if (args[1].equalsIgnoreCase("del"))
            {
                delMacro(args);
            }
            else if (args[1].equalsIgnoreCase("add"))
            {
                for (Command command : custom)
                {
                    if (command.fits(Arrays.copyOfRange(args, 4, args.length)))
                    {
                        command.execute(args);
                        return;
                    }
                }

                String name = args[2];
                String bind = args[3];
                String comm = CommandUtil.concatenate(args, 4);
                Bind parsed = Bind.fromString(bind);
                Macro macro = new Macro(name, parsed, new String[]{comm});

                try
                {
                    Managers.MACRO.register(macro);
                    ChatUtil.sendMessage(TextColor.GREEN
                            + "Added new Macro: " + TextColor.WHITE
                            + macro.getName() + " : " + TextColor.AQUA
                            + parsed + TextColor.WHITE + " : "
                            + TextColor.RED + Commands.getPrefix() + comm);
                }
                catch (AlreadyRegisteredException e)
                {
                    ChatUtil.sendMessage(TextColor.RED
                            + "Couldn't add Macro " + TextColor.WHITE
                            + macro.getName() + TextColor.RED
                            + ", a Macro with that name already exists.");
                }
            }
            else
            {
                onInvalidInput(args);
            }
        }
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args)
    {
        PossibleInputs inputs = super.getPossibleInputs(args);
        if (args.length < 3)
        {
            return inputs;
        }
        else if (args.length == 3)
        {
            Macro macro = getMacroStartingWith(args[2]);
            if ((args[1].equalsIgnoreCase("use")
                    || args[1].equalsIgnoreCase("del")
                    || args[1].equalsIgnoreCase("release"))
                        && macro == null)
            {
                return inputs.setCompletion("")
                             .setRest(TextColor.RED + " not found");
            }
            else if (args[1].equalsIgnoreCase("add") && macro != null)
            {
                return inputs.setCompletion(TextUtil.substring(
                                                macro.getName(),
                                                args[2].length()))
                             .setRest(TextColor.RED
                                         + " <Macro: "
                                         + TextColor.WHITE
                                         + macro.getName()
                                         + TextColor.RED
                                         + ">"
                                         + " already exists.");
            }
            else if (macro != null)
            {
                inputs.setCompletion(TextUtil.substring(macro.getName(),
                                                        args[2].length()));
                if (args[1].equalsIgnoreCase("release"))
                {
                    return inputs.setRest(" <true/false>");
                }

                return inputs.setRest("");
            }

            return inputs.setCompletion("")
                         .setRest(" <bind> <flow/combine/command>");
        }
        else if (args.length == 4)
        {
            if (args[1].equalsIgnoreCase("release"))
            {
                String s = CommandUtil.completeBoolean(args[3]);
                if (s == null)
                {
                    return inputs.setCompletion("")
                                 .setRest(TextColor.RED + " try true/false");
                }

                return inputs.setCompletion(s).setRest("");
            }

            if (args[1].equalsIgnoreCase("del")
                    || args[1].equalsIgnoreCase("use"))
            {
                return PossibleInputs.empty();
            }

            return inputs.setCompletion(TextUtil.substring(
                                            BIND_INSTANCE.getInputs(args[3]),
                                            args[3].length()))
                         .setRest(" <flow/combine/command>");
        }

        if (args[2].equalsIgnoreCase("del")
            || args[1].equalsIgnoreCase("use")
            || args[1].equalsIgnoreCase("release"))
        {
            return PossibleInputs.empty();
        }

        String[] arguments = Arrays.copyOfRange(args, 4, args.length);
        for (Command command : custom)
        {
            if (command.fits(arguments))
            {
                return command.getPossibleInputs(arguments);
            }
        }

        Command target = Managers.COMMANDS.getCommandForMessage(arguments);
        if (target == null)
        {
            return PossibleInputs.empty();
        }

        return target.getPossibleInputs(arguments);
    }

    private void onInvalidInput(String[] args)
    {
        Macro macro = getMacroStartingWith(args[2]);
        if (macro == null)
        {
            Earthhack.getLogger().warn(Arrays.toString(args));
            ChatUtil.sendMessage(TextColor.RED + "Usage is <add/del>.");
        }
        else
        {
            ChatUtil.sendMessage(TextColor.RED + "Bad Input, info about "
                    + TextColor.WHITE + macro.getName() + TextColor.RED
                    + ": " + TextColor.WHITE + "<"
                    + TextColor.AQUA + "bind: "
                    + macro.getBind().toString() + TextColor.WHITE
                    + "> <" + TextColor.AQUA + "commands: "
                    + Arrays.toString(macro.getCommands())
                    + TextColor.WHITE + ">");
        }
    }

    private void delMacro(String[] args)
    {
        Macro macro = getMacroStartingWith(args[2]);
        if (macro == null)
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "Couldn't find macro "
                    + args[2]
                    + ".");
            return;
        }

        if (macro.getName().equalsIgnoreCase(args[2]))
        {
            try
            {
                Managers.MACRO.unregister(macro);
                ChatUtil.sendMessage("Removed Macro "
                        + TextColor.RED
                        + args[2]
                        + TextColor.WHITE
                        + ".");
            }
            catch (CantUnregisterException e)
            {
                ChatUtil.sendMessage("Could not unregister Macro "
                        + TextColor.RED
                        + args[2]
                        + TextColor.WHITE
                        + ".");
            }
        }
        else
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "Couldn't find "
                    + args[2]
                    + " did you mean "
                    + TextColor.AQUA
                    + macro.getName()
                    + TextColor.RED
                    + "?");
        }
    }

    private void executeMacro(String name)
    {
        Macro macro = Managers.MACRO.getObject(name);
        if (macro == null)
        {
            ChatUtil.sendMessage(TextColor.RED
                                    + "Macro "
                                    + TextColor.WHITE
                                    + name
                                    + TextColor.RED
                                    + " couldn't be found!");
            return;
        }

        if (Managers.MACRO.isSafe())
        {
            macro.execute(Managers.COMMANDS);
        }
        else
        {
            try
            {
                macro.execute(Managers.COMMANDS);
            }
            catch (Throwable t)
            {
                ChatUtil.sendMessage(TextColor.RED
                        + "An error occurred while executing macro "
                        + TextColor.WHITE
                        + name
                        + TextColor.RED
                        + ": "
                        + (t.getMessage() == null
                            ? t.getClass().getName()
                            : t.getMessage()));
            }
        }
    }

    private Macro getMacroStartingWith(String name)
    {
        return CommandUtil.getNameableStartingWith(name,
                Managers.MACRO.getRegistered()
                        .stream()
                        .filter(m -> m.getType() != MacroType.DELEGATE)
                        .collect(Collectors.toList()));
    }

}
