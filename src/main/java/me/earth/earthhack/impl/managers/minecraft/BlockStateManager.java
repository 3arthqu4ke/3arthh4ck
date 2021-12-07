package me.earth.earthhack.impl.managers.minecraft;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.event.listeners.ReceiveListener;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.util.math.BlockPos;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

// TODO: SPacketBlock/MultiBlockChange Listeners
//  in most modules can be replaced with this
public class BlockStateManager extends SubscriberImpl implements Globals
{
    private final Map<BlockPos, Queue<Consumer<IBlockState>>> callbacks =
            new ConcurrentHashMap<>();

    public BlockStateManager()
    {
        this.listeners.add(
            new ReceiveListener<>(SPacketBlockChange.class, event ->
        {
            SPacketBlockChange packet = event.getPacket();
            process(packet.getBlockPosition(), packet.getBlockState());
        }));
        this.listeners.add(
            new ReceiveListener<>(SPacketMultiBlockChange.class, event ->
        {
            SPacketMultiBlockChange packet = event.getPacket();
            for (SPacketMultiBlockChange.BlockUpdateData data :
                    packet.getChangedBlocks())
            {
                process(data.getPos(), data.getBlockState());
            }
        }));
        this.listeners.add(
            new ReceiveListener<>(SPacketExplosion.class, event ->
        {
            SPacketExplosion packet = event.getPacket();
            for (BlockPos pos : packet.getAffectedBlockPositions())
            {
                process(pos, Blocks.AIR.getDefaultState());
            }
        }));
        this.listeners.add(new EventListener<WorldClientEvent.Load>
            (WorldClientEvent.Load.class)
        {
            @Override
            public void invoke(WorldClientEvent.Load event)
            {
                callbacks.clear();
            }
        });
        this.listeners.add(new EventListener<WorldClientEvent.Unload>
            (WorldClientEvent.Unload.class)
        {
            @Override
            public void invoke(WorldClientEvent.Unload event)
            {
                callbacks.clear();
            }
        });
    }

    /**
     * The Callback will be invoked and removed when a
     * {@link SPacketBlockChange} or {@link SPacketMultiBlockChange}
     * or a {@link SPacketExplosion} packet is received,
     * which targets the given position.
     *
     * @param pos the position we want to detect state changes at.
     * @param callback called when the BlockState at the pos is changed.
     */
    public void addCallback(BlockPos pos, Consumer<IBlockState> callback)
    {
        callbacks.computeIfAbsent(pos.toImmutable(), v -> new ConcurrentLinkedQueue<>())
                 .add(callback);
    }

    /** Processes a BlockState change and calls all Callbacks. */
    private void process(BlockPos pos, IBlockState state)
    {
        Queue<Consumer<IBlockState>> cbs = callbacks.remove(pos);
        if (cbs != null)
        {
            CollectionUtil.emptyQueue(cbs, c -> c.accept(state));
        }
    }

}
