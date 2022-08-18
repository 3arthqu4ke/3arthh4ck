package me.earth.earthhack.impl.modules.combat.autocrystal.helpers;

import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.SwingTime;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.thread.ThreadUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerTimeHelper extends SubscriberImpl implements Globals
{
    private static final ScheduledExecutorService THREAD;

    private final AutoCrystal module;
    private final Setting<ACRotate> rotate;
    private final Setting<SwingTime> placeSwing;
    private final Setting<Boolean> antiFeetPlace;
    private final Setting<Boolean> newVersion;
    private final Setting<Integer> buffer;

    static
    {
        THREAD = ThreadUtil.newDaemonScheduledExecutor("Server-Helper");
    }

    public ServerTimeHelper(AutoCrystal module,
                            Setting<ACRotate> rotate,
                            Setting<SwingTime> placeSwing,
                            Setting<Boolean> antiFeetPlace,
                            Setting<Boolean> newVersion,
                            Setting<Integer> buffer)
    {
        this.module = module;
        this.rotate = rotate;
        this.placeSwing = placeSwing;
        this.antiFeetPlace = antiFeetPlace;
        this.newVersion = newVersion;
        this.buffer = buffer;
    }

    public void onUseEntity(CPacketUseEntity packet, Entity crystal)
    {
        // You can also check out ICPacketUseEntity to access the entity better
        EntityPlayer closest;
        if (packet.getAction() == CPacketUseEntity.Action.ATTACK
                && antiFeetPlace.getValue()
                && (rotate.getValue() == ACRotate.None || rotate.getValue() == ACRotate.Break)
                && crystal instanceof EntityEnderCrystal
                && (closest = EntityUtil.getClosestEnemy()) != null
                && BlockUtil.isSemiSafe(closest, true, newVersion.getValue())
                && BlockUtil.isAtFeet(mc.world.playerEntities, crystal.getPosition().down(), true, newVersion.getValue()))
        {
            int intoTick = Managers.TICK.getTickTimeAdjusted();
            int sleep = Managers.TICK.getServerTickLengthMS() + Managers.TICK.getSpawnTime() + buffer.getValue() - intoTick;
            place(crystal.getPosition().down(), sleep);
        }
    }

    private void place(BlockPos pos,
                       int sleep)
    {
        SwingTime time = placeSwing.getValue();
        THREAD.schedule(() -> {
            if (InventoryUtil.isHolding(Items.END_CRYSTAL))
            {
                EnumHand hand = InventoryUtil.getHand(Items.END_CRYSTAL);
                RayTraceResult ray = RotationUtil.rayTraceTo(pos, mc.world);
                float[] f = RayTraceUtil.hitVecToPlaceVec(pos, ray.hitVec);
                if (time == SwingTime.Pre)
                {
                    Swing.Packet.swing(hand);
                    Swing.Client.swing(hand);
                }
                mc.player.connection.sendPacket(
                        new CPacketPlayerTryUseItemOnBlock(
                                pos, ray.sideHit, hand, f[0], f[1], f[2]));
                module.sequentialHelper.setExpecting(pos);

                if (time == SwingTime.Post)
                {
                    Swing.Packet.swing(hand);
                    Swing.Client.swing(hand);
                }
            }
        }, sleep, TimeUnit.MILLISECONDS);
    }

}
