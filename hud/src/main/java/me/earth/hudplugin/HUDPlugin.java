package me.earth.hudplugin;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.plugin.Plugin;
import me.earth.earthhack.impl.managers.Managers;

@SuppressWarnings("unused")
public class HUDPlugin implements Plugin
{
    @Override
    public void load()
    {
        try
        {
            Managers.MODULES.register(HUDModule.getInstance());
            Managers.MODULES.register(ComeponentModule.INSTANCE);
            Bus.EVENT_BUS.subscribe(PotionManager.INSTANCE);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
