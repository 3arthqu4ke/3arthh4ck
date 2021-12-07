package me.earth.earthhack.impl.modules.misc.antivanish;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.lookup.LookUp;
import me.earth.earthhack.impl.modules.misc.antivanish.util.VanishedEntry;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.network.play.server.SPacketPlayerListItem;

import java.util.UUID;
import java.util.concurrent.Future;

final class ListenerLatency extends
        ModuleListener<AntiVanish, PacketEvent.Receive<SPacketPlayerListItem>>
{
    public ListenerLatency(AntiVanish module)
    {
        super(module, PacketEvent.Receive.class, SPacketPlayerListItem.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketPlayerListItem> event)
    {
        SPacketPlayerListItem packet = event.getPacket();
        if (packet.getAction() == SPacketPlayerListItem.Action.UPDATE_LATENCY)
        {
            for (SPacketPlayerListItem.AddPlayerData data : packet.getEntries())
            {
                UUID id = data.getProfile().getId();
                //noinspection ConstantConditions
                if (mc.getConnection().getPlayerInfo(id) == null)
                {
                    if (!module.timer.passed(1000))
                    {
                        return;
                    }

                    String name = data.getProfile().getName();
                    if (name == null && module.cache.containsKey(id))
                    {
                        VanishedEntry lookUp = module.cache.get(id);
                        if (lookUp == null)
                        {
                            sendUnknown();
                            return;
                        }
                        else
                        {
                            if (System.currentTimeMillis() - lookUp.getTime()
                                    < 5000)
                            {
                                return;
                            }

                            name = lookUp.getName();
                            if (name == null)
                            {
                                sendUnknown();
                                return;
                            }
                        }
                    }

                    if (name == null)
                    {
                        int lookUpId = module.ids.incrementAndGet();
                        Future<?> future =
                            Managers.LOOK_UP
                                    .doLookUp(new LookUp(LookUp.Type.NAME, id)
                        {
                            @Override
                            public void onSuccess()
                            {
                                module.futures.remove(lookUpId);
                                module.cache.put(id, new VanishedEntry(name));
                                sendMessage(name);
                            }

                            @Override
                            public void onFailure()
                            {
                                module.futures.remove(lookUpId);
                                module.cache.put(id, null);
                                sendUnknown();
                            }
                        });

                        if (future != null)
                        {
                            module.futures.put(lookUpId, future);
                        }
                    }
                    else
                    {
                        sendMessage(name);
                    }
                }
            }
        }
    }

    private void sendUnknown()
    {
        module.timer.reset();
        mc.addScheduledTask(() ->
                ChatUtil.sendMessage("<" + module.getDisplayName()
                        + "> " + TextColor.RED + "Someone just vanished."));
    }

    private void sendMessage(String name)
    {
        module.timer.reset();
        mc.addScheduledTask(() ->
            Managers.CHAT.sendDeleteMessage("<" + module.getDisplayName() + "> "
                    + TextColor.RED + name + " vanished.", name, 7000));
    }

}
