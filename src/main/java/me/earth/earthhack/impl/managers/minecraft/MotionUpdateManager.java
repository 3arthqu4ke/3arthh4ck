package me.earth.earthhack.impl.managers.minecraft;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.misc.GameLoopEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import net.minecraft.network.play.client.CPacketPlayer;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Handles cancelling position update packets for modules that use this functionality.
 * Also can modify the rate at which these packets are sent.
 * Uses include speeding up movement when needed and
 */
public class MotionUpdateManager extends SubscriberImpl implements Globals
{

	/** Queue containing packets that have been delayed and need to be sent. */
	private final Queue<CPacketPlayer> delayed = new LinkedList<>();

	public MotionUpdateManager()
	{
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

		this.listeners.add(new EventListener<GameLoopEvent>(GameLoopEvent.class)
		{
			@Override
			public void invoke(GameLoopEvent event)
			{

			}
		});
	}

}
