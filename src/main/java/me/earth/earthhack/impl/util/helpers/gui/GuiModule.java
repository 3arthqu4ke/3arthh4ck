package me.earth.earthhack.impl.util.helpers.gui;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.event.events.render.GuiScreenEvent;
import net.minecraft.client.gui.GuiScreen;

public abstract class GuiModule extends Module
{
    protected GuiScreen screen;
    protected boolean fromEvent;

    public GuiModule(String name, Category category)
    {
        super(name, category);
        this.listeners.add(new EventListener<GuiScreenEvent<?>>(
                GuiScreenEvent.class)
        {
            @Override
            public void invoke(GuiScreenEvent<?> event)
            {
                onOtherGuiDisplayed();
            }
        });
    }

    @Override
    protected void onEnable()
    {
        screen = mc.currentScreen;
        display();
    }

    @Override
    protected void onDisable()
    {
        if (!fromEvent)
        {
            mc.displayGuiScreen(screen);
        }

        screen    = null;
        fromEvent = false;
    }

    protected void onOtherGuiDisplayed()
    {
        fromEvent = true;
        disable();
    }

    protected void display()
    {
        mc.displayGuiScreen(provideScreen());
    }

    protected abstract GuiScreen provideScreen();

}
