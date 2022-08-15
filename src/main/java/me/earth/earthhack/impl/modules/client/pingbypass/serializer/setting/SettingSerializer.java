package me.earth.earthhack.impl.modules.client.pingbypass.serializer.setting;

import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.observable.Observer;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.event.SettingEvent;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.util.interfaces.Displayable;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.api.util.interfaces.Nameable;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.impl.modules.client.pingbypass.serializer.Serializer;
import me.earth.earthhack.pingbypass.protocol.c2s.C2SBindSettingPacket;
import me.earth.earthhack.pingbypass.protocol.c2s.C2SSettingPacket;
import net.minecraft.network.play.client.CPacketChatMessage;

import java.util.*;


public class SettingSerializer extends SubscriberImpl
        implements Globals, Serializer<Setting<?>>
{
    private static final Set<String> UNSERIALIZABLE = new HashSet<>();

    static
    {
        UNSERIALIZABLE.add("Bind");
        UNSERIALIZABLE.add("Hidden");
        UNSERIALIZABLE.add("Name");
        UNSERIALIZABLE.add("IP");
        UNSERIALIZABLE.add("Port");
        UNSERIALIZABLE.add("Pings");
        UNSERIALIZABLE.add("Toggle");
    }

    protected final Set<Module> modules      = new HashSet<>();
    private final Set<Setting<?>> settings = new HashSet<>();
    private final Set<Setting<?>> changed  = new LinkedHashSet<>();
    private final PingBypassModule module;

    public SettingSerializer(PingBypassModule module, Module...modules)
    {
        this.module = module;
        init(new ListenerSetting(this), modules);
        this.listeners.add(new ListenerDisconnect(this));
        this.listeners.add(new ListenerTick(this));
    }

    @SuppressWarnings("rawtypes")
    protected void init(Observer observer, Module...modules)
    {
        this.modules.addAll(Arrays.asList(modules));
        init(observer);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void init(Observer observer) {
        this.modules.forEach(module ->
        {
            if (module != null)
            {
                module.getSettings().forEach(setting ->
                {
                    if (isSettingSerializable(setting))
                    {
                        setting.addObserver(observer);
                        settings.add(setting);
                    }
                });
            }
        });

        clear();
    }

    public void onSettingChange(SettingEvent<?> event)
    {
        Setting<?> setting = event.getSetting();
        Scheduler.getInstance().schedule(() -> changed.add(setting));
    }

    protected void onTick()
    {
        if (mc.player != null
                && mc.getConnection() != null
                && !changed.isEmpty())
        {
            Setting<?> setting;
            int i = 0;
            while ((setting = pollSetting()) != null && i++ < 500) {
                serializeAndSend(setting);
            }
        }
    }

    public void clear()
    {
        synchronized (changed)
        {
            changed.clear();
            changed.addAll(settings);
        }
    }

    private Setting<?> pollSetting()
    {
        synchronized (changed)
        {
            if (!changed.isEmpty())
            {
                Setting<?> setting = changed.iterator().next();
                changed.remove(setting);
                return setting;
            }
        }

        return null;
    }

    @Override
    public void serializeAndSend(Setting<?> setting)
    {
        String name = null;
        if (setting.getContainer() instanceof Displayable)
        {
            name = ((Nameable) setting.getContainer()).getName();
        }

        if (name == null)
        {
            return;
        }

        if (!module.isOld())
        {
            if (setting instanceof BindSetting) {
                Objects.requireNonNull(mc.getConnection()).sendPacket(
                    new C2SBindSettingPacket(
                        name, setting.getName(),
                        ((BindSetting) setting).getValue().getKey(),
                        setting.changeId.incrementAndGet()));
            } else {
                Objects.requireNonNull(mc.getConnection()).sendPacket(
                    new C2SSettingPacket(
                        name, setting, setting.toJson(),
                        setting.changeId.incrementAndGet()));
            }

            return;
        }

        String command = "@Server"
                + name
                + " "
                + setting.getName()
                + " "
                + setting.getValue().toString();

        Earthhack.getLogger().info(command);
        CPacketChatMessage packet = new CPacketChatMessage(command);
        Objects.requireNonNull(mc.getConnection()).sendPacket(packet);
    }

    protected boolean isSettingSerializable(Setting<?> setting)
    {
        return !UNSERIALIZABLE.contains(setting.getName());
    }

}
