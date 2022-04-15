package me.earth.earthhack.impl.managers.thread.safety;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.Timer;
import me.earth.earthhack.impl.util.math.geocache.Sphere;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.HoleUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.thread.SafeRunnable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.List;

// Make this a Finishable?
public class SafetyRunnable implements Globals, SafeRunnable
{
    private final SafetyManager manager;
    private final List<Entity> crystals;
    private final boolean newVerEntities;
    private final boolean newerVersion;
    private final boolean bedCheck;
    private final float maxDamage;
    private final boolean longs;
    private final boolean big;
    private final boolean anvils;
    private final boolean terrain;
    private final Timer fullCalcTimer;
    private final int fullCalcDelay;

    public SafetyRunnable(SafetyManager manager,
                          List<Entity> crystals,
                          boolean newVerEntities,
                          boolean newerVersion,
                          boolean bedCheck,
                          float maxDamage,
                          boolean longs,
                          boolean big,
                          boolean anvils,
                          boolean terrain,
                          Timer fullCalcTimer,
                          int fullCalcDelay)
    {
        this.manager        = manager;
        this.crystals       = crystals;
        this.newVerEntities = newVerEntities;
        this.newerVersion   = newerVersion;
        this.bedCheck       = bedCheck;
        this.maxDamage      = maxDamage;
        this.longs          = longs;
        this.big            = big;
        this.anvils         = anvils;
        this.terrain        = terrain;
        this.fullCalcTimer = fullCalcTimer;
        this.fullCalcDelay = fullCalcDelay;
    }

    @Override
    public void runSafely()
    {
        // search for bad crystals in range.
        for (Entity entity : crystals)
        {
            if (entity instanceof EntityEnderCrystal && !entity.isDead)
            {
                float damage = DamageUtil.calculate(entity);
                if (damage > maxDamage
                        || damage > EntityUtil.getHealth(mc.player) - 1.0)
                {
                    manager.setSafe(false);
                    return;
                }
            }
        }

        boolean fullArmor = true;
        for (ItemStack stack : mc.player.inventory.armorInventory)
        {
            if (stack.isEmpty())
            {
                fullArmor = false;
                break;
            }
        }

        // If we are in a hole and no bedcheck is required
        // we can just stop it here and not do the big calc.
        Vec3d serverVec = Managers.POSITION.getVec();
        BlockPos position = new BlockPos(serverVec);
        // ensure that we are actually standing on the floor of the hole
        if (fullArmor && position.getY() == serverVec.y)
        {
            boolean[] hole = HoleUtil.isHole(position, false);
            if (hole[0] && (!anvils || hole[1])
                    && (!newerVersion || !bedCheck))
            {
                manager.setSafe(true);
                return;
            }
            else if (!anvils
                    && ((HoleUtil.is2x1(position) && longs
                       || HoleUtil.is2x2Partial(position) && big) && !bedCheck))
            {
                manager.setSafe(true);
                return;
            }
        }

        if (!manager.isSafe() && !fullCalcTimer.passed(fullCalcDelay))
        {
            return;
        }

        fullCalcTimer.reset();
        AxisAlignedBB serverBB = Managers.POSITION.getBB();
        BlockPos middle = PositionUtil.fromBB(serverBB);
        int x = middle.getX();
        int y = middle.getY();
        int z = middle.getZ();
        int maxRadius = Sphere.getRadius(6.0);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int i = 1; i < maxRadius; i++)
        {
            Vec3i v = Sphere.get(i);
            pos.setPos(x + v.getX(), y + v.getY(), z + v.getZ());
            if (BlockUtil.canPlaceCrystal(
                        pos, true, newerVersion, crystals, newVerEntities, 0)
                    || bedCheck && BlockUtil.canPlaceBed(pos, newerVersion))
            {
                float damage = DamageUtil.calculate(pos.getX() + 0.5f,
                                                    pos.getY() + 1,
                                                    pos.getZ() + 0.5f,
                                                    serverBB,
                                                    mc.player,
                                                    mc.world,
                                                    terrain,
                                                    anvils);
                if (damage > maxDamage
                        || damage > EntityUtil.getHealth(mc.player) - 1.0)
                {
                    manager.setSafe(false);
                    return;
                }
            }
        }

        manager.setSafe(true);
    }

}
