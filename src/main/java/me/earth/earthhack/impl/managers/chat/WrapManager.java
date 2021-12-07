package me.earth.earthhack.impl.managers.chat;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.gui.IGuiNewChat;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.events.network.DisconnectEvent;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.gui.chat.AbstractTextComponent;
import me.earth.earthhack.impl.util.text.ChatIDs;
import net.minecraft.client.gui.ChatLine;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages wrapping for
 * {@link AbstractTextComponent}s.
 */
//TODO: This is so bad
public class WrapManager extends SubscriberImpl implements Globals
{
    private final Map<ChatLineReferenceMap, AbstractTextComponent> components =
            new ConcurrentHashMap<>();

    public WrapManager()
    {
        this.listeners.add(
            new EventListener<TickEvent>(TickEvent.class)
            {
                @Override
                public void invoke(TickEvent event)
                {
                    onTick();
                }
            });
        this.listeners.add(
            new EventListener<DisconnectEvent>(DisconnectEvent.class)
            {
                @Override
                public void invoke(DisconnectEvent event)
                {
                    mc.addScheduledTask(() -> clear());
                }
            });
        this.listeners.add(
            new EventListener<WorldClientEvent.Load>
                    (WorldClientEvent.Load.class)
            {
                @Override
                public void invoke(WorldClientEvent.Load event)
                {
                    mc.addScheduledTask(() -> clear());
                }
            });
    }

    private void clear()
    {
        if (mc.ingameGUI != null)
        {
            for (Map.Entry<ChatLineReferenceMap, AbstractTextComponent>
                    entry : components.entrySet())
            {
                mc.ingameGUI
                  .getChatGUI()
                  .deleteChatLine(entry.getKey().getId());
            }
        }

        components.clear();
    }

    private void onTick()
    {
        for (Map.Entry<ChatLineReferenceMap, AbstractTextComponent> entry :
                components.entrySet())
        {
            if (entry.getKey().isEmpty()
                    || !entry.getValue().isWrapping()
                    || mc.ingameGUI == null)
            {
                components.remove(entry.getKey());
            }
            else
            {
                ((IGuiNewChat) mc.ingameGUI.getChatGUI())
                    .replace(entry.getValue(),
                             entry.getKey().getId(),
                             true,
                             false);
            }
        }
    }

    public void registerComponent(AbstractTextComponent component,
                                  ChatLine...references)
    {
        components.put(new ChatLineReferenceMap(references), component);
    }

    private static class ChatLineReferenceMap
            extends WeakHashMap<ChatLine, Boolean>
    {
        private int id = ChatIDs.NONE;

        public ChatLineReferenceMap(ChatLine...references)
        {
            if (references != null)
            {
                for (ChatLine line : references)
                {
                    if (line != null)
                    {
                        super.put(line, true);
                        id = line.getChatLineID();
                    }
                }
            }
        }

        public int getId()
        {
            return id;
        }

        @Override
        public int hashCode()
        {
            return id;
        }

        @Override
        public boolean equals(Object o)
        {
            if (o instanceof ChatLineReferenceMap)
            {
                return ((ChatLineReferenceMap) o).id == this.id;
            }

            return false;
        }
    }

}
