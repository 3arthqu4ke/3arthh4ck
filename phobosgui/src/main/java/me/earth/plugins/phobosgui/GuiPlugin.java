package me.earth.plugins.phobosgui;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.plugin.Plugin;
import me.earth.earthhack.api.register.exception.AlreadyRegisteredException;
import me.earth.earthhack.impl.managers.Managers;

import java.util.ArrayList;
import java.util.List;

public class GuiPlugin implements Plugin {
    private final List<Module> moduleList = new ArrayList<>();

    @Override
    public void load() {
        try {
            Managers.MODULES.register(PhobosGuiModule.getInstance());
        } catch (AlreadyRegisteredException e) {
            e.printStackTrace();
        }
        try {
            Managers.MODULES.register(PhobosColorModule.getInstance());
        } catch (AlreadyRegisteredException e) {
            e.printStackTrace();
        }

        Bus.EVENT_BUS.subscribe(PhobosTextManager.getInstance());
    }

}
