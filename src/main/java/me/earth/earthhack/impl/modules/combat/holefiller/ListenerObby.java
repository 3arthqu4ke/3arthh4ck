package me.earth.earthhack.impl.modules.combat.holefiller;

import me.earth.earthhack.api.event.bus.api.EventBus;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.holes.HoleRunnable;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyListener;
import me.earth.earthhack.impl.util.helpers.blocks.util.TargetResult;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.HoleUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

final class ListenerObby extends ObbyListener<HoleFiller>
{
    private boolean wasRunning;

    public ListenerObby(HoleFiller module)
    {
        super(module, EventBus.DEFAULT_PRIORITY);
    }

    @Override
    public void onModuleToggle()
    {
        super.onModuleToggle();
        wasRunning = false;
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        module.target = null;
        if ((wasRunning || !module.requireTarget.getValue())
            && module.disable.getValue() != 0
            && module.disableTimer.passed(module.disable.getValue()))
        {
            module.disable();
            return;
        }

        if (module.requireTarget.getValue())
        {
            module.target = EntityUtil.getClosestEnemy();
            if (module.target == null
                || module.target.getDistanceSq(mc.player)
                    > MathUtil.square(module.targetRange.getValue())
                || module.waitForHoleLeave.getValue()
                    && (HoleUtil.is1x1(
                            PositionUtil.getPosition(module.target))[0]
                        || HoleUtil.is2x1(
                            PositionUtil.getPosition(module.target), false)
                        || HoleUtil.is2x1(
                            PositionUtil.getPosition(module.target), false)))
            {
                module.waiting = true;
                return;
            }
        }

        module.waiting = false;
        if (!wasRunning)
        {
            module.disableTimer.reset();
        }

        wasRunning = true;
        super.invoke(event);
    }

    @Override
    protected TargetResult getTargets(TargetResult result)
    {
        if (module.calcTimer.passed(module.calcDelay.getValue()))
        {
            HoleRunnable runnable = new HoleRunnable(module, module);
            runnable.run();
            module.calcTimer.reset();
        }

        List<BlockPos> targets = new ArrayList<>(module.safes.size()
                                                 + module.unsafes.size()
                                                 + module.longs.size()
                                                 + module.bigs.size());
        targets.addAll(module.safes);
        targets.addAll(module.unsafes);
        if (module.longHoles.getValue())
        {
            targets.addAll(module.longs);
        }

        if (module.bigHoles.getValue())
        {
            targets.addAll(module.bigs);
        }

        BlockPos playerPos = PositionUtil.getPosition();
        if (!HoleUtil.isHole(playerPos, false)[0]
            && !HoleUtil.is2x1(playerPos)
            && !HoleUtil.is2x2(playerPos)
            && (!module.safety.getValue() || !Managers.SAFETY.isSafe()))
        {
            EntityPlayer p = RotationUtil.getRotationPlayer();
            Vec3d v = p.getPositionVector()
                       .add(p.motionX, p.motionY, p.motionZ);

            targets.removeIf(pos -> pos.distanceSq(v.x, v.y, v.z)
                                < MathUtil.square(module.minSelf.getValue()));
            // place furthest first
            targets.sort(Comparator.comparingDouble(
                            pos -> -BlockUtil.getDistanceSq(pos)));
        }

        module.target = EntityUtil.getClosestEnemy();
        if (module.target != null)
        {
            targets.removeIf(p -> BlockUtil.getDistanceSq(module.target, p)
                                    > MathUtil.square(
                                        module.targetDistance.getValue()));
            targets.sort(Comparator.comparingDouble(p ->
                    BlockUtil.getDistanceSq(module.target, p)));
        }

        result.setTargets(targets);
        return result;
    }

}
