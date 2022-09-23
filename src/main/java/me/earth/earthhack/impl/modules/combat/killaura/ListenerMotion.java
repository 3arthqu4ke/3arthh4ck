package me.earth.earthhack.impl.modules.combat.killaura;

import com.google.common.collect.Multimap;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.core.ducks.entity.IEntityLivingBase;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.killaura.util.AuraSwitch;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import me.earth.earthhack.pingbypass.PingBypass;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;

final class ListenerMotion extends ModuleListener<KillAura, MotionUpdateEvent>
{
    public ListenerMotion(KillAura module)
    {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (event.getStage() == Stage.PRE)
        {
            pre(module, event, true);
        }
        else
        {
            post(module);
        }
    }

    public static void pre(KillAura module,
                           MotionUpdateEvent event,
                           boolean teleports)
    {
        module.isAttacking = false;

        if (mc.player.isSpectator())
        {
            return;
        }

        if (!module.shouldAttack())
        {
            return;
        }

        if (!module.whileEating.getValue()
            && mc.player.getActiveItemStack().getItem() instanceof ItemFood)
        {
            return;
        }

        module.target = module.findTarget();
        if (module.target == null)
        {
            return;
        }

        module.slot = -1;
        if (module.autoSwitch.getValue() != AuraSwitch.None)
        {
            module.slot = findSlot();
        }

        boolean passed = passedDelay(module, module.slot);

        float[] rotations = null;
        module.isTeleporting = false;
        if (module.rotate.getValue())
        {
            rotations = module.rotationSmoother
                    .getRotations(RotationUtil.getRotationPlayer(),
                            module.target,
                            module.height.getValue(),
                            module.soft.getValue());
        }

        if (passed
            && !module.isTeleporting
            && module.isInRange(Managers.POSITION.getVec(), module.target)
            && (module.efficient.getValue()
                || !module.isInRange(
                        mc.player.getPositionVector(), module.target))
            && (!module.rotate.getValue()
            || RotationUtil.isLegit(module.target)
                && (module.rotationTicks.getValue() <= 1
                || module.rotationSmoother.getRotationTicks()
                    >= module.rotationTicks.getValue())))
        {
            module.eff = Managers.POSITION.getVec();
            attack(module, module.target, module.slot);
            module.eff = null;
            module.timer.reset((long) (1000.0 / module.cps.getValue()));

            if (rotations != null && module.stay.getValue())
            {
                event.setYaw(rotations[0]);
                event.setPitch(rotations[1]);
            }

            return;
        }

        if (module.rotate.getValue() && !module.stay.getValue() && !passed)
        {
            return;
        }

        if (rotations != null)
        {
            event.setYaw(rotations[0]);
            event.setPitch(rotations[1]);

            if (module.rotationSmoother.isRotating())
            {
                return;
            }
        }

        module.isAttacking = passed;
        if (module.isAttacking)
        {
            module.timer.reset((long) (1000.0 / module.cps.getValue()));
        }

        if (teleports
            && module.isAttacking
            // More options here with Holes etc. ?
            && (module.movingTeleport.getValue()
                || MovementUtil.noMovementKeys()))
        {
            boolean canSee  = mc.player.canEntityBeSeen(module.target);
            double tp = module.teleportRange.getValue();
            double tpSq = MathUtil.square(tp);
            double distSq = mc.player.getDistanceSq(module.target);
            double dist = Math.sqrt(distSq);
            switch (module.auraTeleport.getValue())
            {
                case Smart:
                    if ((!canSee && distSq >= 9.0 || distSq >= 36.0)
                        && (dist - tp < 3.0 || canSee && dist - tp < 6.0))
                    {
                        Vec3d vec  = module.target.getPositionVector();
                        Vec3d own = mc.player.getPositionVector();
                        if (!module.yTeleport.getValue() && vec.y != own.y)
                        {
                            /*
                                 We want the ?, ? is 6 (3) blocks from vec

                                 noY -- b -- ? -------- own
                                  .
                                  a
                                  .
                                 vec
                             */
                            Vec3d noY = new Vec3d(vec.x, own.y, vec.z);
                            // Pythagoras to get b
                            double cSq = (canSee ? 36.0 : 9.0) - 0.0005;
                            double aSq = noY.squareDistanceTo(vec);
                            double b = Math.sqrt(cSq - aSq);
                            // Get unit vector between noY and own
                            Vec3d dir = own.subtract(noY).normalize();
                            // ? is noY + b * dir
                            Vec3d result = noY.add(dir.scale(b));
                            // Check cause tp might be out of range
                            if (result.distanceTo(own) <= tp
                                    && module.isInRange(result, module.target))
                            {
                                teleport(module, result, event);
                            }

                            return;
                        }

                        // TODO: make this super smart? like peek around walls?
                        // Get Unit vector between us and the player.
                        Vec3d dir = own.subtract(vec).normalize();
                        // Position is vec + distance * dir
                        Vec3d result = // TODO: 0.00005? find closest offset?
                            vec.add(dir.scale((canSee ? 6.0 : 3.0) - 0.005));

                        teleport(module, result, event);
                    }
                    break;
                case Full:
                    if (distSq <= tpSq)
                    {
                        teleport(module,
                                 module.target.getPositionVector(),
                                 event);
                    }
                    break;
                default:
            }
        }
    }

