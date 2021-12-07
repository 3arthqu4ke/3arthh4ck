package me.earth.earthhack.impl.event.events.misc;

import me.earth.earthhack.api.event.events.Event;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

/**
 * Note that {@link net.minecraft.client.Minecraft#player} could
 * be null when this event is fired.
 *
 * This Event will no longer be fired on the Bus because it
 * was called quite often (Particles?).
 * TODO: Profile again, with the new ParticleThing this should be fine!
 */
public class CollisionEvent extends Event
{
    private final Entity entity;
    private final BlockPos pos;
    private final Block block;

    private AxisAlignedBB bb;

    public CollisionEvent(BlockPos pos,
                          AxisAlignedBB bb,
                          Entity entity,
                          Block block)
    {
        this.pos = pos;
        this.bb = bb;
        this.entity = entity;
        this.block = block;
    }

    public AxisAlignedBB getBB()
    {
        return bb;
    }

    public void setBB(AxisAlignedBB bb)
    {
        this.bb = bb;
    }

    public Entity getEntity()
    {
        return entity;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public Block getBlock()
    {
        return block;
    }

    public interface Listener
    {
        void onCollision(CollisionEvent event);
    }

}
