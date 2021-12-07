package me.earth.earthhack.impl.commands.abstracts;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.register.exception.AlreadyRegisteredException;
import me.earth.earthhack.api.register.exception.CantUnregisterException;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.commands.gui.YesNoNonPausing;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.client.macro.DelegateMacro;
import me.earth.earthhack.impl.managers.client.macro.Macro;
import me.earth.earthhack.impl.managers.client.macro.MacroType;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.GuiScreen;

import java.util.Arrays;

public abstract class AbstractMultiMacroCommand<T extends Macro>
        extends Command implements Globals
{
    private final String ifSmallArgs;
    private final String macroName;

    public AbstractMultiMacroCommand(String[][] usage,
                                     String macroName,
                                     String ifSmallArgs)
    {
        super(usage, true);
        this.macroName = macroName;
        this.ifSmallArgs = ifSmallArgs;
    }

    protected abstract T getMacro(String name, Bind bind, Macro...macros);

    @Override
    public void execute(String[] args)
    {
        // actual args are +macro <add> <name> <bind> <combine/flow...> <macros>
        if (args.length <= 5)
        {
            ChatUtil.sendMessage(TextColor.RED + ifSmallArgs);
            return;
        }

        Macro[] macros = new Macro[args.length - 5];
        Macro[] realMacros = new Macro[args.length - 5];
        for (int i = 5; i < args.length; i++)
        {
            Macro macro = Managers.MACRO.getObject(args[i]);
            if (macro == null)
            {
                ChatUtil.sendMessage(TextColor.RED + "Couldn't find macro: "
                        + TextColor.WHITE
                        + args[i]
                        + TextColor.RED + ".");
                return;
            }

            realMacros[i - 5] = macro;
            if (macro.getType() == MacroType.COMBINED
                        || macro.getType() == MacroType.FLOW)
            {
                Earthhack.getLogger().info("Creating Delegate for Macro: "
                        + macro.getName()
                        + " : "
                        + Arrays.toString(macro.getCommands()));

                String name = "CopyOf-" + macro.getName();
                while (Managers.MACRO.getObject(name) != null)
                {
                    name += "I";
                }

                DelegateMacro extraDelegate = DelegateMacro
                                                    .delegate(name, macro);
                String name2 = "Delegate-" + macro.getName();
                while (Managers.MACRO.getObject(name2) != null)
                {
                    name2 += "I";
                }

                DelegateMacro delegate = new DelegateMacro(name2,
                                                    extraDelegate.getName());
                try
                {
                    Managers.MACRO.register(extraDelegate);
                }
                catch (AlreadyRegisteredException e)
                {
                    ChatUtil.sendMessage(TextColor.RED
                            + "An error occurred while delegating your macro: "
                            + e.getMessage());
                    e.printStackTrace();
                    return;
                }

                try
                {
                    Managers.MACRO.register(delegate);
                }
                catch (AlreadyRegisteredException e)
                {
                    ChatUtil.sendMessage(TextColor.RED
                            + "An error occurred while delegating your macro: "
                            + e.getMessage());
                    e.printStackTrace();
                    return;
                }

                macros[i - 5] = delegate;
                continue;
            }

            macros[i - 5] = macro;
        }

        String name = args[2];
        String bind = args[3];
        Bind parsed = Bind.fromString(bind);
        T macro = getMacro(name, parsed, macros);

        StringBuilder conc = new StringBuilder();
        for (int i = 0; i < realMacros.length; i++)
        {
            conc.append(TextColor.RED).append(realMacros[i].getName());
            if (i != realMacros.length - 1)
            {
                conc.append(TextColor.WHITE).append(", ");
            }
        }

        String concatenated = conc.append(TextColor.WHITE).toString();

        GuiScreen before = mc.currentScreen;
        Scheduler.getInstance().schedule(() ->
            mc.displayGuiScreen(new YesNoNonPausing(
                (result, id) ->
                {
                    mc.displayGuiScreen(before);
                    if (!result)
                    {
                        registerMacro(macro, parsed, concatenated);
                        return;
                    }

                    int i = 0;
                    for (; i < realMacros.length; i++)
                    {
                        try
                        {
                            Managers.MACRO.unregister(realMacros[i]);
                        }
                        catch (CantUnregisterException e)
                        {
                            ChatUtil.sendMessage(TextColor.RED
                                    + "A critical error occurred: "
                                    + TextColor.WHITE
                                    + realMacros[i].getName()
                                    + TextColor.RED
                                    + " can't be deleted ("
                                    + e.getMessage() + ").");
                            e.printStackTrace();
                        }
                    }

                    registerMacro(macro, parsed, concatenated);
                },
                "",
                "Do you want to delete the macros " + concatenated + " ?",
                1337)));
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args)
    {
        PossibleInputs inputs = super.getPossibleInputs(args);
        if (args.length == 1)
        {
            return inputs;
        }

        inputs.setRest(" <macro> <macro> <...>");
        Macro macro = CommandUtil.getNameableStartingWith(
                args[args.length - 1],
                Managers.MACRO.getRegistered());
        if (macro == null)
        {
            return inputs.setCompletion("")
                    .setRest(TextColor.RED + " not found");
        }

        return inputs.setCompletion(TextUtil.substring(
                macro.getName(),
                args[args.length - 1].length()));
    }

    private void registerMacro(Macro macro, Bind parsed, String concatenated)
    {
        try
        {
            Managers.MACRO.register(macro);
            ChatUtil.sendMessage(TextColor.GREEN
                    + "Added new " + macroName + ": " + TextColor.WHITE
                    + macro.getName() + " : " + TextColor.AQUA
                    + parsed.toString() + TextColor.WHITE + " : "
                    + TextColor.RED + Commands.getPrefix()
                    + concatenated + ".");
        }
        catch (AlreadyRegisteredException e)
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "Couldn't add Macro " + TextColor.WHITE
                    + macro.getName() + TextColor.RED
                    + ", a Macro with that name already exists.");
        }
    }


}
