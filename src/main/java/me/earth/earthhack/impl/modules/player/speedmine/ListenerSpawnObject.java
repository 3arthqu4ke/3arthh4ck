package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.core.ducks.network.ISPacketSpawnObject;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autocrystal.HelperInstantAttack;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

final class ListenerSpawnObject
    extends ModuleListener<Speedmine, PacketEvent.Receive<SPacketSpawnObject>>
{
    private static final ModuleCache<AutoCrystal> AUTOCRYSTAL =
        Caches.getModule(AutoCrystal.class);

    public ListenerSpawnObject(Speedmine module)
    {
        super(module, PacketEvent.Receive.class, SPacketSpawnObject.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSpawnObject> event)
    {
        World world = mc.world;
        EntityPlayerSP player = mc.player;
        boolean antiAntiSilentSwitch = module.antiAntiSilentSwitch.getValue();
        if ((module.breakInstant.getValue() || antiAntiSilentSwitch)
            && world != null
            && player != null
            && event.getPacket().getType() == 51
            && !((ISPacketSpawnObject) event.getPacket()).isAttacked()
            && AUTOCRYSTAL.isPresent()
            && isBomberPos(event.getPacket()))
        {
            BlockPos pos = module.pos;
            EnumFacing facing = module.facing;
            if (antiAntiSilentSwitch)
            {
                if (pos == null || facing == null)
                {
                    return;
                }

                int fastSlot = module.getFastSlot();
                if (fastSlot == -1)
                {
                    return;
                }

                boolean swap = module.swap.getValue();
                Locks.acquire(Locks.WINDOW_CLICK_LOCK, () ->
                {
                    int lastSlot = player.inventory.currentItem;
                    if (swap)
                    {
                        module.cooldownBypass.getValue().switchTo(fastSlot);
                    }

                    module.sendStopDestroy(pos, facing, false, false);
                    attack(world, event);

                    if (swap)
                    {
                        module.cooldownBypass.getValue().switchBack(
                            lastSlot, fastSlot);
                    }
                });

                mc.addScheduledTask(
                    () -> module.postSend(module.toAir.getValue()));
            }
            else
            {
                attack(world, event);
            }
        }
    }

    private void attack(World world,
                        PacketEvent.Receive<SPacketSpawnObject> event)
    {
        AUTOCRYSTAL.get().bombPos = null;
        SPacketSpawnObject packet = event.getPacket();
        double x = packet.getX();
        double y = packet.getY();
        double z = packet.getZ();
        EntityEnderCrystal entity = new EntityEnderCrystal(world, x, y, z);
        HelperInstantAttack.attack(
            AUTOCRYSTAL.get(), event.getPacket(), event, entity, false, false);
    }

    private boolean isBomberPos(SPacketSpawnObject packet)
    {
        double x = packet.getX();
        double y = packet.getY();
        double z = packet.getZ();
        return new BlockPos(x, y, z).equals(AUTOCRYSTAL.get().bombPos);
    }

}
