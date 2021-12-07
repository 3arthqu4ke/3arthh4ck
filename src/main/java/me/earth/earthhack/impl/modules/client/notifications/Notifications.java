package me.earth.earthhack.impl.modules.client.notifications;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.event.events.client.PostInitEvent;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.text.ChatIDs;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public class Notifications extends Module
{
    protected final Setting<Boolean> totems      =
            register(new BooleanSetting("TotemPops", true));
    protected final Setting<TextColor> totemColor =
            register(new EnumSetting<>("Totem-Color", TextColor.None));
    protected final Setting<TextColor> totemAmountColor =
            register(new EnumSetting<>("Amount-Color", TextColor.None));
    protected final Setting<TextColor> totemPlayerColor =
            register(new EnumSetting<>("Player-Color", TextColor.None));
    protected final Setting<Boolean> modules     =
            register(new BooleanSetting("Modules", true));
    protected final Setting<Boolean> configure   =
            register(new BooleanSetting("Show-Modules", true));
    protected final Setting<Category> categories =
            register(new EnumSetting<>("Categories", Category.Combat));

    protected final Map<Module, Setting<Boolean>> announceMap =
            new HashMap<>();

    public Notifications()
    {
        super("Notifications", Category.Client);
        this.listeners.add(new ListenerTotems(this));
        this.listeners.add(new ListenerDeath(this));
        this.setData(new NotificationData(this));

        Bus.EVENT_BUS.register(
            new EventListener<PostInitEvent>(PostInitEvent.class)
            {
                @Override
                public void invoke(PostInitEvent event)
                {
                    createSettings();
                }
            });
    }

    private void createSettings()
    {
        announceMap.clear();
        Visibilities.VISIBILITY_MANAGER
                .registerVisibility(categories, configure::getValue);

        for (Module module : Managers.MODULES.getRegistered())
        {
            Setting<Boolean> enabled = module.getSetting("Enabled",
                                                    BooleanSetting.class);
            if (enabled == null)
            {
                continue;
            }

            enabled.addObserver(event ->
            {
                if (isEnabled()
                        && !event.isCancelled()
                        && modules.getValue()
                        && announceMap.get(module).getValue())
                {
                    onToggleModule((Module) event.getSetting().getContainer(),
                                            event.getValue());
                }
            });

            String name = module.getName();
            if (this.getSetting(name) != null)
            {
                name = "Show" + name;
            }

            Setting<Boolean> setting =
                    register(new BooleanSetting(name, false));

            announceMap.put(module, setting);

            Visibilities.VISIBILITY_MANAGER.registerVisibility(setting,
                    () -> configure.getValue()
                        && categories.getValue() == module.getCategory());

            this.getData()
                .settingDescriptions()
                .put(setting, "Announce Toggling of " + name + "?");
        }
    }

    protected void onToggleModule(Module module, boolean enabled)
    {
        Setting<Boolean> setting = announceMap.get(module);
        if (setting != null && setting.getValue())
        {
            String message = TextColor.BOLD
                                + module.getDisplayName()
                                + (enabled ? TextColor.GREEN : TextColor.RED)
                                + (enabled ? " enabled." : " disabled.");

            mc.addScheduledTask(() ->
                Managers.CHAT.sendDeleteMessage(message,
                                                module.getName(),
                                                ChatIDs.MODULE));
        }
    }

    public void onPop(Entity player, int totemPops)
    {
        if (this.isEnabled() && totems.getValue())
        {
            String message = totemPlayerColor.getValue().getColor()
                                + player.getName()
                                + totemColor.getValue().getColor()
                                + " popped "
                                + totemAmountColor.getValue().getColor()
                                + totemPops
                                + totemColor.getValue().getColor()
                                + " totem"
                                + (totemPops == 1 ? "." : "s.");

            Managers.CHAT.sendDeleteMessage(message,
                                            player.getName(),
                                            ChatIDs.TOTEM_POPS);
        }
    }

    public void onDeath(Entity player, int totemPops)
    {
        if (this.isEnabled() && totems.getValue())
        {
            String message = totemPlayerColor.getValue().getColor()
                                + player.getName()
                                + totemColor.getValue().getColor()
                                + " died after popping "
                                + totemAmountColor.getValue().getColor()
                                + totemPops
                                + totemColor.getValue().getColor()
                                + " totem"
                                + (totemPops == 1 ? "." : "s.");

            Managers.CHAT.sendDeleteMessage(message,
                                            player.getName(),
                                            ChatIDs.TOTEM_POPS);
        }
    }

}
