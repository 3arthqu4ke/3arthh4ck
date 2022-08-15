package me.earth.earthhack.pingbypass.listeners;

import com.google.common.collect.Sets;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.impl.managers.minecraft.KeyBoardManager;
import me.earth.earthhack.impl.modules.client.clickgui.ClickGui;
import me.earth.earthhack.impl.modules.client.pbgui.PbGui;
import me.earth.earthhack.impl.modules.player.fakeplayer.FakePlayer;
import me.earth.earthhack.pingbypass.PingBypass;

import java.util.Set;

public class PbClientModuleKeyboardService extends KeyBoardManager {
    // these modules are the same in for Pb and client
    private final Set<Class<?>> blackList = Sets.newHashSet(FakePlayer.class,
                                                            ClickGui.class,
                                                            PbGui.class);

    public PbClientModuleKeyboardService() {
        super(PingBypass.MODULES);
    }

    @Override
    protected boolean isModuleValid(Module module) {
        return !PingBypass.isConnected()
            && !blackList.contains(module.getClass());
    }

}
