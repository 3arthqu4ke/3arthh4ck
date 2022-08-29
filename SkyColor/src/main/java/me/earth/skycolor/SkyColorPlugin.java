package me.earth.skycolor;

import me.earth.earthhack.api.plugin.Plugin;
import me.earth.earthhack.api.register.exception.AlreadyRegisteredException;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.client.PluginDescriptions;

@SuppressWarnings("unused")
public class SkyColorPlugin implements Plugin {
    @Override
    public void load() {
        PluginDescriptions.register(this, "Colors the Sky");
        try {
            Managers.MODULES.register(new SkyColor());
        } catch (AlreadyRegisteredException e) {
            e.printStackTrace();
        }
    }

}
