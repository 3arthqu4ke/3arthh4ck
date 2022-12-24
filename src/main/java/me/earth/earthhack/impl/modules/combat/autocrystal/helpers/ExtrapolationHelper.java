package me.earth.earthhack.impl.modules.combat.autocrystal.helpers;

import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.entity.IEntityPlayer;
import me.earth.earthhack.impl.event.events.misc.UpdateEntitiesEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.MotionTracker;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class ExtrapolationHelper extends SubscriberImpl implements Globals {
    private final AutoCrystal module;

    public ExtrapolationHelper(AutoCrystal module) {
        this.module = module;
        this.listeners.add(new LambdaListener<>(UpdateEntitiesEvent.class, e -> {
            for (EntityPlayer player : mc.world.playerEntities) {
                MotionTracker tracker = ((IEntityPlayer) player).getMotionTracker();
                MotionTracker breakTracker = ((IEntityPlayer) player).getBreakMotionTracker();
                MotionTracker blockTracker = ((IEntityPlayer) player).getBlockMotionTracker();
                if (EntityUtil.isDead(player)
                    || RotationUtil.getRotationPlayer().getDistanceSq(player) > 400
                    || !module.selfExtrapolation.getValue()
                        && player.equals(RotationUtil.getRotationPlayer())) {
                    if (tracker != null) {
                        tracker.active = false;
                    }

                    if (breakTracker != null) {
                        breakTracker.active = false;
                    }

                    if (blockTracker != null) {
                        blockTracker.active = false;
                    }

                    continue;
                }

                if (tracker == null && module.extrapol.getValue() != 0) {
                    tracker = new MotionTracker(mc.world, player);
                    ((IEntityPlayer) player).setMotionTracker(tracker);
                }

                if (breakTracker == null && module.bExtrapol.getValue() != 0) {
                    breakTracker = new MotionTracker(mc.world, player);
                    ((IEntityPlayer) player).setBreakMotionTracker(breakTracker);
                }

                if (blockTracker == null && module.blockExtrapol.getValue() != 0) {
                    blockTracker = new MotionTracker(mc.world, player);
                    ((IEntityPlayer) player).setBlockMotionTracker(blockTracker);
                }

                updateTracker(tracker, module.extrapol.getValue());
                updateTracker(breakTracker, module.bExtrapol.getValue());
                updateTracker(blockTracker, module.blockExtrapol.getValue());
            }
        }));
    }

    private void updateTracker(MotionTracker tracker, int ticks) {
        if (tracker == null) {
            return;
        }

        tracker.active = false;
        tracker.copyLocationAndAnglesFrom(tracker.tracked);
        tracker.gravity = module.gravityExtrapolation.getValue();
        tracker.gravityFactor = module.gravityFactor.getValue();
        tracker.yPlusFactor = module.yPlusFactor.getValue();
        tracker.yMinusFactor = module.yMinusFactor.getValue();
        for (tracker.ticks = 0; tracker.ticks < ticks; tracker.ticks++) {
            tracker.updateFromTrackedEntity();
        }

        tracker.active = true;
    }

    public MotionTracker getTrackerFromEntity(Entity player) {
        return ((IEntityPlayer) player).getMotionTracker();
    }

    public MotionTracker getBreakTrackerFromEntity(Entity player) {
        return ((IEntityPlayer) player).getBreakMotionTracker();
    }

    public MotionTracker getBlockTracker(Entity player) {
        return ((IEntityPlayer) player).getBlockMotionTracker();
    }

}
