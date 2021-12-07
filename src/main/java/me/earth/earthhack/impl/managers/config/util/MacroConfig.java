package me.earth.earthhack.impl.managers.config.util;

import me.earth.earthhack.api.config.Config;
import me.earth.earthhack.api.register.exception.AlreadyRegisteredException;
import me.earth.earthhack.api.register.exception.CantUnregisterException;
import me.earth.earthhack.api.util.IdentifiedNameable;
import me.earth.earthhack.impl.managers.client.macro.Macro;
import me.earth.earthhack.impl.managers.client.macro.MacroManager;

import java.util.ArrayList;
import java.util.List;

public class MacroConfig extends IdentifiedNameable implements Config
{
    private final List<Macro> macros = new ArrayList<>();
    private final MacroManager manager;

    public MacroConfig(String name, MacroManager manager)
    {
        super(name);
        this.manager = manager;
    }

    public void add(Macro macro)
    {
        macros.add(macro);
    }

    public List<Macro> getMacros()
    {
        return macros;
    }

    @Override
    public void apply()
    {
        for (Macro macro : new ArrayList<>(manager.getRegistered()))
        {
            try
            {
                manager.unregister(macro);
            }
            catch (CantUnregisterException e)
            {
                e.printStackTrace();
            }
        }

        for (Macro macro : macros)
        {
            try
            {
                manager.register(macro);
            }
            catch (AlreadyRegisteredException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static MacroConfig create(String name, MacroManager manager)
    {
        MacroConfig config = new MacroConfig(name, manager);
        for (Macro macro : manager.getRegistered())
        {
            config.add(macro);
        }

        return config;
    }

}
