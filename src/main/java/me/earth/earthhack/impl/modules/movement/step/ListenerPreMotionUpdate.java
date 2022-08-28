package me.earth.earthhack.impl.modules.movement.step;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import me.earth.earthhack.pingbypass.protocol.c2s.C2SStepPacket;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;

final class ListenerPreMotionUpdate
    extends ModuleListener<Step, MotionUpdateEvent> {
    public ListenerPreMotionUpdate(Step module) {
        super(module, MotionUpdateEvent.class, 15_000);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        AxisAlignedBB bb = module.bb;
        double[] offsets = module.offsets;

        if (module.stepping
            && module.mode.getValue() == StepMode.Slow
            && event.getStage() == Stage.PRE
            && offsets != null
            && bb != null) {
            boolean noMovementKeys = MovementUtil.noMovementKeys();
            if (module.index++ < offsets.length && !noMovementKeys) {
                if (module.useTimer.getValue() && module.index == offsets.length - 1) {
                    Managers.TIMER.reset();
                }
                double y = (module.index / (double) offsets.length)
                    * module.currHeight;

                mc.player.setPosition(module.x, module.y + y, module.z);
                event.setCancelled(true);
            } else if (noMovementKeys) {
                module.reset();
            } else {
                if (PingBypassModule.CACHE.isEnabled()
                    && !PingBypassModule.CACHE.get().isOld()) {
                    mc.player.connection.sendPacket(
                        new C2SStepPacket(
                            offsets, module.x, module.y, module.z));
                } else {
                    for (double offset : offsets) {
                        mc.player.connection.sendPacket(
                            new CPacketPlayer.Position(
                                module.x,
                                module.y + offset,
                                module.z,
                                true));
                    }
                }

                mc.player.setEntityBoundingBox(bb);
                mc.player.resetPositionToBB();
                module.reset();
                if (module.autoOff.getValue()) {
                    module.disable();
                }
            }
        } else if (module.stepping && (bb == null || offsets == null)) {
            module.reset();
        }
    }

}
