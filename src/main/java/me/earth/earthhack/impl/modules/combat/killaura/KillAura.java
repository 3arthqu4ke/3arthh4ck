package me.earth.earthhack.impl.modules.combat.killaura;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.killaura.util.AuraSwitch;
import me.earth.earthhack.impl.modules.combat.killaura.util.AuraTarget;
import me.earth.earthhack.impl.modules.combat.killaura.util.AuraTeleport;
import me.earth.earthhack.impl.util.math.DiscreteTimer;
import me.earth.earthhack.impl.util.math.GuardTimer;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationSmoother;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.entity.EntityNames;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.minecraft.entity.module.EntityTypeModule;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Mouse;

public class KillAura extends EntityTypeModule
{
    protected final Setting<Boolean> passengers =
        register(new BooleanSetting("Passengers", false));
    protected final Setting<AuraTarget> targetMode =
        register(new EnumSetting<>("Target", AuraTarget.Closest));
    protected final Setting<Boolean> prioEnemies =
        register(new BooleanSetting("Enemies", true));
    protected final Setting<Double> range =
        register(new NumberSetting<>("Range", 6.0, 0.0, 6.0));
    protected final Setting<Double> wallRange =
        register(new NumberSetting<>("WallRange", 3.0, 0.0, 6.0));
    protected final Setting<Boolean> swordOnly =
        register(new BooleanSetting("Sword/Axe", true));
    protected final Setting<Boolean> delay =
        register(new BooleanSetting("Delay", true));
    protected final Setting<Float> cps =
        register(new NumberSetting<>("CPS", 20.0f, 0.1f, 100.0f));
    protected final Setting<Boolean> rotate =
        register(new BooleanSetting("Rotate", true));
    protected final Setting<Boolean> stopSneak  =
        register(new BooleanSetting("Release-Sneak", true));
    protected final Setting<Boolean> stopSprint =
        register(new BooleanSetting("Release-Sprint", true));
    protected final Setting<Boolean> stopShield =
        register(new BooleanSetting("AutoBlock", true));
    protected final Setting<Boolean> whileEating =
        register(new BooleanSetting("While-Eating", true));
    protected final Setting<Boolean> stay =
        register(new BooleanSetting("Stay", false));
    protected final Setting<Float> soft =
        register(new NumberSetting<>("Soft", 180.0f, 0.1f, 180.0f));
    protected final Setting<Integer> rotationTicks =
        register(new NumberSetting<>("Rotation-Ticks", 0, 0, 10));
    protected final Setting<AuraTeleport> auraTeleport =
        register(new EnumSetting<>("Teleport", AuraTeleport.None));
    protected final Setting<Double> teleportRange =
        register(new NumberSetting<>("TP-Range", 0.0, 0.0, 100.0));
    protected final Setting<Boolean> yTeleport  =
        register(new BooleanSetting("Y-Teleport", false));
    protected final Setting<Boolean> movingTeleport  =
        register(new BooleanSetting("Move-Teleport", false));
    protected final Setting<Swing> swing =
        register(new EnumSetting<>("Swing", Swing.Full));
    protected final Setting<Boolean> tps =
        register(new BooleanSetting("TPS-Sync", true));
    protected final Setting<Boolean> t2k =
        register(new BooleanSetting("Fast-32ks", true));
    protected final Setting<Float> health =
        register(new NumberSetting<>("Health", 0.0f, 0.0f, 15.0f));
    protected final Setting<Integer> armor =
        register(new NumberSetting<>("Armor", 0, 0, 100));
    protected final Setting<Float> targetRange =
        register(new NumberSetting<>("Target-Range", 10.0f, 0.0f, 20.0f));
    protected final Setting<Boolean> multi32k =
        register(new BooleanSetting("Multi-32k", false));
    protected final Setting<Integer> packets =
        register(new NumberSetting<>("Packets", 1, 0, 20));
    protected final Setting<Double> height =
        register(new NumberSetting<>("Height", 1.0, 0.0, 1.0));
    protected final Setting<Boolean> ridingTeleports =
        register(new BooleanSetting("Riding-Teleports", false));
    protected final Setting<Boolean> efficient =
        register(new BooleanSetting("Efficient", false));
    protected final Setting<Boolean> cancelEntityEquip =
        register(new BooleanSetting("NoEntityEquipment", false));
    protected final Setting<Boolean> tpInfo =
        register(new BooleanSetting("TP-Info", false));
    protected final Setting<Integer> coolDown =
        register(new NumberSetting<>("Cooldown", 0, 0, 500));
    protected final Setting<Boolean> m1Attack =
        register(new BooleanSetting("Hold-Mouse", false));
    protected final Setting<AuraSwitch> autoSwitch =
        register(new EnumSetting<>("AutoSwitch", AuraSwitch.None));

