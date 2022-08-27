package me.earth.earthhack.impl.modules.misc.tracker;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.entity.player.EntityPlayer;

final class ListenerTick extends ModuleListener<Tracker, TickEvent>
{
    public ListenerTick(Tracker module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        if (event.isSafe() && module.isEnabled())
        {
            boolean found = false;
            for (EntityPlayer player : mc.world.playerEntities)
            {
                if (player == null
                        || player.equals(mc.player)
                        || player.equals(RotationUtil.getRotationPlayer())
                        || EntityUtil.isDead(player)
                        || PlayerUtil.isFakePlayer(player))
                {
                    continue;
                }

                if (found && module.only1v1.getValue())
                {
                    ModuleUtil.disableRed(module,
                            "Disabled, you are not in a 1v1!");
                    return;
                }

                if (module.trackedPlayer == null)
                {
                    ModuleUtil.sendMessage(module, TextColor.LIGHT_PURPLE
                            + "Now tracking "
                            + TextColor.DARK_PURPLE
                            + player.getName()
                            + TextColor.LIGHT_PURPLE
                            + "!");
                }

                module.trackedPlayer = player;
                found = true;
            }

            if (module.trackedPlayer == null)
            {
                return;
            }

            int exp = module.exp.get() / 64;
            if (module.expStacks != exp)
            {
                module.expStacks = exp;
                ModuleUtil.sendMessage(module, TextColor.DARK_PURPLE
                        + module.trackedPlayer.getName()
                        + TextColor.LIGHT_PURPLE
                        + " used "
                        + TextColor.WHITE
                        + exp
                        + TextColor.LIGHT_PURPLE
                        + (exp == 1 ? " stack" : " stacks")
                        + " of Exp!", "Exp");
            }

            int crystals = module.crystals.get() / 64;
            if (module.crystalStacks != crystals)
            {
                module.crystalStacks = crystals;
                ModuleUtil.sendMessage(module, TextColor.DARK_PURPLE
                        + module.trackedPlayer.getName()
                        + TextColor.LIGHT_PURPLE
                        + " used "
                        + TextColor.WHITE
                        + crystals
                        + TextColor.LIGHT_PURPLE
                        + (crystals == 1 ? " stack" : " stacks")
                        + " of Crystals!", "Crystals");
            }
        }
        else if (module.awaiting)
        {
            if (module.timer.passed(5000))
            {
                module.enable();
                module.awaiting = false;
                return;
            }

            double time =
                    MathUtil.round((5000 - module.timer.getTime()) / 1000.0, 1);

            ModuleUtil.sendMessage(module,
                TextColor.LIGHT_PURPLE
                + "Duel accepted. Tracker will enable in "
                + TextColor.WHITE
                + time
                + TextColor.LIGHT_PURPLE
                + " seconds!");
        }
    }

}
