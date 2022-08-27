package me.earth.earthhack.impl.modules.client.debug;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.core.ducks.network.ICPacketUseEntity;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.events.misc.UpdateEntitiesEvent;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import me.earth.earthhack.impl.event.listeners.PostSendListener;
import me.earth.earthhack.impl.event.listeners.ReceiveListener;
import me.earth.earthhack.impl.util.client.DebugUtil;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Don't remove debugPlace!
 * and debugBreak
 * and the SlowUpdates thing
 * <p>
 * I wasn't going to :P
 */
public class Debug extends Module
{
    private final Setting<Boolean> debugPlace =
         register(new BooleanSetting("DebugPlacePing", false));
    private final Setting<Boolean> debugPlaceDistance =
        register(new BooleanSetting("DebugPlaceDistance", false));
    private final Setting<Boolean> debugBreak =
        register(new BooleanSetting("DebugBreakPing", false));

    private final Map<BlockPos, Long> times  = new ConcurrentHashMap<>();
    private final Map<BlockPos, Long> attack = new ConcurrentHashMap<>();
    private final Map<Integer, BlockPos> ids = new ConcurrentHashMap<>();

    public Debug()
    {
        super("Debug", Category.Client);
        register(new EnumSetting<>("ConsoleColors", ConsoleColors.Unformatted));
        SimpleData data =
            new SimpleData(this, "An empty module for debugging.");
        // DONT REMOVE THIS!
        Setting<?> s = register(new BooleanSetting("SlowUpdates", false));
        data.register(s, "Makes all Chunk Updates happen on a separate Thread. "
                + "Might increase FPS, but could cause Render lag.");
        this.setData(data);
        this.listeners.add(new EventListener<TickEvent>(TickEvent.class)
        {
            @Override
            public void invoke(TickEvent event)
            {
                // DEBUG
            }
        });
        this.listeners.add(new EventListener<MotionUpdateEvent>(
                MotionUpdateEvent.class)
        {
            @Override
            public void invoke(MotionUpdateEvent event)
            {
                // DEBUG
            }
        });
        this.listeners.add(new EventListener<UpdateEntitiesEvent>(
                UpdateEntitiesEvent.class)
        {
            @Override
            public void invoke(UpdateEntitiesEvent event)
            {
                // DEBUG
            }
        });
        this.listeners.add(new EventListener<WorldClientEvent>(
                WorldClientEvent.class)
        {
            @Override
            public void invoke(WorldClientEvent event)
            {
                reset();
            }
        });
        this.listeners.add(new ReceiveListener<>(SPacketSoundEffect.class, e ->
        {
            SPacketSoundEffect p = e.getPacket();
            if (debugBreak.getValue()
                && p.getCategory() == SoundCategory.BLOCKS
                    && p.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE)
            {
                BlockPos pos = new BlockPos(p.getX(), p.getY() - 1, p.getZ());
                Long l = attack.remove(pos);
                if (l != null)
                {
                    ChatUtil.sendMessageScheduled("Attack took "
                            + (System.currentTimeMillis() - l) + "ms.");
                }
            }
        }));
        this.listeners.add(new PostSendListener<>(CPacketUseEntity.class, e ->
        {
            if (!debugBreak.getValue())
            {
                return;
            }

            int entityId = ((ICPacketUseEntity) e.getPacket()).getEntityID();
            Entity entity = mc.world.getEntityByID(entityId);
            BlockPos pos;
            if (entity == null)
            {
                pos = ids.get(entityId);
            }
            else
            {
                pos = entity.getPosition().down();
            }

            if (pos != null)
            {
                attack.put(pos, System.currentTimeMillis());
            }
        }));
        this.listeners.add(new PostSendListener<>(
                CPacketPlayerTryUseItemOnBlock.class, e ->
        {
            if (mc.player.getHeldItem(e.getPacket().getHand()).getItem()
                    == Items.END_CRYSTAL
                && !times.containsKey(e.getPacket().getPos()))
            {
                times.put(e.getPacket().getPos(), System.currentTimeMillis());
            }
        }));
        this.listeners.add(new ReceiveListener<>(SPacketSpawnObject.class,
                                                 Integer.MAX_VALUE,
                                                 e ->
        {
            if (e.getPacket().getType() == 51)
            {
                BlockPos pos = new BlockPos(e.getPacket().getX(),
                                            e.getPacket().getY() - 1,
                                            e.getPacket().getZ());
                if (debugPlace.getValue() || debugPlaceDistance.getValue())
                {
                    Long l = times.remove(pos);
                    if (l != null)
                    {
                        long diff = System.currentTimeMillis() - l;
                        EntityPlayer player = mc.player;
                        SPacketSpawnObject packet = e.getPacket();
                        double x = packet.getX();
                        double y = packet.getY();
                        double z = packet.getZ();
                        EntityEnderCrystal entity =
                            new EntityEnderCrystal(mc.world, x, y, z);
                        boolean canBeSeen = true;
                        if (debugPlaceDistance.getValue()
                            && diff < 1000 && player != null
                            && (player.getDistanceSq(entity) >= 36.0
                            || !(canBeSeen = player.canEntityBeSeen(entity))
                            && player.getDistanceSq(entity) >= 9.0))
                        {
                            boolean finalCanBeSeen = canBeSeen;
                            mc.addScheduledTask(() ->
                                DebugUtil.debug(pos, (finalCanBeSeen
                                    ? TextColor.RED : TextColor.GOLD)
                                    + "Crystal was out of range!"));
                        }
                        else if (debugPlace.getValue())
                        {
                            mc.addScheduledTask(() -> DebugUtil.debug(
                                pos, "Crystal took " + diff + "ms to spawn."));
                        }
                    }
                }

                if (debugBreak.getValue())
                {
                    ids.put(e.getPacket().getEntityID(), pos);
                }
            }
        }));
        this.listeners.addAll(new CPacketPlayerListener()
        {
            @Override
            protected void onPacket(PacketEvent.Send<CPacketPlayer> event)
            {

            }

            @Override
            protected void onPosition(PacketEvent.Send<CPacketPlayer.Position> event)
            {

            }

            @Override
            protected void onRotation(PacketEvent.Send<CPacketPlayer.Rotation> event)
            {

            }

            @Override
            protected void onPositionRotation(PacketEvent.Send<CPacketPlayer.PositionRotation> event)
            {

            }
        }.getListeners());
    }

    @Override
    protected void onEnable()
    {
        reset();
    }

    @Override
    protected void onDisable()
    {
        reset();
    }

    private void reset()
    {
        times.clear();
        attack.clear();
        ids.clear();
    }

}