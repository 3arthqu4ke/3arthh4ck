package me.earth.earthhack.impl.modules.misc.autolog;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;

final class ListenerTick extends ModuleListener<AutoLog, TickEvent>
{
    public ListenerTick(AutoLog module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        if (mc.world != null && mc.player != null)
        {
            float health = module.absorption.getValue()
                            ? EntityUtil.getHealth(mc.player)
                            : mc.player.getHealth();

            if (health <= module.health.getValue())
            {
                EntityPlayer player = module.enemy.getValue() == 100
                        ? null
                        : EntityUtil.getClosestEnemy();

                if (module.enemy.getValue() == 100
                        || player != null
                            && player.getDistanceSq(mc.player)
                                <= MathUtil.square(module.enemy.getValue()))
                {
                    int totems = InventoryUtil.getCount(Items.TOTEM_OF_UNDYING);
                    if (totems <= module.totems.getValue())
                    {
                        module.disconnect(health, player, totems);
                    }
                }
            }
        }
    }

}
