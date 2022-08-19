package me.earth.earthhack.impl.modules.client.tab;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.util.helpers.gui.GuiModule;
import me.earth.earthhack.pingbypass.input.Mouse;
import net.minecraft.client.gui.GuiScreen;

public class TabModule extends GuiModule
{
    protected final Setting<Boolean> silent =
            register(new BooleanSetting("Silent", true));
    protected final Setting<Boolean> pause =
            register(new BooleanSetting("Pause", true));

    protected boolean isSilent;

    public TabModule()
    {
        super("Tab", Category.Client);
        this.listeners.add(new EventListener<TickEvent>(TickEvent.class)
        {
            @Override
            public void invoke(TickEvent event)
            {
                if (mc.currentScreen == null && isSilent)
                {
                    Mouse.setGrabbed(false);
                }
            }
        });
    }

    @Override
    protected void onEnable()
    {
        isSilent = silent.getValue();
        if (!isSilent)
        {
            super.onEnable();
        }
    }

    @Override
    protected void onDisable()
    {
        if (!isSilent)
        {
            super.onDisable();
        }
    }

    @Override
    protected void onOtherGuiDisplayed()
    {
        if (!isSilent)
        {
            super.onOtherGuiDisplayed();
        }
    }

    @Override
    protected GuiScreen provideScreen()
    {
        return new GuiScreenTab(this);
    }

}
