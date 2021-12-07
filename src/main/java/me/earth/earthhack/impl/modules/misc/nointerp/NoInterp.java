package me.earth.earthhack.impl.modules.misc.nointerp;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.core.ducks.entity.IEntityNoInterp;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

// Could fix DebugCollisionRender?
public class NoInterp extends Module
{
    private final Setting<Boolean> silent =
            register(new BooleanSetting("Silent", true));
    private final Setting<Boolean> setRotations =
            register(new BooleanSetting("Fast-Rotations", false));
    private final Setting<Boolean> noDeathJitter =
            register(new BooleanSetting("NoDeathJitter", true));
    // TODO: THIS, problem with Jockeys...
    private final Setting<Boolean> onlyPlayers =
            register(new BooleanSetting("OnlyPlayers", false));

    public NoInterp()
    {
        super("NoInterp", Category.Misc);
        this.setData(new SimpleData(this, "Makes the client more accurate." +
                " The Packets module is recommended when using this."));
    }

    public boolean isSilent()
    {
        return silent.getValue();
    }

    public boolean shouldFixDeathJitter()
    {
        return noDeathJitter.getValue();
    }

    /**
     * @param entity the entity to noInterp.
     * @param posIncrements the interp increments.
     * @param x the new X
     * @param y the new Y
     * @param z the new Z
     * @throws ClassCastException if the given Entity
     *          is not instanceof {@link IEntityNoInterp}.
     */
    public static void handleNoInterp(NoInterp noInterp,
                                      Entity entity,
                                      int posIncrements,
                                      double x,
                                      double y,
                                      double z,
                                      float yaw,
                                      float pitch)
    {
        IEntityNoInterp entityNoInterp = (IEntityNoInterp) entity;
        if (!entityNoInterp.isNoInterping())
        {
            return;
        }

        if (noInterp.setRotations.getValue())
        {
            entity.setPosition(x, y, z);
            entity.rotationYaw = yaw % 360.0f;
            entity.rotationPitch = pitch % 360.0f;
        }
        else
        {
            entity.setPosition(x, y, z);
        }

        entityNoInterp.setPosIncrements(posIncrements);
    }

    public static double noInterpX(NoInterp noInterp, Entity entity)
    {
        if (noInterp != null
                && noInterp.isEnabled()
                && noInterp.isSilent()
                && entity instanceof IEntityNoInterp
                && ((IEntityNoInterp) entity).isNoInterping())
        {
            return ((IEntityNoInterp) entity).getNoInterpX();
        }

        return entity.posX;
    }

    public static double noInterpY(NoInterp noInterp, Entity entity)
    {
        if (noInterp != null
                && noInterp.isEnabled()
                && noInterp.isSilent()
                && entity instanceof IEntityNoInterp
                && ((IEntityNoInterp) entity).isNoInterping())
        {
            return ((IEntityNoInterp) entity).getNoInterpY();
        }

        return entity.posY;
    }

    public static double noInterpZ(NoInterp noInterp, Entity entity)
    {
        if (noInterp != null
                && noInterp.isEnabled()
                && noInterp.isSilent()
                && entity instanceof IEntityNoInterp
                && ((IEntityNoInterp) entity).isNoInterping())
        {
            return ((IEntityNoInterp) entity).getNoInterpZ();
        }

        return entity.posZ;
    }

    public static boolean update(NoInterp module, Entity entity)
    {
        if (module == null
                || !module.isEnabled()
                || !module.silent.getValue()
                || EntityUtil.isDead(entity))
        {
            return false;
        }

        IEntityNoInterp noInterp;
        if (!(entity instanceof IEntityNoInterp)
                || !(noInterp = (IEntityNoInterp) entity).isNoInterping())
        {
            return false;
        }

        if (noInterp.getPosIncrements() > 0)
        {
            double x = noInterp.getNoInterpX()
                    + (entity.posX - noInterp.getNoInterpX())
                    / (double) noInterp.getPosIncrements();
            double y = noInterp.getNoInterpY()
                    + (entity.posY - noInterp.getNoInterpY())
                    / (double) noInterp.getPosIncrements();
            double z = noInterp.getNoInterpZ()
                    + (entity.posZ - noInterp.getNoInterpZ())
                    / (double) noInterp.getPosIncrements();

            entity.prevPosX = noInterp.getNoInterpX();
            entity.prevPosY = noInterp.getNoInterpY();
            entity.prevPosZ = noInterp.getNoInterpZ();

            entity.lastTickPosX = noInterp.getNoInterpX();
            entity.lastTickPosY = noInterp.getNoInterpY();
            entity.lastTickPosZ = noInterp.getNoInterpZ();

            noInterp.setNoInterpX(x);
            noInterp.setNoInterpY(y);
            noInterp.setNoInterpZ(z);

            noInterp.setPosIncrements(noInterp.getPosIncrements() - 1);
        }

        if (entity instanceof EntityLivingBase)
        {
            EntityLivingBase base = (EntityLivingBase) entity;
            double xDiff = noInterp.getNoInterpX() - entity.prevPosX;
            double zDiff = noInterp.getNoInterpZ() - entity.prevPosZ;
            double yDiff = entity instanceof EntityFlying
                    ? noInterp.getNoInterpY() - entity.prevPosY
                    : 0.0;

            float diff = MathHelper.sqrt(xDiff * xDiff
                                       + zDiff * zDiff
                                       + yDiff * yDiff) * 4.0f;
            if (diff > 1.0f)
            {
                diff = 1.0f;
            }

            float limbSwingAmount = noInterp.getNoInterpSwingAmount();
            base.prevLimbSwingAmount = limbSwingAmount;
            noInterp.setNoInterpPrevSwing(limbSwingAmount);
            noInterp.setNoInterpSwingAmount(limbSwingAmount
                    + (diff - limbSwingAmount) * 0.4f);
            base.limbSwingAmount = noInterp.getNoInterpSwingAmount();
            float limbSwing = noInterp.getNoInterpSwing()
                    + base.limbSwingAmount;
            noInterp.setNoInterpSwing(limbSwing);
            base.limbSwing = limbSwing;
        }

        return true;
    }

}