    protected final RotationSmoother rotationSmoother =
            new RotationSmoother(Managers.ROTATION);
    protected final DiscreteTimer timer =
            new GuardTimer();

    protected boolean isTeleporting;
    protected boolean isAttacking;
    protected boolean ourCrit;
    protected Entity target;
    protected Vec3d eff;
    protected Vec3d pos;
    protected int slot;

    public KillAura()
    {
        super("KillAura", Category.Combat);
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerRiding(this));
        this.listeners.add(new ListenerGameLoop(this));
        this.listeners.add(new ListenerEntityEquipment(this));
        this.setData(new KillAuraData(this));
    }

    @Override
    public String getDisplayInfo()
    {
        if (target == null || EntityUtil.isDead(target))
        {
            return null;
        }

        double distance = mc.player.getDistanceSq(target);
        if (distance > MathUtil.square(targetRange.getValue())
            || !shouldAttack()
                && (!tpInfo.getValue()
                     || teleportRange.getValue() == 0.0
                     || auraTeleport.getValue() != AuraTeleport.Smart))
        {
            return null;
        }

        StringBuilder name = new StringBuilder(EntityNames.getName(target))
                                    .append(TextColor.GRAY)
                                    .append(", ");
        if (distance >= 36.0)
        {
            name.append(TextColor.RED);
        }
        else if (!RotationUtil.getRotationPlayer()
                              .canEntityBeSeen(target) && distance >= 9.0)
        {
            if (target instanceof EntityPlayer
                    && ((EntityPlayer) target).canEntityBeSeen(
                            RotationUtil.getRotationPlayer()))
            {
                name.append(TextColor.WHITE);
            }
            else
            {
                name.append(TextColor.GOLD);
            }
        }
        else
        {
            name.append(TextColor.GREEN);
        }

        return name.append(MathUtil.round(Math.sqrt(distance), 2)).toString();
    }

    @Override
    public boolean isValid(Entity entity)
    {
        if (entity == null
                || mc.player.getDistanceSq(entity)
                    > MathUtil.square(targetRange.getValue())
                || EntityUtil.isDead(entity)
                || entity.equals(mc.player)
                || entity.equals(mc.player.getRidingEntity())
                || entity instanceof EntityPlayer
                    && Managers.FRIENDS.contains((EntityPlayer) entity)
                || !passengers.getValue()
                    && mc.player.getPassengers().contains(entity)
                || entity instanceof EntityExpBottle
                || entity instanceof EntityItem
                || entity instanceof EntityArrow
                || entity instanceof EntityEnderCrystal)
        {
            return false;
        }

        return super.isValid(entity);
    }

    public Entity getTarget()
    {
        return target;
    }

    protected Entity findTarget()
    {
        // TODO: make this better!
        Entity closest = null;
        Entity bestEnemy = null;

        double bestAngle = 360.0;
        float lowest = Float.MAX_VALUE;

        double distance = Double.MAX_VALUE;
        double closestEnemy = Double.MAX_VALUE;

        for (Entity entity : mc.world.loadedEntityList)
        {
            if (!isValid(entity))
            {
                continue;
            }

            double dist = mc.player.getDistanceSq(entity);
            if (targetMode.getValue() == AuraTarget.Angle)
            {
                double angle = RotationUtil.getAngle(entity, 1.75);
                if (angle < bestAngle
                        && Math.sqrt(dist) - teleportRange.getValue() < 6.0)
                {
                    closest = entity;
                    bestAngle = angle;
                }

                continue;
            }

            if (prioEnemies.getValue()
                && entity instanceof EntityPlayer
                && Managers.ENEMIES.contains((EntityPlayer) entity)
                && dist < closestEnemy)
            {
                bestEnemy = entity;
                closestEnemy = dist;
            }

            if (isInRange(RotationUtil.getRotationPlayer(), entity))
            {
                if (health.getValue() != 0.0f
                        && entity instanceof EntityLivingBase)
                {
                    float h = EntityUtil.getHealth((EntityLivingBase) entity);
                    if (h < health.getValue() && h < lowest)
                    {
                        closest = entity;
                        distance = dist;
                        lowest = h;
                    }
                }

                if (armor.getValue() != 0)
                {
                    for (ItemStack stack : entity.getArmorInventoryList())
                    {
                        if (!(stack.getItem() instanceof ItemElytra)
                             && DamageUtil.getPercent(stack) < armor.getValue())
                        {
                            closest = entity;
                            distance = dist;
                            break;
                        }
                    }
                }
            }

            if (closest == null)
            {
                closest = entity;
                distance = dist;
                continue;
            }

            if (dist < distance)
            {
                closest = entity;
                distance = dist;
            }
        }

        return bestEnemy != null ? bestEnemy : closest;
    }

    public boolean isInRange(Entity from, Entity target)
    {
        return isInRange(from.getPositionVector(), target);
    }

    public boolean isInRange(Vec3d from, Entity target)
    {
        double distance = from.squareDistanceTo(target.getPositionVector());
        if (distance >= MathUtil.square(range.getValue()))
        {
            return false;
        }

        if (distance < MathUtil.square(wallRange.getValue()))
        {
            return true;
        }

        return mc.world.rayTraceBlocks(
                new Vec3d(from.x,
                          from.y + mc.player.getEyeHeight(),
                          from.z),
                new Vec3d(target.posX,
                          target.posY + target.getEyeHeight(),
                          target.posZ),
                false,
                true,
                false) == null;
    }

    protected boolean shouldAttack()
    {
        if (m1Attack.getValue() && !Mouse.isButtonDown(0))
        {
            return false;
        }

        return !swordOnly.getValue()
            || mc.player.getHeldItemMainhand().getItem() instanceof ItemSword
            || mc.player.getHeldItemMainhand().getItem() instanceof ItemAxe;
    }

    protected void releaseShield()
    {
        if (mc.player.getHeldItemOffhand().getItem() instanceof ItemShield)
        {
            mc.player.connection.sendPacket(
                    new CPacketPlayerDigging(
                            CPacketPlayerDigging.Action.RELEASE_USE_ITEM,
                            new BlockPos(mc.player),
                            EnumFacing.getFacingFromVector(
                                    (float) Managers.POSITION.getX(),
                                    (float) Managers.POSITION.getY(),
                                    (float) Managers.POSITION.getZ())));
        }
    }

    protected void useShield()
    {
        if ((mc.player.getHeldItemMainhand().getItem() instanceof ItemSword
              || mc.player.getHeldItemMainhand().getItem() instanceof ItemAxe)
              && mc.player.getHeldItemOffhand().getItem() instanceof ItemShield)
        {
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
                mc.playerController
                  .processRightClick(mc.player, mc.world, EnumHand.OFF_HAND));
        }
    }

    public Vec3d criticalCallback(Vec3d vec3d)
    {
        if (this.isEnabled() && ourCrit)
        {
            if (eff != null)
            {
                return eff;
            }

            switch (auraTeleport.getValue())
            {
                case Smart:
                    if (isTeleporting && pos != null)
                    {
                        return pos;
                    }
                    break;
                case Full:
                    return Managers.POSITION.getVec();
            }
        }

        return vec3d;
    }

}
