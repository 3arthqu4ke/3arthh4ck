package me.earth.earthhack.impl.modules.render.holeesp.invalidation;

import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.core.ducks.world.IChunk;
import me.earth.earthhack.impl.event.events.misc.BlockStateChangeEvent;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.events.render.UnloadChunkEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.event.listeners.ReceiveListener;
import me.earth.earthhack.impl.managers.thread.GlobalExecutor;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.blocks.HoleUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.chunk.Chunk;

import java.util.*;

// TODO: replace all incrementation stuff with just offsets, it's very confusing
public class InvalidationHoleManager extends SubscriberImpl implements Globals, HoleManager
{
    // note that these offsets are meant to be added after one another
    private static final Vec3i[] AIR_OFFSETS = new Vec3i[]
            {
                    new Vec3i(0, 1, 0),
                    new Vec3i(-1, -1, 0),
                    new Vec3i(1, 0, 1),
                    new Vec3i(1, 0, -1),
                    new Vec3i(-1, 0, -1)
            };

    private static final Vec3i[] BLOCK_OFFSETS = new Vec3i[]
            {
                    new Vec3i(0, 0, 0),
                    new Vec3i(0, -1, 0)
            };

    private final MutPos mut_pos = new MutPos();
    private final Map<BlockPos, Hole> holes = new HashMap<>();
    private final List<Hole> _1x1_safe = new ArrayList<>();
    private final List<Hole> _1x1_unsafe = new ArrayList<>();
    private final List<Hole> _2x1 = new ArrayList<>();
    private final List<Hole> _2x2 = new ArrayList<>();

    private final AirHoleFinder onAirAdded = new AirHoleFinder(this);
    private final BlockHoleFinder onBlockAdded = new BlockHoleFinder(this);
    private final StopWatch removeTimer = new StopWatch();
    private final StopWatch sortTimer = new StopWatch();
    private final InvalidationConfig config;

    private List<Runnable> callbacks = null;

    public InvalidationHoleManager(InvalidationConfig config)
    {
        this.config = config;
        listeners.add(new LambdaListener<>(TickEvent.class, tickEvent ->
        {
            if (!config.isUsingInvalidationHoleManager())
            {
                return;
            }

            if (mc.player == null || mc.world == null)
            {
                reset();
            }
            else
            {
                if (sortTimer.passed(config.getSortTime()))
                {
                    double x = mc.player.posX;
                    double y = mc.player.posY;
                    double z = mc.player.posZ;
                    _1x1_safe.sort(Comparator.comparingDouble(
                            h -> h.getDistanceSq(x, y, z)));
                    _1x1_unsafe.sort(Comparator.comparingDouble(
                            h -> h.getDistanceSq(x, y, z)));
                    _2x1.sort(Comparator.comparingDouble(
                            h -> h.getDistanceSq(x, y, z)));
                    _2x2.sort(Comparator.comparingDouble(
                            h -> h.getDistanceSq(x, y, z)));
                    sortTimer.reset();
                }

                if (removeTimer.passed(config.getRemoveTime()))
                {
                    // TODO: the map might not need to get cleaned as often?
                    holes.entrySet().removeIf(e -> !e.getValue().isValid());
                    _1x1_safe.removeIf(h -> !h.isValid());
                    _1x1_unsafe.removeIf(h -> !h.isValid());
                    _2x1.removeIf(h -> !h.isValid());
                    _2x2.removeIf(h -> !h.isValid());
                    removeTimer.reset();
                }
            }
        }));

        listeners.add(new ReceiveListener<>(SPacketBlockChange.class,
                e -> handleBlockChangePacket(e, 1)));

        listeners.add(new ReceiveListener<>(SPacketMultiBlockChange.class,
                e -> handleBlockChangePacket(e, e.getPacket().getChangedBlocks().length)));

        listeners.add(new LambdaListener<>(BlockStateChangeEvent.class, event ->
        {
            if (config.isUsingInvalidationHoleManager())
            {
                BlockPos pos = event.getPos();
                IBlockState state = event.getState();
                IChunk chunk = event.getChunk();
                chunk.addHoleTask(() ->
                {
                    mut_pos.setX(pos.getX());
                    mut_pos.setY(pos.getY());
                    mut_pos.setZ(pos.getZ());
                    invalidate(state.getBlock());
                });

                if (callbacks == null)
                {
                    addPostCompilationTask(pos, state, chunk);
                }
                else
                {
                    callbacks.add(() -> addPostCompilationTask(pos, state, chunk));
                }
            }
        }));

        listeners.add(new LambdaListener<>(UnloadChunkEvent.class, event ->
        {
            ((IChunk) event.getChunk()).setHoleVersion(((IChunk) event.getChunk()).getHoleVersion() + 1);
            if (config.isUsingInvalidationHoleManager() && mc.world != null)
            {
                holes.entrySet().removeIf(e ->
                {
                    if (!mc.world.isBlockLoaded(e.getKey()))
                    {
                        e.getValue().invalidate();
                        return true;
                    }

                    return false;
                });
            }
        }));

        listeners.add(new ReceiveListener<>(SPacketChunkData.class, e -> e.addPostEvent(() ->
        {
            if (config.isUsingInvalidationHoleManager())
            {
                SPacketChunkData p = e.getPacket();
                Chunk chunk = mc.world.getChunk(p.getChunkX(), p.getChunkZ());
                HoleFinder compiler = new HoleFinder(chunk, config.getHeight(), this);
                ((IChunk) chunk).setCompilingHoles(true);
                ((IChunk) chunk).setHoleVersion(((IChunk) chunk).getHoleVersion() + 1);
                if (config.shouldCalcChunksAsnyc())
                {
                    if (config.limitChunkThreads())
                    {
                        GlobalExecutor.FIXED_EXECUTOR.submit(compiler);
                    }
                    else
                    {
                        GlobalExecutor.EXECUTOR.submit(compiler);
                    }
                }
                else
                {
                    compiler.run();
                }
            }
        })));
    }

