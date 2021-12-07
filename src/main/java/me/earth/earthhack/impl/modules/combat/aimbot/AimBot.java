package me.earth.earthhack.impl.modules.combat.aimbot;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.minecraft.entity.module.EntityTypeModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;

import java.util.LinkedList;
import java.util.List;

// TODO: Best AimBot ever, use an EntityArrow
//  to check if the arrow would hit any blocks
public class AimBot extends EntityTypeModule
{
    protected final Setting<Boolean> silent =
        register(new BooleanSetting("Silent", true));
    protected final Setting<Boolean> fov =
        register(new BooleanSetting("Fov", false));
    protected final Setting<Integer> extrapolate =
        register(new NumberSetting<>("Extrapolate", 0, 0, 10));
    protected final Setting<Double> maxRange =
        register(new NumberSetting<>("MaxRange", 100.0, 0.0, 500.0));

    protected Entity target;
    protected float yaw;
    protected float pitch;

    public AimBot()
    {
        super("AimBot", Category.Combat);
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerGameLoop(this));
        this.unregister(this.vehicles);
        this.unregister(this.misc);
    }

    @Override
    public String getDisplayInfo()
    {
        return target != null ? target.getName() : null;
    }

    public Entity getTarget()
    {
        List<Entity> entites = new LinkedList<>();
        Entity closest = null;
        double closestAngle  = 360.0;
        double x = RotationUtil.getRotationPlayer().posX;
        double y = RotationUtil.getRotationPlayer().posY;
        double z = RotationUtil.getRotationPlayer().posZ;
        float  h = mc.player.getEyeHeight();
        for (Entity entity : mc.world.loadedEntityList)
        {
            if (!(entity instanceof EntityLivingBase)
                || entity.equals(mc.player)
                || entity.equals(RotationUtil.getRotationPlayer())
                || !EntityUtil.isValid(entity, maxRange.getValue())
                || !this.isValid(entity)
                || (!RayTraceUtil.canBeSeen(
                        new Vec3d(entity.posX,
                                  entity.posY + entity.getEyeHeight(),
                                  entity.posZ),
                        x, y, z, h)
                    && !RayTraceUtil.canBeSeen(
                        new Vec3d(entity.posX,
                                  entity.posY + entity.getEyeHeight() / 2.0,
                                  entity.posZ),
                        x, y, z, h)
                    && !RayTraceUtil.canBeSeen(
                        new Vec3d(entity.posX, entity.posY, entity.posZ),
                        x, y, z, h)))
            {
                continue;
            }

            double angle = RotationUtil.getAngle(entity, 1.4);
            if (fov.getValue() && angle > mc.gameSettings.fovSetting / 2)
            {
                continue;
            }








            if (angle < closestAngle
                && (!fov.getValue()
                    || angle < mc.gameSettings.fovSetting / 2))
            {
                closest = entity;
                closestAngle = angle;
            }
        }

        return closest;
    }

}
