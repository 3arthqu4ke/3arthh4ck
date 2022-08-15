package me.earth.earthhack.impl.managers.minecraft;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import me.earth.earthhack.impl.event.events.render.GuiScreenEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.client.ModuleManager;
import me.earth.earthhack.impl.util.minecraft.KeyBoardUtil;
import me.earth.earthhack.pingbypass.PingBypass;

public class KeyBoardManager extends SubscriberImpl
{
    private final ModuleManager moduleManager;

    public KeyBoardManager()
    {
        this(Managers.MODULES);
    }

    public KeyBoardManager(ModuleManager moduleManager)
    {
        this.moduleManager = moduleManager;
        this.listeners.add(new EventListener<KeyboardEvent>(KeyboardEvent.class)
        {
            @Override
            public void invoke(KeyboardEvent event)
            {
                if (event.getEventState())
                {
                    for (Module module : moduleManager.getRegistered())
                    {
                        if (isModuleValid(module) && module.getBind().getKey() == event.getKey())
                        {
                            module.toggle();
                        }
                    }
                }
                else
                {
                    onRelease(event.getKey());
                }
            }
        });
        this.listeners.add(new EventListener<GuiScreenEvent<?>>
                (GuiScreenEvent.class, Integer.MIN_VALUE + 10)
        {
            @Override
            public void invoke(GuiScreenEvent<?> event)
            {
                if (event.isCancelled() || event.getScreen() == null)
                {
                    return;
                }

                for (Module module : moduleManager.getRegistered())
                {
                    if (isModuleValid(module) && KeyBoardUtil.isKeyDown(module.getBind()))
                    {
                        switch (module.getBindMode())
                        {
                            case Hold:
                                module.toggle();
                                break;
                            case Disable:
                                module.disable();
                                break;
                            default:
                        }
                    }
                }
            }
        });
    }

    private void onRelease(int keyCode)
    {
        for (Module module : moduleManager.getRegistered())
        {
            if (isModuleValid(module) && module.getBind().getKey() == keyCode)
            {
                switch (module.getBindMode())
                {
                    case Hold:
                        module.toggle();
                        break;
                    case Disable:
                        module.disable();
                        break;
                    default:
                }
            }
        }
    }

    protected boolean isModuleValid(Module module)
    {
        return !PingBypass.isConnected();
    }

}
