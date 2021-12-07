package me.earth.earthhack.impl.modules.movement.highjump;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;

final class ListenerMove extends ModuleListener<HighJump, MoveEvent>
{
    public ListenerMove(HighJump module)
    {
        super(module, MoveEvent.class);
    }

    @Override
    public void invoke(MoveEvent event)
    {
        if (!Managers.NCP.passed(module.lagTime.getValue())
                || !mc.player.movementInput.jump
                || module.onGround.getValue() && !mc.player.onGround)
        {
            return;
        }

        if (module.explosions.getValue() || module.velocity.getValue())
        {
            if (module.motionY < module.minY.getValue())
            {
                return;
            }

            if (!module.timer.passed(module.delay.getValue()))
            {
                mc.player.motionY = module.constant.getValue()
                        ? module.height.getValue()
                        : module.motionY * module.factor.getValue();

                if (mc.player.motionY < 0.42f)
                {
                    mc.player.motionY = 0.42f;
                }

                event.setY(mc.player.motionY);
                return;
            }
        }

        if (module.onlySpecial.getValue())
        {
            return;
        }

        mc.player.motionY = module.height.getValue();
        event.setY(mc.player.motionY);
    }

}
