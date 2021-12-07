package me.earth.earthhack.impl.modules.player.freecam;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.freecam.mode.CamMode;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;

final class ListenerMotion extends ModuleListener<Freecam, MotionUpdateEvent>
{
    public ListenerMotion(Freecam module)
    {
        super(module, MotionUpdateEvent.class, 99999999/* -99999999 */);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (event.getStage() == Stage.PRE
                && module.mode.getValue() == CamMode.Position)
        {
            RayTraceResult result = mc.objectMouseOver;
            if (result != null)
            {
                float[] rotations = RotationUtil
                        .getRotations(result.hitVec.x,
                                      result.hitVec.y,
                                      result.hitVec.z,
                                      module.getPlayer());

                module.rotate(rotations[0], rotations[1]);
            }

            module.getPlayer().setHeldItem(EnumHand.MAIN_HAND,
                    mc.player.getHeldItemMainhand());
            module.getPlayer().setHeldItem(EnumHand.OFF_HAND,
                    mc.player.getHeldItemOffhand());

            event.setX(module.getPlayer().posX);
            event.setY(module.getPlayer().getEntityBoundingBox().minY);
            event.setZ(module.getPlayer().posZ);
            event.setYaw(module.getPlayer().rotationYaw);
            event.setPitch(module.getPlayer().rotationPitch);
            event.setOnGround(module.getPlayer().onGround);
        }
    }

}
