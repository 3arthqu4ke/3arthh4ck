package me.earth.earthhack.impl.modules.movement.elytraflight;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.core.ducks.util.IKeyBinding;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.movement.elytraflight.mode.ElytraMode;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;

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

        if (mc.player.isElytraFlying()) {
            if (module.mode.getValue() != ElytraMode.Boost && MovementUtil.anyMovementKeys()) {
                float moveStrafe = mc.player.movementInput.moveStrafe,
                        moveForward = mc.player.movementInput.moveForward;
                float strafe = moveStrafe * 90 * (moveForward != 0 ? 0.5f : 1);
                event.setYaw(MathHelper.wrapDegrees(mc.player.rotationYaw - strafe - (moveForward < 0 ? 180 : 0)));
            }
            if (module.customPitch.getValue()) {
                event.setPitch(module.pitch.getValue().floatValue());
            }
            if (module.rockets.getValue() && module.mode.getValue() != ElytraMode.Packet) {
                if (module.rocketTimer.passed(module.rocketDelay.getValue() * 1000)) {
                    int slot = InventoryUtil.findHotbarItem(Items.FIREWORKS);
                    if (slot != -1) {
                        Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> {
                            int last = mc.player.inventory.currentItem;
                            EnumHand hand = InventoryUtil.getHand(slot);

                            InventoryUtil.switchTo(slot);

                            mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(hand));
                            mc.player.swingArm(hand);

                            if (module.rocketSwitchBack.getValue()) {
                                InventoryUtil.switchTo(last);
                            }
                        });
                    }
                    module.rocketTimer.reset();
                }
            }
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
