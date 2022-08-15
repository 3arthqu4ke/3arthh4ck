package me.earth.earthhack.impl.event.events.network;

import me.earth.earthhack.api.event.events.Event;
import net.minecraft.network.play.client.CPacketPlayer;

/**
 * Fired when a {@link MotionUpdateEvent} has been
 * fired, but no {@link CPacketPlayer} has been sent.
 */
public class NoMotionUpdateEvent extends Event
{

}
