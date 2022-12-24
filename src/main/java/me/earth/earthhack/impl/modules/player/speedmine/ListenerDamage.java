package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.core.ducks.network.IPlayerControllerMP;
import me.earth.earthhack.impl.event.events.misc.DamageBlockEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.antisurround.AntiSurround;
import me.earth.earthhack.impl.modules.misc.nuker.Nuker;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.blocks.mine.MineUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumHand;

final class ListenerDamage extends ModuleListener<Speedmine, DamageBlockEvent>
{
    private static final ModuleCache<Nuker> NUKER =
        Caches.getModule(Nuker.class);
    private static final SettingCache<Boolean, BooleanSetting, Nuker> NUKE =
        Caches.getSetting(Nuker.class, BooleanSetting.class, "Nuke", false);
    private static final ModuleCache<AntiSurround> ANTISURROUND =
        Caches.getModule(AntiSurround.class);

    public ListenerDamage(Speedmine module)
    {
        super(module, DamageBlockEvent.class);
    }

    @Override
    public void invoke(DamageBlockEvent event)
    {
        if (ANTISURROUND.returnIfPresent(AntiSurround::isActive, false))
        {
            return;
        }

        module.checkReset();
        if (MineUtil.canBreak(event.getPos())
                && !PlayerUtil.isCreative(mc.player)
                && (!NUKER.isEnabled() || !NUKE.getValue()))
        {
            switch (module.mode.getValue())
            {
                case Reset:
                    setPos(event);
                    break;
                case Packet:
                case Civ:
                    setPos(event);
                    mc.player.swingArm(EnumHand.MAIN_HAND);

                    CPacketPlayerDigging start =
                            new CPacketPlayerDigging(CPacketPlayerDigging
                                                    .Action
                                                        .START_DESTROY_BLOCK,
                                                    event.getPos(),
                                                    event.getFacing());
                    CPacketPlayerDigging stop  =
                            new CPacketPlayerDigging(CPacketPlayerDigging
                                                    .Action
                                                        .STOP_DESTROY_BLOCK,
                                                    event.getPos(),
                                                    event.getFacing());

                    if (module.event.getValue())
                    {
                        mc.player.connection.sendPacket(start);
                        mc.player.connection.sendPacket(stop);
                    }
                    else
                    {
                        NetworkUtil.sendPacketNoEvent(start, false);
                        NetworkUtil.sendPacketNoEvent(stop, false);
                    }

                    if (module.swingStop.getValue())
                    {
                        Swing.Packet.swing(EnumHand.MAIN_HAND);
                    }

                    event.setCancelled(true);
                    break;
                case Damage:
                    setPos(event);
                    if (((IPlayerControllerMP) mc.playerController)
                            .getCurBlockDamageMP() >= module.limit.getValue())
                    {
                        ((IPlayerControllerMP) mc.playerController)
                                .setCurBlockDamageMP(1.0f);
                    }
                    break;
                case Fast:
                    module.fastHelper.reset();
                    module.fastHelper.sendAbortNextTick = true;
                case Smart:
                    runSmart(event);
                    break;
                case Instant:
                    boolean abortedd = false;
                    if (module.pos != null
                            && !module.pos.equals(event.getPos()))
                    {
                        module.abortCurrentPos();
                        abortedd = true;
                    }

                    if (abortedd || module.timer.passed(module.delay.getValue()))
                    {
                        if (!abortedd && module.pos != null
                                && module.pos.equals(event.getPos()))
                        {
                            module.abortCurrentPos();
                            module.timer.reset();
                            return;
                        }

                        setPos(event);
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                        CPacketPlayerDigging packet =
                                new CPacketPlayerDigging(CPacketPlayerDigging
                                        .Action
                                        .START_DESTROY_BLOCK,
                                        event.getPos(),
                                        event.getFacing());

                        if (module.event.getValue())
                        {
                            mc.player.connection.sendPacket(packet);
                        }
                        else
                        {
                            NetworkUtil.sendPacketNoEvent(packet, false);
                        }

                        module.shouldAbort = true;

                        event.setCancelled(true);
                        module.timer.reset();
                    }

                    break;
                default:
            }
        }
    }

    private void runSmart(DamageBlockEvent event) {
        boolean aborted = false;
        if (module.pos != null
            && !module.pos.equals(event.getPos()))
        {
            module.abortCurrentPos();
            aborted = true;
        }

        if (aborted || module.timer.passed(module.delay.getValue()))
        {
            if (!aborted && module.pos != null
                && module.pos.equals(event.getPos()))
            {
                module.abortCurrentPos();
                module.timer.reset();
                return;
            }

            setPos(event);
            mc.player.swingArm(EnumHand.MAIN_HAND);
            CPacketPlayerDigging packet =
                new CPacketPlayerDigging(CPacketPlayerDigging
                                             .Action
                                             .START_DESTROY_BLOCK,
                                         event.getPos(),
                                         event.getFacing());

            if (module.event.getValue())
            {
                mc.player.connection.sendPacket(packet);
            }
            else
            {
                NetworkUtil.sendPacketNoEvent(packet, false);
            }

            event.setCancelled(true);
            module.timer.reset();
        }

        if (module.cancelEvent.getValue())
        {
            event.setCancelled(true);
        }
    }

    private void setPos(DamageBlockEvent event)
    {
        module.reset();
        module.pos        = event.getPos();
        module.facing     = event.getFacing();
        module.bb         = mc.world
                                .getBlockState(module.pos)
                                .getSelectedBoundingBox(mc.world, module.pos)
                                .grow(0.0020000000949949026);
    }

}
