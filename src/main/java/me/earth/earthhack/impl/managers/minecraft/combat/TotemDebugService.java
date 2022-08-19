package me.earth.earthhack.impl.managers.minecraft.combat;

import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.event.bus.api.EventBus;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.core.mixins.network.server.ISPacketEntityStatus;
import me.earth.earthhack.impl.event.listeners.ReceiveListener;
import me.earth.earthhack.tweaker.launch.Argument;
import me.earth.earthhack.tweaker.launch.DevArguments;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketUpdateHealth;

public class TotemDebugService extends SubscriberImpl implements Globals
{
    private volatile long time;

    public TotemDebugService()
    {
        this.listeners.add(new ReceiveListener<>(SPacketEntityStatus.class, e ->
        {
            EntityPlayer player = mc.player;
            ISPacketEntityStatus packet = (ISPacketEntityStatus) e.getPacket();
            if (player != null
                && packet.getLogicOpcode() == 35
                && packet.getEntityId() == player.getEntityId())
            {
                long t = System.currentTimeMillis();
                Earthhack.getLogger().info(
                    "Pop, last pop: " + (t - time) + "ms");
                time = t;
            }
        }));
        // SPacketEntityMetadata or SPacketEntityProperties might be earlier.
        this.listeners.add(new ReceiveListener<>(SPacketUpdateHealth.class, e ->
        {
            if (e.getPacket().getHealth() <= 0.0f)
            {
                long t = System.currentTimeMillis();
                Earthhack.getLogger().info(
                    "Death, last pop: " + (t - time) + "ms");
                time = t;
            }
        }));
    }

    public static void trySubscribe(EventBus eventBus)
    {
        Argument<Boolean> a = DevArguments.getInstance().getArgument("totems");
        if (a == null || a.getValue())
        {
            Earthhack.getLogger().info("TotemDebugger loaded.");
            eventBus.subscribe(new TotemDebugService());
        }
    }

}
