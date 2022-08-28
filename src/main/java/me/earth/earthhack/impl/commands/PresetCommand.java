package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.config.preset.ModulePreset;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.data.ModuleData;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.abstracts.AbstractModuleCommand;
import me.earth.earthhack.impl.commands.gui.ComponentBuilder;
import me.earth.earthhack.impl.commands.gui.YesNoNonPausing;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.GuiScreen;

public class PresetCommand extends AbstractModuleCommand implements Globals
{
    public PresetCommand()
    {
        super(new String[][]{{"preset"}, {"module"}, {"preset"}}, 1);
        CommandDescriptions.register(this, "Apply only the best, carefully" +
                " handpicked configs from the devs to modules.");
    }

    @Override
    public void execute(String[] args)
    {
        if (args.length == 1)
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "Use this command to apply a preset to a module.");
            return;
        }

        Module module = Managers.MODULES.getObject(args[1]);
        if (module == null)
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "Could not find module "
                    + TextColor.WHITE
                    + args[1]
                    + TextColor.RED
                    + ".");
            return;
        }

        ModuleData<?> data = module.getData();
        if (data == null)
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "The module "
                    + TextColor.WHITE
                    + args[1]
                    + TextColor.RED
                    + " has no Module-Data!");
            return;
        }

        if (args.length == 2)
        {
            boolean first = true;
            ComponentBuilder builder =
                    new ComponentBuilder(args[1] + "-Presets: ");
            for (ModulePreset<?> preset : data.getPresets())
            {
                if (!first)
                {
                    builder.sibling(TextColor.WHITE + ", " + TextColor.AQUA)
                           .append();
                }

                first = false;
                builder.sibling(TextColor.AQUA + preset.getName())
                       .addHover(preset.getDescription())
                       .addSmartClickEvent("preset " + args[1] + " " + preset.getName())
                       .append();
            }

            if (first)
            {
                ChatUtil.sendMessage(TextColor.RED
                        + "The module "
                        + TextColor.WHITE
                        + args[1]
                        + TextColor.RED
                        + " has no Presets.");
                return;
            }

            ChatUtil.sendComponent(builder.build());
        }
        else
        {
            ModulePreset<?> result = null;
            for (ModulePreset<?> preset : data.getPresets())
            {
                if (preset.getName().equalsIgnoreCase(args[2]))
                {
                    result = preset;
                    break;
                }
            }

            if (result == null)
            {
                ChatUtil.sendMessage(TextColor.RED
                        + "The module "
                        + TextColor.WHITE
                        + args[1]
                        + TextColor.RED
                        + " doesn't have a "
                        + TextColor.AQUA
                        + args[2]
                        + TextColor.RED
                        + " preset.");
                return;
            }

            ModulePreset<?> finalResult = result;
            GuiScreen before = mc.currentScreen;
            Scheduler.getInstance().schedule(() ->
                mc.displayGuiScreen(new YesNoNonPausing((r, id) ->
                    {
                        mc.displayGuiScreen(before);
                        if (!r)
                        {
                            return;
                        }

                        ChatUtil.sendMessage(TextColor.GREEN
                                + "Applying preset "
                                + TextColor.AQUA
                                + finalResult.getName()
                                + TextColor.GREEN
                                + " to "
                                + TextColor.WHITE
                                + module.getName()
                                + TextColor.GREEN
                                + ".");

                        finalResult.apply();
                    },
                    TextColor.RED
                        + "Apply preset "
                        + TextColor.WHITE
                        + finalResult.getName()
                        + TextColor.RED
                        + " to module "
                        + TextColor.WHITE
                        + module.getName()
                        + TextColor.RED
                        + "?",
                    "This will override your current settings for "
                        + module.getName()
                        + ".",
                    1337)));
        }
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args)
    {
        if (args.length > 2)
        {
            if (args.length == 3)
            {
                Module module = Managers.MODULES.getObject(args[1]);
                if (module == null)
                {
                    return PossibleInputs.empty();
                }

                ModuleData<?> data = module.getData();
                if (data == null)
                {
                    return PossibleInputs.empty();
                }

                ModulePreset<?> preset = CommandUtil.getNameableStartingWith(
                                                    args[2],
                                                    data.getPresets());
                if (preset != null)
                {
                    return new PossibleInputs(
                        TextUtil.substring(preset.getName(),
                                           args[2].length()), "");
                }
            }

            return PossibleInputs.empty();
        }

        return super.getPossibleInputs(args);
    }

}
