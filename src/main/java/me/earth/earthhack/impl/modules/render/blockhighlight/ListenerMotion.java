package me.earth.earthhack.impl.modules.render.blockhighlight;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.minecraft.movement.PositionManager;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

final class ListenerMotion extends
        ModuleListener<BlockHighlight, MotionUpdateEvent>
{
    public ListenerMotion(BlockHighlight module)
    {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (event.getStage() == Stage.POST
                && module.distance.getValue()
                && module.current != null
                && mc.player != null
                && mc.objectMouseOver != null
                && mc.objectMouseOver.hitVec != null)
        {
            RayTraceResult r = mc.objectMouseOver;

            double d;
            boolean see;
            double x;
            double y;
            double z;
            boolean noPosition = false;
            //noinspection ConstantConditions
            if (r.typeOfHit == RayTraceResult.Type.BLOCK
                    && r.getBlockPos() != null)
            {
                BlockPos p = r.getBlockPos();
                if (module.hitVec.getValue())
                {
                    d = Managers.POSITION.getVec()
                                         .add(0.0, module.eyes.getValue()
                                                ? mc.player.getEyeHeight()
                                                : 0.0,
                                              0.0)
                                         .distanceTo(r.hitVec);
                }
                else if (module.toCenter.getValue())
                {
                    d = Managers.POSITION.getVec()
                                         .add(0.0, module.eyes.getValue()
                                             ? mc.player.getEyeHeight()
                                             : 0.0, 0.0)
                                         .distanceTo(
                     new Vec3d(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5));
                }
                else
                {
                    d = Managers.POSITION.getVec()
                                         .add(0.0, module.eyes.getValue()
                                             ? mc.player.getEyeHeight()
                                             : 0.0, 0.0)
                                         .distanceTo(new Vec3d(p));
                }

                x = p.getX();
                y = p.getY();
                z = p.getZ();

                see = canSee(r.hitVec, Managers.POSITION);
            }
            else if (r.typeOfHit == RayTraceResult.Type.ENTITY
                        && r.entityHit != null)
            {
                Entity e = r.entityHit;
                d = r.entityHit.getDistance(Managers.POSITION.getX(),
                                            Managers.POSITION.getY()
                                                + (module.eyes.getValue()
                                                    ? mc.player.getEyeHeight()
                                                    : 0.0),
                                            Managers.POSITION.getZ());

                see = canSee(
                    new Vec3d(e.posX, e.posY + e.getEyeHeight(), e.posZ),
                    Managers.POSITION);

                x = e.posX;
                y = e.posY;
                z = e.posZ;
            }
            else
            {
                d = Managers.POSITION.getVec()
                                     .add(0.0,
                                          module.eyes.getValue()
                                            ? mc.player.getEyeHeight()
                                            : 0.0,
                                          0.0)
                                     .distanceTo(r.hitVec);

                see = canSee(r.hitVec, Managers.POSITION);
                x = y = z = 0.0;
                noPosition = true;
            }

            StringBuilder builder = new StringBuilder(module.current);
            builder.append(", ");

            if (d >= 6.0)
            {
                builder.append(TextColor.RED);
            }
            else if (d >= 3.0 && !see)
            {
                builder.append(TextColor.GOLD);
            }
            else
            {
                builder.append(TextColor.GREEN);
            }

            builder.append(MathUtil.round(d, 2));
            if (module.position.getValue() && !noPosition)
            {
                builder.append(TextColor.WHITE).append(", ").append(MathUtil.round(x, 2)).append(TextColor.GRAY).append("x")
                       .append(TextColor.WHITE).append(", ").append(MathUtil.round(y, 2)).append(TextColor.GRAY).append("y")
                       .append(TextColor.WHITE).append(", ").append(MathUtil.round(z, 2)).append(TextColor.GRAY).append("z");
            }

            module.current = builder.toString();
        }
    }

    private boolean canSee(Vec3d toSee, PositionManager m)
    {
        return RayTraceUtil.canBeSeen(toSee,
                                      m.getX(),
                                      m.getY(),
                                      m.getZ(),
                                      mc.player.getEyeHeight());
    }

}
