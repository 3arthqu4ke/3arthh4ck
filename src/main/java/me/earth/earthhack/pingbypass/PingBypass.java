package me.earth.earthhack.pingbypass;

import io.netty.channel.epoll.Epoll;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pbteleport.PbTeleport;
import me.earth.earthhack.impl.modules.misc.autoreconnect.AutoReconnect;
import me.earth.earthhack.impl.util.render.SplashScreenHelper;
import me.earth.earthhack.pingbypass.input.ClientInputService;
import me.earth.earthhack.pingbypass.input.ServerInputService;
import me.earth.earthhack.pingbypass.listeners.*;
import me.earth.earthhack.pingbypass.modules.PbModuleManager;
import me.earth.earthhack.pingbypass.nethandler.BaseNetHandler;
import me.earth.earthhack.pingbypass.nethandler.ServerInfo;
import me.earth.earthhack.pingbypass.netty.PbNetworkSystem;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.util.CryptManager;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.security.KeyPair;

public class PingBypass implements Globals {
    private static final ModuleCache<AutoReconnect> RECONNECT =
        Caches.getModule(AutoReconnect.class);
    private static final ModuleCache<PbTeleport> TELEPORT =
        Caches.getModule(PbTeleport.class);

    private static final Logger LOGGER = LogManager.getLogger(PingBypass.class);
    // I KNOW EVERYTHING IS STATIC AND NO DEPENDENCY INJECTION ANYWHERE ETC: WHATEVER IM JUST TRYING TO WRITE THIS TOGETHER REAL QUICk
    public static final PbModuleManager MODULES = new PbModuleManager();
    public static final ServerInfo INFO = new ServerInfo();
    public static final PingBypassConfig CONFIG = new PingBypassConfig();
    public static final Pb2bQueueListener QUEUE = new Pb2bQueueListener();
    public static final KeyPair KEY_PAIR = CryptManager.generateKeyPair();
    public static final Pb2SManager PACKET_MANAGER = new Pb2SManager();
    public static final PbRenderer RENDER = new PbRenderer();
    public static final PbStatisticsManager STATISTICS = new PbStatisticsManager();
    public static final PbWindowClickService WINDOW_CLICK = new PbWindowClickService();
    public static final PacketFlyService PACKET_SERVICE = new PacketFlyService();
    public static final PbDisconnectListener DISCONNECT_SERVICE = new PbDisconnectListener();
    public static final UnloadedTickService UNLOADED_TICK_SERVICE = new UnloadedTickService();
    public static final CPacketInputService PACKET_INPUT = new CPacketInputService();

    private static volatile NetworkManager networkManager;
    private static volatile boolean server;
    private static volatile boolean connected;
    private static volatile boolean stay;
    private static int ping;

