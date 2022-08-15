package me.earth.earthhack.impl.modules.client.management;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.media.Media;

final class ListenerTick extends ModuleListener<Management, TickEvent>
{
    private static final ModuleCache<Media> MEDIA =
            Caches.getModule(Media.class);

    public ListenerTick(Management module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        if (module.friend.getValue()) {
            if (mc.player != module.player) {
                module.player = mc.player;
                if (mc.player != null
                    && !Managers.FRIENDS.contains(mc.player)) {
                    Managers.FRIENDS.add(
                        mc.player.getGameProfile().getName(),
                        mc.player.getGameProfile().getId());
                    // TODO: reload media based on this too?
                }
            }

            if (module.lastProfile != null
                && !module.lastProfile.equals(mc.getSession().getProfile())) {
                module.lastProfile = mc.getSession().getProfile();
                Managers.FRIENDS.add(module.lastProfile.getName(),
                                     module.lastProfile.getId());
                MEDIA.computeIfPresent(Media::reload);
            }
        }
    }

}
