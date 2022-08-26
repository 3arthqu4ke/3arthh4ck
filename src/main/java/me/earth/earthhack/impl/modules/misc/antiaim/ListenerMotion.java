package me.earth.earthhack.impl.modules.misc.antiaim;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

final class ListenerMotion extends ModuleListener<AntiAim, MotionUpdateEvent>
{
    private static final Random RANDOM = new Random();
    private int skip;

    public ListenerMotion(AntiAim module)
    {
        super(module, MotionUpdateEvent.class, Integer.MAX_VALUE - 1000);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (event.getStage() == Stage.POST || module.dontRotate())
        {
            return;
        }

        if (module.skip.getValue() != 1 && skip++ % module.skip.getValue() == 0)
        {
            event.setYaw(module.lastYaw);
            event.setPitch(module.lastPitch);
            return;
        }

        switch (module.mode.getValue())
        {
            case Random:
                module.lastYaw = (float) ThreadLocalRandom.current()
                                                    .nextDouble(-180.0, 180.0);
                module.lastPitch = -90.0f + RANDOM.nextFloat() * (180.0f);
                break;
            case Spin:
                module.lastYaw   =
                        (module.lastYaw + module.hSpeed.getValue()) % 360;
                module.lastPitch =
                        (module.lastPitch + module.vSpeed.getValue());
                break;
            case Down:
                module.lastYaw = event.getYaw();
                module.lastPitch = 90.0f;
                break;
            case Headbang:
                module.lastYaw = event.getYaw();
                module.lastPitch =
                        (module.lastPitch + module.vSpeed.getValue());
                break;
            case Horizontal:
                module.lastPitch = event.getPitch();
                module.lastYaw   =
                    (module.lastYaw + module.hSpeed.getValue()) % 360;
                break;
            case Constant:
                event.setYaw(module.yaw.getValue());
                event.setPitch(module.pitch.getValue());
                return;
            case Flip:
                if (module.flipYaw.getValue())
                {
                    module.lastYaw = (event.getYaw() + 180) % 360;
                }

                if (module.flipPitch.getValue())
                {
                    module.lastPitch = -event.getPitch();
                }
            default:
        }

        if (module.lastPitch > 90.0f && module.lastPitch != event.getPitch())
        {
            module.lastPitch = -90.0f;
        }

        event.setYaw(module.lastYaw);
        event.setPitch(module.lastPitch);
    }

}
