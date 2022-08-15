package me.earth.earthhack.impl.managers.chat;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.gui.IGuiNewChat;
import me.earth.earthhack.impl.event.events.network.DisconnectEvent;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.util.misc.SkippingCounter;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.pingbypass.PingBypass;
import net.minecraft.util.text.ITextComponent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatManager extends SubscriberImpl implements Globals
{
    private final Map<Integer, Map<String, Integer>> message_ids;
    private final SkippingCounter counter = PingBypass.isServer()
        ? new SkippingCounter(Integer.MIN_VALUE, i -> i != -1)
        : new SkippingCounter(1337, i -> i != -1);

    public ChatManager()
    {
        message_ids = new ConcurrentHashMap<>();
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

    public void clear()
    {
        if (mc.ingameGUI != null)
        {
            message_ids.values().forEach(m ->
                m.values().forEach(id->
                    mc.ingameGUI.getChatGUI().deleteChatLine(id)));
        }

        message_ids.clear();
        counter.reset();
    }

    public void sendDeleteMessageScheduled(String message,
                                           String uniqueWord,
                                           int senderID)
    {
        Integer id = message_ids
                .computeIfAbsent(senderID, v -> new ConcurrentHashMap<>())
                .computeIfAbsent(uniqueWord, v -> counter.next());

        mc.addScheduledTask(() -> ChatUtil.sendMessage(message, id));
    }

    public void sendDeleteMessage(String message,
                                  String uniqueWord,
                                  int senderID)
    {
        Integer id = message_ids
                .computeIfAbsent(senderID, v -> new ConcurrentHashMap<>())
                .computeIfAbsent(uniqueWord, v -> counter.next());

        ChatUtil.sendMessage(message, id);
    }

    public void deleteMessage(String uniqueWord, int senderID)
    {
        Map<String, Integer> map = message_ids.get(senderID);
        if (map != null)
        {
            Integer id = map.remove(uniqueWord);
            if (id != null)
            {
                ChatUtil.deleteMessage(id);
            }
        }
    }

    public void sendDeleteComponent(ITextComponent component,
                                    String uniqueWord,
                                    int senderID)
    {
        Integer id = message_ids
                .computeIfAbsent(senderID, v -> new ConcurrentHashMap<>())
                .computeIfAbsent(uniqueWord, v -> counter.next());

        ChatUtil.sendComponent(component, id);
    }

    /**
     * @param uniqueWord a qualifier for the message.
     * @param senderID who initially sent the message.
     * @return the id for the given arguments or -1 if no such line has
     *          been sent using this.
     */
    public int getId(String uniqueWord, int senderID)
    {
        Map<String, Integer> map = message_ids.get(senderID);
        if (map != null)
        {
            Integer id = map.get(uniqueWord);
            if (id != null)
            {
                return id;
            }
        }

        return -1;
    }

    // TODO: THIS IS GARBAGÉ
    public void replace(ITextComponent component,
                        String uniqueWord,
                        int senderID,
                        boolean wrap,
                        boolean multiple,
                        boolean sendIfAbsent)
    {
        Map<String, Integer> map = message_ids.get(senderID);
        if (map != null)
        {
            Integer id = map.get(uniqueWord);
            if (id != null && mc.ingameGUI != null)
            {
                IGuiNewChat gui = (IGuiNewChat) mc.ingameGUI.getChatGUI();
                if (gui.replace(component, senderID, wrap, !multiple))
                {
                    return;
                }
            }
        }

        if (sendIfAbsent)
        {
            sendDeleteComponent(component, uniqueWord, senderID);
        }
    }

}
