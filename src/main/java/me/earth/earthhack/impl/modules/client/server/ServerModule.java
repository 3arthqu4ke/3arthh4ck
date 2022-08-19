package me.earth.earthhack.impl.modules.client.server;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.observable.Observer;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.GlobalExecutor;
import me.earth.earthhack.impl.modules.client.server.api.*;
import me.earth.earthhack.impl.modules.client.server.client.Client;
import me.earth.earthhack.impl.modules.client.server.host.Host;
import me.earth.earthhack.impl.modules.client.server.protocol.Protocol;
import me.earth.earthhack.impl.modules.client.server.protocol.ProtocolUtil;
import me.earth.earthhack.impl.modules.client.server.protocol.handlers.*;
import me.earth.earthhack.impl.modules.client.server.util.ChatLogger;
import me.earth.earthhack.impl.modules.client.server.util.ServerMode;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.earthhack.impl.util.thread.SafeRunnable;
import net.minecraft.entity.player.EntityPlayer;

// TODO: maybe instead of opening 1 thread for every
//  connection we should make some channel thingy?
public class ServerModule extends Module
        implements IShutDownHandler, GlobalExecutor, IVelocityHandler
{
    protected final Setting<ServerMode> mode =
        register(new EnumSetting<>("Mode", ServerMode.Client));
    protected final Setting<String> ip =
        register(new StringSetting("IP", "127.0.0.1"));
    protected final Setting<String> port =
        register(new StringSetting("Port", "0"));
    protected final Setting<Integer> max =
        register(new NumberSetting<>("Connections", 50, 1, 50));
    protected final Setting<Boolean> clientInput =
        register(new BooleanSetting("ClientMessages", false));
    protected final Setting<String> name =
        register(new StringSetting("Name", "3arthh4ck-Host"));
    protected final Setting<Boolean> sync =
        register(new BooleanSetting("Sync", false));

    protected final IServerList serverList = new SimpleServerList();
    protected final IPacketManager sPackets = new SimplePacketManager();
    protected final IPacketManager cPackets = new SimplePacketManager();

    protected ServerMode currentMode;
    protected IConnectionManager connectionManager;
    protected IClient client;
    protected IHost host;
    protected boolean isEating;
    protected double lastX;
    protected double lastY;
    protected double lastZ;

    public ServerModule()
    {
        super("Server", Category.Client);
        this.listeners.addAll(new ListenerCPacket(this).getListeners());
        this.listeners.add(new ListenerStartEating(this));
        this.listeners.add(new ListenerStopEating(this));
        this.listeners.add(new ListenerMove(this));
        this.listeners.add(new ListenerNoUpdate(this));
        name.setValue(mc.getSession().getProfile().getName());
        Observer<Object> observer = e ->
        {
            if (this.isEnabled())
            {
                ChatUtil.sendMessageScheduled("The server has to be restarted" +
                        " in order for the changes to take effect.");
            }
        };
        mode       .addObserver(observer);
        ip         .addObserver(observer);
        port       .addObserver(observer);
        max        .addObserver(observer);
        clientInput.addObserver(observer);
        name       .addObserver(observer);
        setupConnectionManagers();
    }

    @Override
    public String getDisplayInfo()
    {
        if (host != null)
        {
            return host.getConnectionManager().getConnections().size() + "";
        }

        return null;
    }

    @Override
    protected void onEnable()
    {
        try
        {
            boolean receive = clientInput.getValue();
            int port = Integer.parseInt(this.port.getValue());
            currentMode = mode.getValue();
            switch (currentMode)
            {
                case Host:
                    this.connectionManager =
                        new SimpleConnectionManager(cPackets, max.getValue());
                    host = Host.createAndStart(
                        EXECUTOR, connectionManager, this, port, receive);
                    ModuleUtil.sendMessage(this, TextColor.GREEN
                        + "Server is listening on port: " + TextColor.WHITE
                        + host.getPort() + TextColor.GREEN + ".");
                    break;
                case Client:
                    client =
                        new Client(sPackets, serverList, ip.getValue(), port);
                    Managers.THREAD.submit((SafeRunnable) client);
                    String s = name.getValue();
                    client.setName(s);
                    ProtocolUtil.sendMessage(client, Protocol.NAME, s);
                    break;
                default:
            }
        }
        catch (NumberFormatException e)
        {
            ModuleUtil.disableRed(this, "Couldn't parse port: "
                    + port.getValue() + ".");
        }
        catch (Throwable t)
        {
            ModuleUtil.disableRed(this, t.getMessage());
        }
    }

    @Override
    protected void onDisable()
    {
        if (client != null)
        {
            client.close();
            client = null;
        }

        if (host != null)
        {
            host.close();
            host = null;
        }

        this.connectionManager = null;
        this.serverList.set(new IConnectionEntry[0]);
    }

    @Override
    public void disable(String message)
    {
        mc.addScheduledTask(() -> ModuleUtil.disableRed(this, message));
    }

    public IClient getClient()
    {
        return client;
    }

    public IHost getHost()
    {
        return host;
    }

    private void setupConnectionManagers()
    {
        ILogger logger = new ChatLogger();
        sPackets.add(Protocol.PACKET,   new PacketHandler(logger));
        sPackets.add(Protocol.POSITION, new PositionHandler(logger));
        sPackets.add(Protocol.VELOCITY, new VelocityHandler(this));
        sPackets.add(Protocol.EATING,   new EatingHandler());
        sPackets.add(Protocol.LIST,     new ServerListHandler(serverList));
        // SERVER.add();
    }

    @Override
    public void onVelocity(double x, double y, double z)
    {
        EntityPlayer player = RotationUtil.getRotationPlayer();
        if (player == null)
        {
            return;
        }

        this.lastX = x;
        this.lastY = y;
        this.lastZ = z;
        mc.addScheduledTask(() -> player.setVelocity(x, y, z));
    }

    @Override
    public double getLastX()
    {
        return lastX;
    }

    @Override
    public double getLastY()
    {
        return lastY;
    }

    @Override
    public double getLastZ()
    {
        return lastZ;
    }

}