    public static void init() {
        SplashScreenHelper.setSubStep("Loading PingBypass");
        Bus.EVENT_BUS.register(new PbAntiTrollListener());
        if (isServer()) {
            String password = CONFIG.getPassword();
            if (!CONFIG.noPassword() && (password == null || password.isEmpty())) {
                throw new IllegalStateException("Please set a password for your PingBypass!");
            }

            LOGGER.info("Initializing PingBypass-Server!");
            // preload these LazyLoadBase because they are not really made for
            // concurrency (value is not volatile, not synchronized)
            // TODO: transform to make volatile?
            //  either with @Overwrite or a transformer?
            if (Epoll.isAvailable() && mc.gameSettings.useNativeTransport) {
                NetworkManager.CLIENT_EPOLL_EVENTLOOP.getValue();
            } else {
                NetworkManager.CLIENT_NIO_EVENTLOOP.getValue();
            }

            Bus.EVENT_BUS.subscribe(WINDOW_CLICK);
            Bus.EVENT_BUS.register(new PbReceiveListener());
            Bus.EVENT_BUS.register(new PbResourcePackListener());
            Bus.EVENT_BUS.register(new PbTransactionListener());
            Bus.EVENT_BUS.subscribe(QUEUE);

            PbNetworkSystem system = new PbNetworkSystem(CONFIG);
            try {
                system.addEndpoint(InetAddress.getByName(CONFIG.getIp()), CONFIG.getPort());
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }

            Runtime.getRuntime().addShutdownHook(new Thread(system::terminateEndpoints));
            Bus.EVENT_BUS.register(new PbTickListener(system));
            Bus.EVENT_BUS.subscribe(PACKET_MANAGER);
            Bus.EVENT_BUS.register(new PbMoveListener());
            Bus.EVENT_BUS.register(DISCONNECT_SERVICE);
            Bus.EVENT_BUS.register(STATISTICS);
            Bus.EVENT_BUS.register(new PbCustomPayloadListener());
            Bus.EVENT_BUS.register(new PbPlayerDiggingListener());
            Bus.EVENT_BUS.register(new PbSettingListener());
            Bus.EVENT_BUS.subscribe(new ServerInputService());
            Bus.EVENT_BUS.register(new PbLoginSuccessService());
        } else {
            Bus.EVENT_BUS.subscribe(new InventoryService());
            Bus.EVENT_BUS.subscribe(new PbBlockBreakService());
            Bus.EVENT_BUS.subscribe(new CPacketPlayerService());
            Bus.EVENT_BUS.subscribe(RENDER);
            Bus.EVENT_BUS.subscribe(new PbClientModuleKeyboardService());
            Bus.EVENT_BUS.subscribe(new ClientInputService());
            Bus.EVENT_BUS.register(new ClientSpeedService());
            Bus.EVENT_BUS.subscribe(UNLOADED_TICK_SERVICE);
            Bus.EVENT_BUS.subscribe(PACKET_INPUT);
            Bus.EVENT_BUS.subscribe(new ClientDiggingService());
        }
    }

    public static boolean isServer() {
        if (!CONFIG.isLoaded()) {
            CONFIG.load();
            PingBypass.server = CONFIG.isServer();
        }

        return server;
    }

    public static boolean isConnected() {
        return server && connected;
    }

    public static void setConnected(boolean connected) {
        PingBypass.connected = connected;
        if (connected) {
            RECONNECT.get().setConnected(true);
        } else {
            PingBypass.networkManager = null;
            mc.addScheduledTask(() -> {
                TELEPORT.disable();
                PACKET_SERVICE.setPacketFlying(false);
                PACKET_SERVICE.setActualPos(null);
            });
        }
    }

    public static int getPlayerCount() {
        return isConnected() ? 1 : 0;
    }

    public static int getPing() {
        return ping;
    }

    public static void setPing(int ping) {
        PingBypass.ping = ping;
    }

    public static NetworkManager getNetworkManager() {
        return networkManager;
    }

    public static void setNetworkManager(
        NetworkManager networkManager) {
        PingBypass.networkManager = networkManager;
    }

    public static void sendPacket(Packet<?> packet) {
        NetworkManager manager = PingBypass.networkManager;
        if (manager != null) {
            manager.sendPacket(packet);
        }
    }

    public static boolean isStaying() {
        return stay;
    }

    public static void setStay(boolean stay) {
        PingBypass.stay = stay;
        if (stay) {
            RECONNECT.get().setConnected(true);
        }
    }

    public static void sendToActualServer(Packet<?> packet) {
        NetHandlerPlayClient client = mc.getConnection();
        if (client != null) {
            mayAuthorize(packet);
            client.sendPacket(packet);
        }
    }

    public static void mayAuthorize(Packet<?> packet) {
        if (isServer()) {
            PACKET_MANAGER.authorize(packet);
        }
    }

    public static void disconnect(ITextComponent reason) {
        NetworkManager manager = PingBypass.networkManager;
        if (manager != null) {
            LOGGER.info("Sending reason: " + reason.getUnformattedText());
            INetHandler netHandler = manager.getNetHandler();
            if (netHandler instanceof BaseNetHandler) {
                ((BaseNetHandler) netHandler).disconnect(reason);
            } else {
                manager.closeChannel(reason);
            }
        }
    }

}
