package me.earth.earthhack.impl.managers.minecraft.combat;

import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.listeners.ReceiveListener;
import net.minecraft.network.play.server.SPacketUpdateHealth;

// TODO: THIS!
public class HealthManager extends SubscriberImpl implements Globals
{
    private volatile float lastAbsorption = -1.0f;
    private volatile float lastHealth     = -1.0f;

    public HealthManager()
    {
        this.listeners.add(new ReceiveListener<>(SPacketUpdateHealth.class, e ->
        {
            SPacketUpdateHealth packet = e.getPacket();
        }));
    }

    public float getLastHealth()
    {
        return lastHealth;
    }
}
