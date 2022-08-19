package me.earth.earthhack.impl.modules.client.autoconfig;

import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.client.macro.Macro;
import me.earth.earthhack.impl.managers.config.helpers.ModuleConfigHelper;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.helpers.addable.RegisteringModule;
import me.earth.earthhack.impl.util.helpers.command.CustomCompleterResult;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.misc.FileUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AutoConfig extends RegisteringModule<String, RemovingString>
{
    private final StopWatch timer = new StopWatch();
    private ServerList serverList;

    public AutoConfig()
    {
        super("AutoConfig",
                Category.Client,
                "Add Config",
                "ip> <macro",
                s -> new RemovingString(s, s),
                s -> "Applies configs on " + s.getName() + ".");

        this.unregister(listType);
        this.setData(new SimpleData(this,
                "Automatically executes a Macro when joining a server."));
    }

    @Override
    public void add(String string)
    {
        if (addSetting(string) != null)
        {
            ChatUtil.sendMessage(TextColor.GREEN
                    + "Added AutoConfig "
                    + TextColor.WHITE
                    + string
                    + TextColor.GREEN
                    + " successfully.");
        }
        else
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "Something went wrong while adding AutoConfig "
                    + TextColor.WHITE
                    + string
                    + TextColor.RED
                    + ". Maybe a config of this name already exists?");
        }
    }

    @Override
    public boolean execute(String[] args)
    {
        if (args.length >= 2 && args[1].equalsIgnoreCase("secret"))
        {
            FileUtil.createDirectory(Paths.get("earthhack/modules"));
            String name = "3arthqu4ke";
            String configName = "earthhack/modules/" + name + ".json";
            Path path = Paths.get(configName);
            int i = 1;
            while (Files.exists(path))
            {
                name = "3arthqu4ke" + (i++);
                configName = "earthhack/modules/" + name + ".json";
                path = Paths.get(configName);
            }

            try (InputStream in = this
                            .getClass()
                            .getClassLoader()
                            .getResourceAsStream("configs/3arthconfig.json");
                 OutputStream out = Files.newOutputStream(path))
            {
                if (in  == null)
                {
                    throw new IOException("InputStream was null!");
                }

                IOUtils.copy(in, out);
            }
            catch (IOException e)
            {
                ChatUtil.sendMessage(TextColor.RED
                        + "An error occurred: " + e.getMessage());
                e.printStackTrace();
                return true;
            }

            ChatUtil.sendMessage(TextColor.GREEN
                    + "Added secret config: "
                    + TextColor.WHITE
                    + name
                    + TextColor.GREEN
                    + " to your config folder.");

            ModuleConfigHelper helper =
                    Managers.CONFIG.getByClass(ModuleConfigHelper.class);

            if (helper != null)
            {
                try
                {
                    helper.refresh(path.toString());
                }
                catch (IOException e)
                {
                    ChatUtil.sendMessage(TextColor.RED
                            + "An error occurred while refreshing the config!");
                    e.printStackTrace();
                }
            }

            return true;
        }

        return super.execute(args);
    }

    @Override
    public boolean getInput(String[] args, PossibleInputs inputs)
    {
        if (args.length >= 2
                && !args[1].isEmpty()
                && TextUtil.startsWith("secret", args[1].toLowerCase()))
        {
            inputs.setRest("");
            if (args.length == 2)
            {
                inputs.setCompletion(
                    TextUtil.substring("secret", args[1].length()));
            }

            return true;
        }

        return super.getInput(args, inputs);
    }

    @Override
    public CustomCompleterResult complete(Completer completer)
    {
        return super.complete(completer);
    }

    @Override
    public void add(String[] args)
    {
        if (args.length < 4)
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "Please specify a Macro!");
            return;
        }

        RemovingString setting = addSetting(args[2]);
        if (setting == null)
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "An AutoConfig for "
                    + TextColor.WHITE
                    + args[2]
                    + TextColor.RED
                    + " already exists!");
            return;
        }

        setting.fromString(CommandUtil.concatenate(args, 3));
    }

    @Override
    protected PossibleInputs getInput(String input, String[] args)
    {
        if (args.length == 1 && "del".startsWith(args[0].toLowerCase()))
        {
            return super.getInput(input, args).setRest(" <ip>");
        }
        else if (args.length > 1 && args[0].equalsIgnoreCase("add"))
        {
            PossibleInputs inputs = PossibleInputs.empty();
            if (args.length == 2)
            {
                setupServerList();
                String ip = getIpStartingWith(args[1], serverList);
                if (ip == null)
                {
                    return inputs;
                }
                else
                {
                    return inputs.setCompletion(
                                       TextUtil.substring(ip, args[1].length()))
                             .setRest(" <macro>");
                }
            }
            else
            {
                Macro macro = CommandUtil.getNameableStartingWith(args[2],
                        Managers.MACRO.getRegistered());

                if (macro == null)
                {
                    return inputs.setRest(TextColor.RED + " not found!");
                }

                return inputs.setCompletion(
                     TextUtil.substring(macro.getName(), args[2].length()));
            }
        }

        return super.getInput(input, args);
    }

    public void onConnect(String ip)
    {
        RemovingString setting = getSetting("all", RemovingString.class);
        if (setting != null)
        {
            execute(setting);
        }

        setting = getSetting(ip, RemovingString.class);
        if (setting != null)
        {
            execute(setting);
        }
    }

    private void execute(RemovingString setting)
    {
        Macro macro = Managers.MACRO.getObject(setting.getValue());
        if (macro == null)
        {
            ChatUtil.sendMessage("<AutoConfig>"
                                    + TextColor.RED
                                    + " Couldn't find macro "
                                    + setting.getValue()
                                    + "!");
            return;
        }

        ChatUtil.sendMessage("<AutoConfig>"
                                + TextColor.GREEN
                                + " Applying macro "
                                + TextColor.WHITE
                                + setting.getValue()
                                + TextColor.GREEN
                                + ".");
        try
        {
            macro.execute(Managers.COMMANDS);
        }
        catch (Throwable t)
        {
            ChatUtil.sendMessage("<AutoConfig>"
                                    + TextColor.RED
                                    + " An Error occurred while"
                                    + " executing macro "
                                    + TextColor.WHITE
                                    + macro.getName()
                                    + TextColor.RED
                                    + ": "
                                    + t.getMessage());
            t.printStackTrace();
        }
    }

    private String getIpStartingWith(String prefixIn, ServerList list)
    {
        String prefix = prefixIn.toLowerCase();
        for (int i = 0; i < list.countServers(); i++)
        {
            ServerData data = list.getServerData(i);
            if (data.serverIP != null
                    && data.serverIP.toLowerCase().startsWith(prefix))
            {
                return data.serverIP;
            }
        }

        return "singleplayer".startsWith(prefix)
                ? "singleplayer"
                : "all".startsWith(prefix)
                    ? "all"
                    : null;
    }

    private void setupServerList()
    {
        if (serverList == null || timer.passed(60000))
        {
            serverList = new ServerList(mc);
            serverList.loadServerList();
            timer.reset();
        }
    }

}
