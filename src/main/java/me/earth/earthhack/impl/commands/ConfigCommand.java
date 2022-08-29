package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.config.Config;
import me.earth.earthhack.api.config.ConfigHelper;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.api.util.interfaces.Nameable;
import me.earth.earthhack.impl.commands.gui.YesNoNonPausing;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.gui.chat.clickevents.SmartClickEvent;
import me.earth.earthhack.impl.gui.chat.components.SimpleComponent;
import me.earth.earthhack.impl.gui.chat.components.SuppliedComponent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.config.helpers.AllConfigHelper;
import me.earth.earthhack.impl.managers.config.helpers.CurrentConfig;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.misc.io.IORunnable;
import me.earth.earthhack.impl.util.text.ChatIDs;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;

import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;

public class ConfigCommand extends Command implements Globals
{
    private final AllConfigHelper allConfigHelper =
        new AllConfigHelper(Managers.CONFIG);

    public ConfigCommand()
    {
        super(new String[][]{{"config"},
                             {"config"},
                             {"save", "delete", "load", "refresh"},
                             {"name..."}});
        CommandDescriptions.register(this, "Manage your configs.");
    }

    @Override
    public void execute(String[] args)
    {
        if (args.length == 1)
        {
            Managers.CHAT.sendDeleteMessage(
                    "Use this command to save/load your config.",
                    "config",
                    ChatIDs.COMMAND);
            return;
        }

        ConfigHelper<?> tempHelper = Managers.CONFIG.getObject(args[1]);
        if (tempHelper == null)
        {
            if ("all".equalsIgnoreCase(args[1]))
            {
                tempHelper = allConfigHelper;
            }
            else
            {
                Managers.CHAT.sendDeleteMessage(
                    TextColor.AQUA
                        + args[1]
                        + TextColor.RED
                        + " unknown. Use: " + getConcatenatedHelpers(),
                    "config1",
                    ChatIDs.COMMAND);
                return;
            }
        }

        ConfigHelper<?> helper = tempHelper;
        switch (args.length)
        {
            case 2:
                sendConfigs(helper);
                break;
            case 3:
                switch (args[2].toLowerCase())
                {
                    case "save":
                        try
                        {
                            Managers.CONFIG.save(helper);
                            Managers.CHAT.sendDeleteMessage(
                                    TextColor.GREEN
                                        + "Saved the "
                                        + helper.getName()
                                        + " config.",
                                    "config5",
                                    ChatIDs.COMMAND);
                        }
                        catch (IOException e)
                        {
                            Managers.CHAT.sendDeleteMessage(
                                TextColor.RED + "An error occurred while " +
                                    "saving " + helper.getName() + ": "
                                    + TextColor.WHITE + e.getMessage()
                                    + TextColor.RED + ".",
                                "config6",
                                ChatIDs.COMMAND);
                            e.printStackTrace();
                        }

                        return;
                    case "delete":
                        Managers.CHAT.sendDeleteMessage(
                                TextColor.RED
                                        + "Please specify a "
                                        + TextColor.WHITE
                                        + helper.getName()
                                        + TextColor.RED
                                        + " config to delete!",
                                "config6",
                                ChatIDs.COMMAND);
                        return;
                    case "load":
                        Managers.CHAT.sendDeleteMessage(
                            TextColor.RED + "Please specify a config to load.",
                            "config6",
                            ChatIDs.COMMAND);
                        return;
                    case "refresh":
                        GuiScreen before = mc.currentScreen;
                        Scheduler.getInstance().schedule(() ->
                            mc.displayGuiScreen(new YesNoNonPausing(
                                (result, id) ->
                            {
                                mc.displayGuiScreen(before);
                                if (!result)
                                {
                                    return;
                                }

                                try
                                {
                                    helper.refresh();
                                    Managers.CHAT.sendDeleteMessage(
                                        TextColor.GREEN
                                                + "Refreshed the "
                                                + helper.getName()
                                                + " config.",
                                        "config7",
                                        ChatIDs.COMMAND);
                                }
                                catch (IOException e)
                                {
                                    Managers.CHAT.sendDeleteMessage(
                                            TextColor.RED
                                                + "An error occurred while " +
                                                "saving "
                                                + helper.getName()
                                                + ": "
                                                + TextColor.WHITE
                                                + e.getMessage()
                                                + TextColor.RED + ".",
                                            "config6",
                                            ChatIDs.COMMAND);
                                    e.printStackTrace();
                                }
                            },
                            TextColor.RED
                                    + "Reload the "
                                    + helper.getName()
                                    + " config from the disk.",
                            "This action will override your current "
                                    + helper.getName()
                                    + " configs. Continue?",
                            1337)));
                        return;
                    default:
                        Managers.CHAT.sendDeleteMessage(
                                TextColor.RED +
                                "Can't recognize option " + args[2] + ".",
                                "config4",
                                ChatIDs.COMMAND);
                }
                break;
            default:
                String[] configs = Arrays.copyOfRange(args, 3, args.length);
                String cString = "config" + (configs.length > 1 ? "s" : "");
                switch (args[2].toLowerCase())
                {
                    case "save":
                        displayYesNo("Sav",
                            "Save the  " + helper.getName()
                                + " - " + Arrays.toString(configs)
                                + " " + cString + "?",
                            helper,
                            () ->
                            {
                                Managers.CONFIG.save(helper, configs);
                                ChatUtil.sendMessage(TextColor.GREEN
                                        + "Saved the "
                                        + TextColor.WHITE
                                        + helper.getName()
                                        + TextColor.GREEN
                                        + " : "
                                        + TextColor.WHITE
                                        + Arrays.toString(configs)
                                        + TextColor.GREEN
                                        + " " + cString + ".");
                            });
                        break;
                    case "delete":
                        try
                        {
                            helper.delete(args[3]);
                            ChatUtil.sendMessage(TextColor.GREEN
                                    + "Deleted "
                                    + TextColor.RED
                                    + args[3]
                                    + TextColor.GREEN
                                    + " from the "
                                    + helper.getName()
                                    + "s config.");
                        }
                        catch (Exception e)
                        {
                            ChatUtil.sendMessage(TextColor.RED
                                    + "Can't delete "
                                    + TextColor.WHITE
                                    + args[3]
                                    + TextColor.RED
                                    + ": "
                                    + e.getMessage());
                            e.printStackTrace();
                        }

                        break;
                    case "load":
                        try
                        {
                            Managers.CONFIG.load(helper, args[3]);
                            ChatUtil.sendMessage(TextColor.GREEN + "Loaded the "
                                    + TextColor.WHITE
                                    + helper.getName()
                                    + TextColor.GREEN
                                    + " : "
                                    + TextColor.WHITE
                                    + args[3]
                                    + TextColor.GREEN
                                    + " config.");
                        }
                        catch (IOException e)
                        {
                            ChatUtil.sendMessage(TextColor.RED
                                    + "An error occurred while loading the "
                                    + TextColor.WHITE
                                    + helper.getName()
                                    + TextColor.RED
                                    + " : "
                                    + TextColor.WHITE
                                    + args[3]
                                    + TextColor.RED
                                    + " config.");
                            e.printStackTrace();
                        }
                        break;
                    case "refresh":
                        displayYesNo("Refresh",
                            "This action will override your current "
                            + helper.getName()
                            + " - " + Arrays.toString(configs)
                            + " " + cString + ". Continue?",
                            helper,
                            () ->
                            {
                                Managers.CONFIG.load(helper, configs);
                                ChatUtil.sendMessage(TextColor.GREEN
                                        + "Refreshed the "
                                        + TextColor.WHITE
                                        + helper.getName()
                                        + TextColor.GREEN
                                        + " : "
                                        + TextColor.WHITE
                                        + Arrays.toString(configs)
                                        + TextColor.GREEN
                                        + " " + cString + ".");
                            });
                        break;
                    default:
                        ChatUtil.sendMessage(TextColor.RED
                                + "Can't recognize option "
                                + TextColor.WHITE + args[2]
                                + TextColor.RED + ".");
                }
        }
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args)
    {
        PossibleInputs inputs = super.getPossibleInputs(args);

        if (args.length == 1)
        {
            return inputs.setRest(" <" + getConcatenatedHelpers()
                                       + "> <save/delete/load/refresh> <name>");
        }

        ConfigHelper<?> helper = CommandUtil.getNameableStartingWith(
                args[1], getConfigHelpers());

        if (helper == null)
        {
            return inputs.setCompletion("")
                         .setRest(TextColor.RED + " config type not found");
        }

        switch (args.length)
        {
            case 2:
                return inputs.setCompletion(TextUtil.substring(
                                            helper.getName(),
                                            args[1].length()))
                             .setRest(" <save/delete/load/refresh> <name>");
            case 3:
                return inputs;
            default:
                Nameable nameable =
                        CommandUtil.getNameableStartingWith(
                                args[args.length - 1],
                                helper.getConfigs());

                if (nameable != null)
                {
                    return inputs.setRest("")
                                 .setCompletion(
                                     TextUtil.substring(
                                        nameable.getName(),
                                        args[args.length - 1].length()));
                }
        }

        return inputs;
    }

