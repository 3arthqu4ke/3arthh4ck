package me.earth.earthhack.impl.modules.combat.legswitch;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.legswitch.modes.LegAutoSwitch;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.init.Items;

final class ListenerMotion extends ModuleListener<LegSwitch, MotionUpdateEvent>
{
    public ListenerMotion(LegSwitch module)
    {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (!module.isStackValid(mc.player.getHeldItemOffhand())
                && !module.isStackValid(mc.player.getHeldItemMainhand()))
        {
            module.active = false;
            return;
        }

        if (!InventoryUtil.isHolding(Items.END_CRYSTAL))
        {
            if (module.autoSwitch.getValue() == LegAutoSwitch.None
                    || InventoryUtil.findHotbarItem(Items.END_CRYSTAL) == -1)
            {
                module.active = false;
                return;
            }
        }

        if (event.getStage() == Stage.PRE)
        {
            if (module.constellation == null
                || !module.constellation.isValid(module, mc.player, mc.world))
            {
                module.constellation = ConstellationFactory.create(module,
                                                       mc.world.playerEntities);
                if (module.constellation != null
                    && !module.obsidian.getValue()
                    && (module.constellation.firstNeedsObby
                        || module.constellation.secondNeedsObby))
                {
                    module.constellation = null;
                }
            }

            if (module.constellation == null)
            {
                module.active = false;
                return;
            }

            module.active = true;
            module.prepare();
            if (module.rotations != null)
            {
                event.setYaw(module.rotations[0]);
                event.setPitch(module.rotations[1]);
            }
        }
        else
        {
            module.execute();
        }
    }

}
