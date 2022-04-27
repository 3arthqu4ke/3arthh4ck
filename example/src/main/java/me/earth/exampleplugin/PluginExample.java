package me.earth.exampleplugin;

import me.earth.earthhack.api.plugin.Plugin;

@SuppressWarnings("unused")
public class PluginExample implements Plugin
{
    @Override
    public void load()
    {
        System.out.println("Hello from the ExamplePlugin!");
    }

}
