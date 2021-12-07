package me.earth.earthhack.impl.modules.player.fakeplayer;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;

final class ListenerExplosion extends
        ModuleListener<FakePlayer, PacketEvent.Receive<SPacketExplosion>>
{
    public ListenerExplosion(FakePlayer module)
    {
        super(module, PacketEvent.Receive.class, SPacketExplosion.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketExplosion> event)
    {
        if (module.damage.getValue())
        {
            mc.addScheduledTask(() -> handleExplosion(event.getPacket()));
        }
    }

    private void handleExplosion(SPacketExplosion packet)
    {
        if (mc.world == null
            || module.fakePlayer == null
            || !module.isEnabled())
        {
            return;
        }

        double x = packet.getX();
        double y = packet.getY();
        double z = packet.getZ();

        double distance = module.fakePlayer.getDistance(x, y, z) / 12.0;
        if (distance > 1.0)
        {
            return;
        }

        float size = packet.getStrength();
        double density = mc.world.getBlockDensity(new Vec3d(x, y, z),
                module.fakePlayer.getEntityBoundingBox());

        double densityDistance = distance = (1.0 - distance) * density;
        float damage = (float) ((densityDistance * densityDistance + distance)
                                    / 2.0 * 7.0 * size * 2.0f + 1.0);
        DamageSource damageSource = DamageSource.causeExplosionDamage(
                new Explosion(mc.world, mc.player, x, y, z, size, false, true));

        float limbSwing = module.fakePlayer.limbSwingAmount;
        module.fakePlayer.attackEntityFrom(damageSource, damage);
        module.fakePlayer.limbSwingAmount = limbSwing;
    }

}
