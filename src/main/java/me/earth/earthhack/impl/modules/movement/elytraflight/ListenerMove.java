package me.earth.earthhack.impl.modules.movement.elytraflight;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

final class ListenerMove extends ModuleListener<ElytraFlight, MoveEvent> {
    public ListenerMove(ElytraFlight module) {
        super(module, MoveEvent.class);
    }

    @Override
    public void invoke(MoveEvent event) {
        ItemStack stack = mc.player
                .getItemStackFromSlot(EntityEquipmentSlot.CHEST);

        if (stack.getItem() == Items.ELYTRA && ItemElytra.isUsable(stack)) {
            switch (module.mode.getValue()) {
                case Wasp:
                    if (!mc.player.isElytraFlying()) {
                        return;
                    }

                    double vSpeed = mc.gameSettings.keyBindJump.isKeyDown()
                            ? module.vSpeed.getValue()
                            : mc.gameSettings.keyBindSneak.isKeyDown()
                            ? -module.vSpeed.getValue()
                            : 0;

                    event.setY(vSpeed);
                    mc.player.setVelocity(0, 0, 0);
                    mc.player.motionY = vSpeed;
                    mc.player.moveVertical = (float) vSpeed;

                    if (MovementUtil.noMovementKeys()
                            && !mc.gameSettings.keyBindJump.isKeyDown()
                            && !mc.gameSettings.keyBindSneak.isKeyDown()) {

                        event.setX(0);
                        event.setY(0);
                        event.setY(module.antiKick.getValue() ? -module.glide.getValue() : 0);
                        return;
                    }

                    MovementUtil.strafe(event, module.hSpeed.getValue());
                    break;
                case Packet:
                    if (!mc.player.onGround || !module.noGround.getValue()) {
                        if (module.accel.getValue()) {
                            if (module.lag) {
                                module.speed = 1.0;
                                module.lag = false;
                            }

                            if (module.speed < module.hSpeed.getValue()) {
                                module.speed += 0.1;
                            }

                            if (module.speed - 0.1D > module.hSpeed.getValue()) {
                                module.speed -= 0.1;
                            }
                        } else {
                            module.speed = module.hSpeed.getValue();
                        }

                        if (!MovementUtil.anyMovementKeys()
                                && !mc.player.collided
                                && module.antiKick.getValue()) {
                            if (module.timer.passed(1000)) {
                                module.lag = true;
                                mc.player.motionX += 0.03
                                        * Math.sin(Math.toRadians(++module.kick * 4));
                                mc.player.motionZ += 0.03
                                        * Math.cos(Math.toRadians(module.kick * 4));
                            }
                        } else {
                            module.timer.reset();
                            module.lag = false;
                        }

                        if (module.vertical.getValue()
                                && mc.player.movementInput.jump) {
                            mc.player.motionY = module.vSpeed.getValue();
                            event.setY(module.vSpeed.getValue());
                        } else if (mc.player.movementInput.sneak) {
                            mc.player.motionY = -module.vSpeed.getValue();
                            event.setY(-module.vSpeed.getValue());
                        } else if (module.ncp.getValue()) {
                            if (mc.player.ticksExisted % 32 != 0
                                    || module.lag
                                    || !(Math.abs(event.getX()) >= 0.05D)
                                    && !(Math.abs(event.getZ()) >= 0.05D)) {
                                mc.player.motionY = -2.0E-4;
                                event.setY(-2.0E-4);
                            } else {
                                module.speed = module.speed - module.speed / 2.0 * 0.1;
                                mc.player.motionY = -2.0E-4D;
                                event.setY(0.006200000000000001);
                            }
                        } else {
                            mc.player.motionY = 0.0;
                            event.setY(0.0);
                        }

                        event.setX(event.getX() * (module.lag ? 0.5 : module.speed));
                        event.setZ(event.getZ() * (module.lag ? 0.5 : module.speed));
                    }

                    break;
                case Boost:
                    if (mc.player.isElytraFlying()
                            && module.noWater.getValue()
                            && mc.player.isInWater()) {
                        return;
                    }

                    if (mc.player.movementInput.jump
                            && mc.player.isElytraFlying()) {
                        float yaw = mc.player.rotationYaw * 0.017453292f;
                        mc.player.motionX -= MathHelper.sin(yaw) * 0.15f;
                        mc.player.motionZ += MathHelper.cos(yaw) * 0.15f;
                    }

                    break;
                case Control:
                    if (mc.player.isElytraFlying()) {
                        if (!mc.player.movementInput.forwardKeyDown
                                && !mc.player.movementInput.sneak) {
                            mc.player.setVelocity(0.0, 0.0, 0.0);
                        } else if (mc.player.movementInput.forwardKeyDown
                                && (module.vertical.getValue()
                                || mc.player.prevRotationPitch > 0.0F)) {
                            float yaw = (float) Math.toRadians(mc.player.rotationYaw);
                            double speed = module.hSpeed.getValue() / 10.0;
                            mc.player.motionX = MathHelper.sin(yaw) * -speed;
                            mc.player.motionZ = MathHelper.cos(yaw) * speed;
                        }
                    }

                    break;
                case Normal:
                    if (mc.player.isElytraFlying()
                            && module.noWater.getValue()
                            && mc.player.isInWater()) {
                        return;
                    }

                    if (mc.player.movementInput.jump
                            || !mc.inGameHasFocus
                            && mc.player.isElytraFlying()) {
                        event.setY(0.0);
                    }

                    if (mc.inGameHasFocus
                            && module.instant.getValue()
                            && mc.player.movementInput.jump
                            && !mc.player.isElytraFlying()
                            && module.timer.passed(1000)) {
                        mc.player.setJumping(false);
                        mc.player.setSprinting(true);
                        mc.player.jump();
                        module.sendFallPacket();
                        module.timer.reset();
                        return;
                    }

                    break;
                default:
            }
        }
    }

}
