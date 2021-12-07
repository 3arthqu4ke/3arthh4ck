package me.earth.earthhack.impl.modules.misc.announcer;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.misc.announcer.util.Announcement;
import me.earth.earthhack.impl.modules.misc.announcer.util.AnnouncementType;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;

import java.util.Map;
import java.util.Objects;

final class ListenerMotion extends ModuleListener<Announcer, MotionUpdateEvent>
{
    public ListenerMotion(Announcer module)
    {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (event.getStage() == Stage.PRE)
        {
            if (module.refresh.getValue())
            {
                module.reset();
                module.loadFiles();
                ChatUtil.sendMessage(TextColor.GREEN
                                        + "<"
                                        + TextColor.WHITE
                                        + module.getDisplayName()
                                        + TextColor.GREEN
                                        + "> Files loaded.");

                module.refresh.setValue(false);
            }

            if (module.distance.getValue())
            {
                module.travelled += MovementUtil.getDistance2D();
            }

            if (module.autoEZ.getValue())
            {
                EntityPlayer autoCrystal = Managers.TARGET.getAutoCrystal();
                Entity killAura          = Managers.TARGET.getKillAura();

                if (autoCrystal != null)
                {
                    module.targets.add(Managers.TARGET.getAutoCrystal());
                }

                if (killAura instanceof EntityPlayer)
                {
                    module.targets.add((EntityPlayer) killAura);
                }
            }

            if (module.timer.passed(module.delay.getValue() * 1000))
            {
                String next = module.getNextMessage();
                if (next != null)
                {
                    mc.player.sendChatMessage(next);
                    module.timer.reset();
                }
            }

        }
    }

}
