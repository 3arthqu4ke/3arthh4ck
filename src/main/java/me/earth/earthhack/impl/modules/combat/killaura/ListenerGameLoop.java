package me.earth.earthhack.impl.modules.combat.killaura;

import me.earth.earthhack.impl.event.events.misc.GameLoopEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

final class ListenerGameLoop extends ModuleListener<KillAura, GameLoopEvent>
{
    public ListenerGameLoop(KillAura module)
    {
        super(module, GameLoopEvent.class);
    }

    @Override
    public void invoke(GameLoopEvent event)
    {
        Entity from = RotationUtil.getRotationPlayer();
        if (mc.world == null || from == null)
        {
            return;
        }

        boolean k = DamageUtil.isSharper(mc.player.getHeldItemMainhand(), 1000);
        boolean multi = module.multi32k.getValue() && k;
        if ((module.target != null || multi)
            && module.shouldAttack()
            && (!module.rotate.getValue()
                || RotationUtil.isLegit(module.target))
            && (!module.delay.getValue()
                || module.t2k.getValue() && k)
            && module.cps.getValue() > 20
            && module.timer.passed((long) (1000.0 / module.cps.getValue())))
        {
            if (multi)
            {
                int packets = 0;
                for (EntityPlayer player : mc.world.playerEntities)
                {
                    if (module.isValid(player)
                            && module.isInRange(Managers.POSITION.getVec(),
                                                player))
                    {
                        PacketUtil.attack(player);
                        if (++packets >= module.packets.getValue())
                        {
                            break;
                        }
                    }
                }
            }
            else
            {
                for (int i = 0; i < module.packets.getValue(); i++)
                {
                    PacketUtil.attack(module.target);
                }

                if (module.swing.getValue() == Swing.Client
                    || module.swing.getValue() == Swing.Full)
                {
                    Swing.Client.swing(EnumHand.MAIN_HAND);
                }
            }

            module.timer.reset((long) (1000.0 / module.cps.getValue()));
        }
    }
}
