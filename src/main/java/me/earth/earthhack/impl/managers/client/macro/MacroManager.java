package me.earth.earthhack.impl.managers.client.macro;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.api.Listener;
import me.earth.earthhack.api.event.bus.api.Subscriber;
import me.earth.earthhack.api.register.IterationRegister;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.commands.MacroCommand;
import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MacroManager extends IterationRegister<Macro>
        implements Subscriber, Globals
{
    private final List<Listener<?>> listeners = new ArrayList<>();
    private boolean safe;

    public MacroManager()
    {
        listeners.add(
            new EventListener<KeyboardEvent>
                (KeyboardEvent.class, 100)
        {
            @Override
            public void invoke(KeyboardEvent event)
            {
                for (Macro macro : getRegistered())
                {
                    if (macro.getType() != MacroType.DELEGATE
                            && macro.getBind().getKey() == event.getKey()
                            && macro.isRelease() != event.getEventState())
                    {
                        try
                        {
                            safe = true;
                            macro.execute(Managers.COMMANDS);
                        }
                        catch (Throwable t) // catching stack overflows...
                        {
                            ChatUtil.sendMessage(TextColor.RED
                                + "An error occurred while executing macro "
                                + TextColor.WHITE
                                + macro.getName()
                                + TextColor.RED
                                + ": "
                                + (t.getMessage() == null
                                    ? t.getClass().getName()
                                    : t.getMessage())
                                + ". I strongly recommend deleting it"
                                + " for now and checking your logic!");
                            t.printStackTrace();
                        }
                        finally
                        {
                            safe = false;
                        }
                    }
                }
            }
        });
    }

    /**
     * Removes DelegateMacros which aren't needed anymore.
     */
    public void validateAll()
    {
        getRegistered().removeIf(macro ->
        {
            if (macro instanceof DelegateMacro
                    && !((DelegateMacro) macro).isReferenced(this))
            {
                Earthhack.getLogger().info("Deleting DelegateMacro "
                        + macro.getName()
                        + " it's not being referenced anymore.");
                return true;
            }

            return false;
        });
    }

    @Override
    public Collection<Listener<?>> getListeners()
    {
        return listeners;
    }

    /** Only use if you are the {@link MacroCommand}! */
    public boolean isSafe()
    {
        return mc.isCallingFromMinecraftThread() && safe;
    }

}
