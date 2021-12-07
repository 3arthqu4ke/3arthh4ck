package me.earth.earthhack.impl.util.helpers.blocks.modes;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.init.Items;

public enum Pop implements Globals
{
    None
    {
        @Override
        public boolean shouldPop(float damage, int popTime)
        {
            return damage < EntityUtil.getHealth(mc.player) + 1.0;
        }
    },
    Time
    {
        @Override
        public boolean shouldPop(float damage, int popTime)
        {
            return None.shouldPop(damage, popTime)
                    || mc.player.getHeldItemOffhand().getItem()
                        == Items.TOTEM_OF_UNDYING
                            && Managers.COMBAT.lastPop(mc.player) < popTime;
        }
    },
    Always
    {
        @Override
        public boolean shouldPop(float damage, int popTime)
        {
            return None.shouldPop(damage, popTime)
                    || mc.player.getHeldItemOffhand().getItem()
                                == Items.TOTEM_OF_UNDYING;
        }
    };

    public abstract boolean shouldPop(float damage, int popTime);
}
