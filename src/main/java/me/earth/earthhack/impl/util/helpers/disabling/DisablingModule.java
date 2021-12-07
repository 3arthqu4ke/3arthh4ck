package me.earth.earthhack.impl.util.helpers.disabling;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.api.Listener;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.event.events.client.ShutDownEvent;
import me.earth.earthhack.impl.event.events.misc.DeathEvent;
import me.earth.earthhack.impl.event.events.network.DisconnectEvent;

/**
 * A module that turns off when you
 * log off/ close the game/ die.
 */
public abstract class DisablingModule extends Module implements IDisablingModule
{
    public DisablingModule(String name, Category category)
    {
        super(name, category);
        this.listeners.add(newDisconnectDisabler(this));
        this.listeners.add(newDeathDisabler(this));
        this.listeners.add(newShutDownDisabler(this));
    }

    @Override
    public void onShutDown()
    {
        this.disable();
    }

    @Override
    public void onDeath()
    {
        this.disable();
    }

    @Override
    public void onDisconnect()
    {
        this.disable();
    }

    public static void makeDisablingModule(Module module)
    {
        module.getListeners().add(newDisconnectDisabler(module));
        module.getListeners().add(newDeathDisabler(module));
        module.getListeners().add(newShutDownDisabler(module));
    }

    public static Listener<?> newDisconnectDisabler(Module module)
    {
        if (module instanceof IDisablingModule)
        {
            IDisablingModule disabling = (IDisablingModule) module;
            return new EventListener<DisconnectEvent>(DisconnectEvent.class)
            {
                @Override
                public void invoke(DisconnectEvent event)
                {
                    mc.addScheduledTask(disabling::onDisconnect);
                }
            };
        }

        return new EventListener<DisconnectEvent>(DisconnectEvent.class)
        {
            @Override
            public void invoke(DisconnectEvent event)
            {
                mc.addScheduledTask(module::disable);
            }
        };
    }

    public static Listener<?> newDeathDisabler(Module module)
    {
        if (module instanceof IDisablingModule)
        {
            IDisablingModule disabling = (IDisablingModule) module;
            return new EventListener<DeathEvent>(DeathEvent.class)
            {
                @Override
                public void invoke(DeathEvent event)
                {
                    if (event.getEntity() != null
                            && event.getEntity().equals(mc.player))
                    {
                        mc.addScheduledTask(disabling::onDeath);
                    }
                }
            };
        }

        return new EventListener<DeathEvent>(DeathEvent.class)
        {
            @Override
            public void invoke(DeathEvent event)
            {
                if (event.getEntity() != null
                        && event.getEntity().equals(mc.player))
                {
                    mc.addScheduledTask(module::disable);
                }
            }
        };
    }

    public static Listener<?> newShutDownDisabler(Module module)
    {
        if (module instanceof IDisablingModule)
        {
            IDisablingModule disabling = (IDisablingModule) module;
            return new EventListener<ShutDownEvent>(ShutDownEvent.class)
            {
                @Override
                public void invoke(ShutDownEvent event)
                {
                    mc.addScheduledTask(disabling::onDisconnect);
                }
            };
        }

        return new EventListener<ShutDownEvent>(ShutDownEvent.class)
        {
            @Override
            public void invoke(ShutDownEvent event)
            {
                mc.addScheduledTask(module::disable);
            }
        };
    }

}
