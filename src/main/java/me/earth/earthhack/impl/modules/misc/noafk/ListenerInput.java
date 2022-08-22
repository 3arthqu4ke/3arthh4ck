package me.earth.earthhack.impl.modules.misc.noafk;

import me.earth.earthhack.impl.event.events.movement.MovementInputEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;

import java.util.Random;

final class ListenerInput extends ModuleListener<NoAFK, MovementInputEvent>
{
    private final Random random = new Random();
    private boolean backwards = false;

    public ListenerInput(NoAFK module)
    {
        super(module, MovementInputEvent.class);
    }

    @Override
    public void invoke(MovementInputEvent event)
    {
        if (Managers.NCP.passed(module.lagTime.getValue()))
        {
            if (module.sneak.getValue())
            {
                if (module.sneak_timer.passed(2000))
                {
                    module.sneaking = !module.sneaking;
                    module.sneak_timer.reset();
                }

                event.getInput().sneak = module.sneaking;
            }

            if (module.jump.getValue()
                && module.jumpTimer.passed(module.jumpDelay.getValue() * 1000))
            {
                event.getInput().jump = true;
                module.jumpTimer.reset();
            }

            if (module.walk.getValue())
            {
                if (module.walkTimer.passed(module.walking ? (module.walkFor.getValue() * 1000) : (module.waitFor.getValue()) * 1000))
                {
                    backwards = module.randomlyBackwards.getValue() && random.nextBoolean();
                    module.walking = !module.walking;
                    module.walkTimer.reset();
                    if (!module.walking && mc.player != null)
                    {
                        mc.player.rotationYaw = (mc.player.rotationYaw + module.yaw.getValue()) % 360;
                    }
                }

                event.getInput().moveForward = module.walking ? (backwards ? -1.0f : 1.0f) : 0.0f;
                if (event.getInput().sneak)
                {
                    event.getInput().moveForward *= 0.3f;
                }
            }
        }
    }

}
