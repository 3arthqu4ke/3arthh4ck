package me.earth.earthhack.impl.managers.minecraft.movement;

import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.entity.IEntity;
import me.earth.earthhack.impl.core.ducks.entity.IEntityPlayer;
import me.earth.earthhack.impl.core.ducks.network.ISPacketEntityTeleport;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.event.listeners.ReceiveListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.management.Management;
import me.earth.earthhack.impl.modules.render.nametags.IEntityNoNametag;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntity;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.util.math.MathHelper;

import java.util.function.Consumer;

/**
 * Calculates motionX, Y, Z for players.
 */
public class PlayerMotionService extends SubscriberImpl implements Globals {
    private static final SettingCache
        <Boolean, BooleanSetting, Management> ACTIVE =
        Caches.getSetting(Management.class, BooleanSetting.class, "MotionService", true);
    private static final SettingCache
        <Double, Setting<Double>, Management> UPDATES =
        Caches.getSetting(Management.class, Setting.class, "EntityTracker-Updates", 2.0);

    public PlayerMotionService() {
        this.listeners.add(new LambdaListener<>(TickEvent.class, e -> {
            if (e.isSafe()) {
                for (EntityPlayer player : mc.world.playerEntities) {
                    if (!(player instanceof IEntityNoNametag) && !(player instanceof EntityPlayerSP)) {
                        int ticks = ((IEntityPlayer) player).getTicksWithoutMotionUpdate();
                        if (ticks > UPDATES.getValue() + 1) {
                            player.motionX = 0.0;
                            player.motionY = 0.0;
                            player.motionZ = 0.0;
                        } else {
                            ((IEntityPlayer) player).setTicksWithoutMotionUpdate(ticks + 1);
                        }
                    }
                }
            }
        }));
        this.listeners.add(new ReceiveListener<>(SPacketEntity.S15PacketEntityRelMove.class, Integer.MAX_VALUE, e ->
            addPlayerCallback(e.getPacket(), entity -> {
                if (e.getPacket().getX() == 0 && e.getPacket().getY() == 0 && e.getPacket().getZ() == 0) {
                    return;
                }

                ((IEntityPlayer) entity).setTicksWithoutMotionUpdate(0);
                // we divide by 4096 instead of 8192 because the entity tracker only updates every 2 ticks
                entity.motionX = e.getPacket().getX() / (4092.0 * UPDATES.getValue());
                entity.motionY = e.getPacket().getY() / (4092.0 * UPDATES.getValue());
                entity.motionZ = e.getPacket().getZ() / (4092.0 * UPDATES.getValue());
            })
        ));
        this.listeners.add(new ReceiveListener<>(SPacketEntity.S17PacketEntityLookMove.class, Integer.MAX_VALUE, e ->
            addPlayerCallback(e.getPacket(), entity -> {
                if (e.getPacket().getX() == 0 && e.getPacket().getY() == 0 && e.getPacket().getZ() == 0) {
                    return;
                }

                ((IEntityPlayer) entity).setTicksWithoutMotionUpdate(0);
                // we divide by 4096 instead of 8192 because the entity tracker only updates every 2 ticks
                entity.motionX = e.getPacket().getX() / (4092.0 * UPDATES.getValue());
                entity.motionY = e.getPacket().getY() / (4092.0 * UPDATES.getValue());
                entity.motionZ = e.getPacket().getZ() / (4092.0 * UPDATES.getValue());
            })
        ));
        this.listeners.add(new ReceiveListener<>(SPacketEntity.S16PacketEntityLook.class, Integer.MAX_VALUE, e ->
            addPlayerCallback(e.getPacket(), entity -> {
                ((IEntityPlayer) entity).setTicksWithoutMotionUpdate(0);
                entity.motionX = 0.0;
                entity.motionY = 0.0;
                entity.motionZ = 0.0;
            })
        ));
        this.listeners.add(new ReceiveListener<>(SPacketEntity.class, Integer.MAX_VALUE, e ->
            addPlayerCallback(e.getPacket(), entity -> {
                ((IEntityPlayer) entity).setTicksWithoutMotionUpdate(0);
                entity.motionX = 0.0;
                entity.motionY = 0.0;
                entity.motionZ = 0.0;
            })
        ));
        this.listeners.add(new ReceiveListener<>(SPacketEntityTeleport.class, Integer.MIN_VALUE, e -> {
            if (ACTIVE.getValue()) {
                double x = e.getPacket().getX();
                double y = e.getPacket().getY();
                double z = e.getPacket().getZ();
                mc.addScheduledTask(() -> {
                    if (mc.world != null) {
                        Entity entity = mc.world.getEntityByID(e.getPacket().getEntityId());
                        if (entity instanceof EntityPlayer && !(entity instanceof EntityPlayerSP)) {
                            long serverPosX = ((ISPacketEntityTeleport) e.getPacket()).hasBeenSetByPackets()
                                ? ((IEntity) entity).getOldServerPosX()
                                : entity.serverPosX;
                            long serverPosY = ((ISPacketEntityTeleport) e.getPacket()).hasBeenSetByPackets()
                                ? ((IEntity) entity).getOldServerPosY()
                                : entity.serverPosY;
                            long serverPosZ = ((ISPacketEntityTeleport) e.getPacket()).hasBeenSetByPackets()
                                ? ((IEntity) entity).getOldServerPosZ()
                                : entity.serverPosZ;

                            ((IEntityPlayer) entity).setTicksWithoutMotionUpdate(0);
                            // we divide by 4096 instead of 8192 because the entity tracker only updates every 2 ticks
                            entity.motionX = (MathHelper.lfloor(x * 4096.0) - serverPosX) / (4092.0 * UPDATES.getValue());
                            entity.motionY = (MathHelper.lfloor(y * 4096.0) - serverPosY) / (4092.0 * UPDATES.getValue());
                            entity.motionZ = (MathHelper.lfloor(z * 4096.0) - serverPosZ) / (4092.0 * UPDATES.getValue());
                        }
                    }
                });
            }
        }));
    }

    private void addPlayerCallback(SPacketEntity packet, Consumer<Entity> action) {
        if (ACTIVE.getValue()) {
            mc.addScheduledTask(() -> {
                if (mc.world != null) {
                    Entity entity = packet.getEntity(mc.world);
                    if (entity instanceof EntityPlayer
                        && !(entity instanceof IEntityNoNametag)
                        && !(entity instanceof EntityPlayerSP)) {
                        action.accept(entity);
                    }
                }
            });
        }
    }

}
