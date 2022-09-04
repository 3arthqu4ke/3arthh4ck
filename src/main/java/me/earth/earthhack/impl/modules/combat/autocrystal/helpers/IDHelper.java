package me.earth.earthhack.impl.modules.combat.autocrystal.helpers;

import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.listeners.ReceiveListener;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.SwingTime;
import me.earth.earthhack.impl.util.helpers.blocks.modes.PlaceSwing;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.network.PacketUtil;
import me.earth.earthhack.impl.util.thread.ThreadUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.*;
import net.minecraft.util.EnumHand;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class IDHelper extends SubscriberImpl implements Globals
{
    private static final ScheduledExecutorService THREAD;

    static
    {
        THREAD = ThreadUtil.newDaemonScheduledExecutor("ID-Helper");
    }

    private final Setting<Boolean> basePlaceOnly;
    private volatile int highestID;
    private boolean updated;

    public IDHelper(Setting<Boolean> basePlaceOnly)
    {
        this.basePlaceOnly = basePlaceOnly;
        this.listeners.add(new ReceiveListener<>(SPacketSpawnObject.class,
            event -> checkID(event.getPacket().getEntityID())));
        this.listeners.add(new ReceiveListener<>(SPacketSpawnExperienceOrb.class,
            event -> checkID(event.getPacket().getEntityID())));
        this.listeners.add(new ReceiveListener<>(SPacketSpawnPlayer.class,
            event -> checkID(event.getPacket().getEntityID())));
        this.listeners.add(new ReceiveListener<>(SPacketSpawnGlobalEntity.class,
            event -> checkID(event.getPacket().getEntityId())));
        this.listeners.add(new ReceiveListener<>(SPacketSpawnPainting.class,
            event -> checkID(event.getPacket().getEntityID())));
        this.listeners.add(new ReceiveListener<>(SPacketSpawnMob.class,
            event -> checkID(event.getPacket().getEntityID())));
    }

    public int getHighestID()
    {
        return highestID;
    }

    public void setHighestID(int id)
    {
        this.highestID = id;
    }

    public boolean isUpdated()
    {
        return updated;
    }

    public void setUpdated(boolean updated)
    {
        this.updated = updated;
    }

    public void update()
    {
        int highest = getHighestID();
        for (Entity entity : mc.world.loadedEntityList)
        {
            if (entity.getEntityId() > highest)
            {
                highest = entity.getEntityId();
            }
        }
        // check one more time in case a packet
        // changed this. kinda bad but whatever
        if (highest > highestID)
        {
            highestID = highest;
        }
    }

    public boolean isSafe(List<EntityPlayer> players,
                          boolean holdingCheck,
                          boolean toolCheck)
    {
        if (!holdingCheck)
        {
            return true;
        }

        for (EntityPlayer player : players)
        {
            if (isDangerous(player, true, toolCheck))
            {
                return false;
            }
        }

        return true;
    }

    public boolean isDangerous(EntityPlayer player,
                               boolean holdingCheck,
                               boolean toolCheck)
    {
        if (!holdingCheck)
        {
            return false;
        }

        return InventoryUtil.isHolding(player, Items.BOW)
            || InventoryUtil.isHolding(player, Items.EXPERIENCE_BOTTLE)
            || toolCheck && (
               player.getHeldItemMainhand().getItem() instanceof ItemPickaxe
                || player.getHeldItemMainhand().getItem() instanceof ItemSpade);
    }

    public void attack(SwingTime breakSwing,
                       PlaceSwing godSwing,
                       int idOffset,
                       int packets,
                       int sleep)
    {

        if (basePlaceOnly.getValue())
        {
            return;
        }

        if (sleep <= 0)
        {
            attackPackets(breakSwing, godSwing, idOffset, packets);
        }
        else
        {
            THREAD.schedule(() -> {
                    update();
                    attackPackets(breakSwing, godSwing, idOffset, packets);
                },
                sleep,
                TimeUnit.MILLISECONDS);
        }
    }

    private void attackPackets(SwingTime breakSwing,
                               PlaceSwing godSwing,
                               int idOffset,
                               int packets)
    {
        for (int i = 0; i < packets; i++)
        {
            int id = highestID + idOffset + i;
            Entity entity = mc.world.getEntityByID(id);
            if (entity == null || entity instanceof EntityEnderCrystal)
            {
                if (godSwing == PlaceSwing.Always
                        && breakSwing == SwingTime.Pre)
                {
                    Swing.Packet.swing(EnumHand.MAIN_HAND);
                }

                CPacketUseEntity packet = PacketUtil.attackPacket(id);
                mc.player.connection.sendPacket(packet);

                if (godSwing == PlaceSwing.Always
                        && breakSwing == SwingTime.Post)
                {
                    Swing.Packet.swing(EnumHand.MAIN_HAND);
                }
            }
        }

        if (godSwing == PlaceSwing.Once)
        {
            Swing.Packet.swing(EnumHand.MAIN_HAND);
        }
    }

    private void checkID(int id)
    {
        if (id > highestID)
        {
            highestID = id;
        }
    }

}
