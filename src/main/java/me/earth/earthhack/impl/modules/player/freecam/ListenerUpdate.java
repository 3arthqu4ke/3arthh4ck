package me.earth.earthhack.impl.modules.player.freecam;

import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;

final class ListenerUpdate extends ModuleListener<Freecam, UpdateEvent>
{
    public ListenerUpdate(Freecam module)
    {
        super(module, UpdateEvent.class);
    }

    @Override
    public void invoke(UpdateEvent event)
    {
        mc.player.noClip = true;
        mc.player.setVelocity(0, 0, 0);
        mc.player.jumpMovementFactor = module.speed.getValue();
        double[] dir = MovementUtil.strafe(module.speed.getValue());
        if (mc.player.movementInput.moveStrafe != 0
                || mc.player.movementInput.moveForward != 0)
        {
            mc.player.motionX = dir[0];
            mc.player.motionZ = dir[1];
        }
        else
        {
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        }

        mc.player.setSprinting(false);

        if (mc.gameSettings.keyBindJump.isKeyDown())
        {
            mc.player.motionY += module.speed.getValue();
        }

        if (mc.gameSettings.keyBindSneak.isKeyDown())
        {
            mc.player.motionY -= module.speed.getValue();
        }
    }

}