    @Override
    public Completer onTabComplete(Completer completer)
    {
        return super.onTabComplete(completer);
    }

    private String getConcatenatedHelpers()
    {
        StringBuilder builder = new StringBuilder();
        Iterator<ConfigHelper<?>> it = Managers.CONFIG.getRegistered().iterator();
        while (it.hasNext())
        {
            ConfigHelper<?> helper = it.next();
            builder.append(helper.getName());
            if (it.hasNext())
            {
                builder.append("/");
            }
        }

        return builder.append("/all").toString();
    }

    private Collection<ConfigHelper<?>> getConfigHelpers() {
        List<ConfigHelper<?>> helpers = new ArrayList<>(Managers.CONFIG.getRegistered().size() + 1);
        helpers.addAll(Managers.CONFIG.getRegistered());
        helpers.add(allConfigHelper);
        return helpers;
    }

    /**
     * {@link me.earth.earthhack.impl.modules.client.customfont.FontMod#sendFonts()}
     */
    public void sendConfigs(ConfigHelper<?> helper)
    {
        final Supplier<String> current = () -> {
            String lCurrent = CurrentConfig.getInstance().get(helper);
            if (lCurrent == null)
            {
                lCurrent = TextColor.RED + "None";
            }
            return lCurrent;
        };

        SimpleComponent component =
                new SimpleComponent("Use this command" +
                        " to save/delete/load the " +
                        TextColor.AQUA +
                        helper.getName() +
                        TextColor.WHITE +
                        " config. List: ");
        component.setWrap(true);

        List<Config> configs = Arrays.asList(helper.getConfigs().toArray(new Config[]{}));
        for (int i = 0; i < configs.size(); i++)
        {
            Config config = configs.get(i);
            int finalI = i;
            component.appendSibling(
                    new SuppliedComponent(() ->
                            (current.get().equals(config.getName())
                                    ? TextColor.GREEN
                                    : TextColor.AQUA)
                                    + config.getName()
                                    + TextColor.WHITE
                                    + (finalI == configs.size() - 1
                                    ? ""
                                    : ", "))
                            .setStyle(new Style()
                                    .setClickEvent(new SmartClickEvent
                                            (ClickEvent.Action.RUN_COMMAND)
                                    {
                                        @Override
                                        public String getValue()
                                        {
                                            return Commands.getPrefix()
                                                    + "config "
                                                    + helper.getName()
                                                    + " load "
                                                    + "\"" + config.getName() + "\"";
                                        }
                                    })));
        }

        Managers.CHAT.sendDeleteComponent(
                component,
                "config2",
                ChatIDs.COMMAND
        );
    }

    private void displayYesNo(String action,
                              String message2,
                              ConfigHelper<?> helper,
                              IORunnable runnable)
    {
        GuiScreen before = mc.currentScreen;
        Scheduler.getInstance().schedule(() ->
            mc.displayGuiScreen(new YesNoNonPausing(
                (result, id) ->
                {
                    mc.displayGuiScreen(before);
                    if (!result)
                    {
                        return;
                    }

                    try
                    {
                        runnable.run();
                    }
                    catch (IOException e)
                    {
                        Managers.CHAT.sendDeleteMessage(
                                TextColor.RED
                                        + "An error occurred while "
                                        + action.toLowerCase() + "ing "
                                        + helper.getName()
                                        + ": "
                                        + TextColor.WHITE
                                        + e.getMessage()
                                        + TextColor.RED + ".",
                                "config6",
                                ChatIDs.COMMAND);
                        e.printStackTrace();
                    }
                },
                TextColor.RED
                        + action
                        + "ing the "
                        + TextColor.WHITE
                        + helper.getName()
                        + TextColor.RED
                        + " config.",
                message2,
                1337)));
    }

}
