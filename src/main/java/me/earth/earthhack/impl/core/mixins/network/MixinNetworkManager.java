package me.earth.earthhack.impl.core.mixins.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.core.ducks.network.INetworkManager;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.logger.Logger;
import me.earth.earthhack.impl.modules.misc.logger.util.LoggerMode;
import me.earth.earthhack.impl.modules.misc.packetdelay.PacketDelay;
import me.earth.earthhack.impl.util.mcp.MappingProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.network.*;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

@Mixin(NetworkManager.class)
public abstract class MixinNetworkManager implements INetworkManager
{
    private static final ModuleCache<Logger> LOGGER_MODULE =
            Caches.getModule(Logger.class);

    private static final ModuleCache<PacketDelay> PACKET_DELAY =
            Caches.getModule(PacketDelay.class);

    @Shadow
    @Final
    private static org.apache.logging.log4j.Logger LOGGER;

    @Shadow
    public abstract boolean isChannelOpen();

    @Shadow
    protected abstract void flushOutboundQueue();

    @Shadow
    protected abstract void dispatchPacket(
        final Packet<?> inPacket,
        final GenericFutureListener
                <? extends Future <? super Void >>[] futureListeners);

    @Shadow
    private Channel channel;

    @Shadow
    public abstract void setConnectionState(EnumConnectionState newState);

    @Shadow
    private INetHandler packetListener;

    @Shadow
    public abstract void sendPacket(Packet<?> packetIn);

    @Shadow @Final private EnumPacketDirection direction;

    @Override
    public Packet<?> sendPacketNoEvent(Packet<?> packetIn)
    {
        return sendPacketNoEvent(packetIn, true);
    }

    @Override
    public Packet<?> sendPacketNoEvent(Packet<?> packet, boolean post)
    {
        // TODO: use PacketEvent.NoEvent instead!
        if (LOGGER_MODULE.isEnabled()
                && LOGGER_MODULE.get().getMode() == LoggerMode.Normal)
        {
            LOGGER_MODULE.get().logPacket(packet,
                    "Sending (No Event) Post: " + post + ", ", false, true);
        }

        PacketEvent.NoEvent<?> event = getNoEvent(packet, post);
        Bus.EVENT_BUS.post(event, packet.getClass());
        if (event.isCancelled())
        {
            return packet;
        }

        if (this.isChannelOpen())
        {
            this.flushOutboundQueue();

            if (post)
            {
                this.dispatchPacket(packet, null);
            }
            else
            {
                this.dispatchSilently(packet);
            }

            return packet;
        }

        return null;
    }

    @Override
    public boolean isPingBypass()
    {
        return false;
    }

    @Override
    public EnumPacketDirection getPacketDirection()
    {
        return direction;
    }

    @Inject(
        method = "sendPacket(Lnet/minecraft/network/Packet;)V",
        at = @At("HEAD"),
        cancellable = true)
    public void onSendPacketPre(Packet<?> packet, CallbackInfo info)
    {
        onSendPacket(packet, info);
    }

    @Inject(
        method = "sendPacket(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;[Lio/netty/util/concurrent/GenericFutureListener;)V",
        at = @At("HEAD"),
        cancellable = true)
    public void onSendPacketPre2(Packet<?> packetIn,
                                  GenericFutureListener<? extends Future<? super Void>> listener,
                                  GenericFutureListener<? extends Future<? super Void>>[] listeners,
                                  CallbackInfo ci)
    {
        onSendPacket(packetIn, ci);
    }

    public void onSendPacket(Packet<?> packet, CallbackInfo ci) {
        if (PACKET_DELAY.isEnabled()
            && !PACKET_DELAY.get().packets.contains(packet)
            && PACKET_DELAY.get().isPacketValid(
            MappingProvider.simpleName(packet.getClass())))
        {
            ci.cancel();
            PACKET_DELAY.get().service.schedule(() ->
            {
                PACKET_DELAY.get().packets.add(packet);
                sendPacket(packet);
                PACKET_DELAY.get().packets.remove(packet);
            }, PACKET_DELAY.get().getDelay(), TimeUnit.MILLISECONDS);
            return;
        }

        PacketEvent.Send<?> event = getSendEvent(packet);
        Bus.EVENT_BUS.post(event, packet.getClass());

        if (event.isCancelled())
        {
            ci.cancel();
        }
    }

