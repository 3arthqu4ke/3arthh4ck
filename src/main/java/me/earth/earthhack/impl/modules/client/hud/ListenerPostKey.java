package me.earth.earthhack.impl.modules.client.hud;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Hidden;
import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.hud.modes.Modules;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.pingbypass.PingBypass;
import me.earth.earthhack.pingbypass.modules.PbModule;
import net.minecraft.client.gui.ScaledResolution;

import java.util.*;

final class ListenerPostKey extends ModuleListener<HUD, KeyboardEvent.Post>
{
    private final Set<Module> addedModules = new HashSet<>();

    public ListenerPostKey(HUD module)
    {
        super(module, KeyboardEvent.Post.class);
    }

    @Override
    public void invoke(KeyboardEvent.Post event)
    {
        if (mc.player == null || mc.world == null)
        {
            return;
        }

        module.resolution = new ScaledResolution(mc);
        module.width = module.resolution.getScaledWidth();
        module.height = module.resolution.getScaledHeight();
        module.modules.clear();
        addedModules.clear();

        if (module.renderModules.getValue() != Modules.None)
        {
            for (Module mod : Managers.MODULES.getRegistered())
            {
                if (mod.isEnabled() && mod.isHidden() != Hidden.Hidden)
                {
                    Map.Entry<String, Module> entry = new AbstractMap.SimpleEntry<>(ModuleUtil.getHudName(mod), mod);
                    addedModules.add(mod);
                    module.modules.add(entry);
                }
            }

            for (Module mod : PingBypass.MODULES.getRegistered())
            {
                if (mod.isEnabled() && mod.isHidden() != Hidden.Hidden
                    && !(addedModules.contains(mod) || mod instanceof PbModule && addedModules.contains(((PbModule) mod).getModule())))
                {
                    Map.Entry<String, Module> entry = new AbstractMap.SimpleEntry<>(ModuleUtil.getHudName(mod), mod);
                    addedModules.add(mod);
                    module.modules.add(entry);
                }
            }

            if (module.renderModules.getValue() == Modules.Length)
            {
                module.modules.sort(Comparator.comparing(entry -> Managers.TEXT.getStringWidth(entry.getKey()) *  -1));
            }
            else
            {
                module.modules.sort(Map.Entry.comparingByKey());
            }
        }
    }

}
