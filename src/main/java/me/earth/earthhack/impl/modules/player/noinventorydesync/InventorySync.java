package me.earth.earthhack.impl.modules.player.noinventorydesync;

import io.netty.util.internal.ConcurrentSet;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.events.misc.WindowClickEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.event.listeners.PostSendListener;
import me.earth.earthhack.impl.event.listeners.ReceiveListener;
import me.earth.earthhack.impl.event.listeners.SendListener;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.server.SPacketConfirmTransaction;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InventorySync extends Module {
    private final Map<Integer, Map.Entry<Short, Long>> window2Id =
        new ConcurrentHashMap<>();
    private final Set<Short> confirmed =
        new ConcurrentSet<>();
    private final Setting<Boolean> alwaysConfirm = register(
        new BooleanSetting("AlwaysConfirm", false));
    private final Setting<Integer> confirmDelay = register(
        new NumberSetting<>("ConfirmDelay", 0, 0, 10_000));
    private final Setting<Boolean> discard = register(
        new BooleanSetting("Discard", false));
    private final Setting<Boolean> accept = register(
        new BooleanSetting("Accept", false));
    private final Setting<Boolean> cancel = register(
        new BooleanSetting("Cancel", true));

    public InventorySync()
    {
        super("InventorySync", Category.Player);
        alwaysConfirm.addObserver(e -> {
            window2Id.clear();
            confirmed.clear();
        });
        this.listeners.add(new LambdaListener<>(WindowClickEvent.class, e ->
        {
            if (!alwaysConfirm.getValue())
            {
                Map.Entry<Short, Long> id = window2Id.get(e.getWindowId());
                if (id != null)
                {
                    e.setCancelled(true);
                }
            }

            // TODO: save state before WindowClick and if
            //  SPacketConfirmTransaction was not accepted, restore the state?
        }));
        this.listeners.add(new PostSendListener<>(CPacketClickWindow.class, p ->
        {
            EntityPlayerSP playerSP;
            if (alwaysConfirm.getValue() && (playerSP = mc.player) != null)
            {
                CPacketClickWindow packet = p.getPacket();
                confirmed.add(packet.getActionNumber());
                playerSP.connection.sendPacket(new CPacketConfirmTransaction(
                    packet.getWindowId(), packet.getActionNumber(), true));
            }
            else
            {
                window2Id.put(p.getPacket().getWindowId(),
                              new AbstractMap.SimpleEntry<>(
                                  p.getPacket().getActionNumber(),
                                  System.currentTimeMillis()));
            }
        }));
        this.listeners.add(new ReceiveListener<>(
            SPacketConfirmTransaction.class, p ->
        {
            if (alwaysConfirm.getValue())
            {
                if (cancel.getValue()
                    && confirmed.remove(p.getPacket().getActionNumber()))
                {
                    p.setCancelled(true);
                }
            }
            else if (p.getPacket().wasAccepted())
            {
                window2Id.remove(p.getPacket().getWindowId());
            }
        }));
        this.listeners.add(new SendListener<>(
            CPacketConfirmTransaction.class, p ->
        {
            if (!alwaysConfirm.getValue())
            {
                CPacketConfirmTransaction packet = p.getPacket();
                Map.Entry<Short, Long> id = window2Id.get(packet.getWindowId());
                if (id != null && id.getKey() == packet.getUid())
                {
                    window2Id.remove(packet.getWindowId());
                }
            }
        }));
        this.listeners.add(new LambdaListener<>(TickEvent.class, e ->
        {
            if (mc.player == null
                && (!confirmed.isEmpty() || window2Id.isEmpty()))
            {
                confirmed.clear();
                window2Id.clear();
            }
            else if (!alwaysConfirm.getValue())
            {
                int delay = confirmDelay.getValue();
                if (delay != 0)
                {
                    for (Map.Entry<Integer, Map.Entry<Short, Long>> entry :
                        window2Id.entrySet())
                    {
                        if (entry.getValue() != null
                            && System.currentTimeMillis()
                                - entry.getValue().getValue()
                                    > confirmDelay.getValue())
                        {
                            EntityPlayerSP player = mc.player;
                            if (window2Id.remove(entry.getKey(),
                                                 entry.getValue())
                                && !discard.getValue()
                                && player != null)
                            {
                                player.connection.sendPacket(
                                    new CPacketConfirmTransaction(
                                        entry.getKey(),
                                        entry.getValue().getKey(),
                                        accept.getValue()));
                            }
                        }
                    }
                }
            }
        }));
    }

    @Override
    protected void onEnable()
    {
        confirmed.clear();
        window2Id.clear();
    }

}
