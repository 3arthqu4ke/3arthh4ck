package me.earth.earthhack.impl.modules.player.fakeplayer;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.core.ducks.network.ICPacketUseEntity;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.criticals.Criticals;
import net.minecraft.entity.Entity;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketUseEntity;

final class ListenerAttack extends
        ModuleListener<FakePlayer, PacketEvent.Send<CPacketUseEntity>>
{
    private static final ModuleCache<Criticals> CRITICALS =
            Caches.getModule(Criticals.class);

    public ListenerAttack(FakePlayer module)
    {
        super(module, PacketEvent.Send.class, CPacketUseEntity.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketUseEntity> event)
    {
        if (event.isCancelled())
        {
            return;
        }

        Entity entity = ((ICPacketUseEntity) event.getPacket())
                                                  .getAttackedEntity();
        if (module.fakePlayer.equals(entity))
        {
            event.setCancelled(true);
            if (CRITICALS.isEnabled()
                || !mc.player.isSprinting()
                    && mc.player.fallDistance > 0.0F
                    && !mc.player.onGround
                    && !mc.player.isOnLadder()
                    && !mc.player.isInWater()
                    && !mc.player.isPotionActive(MobEffects.BLINDNESS)
                    && !mc.player.isRiding())
            {
                mc.world.playSound(
                        mc.player,
                        mc.player.posX,
                        mc.player.posY,
                        mc.player.posZ,
                        SoundEvents.ENTITY_PLAYER_ATTACK_CRIT,
                        mc.player.getSoundCategory(),
                        1.0F, 1.0F);
            }
            else if (mc.player.getCooledAttackStrength(0.5f) > 0.9)
            {
                mc.world.playSound(
                        mc.player,
                        mc.player.posX,
                        mc.player.posY,
                        mc.player.posZ,
                        SoundEvents.ENTITY_PLAYER_ATTACK_STRONG,
                        mc.player.getSoundCategory(),
                        1.0F, 1.0F);
            }
            else
            {
                mc.world.playSound(
                        mc.player,
                        mc.player.posX,
                        mc.player.posY,
                        mc.player.posZ,
                        SoundEvents.ENTITY_PLAYER_ATTACK_WEAK,
                        mc.player.getSoundCategory(),
                        1.0F, 1.0F);
            }
        }
    }

}
