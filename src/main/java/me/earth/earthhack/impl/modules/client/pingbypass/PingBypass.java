package me.earth.earthhack.impl.modules.client.pingbypass;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.register.exception.AlreadyRegisteredException;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.media.Media;
import me.earth.earthhack.impl.modules.client.pingbypass.packets.PayloadIDs;
import me.earth.earthhack.impl.modules.client.pingbypass.packets.PayloadManager;
import me.earth.earthhack.impl.modules.client.pingbypass.serializer.friend.FriendSerializer;
import me.earth.earthhack.impl.modules.client.pingbypass.serializer.setting.SettingSerializer;
import me.earth.earthhack.impl.modules.client.pingbypass.submodules.sSafety.ServerSafety;
import me.earth.earthhack.impl.modules.client.pingbypass.submodules.sautocrystal.ServerAutoCrystal;
import me.earth.earthhack.impl.modules.client.pingbypass.submodules.sautototem.ServerAutoTotem;
import me.earth.earthhack.impl.modules.client.pingbypass.submodules.sinventory.ServerInventory;
import me.earth.earthhack.impl.modules.misc.pingspoof.PingSpoof;
import me.earth.earthhack.impl.modules.player.fakeplayer.FakePlayer;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.network.ServerUtil;

public class PingBypass extends Module
{
    private static final ModuleCache<Media> MEDIA =
            Caches.getModule(Media.class);

    private final PayloadManager payloadManager = new PayloadManager();

    protected final Setting<String> port     =
        register(new StringSetting("Port", "0"));
    protected final Setting<Integer> pings   =
        register(new NumberSetting<>("Pings", 5, 1, 30));

    protected SettingSerializer serializer;
    protected FriendSerializer friendSerializer;

    protected StopWatch timer = new StopWatch();
    protected String serverName;
    protected long startTime;
    protected int serverPing;
    protected long ping;
    protected boolean handled;

    public PingBypass()
    {
        super("PingBypass", Category.Client);
        register(new BooleanSetting("NoRender", false));
        register(new StringSetting("IP", "Proxy-IP"));

        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerKeepAlive(this));
        this.listeners.add(new ListenerLogin(this));
        this.listeners.add(new ListenerCustomPayload(payloadManager));

        ServerAutoTotem sAutoTotem = new ServerAutoTotem(this);
        ServerAutoCrystal sCrystal = new ServerAutoCrystal(this);
        ServerInventory sInventory = new ServerInventory(this);
        ServerSafety sSafety = new ServerSafety(this);

        try
        {
            Managers.MODULES.register(sAutoTotem);
            Managers.MODULES.register(sCrystal);
            Managers.MODULES.register(sInventory);
            Managers.MODULES.register(sSafety);
        }
        catch (AlreadyRegisteredException e)
        {
            throw new IllegalStateException(
                    "Couldn't register PingBypass Submodules : "
                            + e.getTrying().getName(), e);
        }

        serializer = new SettingSerializer(this,
                                            sAutoTotem,
                                            sCrystal,
                                            Managers.MODULES.getByClass(FakePlayer.class),
                                            sSafety,
                                            Managers.MODULES.getByClass(PingSpoof.class),
                                            sInventory);

        this.listeners.addAll(serializer.getListeners());
        this.friendSerializer = new FriendSerializer();
        this.listeners.addAll(friendSerializer.getListeners());
        registerPayloadReaders();
        this.setData(new PingBypassData(this));
    }

    @Override
    protected void onEnable()
    {
        Managers.FRIENDS.addObserver(friendSerializer.getObserver());
        ServerUtil.disconnectFromMC("PingBypass enabled.");
        serializer.clear();
        friendSerializer.clear();
    }

    @Override
    protected void onDisable()
    {
        Managers.FRIENDS.removeObserver(friendSerializer.getObserver());
        ServerUtil.disconnectFromMC("PingBypass disabled.");
        serializer.clear();
        friendSerializer.clear();
    }

    @Override
    public String getDisplayInfo()
    {
        return ping + "ms";
    }

    private void registerPayloadReaders()
    {
        payloadManager.register(PayloadIDs.NAME, buffer ->
        {
            String name = buffer.readString(Short.MAX_VALUE);
            mc.addScheduledTask(() ->
            {
                setServerName(name);
                MEDIA.computeIfPresent(media ->
                        media.setPingBypassName(getServerName()));
            });
        });
    }

    public String getServerName()
    {
        return serverName;
    }

    public void setServerName(String name)
    {
        this.serverName = name;
    }

    public int getServerPing()
    {
        return serverPing;
    }

    public int getPort()
    {
        try
        {
            return Integer.parseInt(port.getValue());
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
        }

        return 0;
    }

    @SuppressWarnings("unused")
    public PayloadManager getPayloadManager()
    {
        return payloadManager;
    }

}
