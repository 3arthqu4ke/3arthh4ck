package me.earth.earthhack.impl.managers.config;

import me.earth.earthhack.api.config.ConfigHelper;
import me.earth.earthhack.api.register.AbstractRegister;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.config.helpers.*;
import me.earth.earthhack.impl.util.misc.io.BiIOConsumer;
import me.earth.earthhack.impl.util.misc.io.IOConsumer;
import me.earth.earthhack.pingbypass.PingBypass;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class ConfigManager extends AbstractRegister<ConfigHelper<?>>
{
    public ConfigManager()
    {
        super(new LinkedHashMap<>());
        this.registered.put("macro",  new MacroConfigHelper(Managers.MACRO));
        this.registered.put("friend", new FriendConfigHelper());
        this.registered.put("enemy",  new EnemyConfigHelper());

        if (!PingBypass.isServer()) {
            this.registered.put("bind",   new BindConfigHelper());
        }

        // load modules last!
        this.registered.put("module", new ModuleConfigHelper(Managers.MODULES));
    }

    public void saveAll() throws IOException
    {
        for (ConfigHelper<?> helper : getRegistered())
        {
            Earthhack.getLogger().info("Config saving: " + helper.getName());
            helper.save();
        }

        Earthhack.getLogger().info("Config saving: current");
        CurrentConfig.getInstance().save();
    }

    public void refreshAll() throws IOException
    {
        Earthhack.getLogger().info("Config refreshing: current");
        CurrentConfig.getInstance().refresh();
        for (ConfigHelper<?> helper : getRegistered())
        {
            Earthhack.getLogger().info("Config refreshing: " + helper.getName());
            helper.refresh();

            String current = CurrentConfig.getInstance().get(helper);
            if (current == null)
            {
                CurrentConfig.getInstance().set(helper, "default");
            }

            helper.load(CurrentConfig.getInstance().get(helper));
        }
    }

    public void save(ConfigHelper<?> helper, String...args) throws IOException
    {
        Earthhack.getLogger().info("Config: saving " + helper.getName() + " "
                                    + Arrays.toString(args));
        execute(helper, ConfigHelper::save, ConfigHelper::save, args);
    }

    public void load(ConfigHelper<?> helper, String...args) throws IOException
    {
        Earthhack.getLogger().info("Config: loading " + helper.getName() + " "
                                    + Arrays.toString(args));
        execute(helper, h -> {}, ConfigHelper::load, args);
    }

    public void refresh(ConfigHelper<?> helper, String...args) throws IOException
    {
        Earthhack.getLogger().info("Config: refreshing " + helper.getName()
                                    + " " + Arrays.toString(args));
        execute(helper, ConfigHelper::refresh, ConfigHelper::refresh, args);
    }

    private void execute(ConfigHelper<?> helper,
                         IOConsumer<ConfigHelper<?>> ifNoArgs,
                         BiIOConsumer<ConfigHelper<?>, String> consumer,
                         String...args) throws IOException
    {
        if (args == null || args.length == 0)
        {
            ifNoArgs.accept(helper);
            return;
        }

        for (String arg : args)
        {
            consumer.accept(helper, arg);
        }
    }

}
