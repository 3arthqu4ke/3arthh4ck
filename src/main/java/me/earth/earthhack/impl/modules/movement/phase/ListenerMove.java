package me.earth.earthhack.impl.modules.movement.phase;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.phase.mode.PhaseMode;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;

final class ListenerMove
        extends ModuleListener<Phase, MoveEvent>
{
    public ListenerMove(Phase module)
    {
        super(module, MoveEvent.class);
    }

    @Override
    public void invoke(MoveEvent event)
    {
        if (module.mode.getValue() == PhaseMode.Constantiam
                && module.isPhasing())
        {
            if (module.constStrafe.getValue()) {
                MovementUtil.strafe(event, 0.2873 * module.constSpeed.getValue());
            }
        }

        if (module.mode.getValue() == PhaseMode.ConstantiamNew
                && module.isPhasing()) {
            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                event.setY(mc.player.motionY += 0.09f);
            } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                event.setY(mc.player.motionY -= 0.0);
            } else {
                mc.player.motionY = 0.0;
                event.setY(0.0);
            }
            MovementUtil.strafe(event, 0.2783);
        }
    }
}