    public static void post(KillAura module)
    {
        if (module.target == null || !module.isAttacking || mc.player.isSpectator())
        {
            return;
        }

        attack(module, module.target, module.slot);
    }

    private static void teleport(KillAura module,
                                 Vec3d to,
                                 MotionUpdateEvent event)
    {
        module.isTeleporting = true;
        module.pos = to;

        event.setX(to.x);
        event.setY(to.y);
        event.setZ(to.z);

        if (module.tpSetPos.getValue())
        {
            mc.player.setPosition(to.x, to.y, to.z);
        }
    }

    private static void attack(KillAura module, Entity entity, int slot)
    {
        if (module.rotate.getValue()
            && !module.rotationSmoother.isRotating()
            && module.rotationSmoother.getRotationTicks()
                < module.rotationTicks.getValue())
        {
            module.rotationSmoother.incrementRotationTicks();
            return;
        }

        if (!module.isInRange(Managers.POSITION.getVec(), module.target))
        {
            return;
        }

        boolean stopSneak  =
                module.stopSneak.getValue()
                        && Managers.ACTION.isSneaking();

        boolean stopSprint =
                module.stopSprint.getValue()
                        && mc.player.isSprinting();

        boolean stopShield =
                module.stopShield.getValue()
                        && mc.player.isActiveItemStackBlocking();

        if (stopSneak)
        {
            PingBypass.sendToActualServer(new CPacketEntityAction(mc.player,
                                               CPacketEntityAction.Action.STOP_SNEAKING));
        }

        if (stopSprint)
        {
            PingBypass.sendToActualServer(new CPacketEntityAction(mc.player,
                    CPacketEntityAction.Action.STOP_SPRINTING));
        }

        if (stopShield)
        {
            module.releaseShield();
        }

        Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
        {
            int last = mc.player.inventory.currentItem;
            if (slot != -1)
            {
                InventoryUtil.switchTo(slot);
            }

            module.ourCrit = true;
            mc.playerController.attackEntity(mc.player, entity);
            module.ourCrit = false;

            module.swing.getValue().swing(EnumHand.MAIN_HAND);

            if (module.autoSwitch.getValue() != AuraSwitch.Keep && slot != -1)
            {
                InventoryUtil.switchTo(last);
            }
        });

        if (stopSneak)
        {
            PingBypass.sendToActualServer(new CPacketEntityAction(mc.player,
                    CPacketEntityAction.Action.START_SNEAKING));
        }

        if (stopSprint)
        {
            PingBypass.sendToActualServer(new CPacketEntityAction(mc.player,
                    CPacketEntityAction.Action.START_SPRINTING));
        }

        if (stopShield)
        {
            module.useShield();
        }
    }

    private static boolean passedDelay(KillAura module, int slot)
    {
        if (module.delay.getValue()
            && (!module.t2k.getValue()
               || !DamageUtil.isSharper(mc.player.getHeldItemMainhand(), 1000)))
        {
            float tps = module.tps.getValue() ? 20.0f - Managers.TPS.getTps()
                                              : 0.0f;
            if (slot == -1)
            {

                //mc.player.attackEntityFrom()
                return mc.player.getCooledAttackStrength(0.5f - tps) >= 1.0f;
            }
            else
            {
                ItemStack stack = mc.player.inventory.getStackInSlot(slot);
                double value    = mc.player
                    .getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED)
                    .getBaseValue();

                Multimap<String, AttributeModifier> map = stack
                    .getAttributeModifiers(EntityEquipmentSlot.MAINHAND);
                Collection<AttributeModifier> modifiers = map
                    .get(SharedMonsterAttributes.ATTACK_SPEED.getName());

                if (modifiers != null)
                {
                    for (AttributeModifier modifier : modifiers)
                    {
                        value += modifier.getAmount();
                    }
                }

                float swing = ((IEntityLivingBase) mc.player)
                                    .getTicksSinceLastSwing() + 0.5f - tps;
                float cooldown = (float) (1.0 / value * 20.0);

                return MathHelper.clamp(swing / cooldown, 0.0f, 1.0f) >= 1.0f;
            }
        }

        return module.cps.getValue() >= 20
                || module.timer.passed((long) (1000.0 / module.cps.getValue()));
    }

    private static int findSlot()
    {
        int slot = -1;
        int bestSharp = -1;
        for (int i = 8; i > -1; i--)
        {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() instanceof ItemSword
                    || stack.getItem() instanceof ItemAxe)
            {
                int level = EnchantmentHelper.getEnchantmentLevel(
                                        Enchantments.SHARPNESS, stack);
                if (level > bestSharp)
                {
                    bestSharp = level;
                    slot = i;
                }
            }
        }

        return slot;
    }

}
