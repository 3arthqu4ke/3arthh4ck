package me.earth.earthhack.pingbypass.modules;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.register.exception.AlreadyRegisteredException;
import me.earth.earthhack.impl.managers.client.ModuleManager;
import me.earth.earthhack.impl.modules.client.clickgui.ClickGui;
import me.earth.earthhack.impl.modules.client.nospoof.NoSpoof;
import me.earth.earthhack.impl.modules.client.pbteleport.PbTeleport;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassSubmodule;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.misc.nointerp.NoInterp;
import me.earth.earthhack.impl.modules.player.fakeplayer.FakePlayer;
import me.earth.earthhack.impl.modules.player.suicide.Suicide;

public class PbModuleManager extends ModuleManager {
    public void init(ModuleManager manager) {
        for (Module module : manager.getRegistered()) {
            if (module instanceof PingBypassModule) {
                return;
            }

            Module pbModule;
            if (module instanceof FakePlayer
                || module instanceof ClickGui
                || module instanceof PingBypassSubmodule
                || module instanceof PbTeleport
                || module instanceof NoSpoof) {
                pbModule = module;
            } else if (module instanceof AutoCrystal
                || module instanceof Suicide) {
                pbModule = new PbModule(module);
                PbAC.registerAcPages(pbModule);
            } else {
                pbModule = new PbModule(module);
            }

            if (module instanceof NoInterp) {
                pbModule.enable();
            }

            try {
                this.register(pbModule);
            } catch (AlreadyRegisteredException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void init() {

    }

}
