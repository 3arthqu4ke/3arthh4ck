package me.earth.earthhack.impl.modules.client.pingbypass.serializer.setting;

import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.observable.Observer;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.event.SettingEvent;
import me.earth.earthhack.api.util.interfaces.Displayable;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.api.util.interfaces.Nameable;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.modules.client.pingbypass.serializer.Serializer;
import net.minecraft.network.play.client.CPacketChatMessage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;


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

    private final Set<Module> modules      = new HashSet<>();
    private final Set<Setting<?>> settings = new HashSet<>();
    private final Set<Setting<?>> changed  = new LinkedHashSet<>();

    public SettingSerializer(Module...modules)
    {
        init(new ListenerSetting(this), modules);
        this.listeners.add(new ListenerDisconnect(this));
        this.listeners.add(new ListenerTick(this));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void init(Observer observer, Module...modules)
    {
        this.modules.addAll(Arrays.asList(modules));
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
            Setting<?> setting = pollSetting();
            if (setting != null)
            {
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
        if (!changed.isEmpty())
        {
            Setting<?> setting = changed.iterator().next();
            changed.remove(setting);
            return setting;
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

    private boolean isSettingSerializable(Setting<?> setting)
    {
        return !UNSERIALIZABLE.contains(setting.getName());
    }

}
