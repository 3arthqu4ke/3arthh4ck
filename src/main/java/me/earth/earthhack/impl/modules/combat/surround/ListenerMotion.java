package me.earth.earthhack.impl.modules.combat.surround;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * MotionUpdateListener for Surround.
 * Very similar to the Listener of autoTrap.
 * Maybe we can do something there?
 */
final class ListenerMotion extends ModuleListener<Surround, MotionUpdateEvent>
{
    public ListenerMotion(Surround module)
    {
        super(module, MotionUpdateEvent.class, -999999999);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (event.getStage() == Stage.PRE)
        {
            start(module, event);
        }
        else
        {
            module.setPosition = true;
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, module::execute);
            Managers.ROTATION.setBlocking(false);
        }
    }

    public static void start(Surround module)
    {
        start(module, new MotionUpdateEvent(Stage.PRE, 0, 0, 0, 0, 0, false));
    }

    public static void start(Surround module, MotionUpdateEvent event)
    {
        module.rotations    = null;
        module.attacking    = null;
        module.blocksPlaced = 0;
        module.center();

        if (module.updatePosAndBlocks())
        {
            // confirmed blocks can now be checked for blockstate changes.
            module.placed.removeAll(module.confirmed);
            boolean hasPlaced = false;

            // ensure that we attack the pos with the crystal first before
            // the others, so we dont have to deal with switchcooldown.
            Optional<BlockPos> crystalPos = module.targets
                    .stream()
                    .filter(pos ->
                            !mc.world
                               .getEntitiesWithinAABB(
                                    EntityEnderCrystal.class,
                                    new AxisAlignedBB(pos))
                               .isEmpty()
                            && mc.world.getBlockState(pos)
                                       .getMaterial()
                                       .isReplaceable())
                    .findFirst();

            if (crystalPos.isPresent())
            {
                BlockPos pos = crystalPos.get();
                module.confirmed.remove(pos);
                hasPlaced = module.placeBlock(pos);
            }

            if (!hasPlaced || !module.crystalCheck.getValue())
            {
                List<BlockPos> surrounding = new ArrayList<>(module.targets);
                if (module.getPlayer().motionX != 0.0
                        || module.getPlayer().motionZ != 0.0)
                {
                    // place in front of us first to stop our movement.
                    // I am aware that this looks really bad
                    BlockPos pos = new BlockPos(module.getPlayer())
                            .add(module.getPlayer().motionX * 10000,
                                 0,
                                 module.getPlayer().motionZ * 10000);

                    surrounding.sort(Comparator.comparingDouble(
                            p -> p.distanceSq(
                                    pos.getX() + 0.5,
                                    pos.getY(),
                                    pos.getZ() + 0.5)));
                }

                for (BlockPos pos : surrounding)
                {
                    if (!module.placed.contains(pos)
                            && mc.world.getBlockState(pos)
                                       .getMaterial()
                                       .isReplaceable())
                    {
                        module.confirmed.remove(pos);
                        if (module.placeBlock(pos))
                        {
                            break;
                        }
                    }
                }
            }
        }

        if (module.blocksPlaced == 0)
        {
            module.placed.clear();
        }

        if (module.rotate.getValue() != Rotate.None)
        {
            if (module.rotations != null)
            {
                Managers.ROTATION.setBlocking(true);
                event.setYaw(module.rotations[0]);
                event.setPitch(module.rotations[1]);

                if (Surround.FREECAM.isEnabled())
                {
                    Surround.FREECAM.get().rotate(module.rotations[0],
                                                  module.rotations[1]);
                }
            }
        }
        else if (module.setPosition)
        {
            // if we dont need to rotate and are centered, send immediately.
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, module::execute);
        }
    }

}
