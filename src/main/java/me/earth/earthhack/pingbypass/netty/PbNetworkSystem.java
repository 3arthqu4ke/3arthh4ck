package me.earth.earthhack.pingbypass.netty;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import me.earth.earthhack.pingbypass.PingBypassConfig;
import me.earth.earthhack.pingbypass.nethandler.HandshakeHandler;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.network.*;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.util.LazyLoadBase;
import net.minecraft.util.ReportedException;
import net.minecraft.util.text.TextComponentString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * {@link NetworkSystem} for PingBypass.
 */
@SuppressWarnings("NullableProblems")
public class PbNetworkSystem {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final LazyLoadBase<NioEventLoopGroup> SERVER_NIO_EVENTLOOP = new LazyLoadBase<NioEventLoopGroup>()
    {
        protected NioEventLoopGroup load()
        {
            return new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Server IO #%d").setDaemon(true).build());
        }
    };
    public static final LazyLoadBase<EpollEventLoopGroup> SERVER_EPOLL_EVENTLOOP = new LazyLoadBase<EpollEventLoopGroup>()
    {
        protected EpollEventLoopGroup load()
        {
            return new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Server IO #%d").setDaemon(true).build());
        }
    };

    /** True if this NetworkSystem has never had his endpoints terminated */
    public volatile boolean isAlive;
    /** Contains all endpoints added to this NetworkSystem */
    private final List<ChannelFuture> endpoints = Collections.synchronizedList(Lists.newArrayList());
    /** A list containing all NetworkManager instances of all endpoints */
    private final List<NetworkManager> networkManagers = Collections.synchronizedList(Lists.newArrayList());

    private final PingBypassConfig config;

    public PbNetworkSystem(PingBypassConfig config)
    {
        this.config = config;
        this.isAlive = true;
    }

    /**
     * Adds a channel that listens on publicly accessible network ports
     */
    public void addEndpoint(InetAddress address, int port) throws IOException
    {
        LOGGER.info("Listening on " + address + " : " + port);
        synchronized (this.endpoints)
        {
            Class <? extends ServerSocketChannel > oclass;
            LazyLoadBase <? extends EventLoopGroup > lazyloadbase;

            if (Epoll.isAvailable() && this.config.shouldUseNativeTransport())
            {
                oclass = EpollServerSocketChannel.class;
                lazyloadbase = SERVER_EPOLL_EVENTLOOP;
                LOGGER.info("Using epoll channel type");
            }
            else
            {
                oclass = NioServerSocketChannel.class;
                lazyloadbase = SERVER_NIO_EVENTLOOP;
                LOGGER.info("Using default channel type");
            }

            this.endpoints.add((new ServerBootstrap()).channel(oclass).childHandler(new ChannelInitializer<Channel>()
            {
                protected void initChannel(Channel p_initChannel_1_)
                {
                    try
                    {
                        p_initChannel_1_.config().setOption(ChannelOption.TCP_NODELAY, Boolean.TRUE);
                    }
                    catch (ChannelException ignored)
                    {

                    }

                    p_initChannel_1_
                        .pipeline()
                        .addLast("timeout", new ReadTimeoutHandler(30))
                        .addLast("legacy_query", new PbLegacyPingHandler())
                        .addLast("splitter", new NettyVarint21FrameDecoder())
                        .addLast("decoder", new NettyPacketDecoder(EnumPacketDirection.SERVERBOUND))
                        .addLast("prepender", new NettyVarint21FrameEncoder())
                        .addLast("encoder", new NettyPacketEncoder(EnumPacketDirection.CLIENTBOUND));

                    NetworkManager networkmanager = new PbNetworkManager(EnumPacketDirection.SERVERBOUND);
                    PbNetworkSystem.this.networkManagers.add(networkmanager);
                    p_initChannel_1_.pipeline().addLast("packet_handler", networkmanager);
                    networkmanager.setNetHandler(new HandshakeHandler(networkmanager));
                }
            }).group(lazyloadbase.getValue()).localAddress(address, port).bind().syncUninterruptibly());
        }
    }

    /**
     * Shuts down all open endpoints (with immediate effect?)
     */
    public void terminateEndpoints()
    {
        this.isAlive = false;

        for (ChannelFuture channelfuture : this.endpoints)
        {
            try
            {
                channelfuture.channel().close().sync();
            }
            catch (InterruptedException var4)
            {
                LOGGER.error("Interrupted whilst closing channel");
            }
        }
    }

    /**
     * Will try to process the packets received by each NetworkManager, gracefully manage processing failures and cleans
     * up dead connections
     */
    @SuppressWarnings("unchecked")
    public void networkTick()
    {
        synchronized (this.networkManagers)
        {
            Iterator<NetworkManager> iterator = this.networkManagers.iterator();

            while (iterator.hasNext())
            {
                final NetworkManager networkmanager = iterator.next();

                if (!networkmanager.hasNoChannel())
                {
                    if (networkmanager.isChannelOpen())
                    {
                        try
                        {
                            networkmanager.processReceivedPackets();
                        }
                        catch (Exception exception)
                        {
                            if (networkmanager.isLocalChannel())
                            {
                                CrashReport crashreport = CrashReport.makeCrashReport(exception, "Ticking memory connection");
                                CrashReportCategory crashreportcategory = crashreport.makeCategory("Ticking connection");
                                crashreportcategory.addDetail("Connection", networkmanager::toString);
                                throw new ReportedException(crashreport);
                            }

                            LOGGER.warn("Failed to handle packet for {}", networkmanager.getRemoteAddress(), exception);
                            exception.printStackTrace();
                            TextComponentString textcomponentstring = new TextComponentString("Internal server error");
                            networkmanager.sendPacket(new SPacketDisconnect(textcomponentstring), p -> networkmanager.closeChannel(textcomponentstring));
                            networkmanager.disableAutoRead();
                        }
                    }
                    else
                    {
                        iterator.remove();
                        networkmanager.handleDisconnection();
                    }
                }
            }
        }
    }

}