    @Inject(
        method = "dispatchPacket",
        at = @At("RETURN"))
    public void onSendPacketPost(
          final Packet<?> packetIn,
          @Nullable final GenericFutureListener
                  <? extends Future <? super Void >>[] futureListeners,
          CallbackInfo info)
    {
        PacketEvent.Post<?> event = getPost(packetIn);
        Bus.EVENT_BUS.post(event, packetIn.getClass());
    }

    /**
     * target = {@link Packet#processPacket(INetHandler)}
     */
    @Inject(
        method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/Packet;processPacket" +
                     "(Lnet/minecraft/network/INetHandler;)V",
            shift = At.Shift.BEFORE),
        cancellable = true)
    @SuppressWarnings("unchecked")
    public void onChannelRead(ChannelHandlerContext context,
                               Packet<?> packet,
                               CallbackInfo info)
    {
        PacketEvent.Receive<?> event = getReceive(packet);

        try
        {
            Bus.EVENT_BUS.post(event, packet.getClass());
        }
        catch (Throwable t) // TODO: find all causes and fix them!
        {
            t.printStackTrace();
        }

        if (event.isCancelled())
        {
            info.cancel();
        }
        else if (!event.getPostEvents().isEmpty())
        {
            try
            {
                ((Packet<INetHandler>) packet)
                        .processPacket(this.packetListener);
            }
            catch (ThreadQuickExitException e)
            {
                // Could use @Redirect instead, but @Inject breaks less
            }

            for (Runnable runnable : event.getPostEvents())
            {
                // TODO: check that this fix didn't break anything
                // Scheduler.getInstance().scheduleAsynchronously(runnable);
                Minecraft.getMinecraft().addScheduledTask(runnable);
            }

            info.cancel();
        }
    }

    @Inject(
        method = "closeChannel",
        at = @At(
            value = "INVOKE",
            target = "Lio/netty/channel/Channel;isOpen()Z",
            remap = false))
    public void onDisconnectHook(ITextComponent component, CallbackInfo info)
    {
        if (this.isChannelOpen())
        {
            Bus.EVENT_BUS.post(getDisconnect(component));
        }
    }

    private void dispatchSilently(Packet<?> inPacket)
    {
        final EnumConnectionState enumconnectionstate =
                EnumConnectionState.getFromPacket(inPacket);
        final EnumConnectionState protocolConnectionState =
                this.channel.attr(NetworkManager.PROTOCOL_ATTRIBUTE_KEY).get();

        if (protocolConnectionState != enumconnectionstate)
        {
            LOGGER.debug("Disabled auto read");
            this.channel.config().setAutoRead(false);
        }

        if (this.channel.eventLoop().inEventLoop())
        {
            if (enumconnectionstate != protocolConnectionState)
            {
                this.setConnectionState(enumconnectionstate);
            }

            ChannelFuture channelfuture =
                    this.channel.writeAndFlush(inPacket);
            channelfuture.addListener(
                    ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }
        else
        {
            this.channel.eventLoop().execute(() ->
            {
                if (enumconnectionstate != protocolConnectionState)
                {
                    setConnectionState(enumconnectionstate);
                }

                ChannelFuture channelfuture1 =
                        channel.writeAndFlush(inPacket);
                channelfuture1.addListener(
                        ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            });
        }
    }

    @Inject(method = "exceptionCaught", at = @At("RETURN"))
    public void onExceptionCaught(ChannelHandlerContext p_exceptionCaught_1_, Throwable p_exceptionCaught_2_, CallbackInfo ci)
    {
        p_exceptionCaught_2_.printStackTrace();
        System.out.println("----------------------------------------------");
        Thread.dumpStack();
    }

}