    private void handleBlockChangePacket(PacketEvent.Receive<?> event, int amount)
    {
        if (config.isUsingInvalidationHoleManager())
        {
            List<Runnable> packetCallbacks = new ArrayList<>(amount);
            mc.addScheduledTask(() ->
            {
                this.callbacks = packetCallbacks;
            });
            event.addPostEvent(() ->
            {
                if (packetCallbacks != this.callbacks)
                {
                    Earthhack.getLogger().error(
                            "Callbacks have changed while processing " +
                                    "a BlockChange packet!");
                    return;
                }

                this.callbacks = null;
                packetCallbacks.forEach(Runnable::run);
            });
        }
    }

    private void addPostCompilationTask(BlockPos pos, IBlockState state, IChunk chunk)
    {
        chunk.addHoleTask(() ->
        {
            Block block = state.getBlock();
            if (HoleUtil.NO_BLAST.contains(block))
            {
                onBlockAdded.setPos(pos);
                onBlockAdded.setChunk(chunk);
                onBlockAdded.calcHoles();
            }
            else if (block == Blocks.AIR)
            {
                onAirAdded.setPos(pos);
                onAirAdded.setChunk(chunk);
                onAirAdded.calcHoles();
            }
        });
    }

    private void invalidate(Block block)
    {
        if (HoleUtil.NO_BLAST.contains(block))
        {
            invalidate(BLOCK_OFFSETS);
        }
        else if (block == Blocks.AIR)
        {
            invalidate(AIR_OFFSETS);
        }
        else
        {
            int x = mut_pos.getX();
            int y = mut_pos.getY();
            int z = mut_pos.getZ();
            invalidate(AIR_OFFSETS);
            mut_pos.setPos(x, y, z);
            invalidate(BLOCK_OFFSETS);
        }
    }

    private void invalidate(Vec3i... offsets)
    {
        for (Vec3i vec3i : offsets)
        {
            mut_pos.incrementX(vec3i.getX());
            mut_pos.incrementY(vec3i.getY());
            mut_pos.incrementZ(vec3i.getZ());
            Hole hole = holes.get(mut_pos);
            if (hole != null && (hole.isAirPart(mut_pos)))
            {
                holes.remove(mut_pos);
                hole.invalidate();
            }
        }
    }

    @Override
    public Map<BlockPos, Hole> getHoles()
    {
        return holes;
    }

    @Override
    public List<Hole> get1x1()
    {
        return _1x1_safe;
    }

    @Override
    public List<Hole> get1x1Unsafe()
    {
        return _1x1_unsafe;
    }

    @Override
    public List<Hole> get2x1()
    {
        return _2x1;
    }

    @Override
    public List<Hole> get2x2()
    {
        return _2x2;
    }

}
