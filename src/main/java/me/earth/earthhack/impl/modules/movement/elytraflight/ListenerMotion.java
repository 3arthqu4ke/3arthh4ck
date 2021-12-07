package me.earth.earthhack.impl.modules.movement.elytraflight;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.core.ducks.util.IKeyBinding;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.movement.elytraflight.mode.ElytraMode;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;

import java.util.Random;

final class ListenerMotion extends
        ModuleListener<ElytraFlight, MotionUpdateEvent> {
    private static final Random RANDOM = new Random();
    private static float previousTimerVal = -1.0f;

    public ListenerMotion(ElytraFlight module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (event.getStage() != Stage.PRE) {
            return;
        }

        ItemStack stack =
                mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

        if (stack.getItem() != Items.ELYTRA || !ItemElytra.isUsable(stack)) {
            return;
        }

        if (mc.player.isElytraFlying()
                && (module.noWater.getValue() && mc.player.isInWater()
                || module.noGround.getValue() && mc.player.onGround)) {
            module.sendFallPacket();
            return;
        }

        if (module.mode.getValue() == ElytraMode.Packet) {
            boolean falling = false;
            if (module.infDura.getValue() || !mc.player.isElytraFlying()) {
                module.sendFallPacket();
                falling = true;
            }

            if (module.ncp.getValue()
                    && !module.lag
                    && (Math.abs(event.getX()) >= 0.05
                    || Math.abs(event.getZ()) >= 0.05)) {
                double y = 1.0E-8 + 1.0E-8 * (1.0 + RANDOM.nextInt(
                        1 + (RANDOM.nextBoolean()
                                ? RANDOM.nextInt(34)
                                : RANDOM.nextInt(43))));

                if (mc.player.onGround || mc.player.ticksExisted % 2 == 0) {
                    event.setY(event.getY() + y);
                    return;
                }

                event.setY(event.getY() - y);
                return;
            }

            if (falling) {
                return;
            }
        }

        if (module.autoStart.getValue()
                && mc.gameSettings.keyBindJump.isKeyDown()
                && !mc.player.isElytraFlying()
                && mc.player.motionY < 0) {
            if (previousTimerVal == -1.0f) {
                previousTimerVal = Managers.TIMER.getSpeed();
            }
            Managers.TIMER.setTimer(0.17f);
            if (module.timer.passed(10)) {
                ((IKeyBinding) mc.gameSettings.keyBindJump).setPressed(true);
                module.sendFallPacket();
                module.timer.reset();
            } else {
                ((IKeyBinding) mc.gameSettings.keyBindJump).setPressed(false);
            }
            return;
        } else {
            if (previousTimerVal != -1.0f) {
                Managers.TIMER.setTimer(previousTimerVal);
                previousTimerVal = -1.0f;
            }
        }

        if (module.infDura.getValue() && mc.player.isElytraFlying()) {
            module.sendFallPacket();
        }
    }

}
