package me.earth.earthhack.impl.util.helpers.addable;

import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.CommandSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.util.helpers.command.AddableCommandModule;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class AddableModule extends AddableCommandModule
{
    public final Set<String> strings = new HashSet<>();
    public final String descriptor;

    public final Setting<ListType> listType =
            register(new EnumSetting<>("List-Type", ListType.WhiteList));
    public final Setting<String> commandSetting;

    public AddableModule(String name,
                         Category category,
                         String command,
                         String descriptor)
    {
        super(name, category);
        this.descriptor = descriptor;
        this.commandSetting =
            register(new CommandSetting(command, this::onSettingInput));
    }

    @Override
    public <T, S extends Setting<T>> S register(S setting)
    {
        if (setting.getName().equalsIgnoreCase("add")
                || setting.getName().equalsIgnoreCase("del"))
        {
            // hmm Exception idk SettingContainer could implement Register
            Earthhack.getLogger().error(this.getName()
                    + " Can't register the setting: "
                    + setting.getName()
                    + " in AddableModules these names (add/del) are"
                    + " reserved for the module command!");

            return setting;
        }

        return super.register(setting);
    }

    @Override
    public void add(String string)
    {
        strings.add(formatString(string));
    }

    @Override
    public void del(String string)
    {
        strings.remove(formatString(string));
    }

    @Override
    public PossibleInputs getSettingInput(String input, String[] args)
    {
        if (input == null || input.isEmpty())
        {
            return new PossibleInputs("<add/del> <" + descriptor + ">", "");
        }

        //String[] arguments = input.split(" ");
        return getInput(input, args);
    }

    public boolean isValid(String string)
    {
        if (string == null)
        {
            return false;
        }

        if (listType.getValue() == ListType.WhiteList)
        {
            return strings.contains(formatString(string));
        }

        return !strings.contains(formatString(string));
    }

    public Collection<String> getList()
    {
        return strings;
    }

    public String getInput(String input, boolean add)
    {
        if (!add)
        {
            for (String s : strings)
            {
                if (TextUtil.startsWith(s, input))
                {
                    return TextUtil.substring(s, input.length());
                }
            }
        }

        return "";
    }

    protected void onSettingInput(String input)
    {
        add(formatString(input));
    }

    protected PossibleInputs getInput(String input, String[] args)
    {
        PossibleInputs inputs = PossibleInputs.empty()
                                              .setRest(" <" + descriptor + ">");
        if (args.length == 1)
        {
            if ("add".startsWith(args[0].toLowerCase()))
            {
                return inputs.setCompletion(
                        TextUtil.substring("add", args[0].length()));
            }
            else if ("del".startsWith(args[0].toLowerCase()))
            {
                return inputs.setCompletion(
                        TextUtil.substring("del", args[0].length()));
            }

            return inputs;
        }
        else if (args.length > 1)
        {
            inputs.setRest("");
            if (args[0].equalsIgnoreCase("add"))
            {
                return inputs.setCompletion(
                        getInput(input.substring(4), true));
            }
            else if (args[0].equalsIgnoreCase("del"))
            {
                return inputs.setCompletion(
                        getInput(input.substring(4), false));
            }

            return inputs;
        }

        return inputs.setRest("");
    }

    protected String formatString(String string)
    {
        return string.toLowerCase();
    }

}
