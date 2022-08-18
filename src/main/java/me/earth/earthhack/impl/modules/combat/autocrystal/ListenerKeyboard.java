package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.AutoSwitch;
import me.earth.earthhack.impl.modules.combat.offhand.Offhand;
import me.earth.earthhack.impl.modules.combat.offhand.modes.OffhandMode;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.text.TextColor;

final class ListenerKeyboard extends ModuleListener<AutoCrystal, KeyboardEvent>
{
    private static final ModuleCache<Offhand> OFFHAND =
            Caches.getModule(Offhand.class);

    public ListenerKeyboard(AutoCrystal module)
    {
        super(module, KeyboardEvent.class);
    }

    @Override
    public void invoke(KeyboardEvent event)
    {
        if (event.getEventState()
                && event.getKey() == module.switchBind.getValue().getKey())
        {
            if (module.useAsOffhand.getValue() || module.isPingBypass())
            {
                OffhandMode m = OFFHAND.returnIfPresent(Offhand::getMode, null);
                if (m != null)
                {
                    if (m.equals(OffhandMode.CRYSTAL))
                    {
                        OFFHAND.computeIfPresent(o ->
                                o.setMode(OffhandMode.TOTEM));
                    }
                    else
                    {
                        OFFHAND.computeIfPresent(o ->
                                o.setMode(OffhandMode.CRYSTAL));
                    }
                }

                module.switching = false;
            }
            else if (module.autoSwitch.getValue() == AutoSwitch.Bind)
            {
                module.switching = !module.switching;
                if (module.switchMessage.getValue()) {
                    ModuleUtil.sendMessageWithAquaModule(module,
                                           module.switching
                                               ? TextColor.GREEN + "Switch on"
                                               : TextColor.RED + "Switch off",
                                            "");
                }
            }
        }
    }

}
