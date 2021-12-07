package me.earth.earthhack.impl.modules.combat.offhand;

import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.offhand.modes.OffhandMode;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.init.Items;

final class ListenerKeyboard extends ModuleListener<Offhand, KeyboardEvent>
{
    public ListenerKeyboard(Offhand module)
    {
        super(module, KeyboardEvent.class);
    }

    @Override
    public void invoke(KeyboardEvent event)
    {
        if (event.getEventState())
        {
            if (event.getKey() == module.gappleBind.getValue().getKey())
            {
                if (module.cToTotem.getValue()
                    && (!module.crystalsIfNoTotem.getValue()
                        || InventoryUtil.getCount(Items.TOTEM_OF_UNDYING) != 0
                        || !module.setSlotTimer.passed(250))
                    && OffhandMode.CRYSTAL.equals(module.getMode()))
                {
                    module.setMode(OffhandMode.TOTEM);
                }
                else
                {
                    module.setMode(module.getMode() == OffhandMode.GAPPLE
                            ? OffhandMode.TOTEM
                            : OffhandMode.GAPPLE);
                }
            }
            else if (event.getKey() == module.crystalBind.getValue().getKey())
            {
                module.setMode(OffhandMode.CRYSTAL.equals(module.getMode())
                        ? OffhandMode.TOTEM
                        : OffhandMode.CRYSTAL);
            }
        }
    }

}
