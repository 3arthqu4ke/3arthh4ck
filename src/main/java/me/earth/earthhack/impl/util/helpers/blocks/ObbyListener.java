package me.earth.earthhack.impl.util.helpers.blocks;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.helpers.blocks.util.TargetResult;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.*;

import static me.earth.earthhack.impl.util.helpers.blocks.ObbyModule.HELPER;

public abstract class ObbyListener<T extends ObbyListenerModule<?>>
        extends ModuleListener<T, MotionUpdateEvent>
{
    public final Map<BlockPos, Long> placed = new HashMap<>();
    public List<BlockPos> targets = new LinkedList<>();

    public ObbyListener(T module, int priority)
    {
        super(module, MotionUpdateEvent.class, priority);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (event.getStage() == Stage.PRE)
        {
            pre(event);
        }
        else
        {
            post(event);
        }
    }

    protected abstract TargetResult getTargets(TargetResult result);

    public void onModuleToggle()
    {
        placed.clear();
        targets = new LinkedList<>();
    }

    protected void pre(MotionUpdateEvent event)
    {
        module.rotations = null;
        module.blocksPlaced = 0;

        if (update())
        {
            if (!attackCrystalFirst())
            {
                placeTargets();
            }
        }

        if (rotateCheck())
        {
            if (module.rotations != null)
            {
                setRotations(module.rotations, event);
            }
        }
        else
        {
            execute();
        }
    }

    protected boolean rotateCheck()
    {
        return module.rotate.getValue() != Rotate.None;
    }

    protected void placeTargets()
    {
        for (BlockPos pos : targets)
        {
            if (!placed.containsKey(pos)
                    && HELPER.getBlockState(pos)
                             .getMaterial()
                             .isReplaceable())
            {
                if (module.placeBlock(pos))
                {
                    break;
                }
            }
        }
    }

    protected boolean attackCrystalFirst()
    {
        boolean hasPlaced = false;
        // ensure that we attack the pos with the
        // crystal first before the others,
        // so we dont have to deal with switchcooldown.
        Optional<BlockPos> crystalPos = targets
                .stream()
                .filter(pos ->
                        !mc.world
                           .getEntitiesWithinAABB(EntityEnderCrystal.class,
                                                  new AxisAlignedBB(pos))
                           .isEmpty()
                                && mc.world.getBlockState(pos)
                                           .getMaterial()
                                           .isReplaceable())
                .findFirst();

        if (crystalPos.isPresent())
        {
            hasPlaced = module.placeBlock(crystalPos.get());
        }

        return hasPlaced;
    }

    protected boolean update()
    {
        if (updatePlaced())
        {
            return false;
        }

        module.slot = getSlot();
        if (module.slot == -1)
        {
            disableModule();
            return false;
        }

        if (hasTimerNotPassed())
        {
            return false;
        }

        TargetResult result = getTargets(new TargetResult());
        targets = result.getTargets();
        return result.isValid();
    }

    protected boolean hasTimerNotPassed()
    {
        return !module.timer.passed(module.getDelay());
    }

    public void addCallback(BlockPos pos)
    {
        Managers.BLOCKS.addCallback(pos, s ->
            mc.addScheduledTask(() -> placed.remove(pos)));
        placed.put(pos, System.currentTimeMillis());
    }

    /**
     * Disables the Module using the getDisableString.
     */
    protected void disableModule()
    {
        ModuleUtil.disableRed(module, getDisableString());
    }

    /**
     * Confirmed blocks can now be checked for blockstate changes.
     */
    protected boolean updatePlaced()
    {
        placed.entrySet().removeIf(entry ->
                System.currentTimeMillis() - entry.getValue()
                        >= module.confirm.getValue());
        return false;
    }

    protected int getSlot()
    {
        return InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
    }

    protected String getDisableString()
    {
        return "Disabled, no Obsidian.";
    }

    @SuppressWarnings("unused")
    protected void post(MotionUpdateEvent event)
    {
        execute();
    }

    protected void setRotations(float[] rotations, MotionUpdateEvent event)
    {
        event.setYaw(rotations[0]);
        event.setPitch(rotations[1]);
    }

    protected void execute()
    {
        Locks.acquire(Locks.PLACE_SWITCH_LOCK, module::execute);
    }

}
