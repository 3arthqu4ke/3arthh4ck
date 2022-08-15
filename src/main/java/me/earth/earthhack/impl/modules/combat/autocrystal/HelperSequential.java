package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.network.ICPacketUseEntity;
import me.earth.earthhack.impl.event.listeners.PostSendListener;
import me.earth.earthhack.impl.event.listeners.ReceiveListener;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class HelperSequential extends SubscriberImpl implements Globals {
    private final StopWatch timer = new StopWatch();
    private final AutoCrystal module;
    private volatile BlockPos expecting;
    private volatile Vec3d crystalPos;

    public HelperSequential(AutoCrystal module) {
        this.module = module;
        listeners.add(new ReceiveListener<>(SPacketBlockChange.class, e -> {
            BlockPos expected = expecting;
            if (expected != null && expected.equals(e.getPacket().getBlockPosition())) {
                if (module.antiPlaceFail.getValue() && crystalPos == null) {
                    module.placeTimer.setTime(0);
                    setExpecting(null);
                    if (module.debugAntiPlaceFail.getValue()) {
                        mc.addScheduledTask(
                            () -> ModuleUtil.sendMessageWithAquaModule(
                            module, "Crystal failed to place!",
                            "antiPlaceFail"));
                    }
                }
            }
        }));
        listeners.add(new ReceiveListener<>(SPacketSpawnObject.class, e -> {
            if (e.getPacket().getType() == 51) {
                BlockPos pos = new BlockPos(e.getPacket().getX(),
                                            e.getPacket().getY(),
                                            e.getPacket().getZ());
                if (pos.down().equals(expecting)) {
                    if (module.endSequenceOnSpawn.getValue()) {
                        setExpecting(null);
                    } else if (crystalPos == null) {
                        crystalPos = new Vec3d(
                            e.getPacket().getX(),
                            e.getPacket().getY(),
                            e.getPacket().getZ());
                    }
                }
            }
        }));
        listeners.add(new PostSendListener<>(CPacketUseEntity.class, e -> {
            Entity entity = ((ICPacketUseEntity) e.getPacket()).getAttackedEntity();
            if (entity instanceof EntityEnderCrystal) {
                if (module.endSequenceOnBreak.getValue()) {
                    setExpecting(null);
                } else {
                    crystalPos = entity.getPositionVector();
                }
            }
        }));
        listeners.add(new ReceiveListener<>(SPacketSoundEffect.class, e -> {
            Vec3d cPos = crystalPos;
            if (module.endSequenceOnExplosion.getValue()
                && e.getPacket().getCategory() == SoundCategory.BLOCKS
                && e.getPacket().getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE
                && cPos != null
                && cPos.squareDistanceTo(e.getPacket().getX(), e.getPacket().getY(), e.getPacket().getZ()) < 144) {
                setExpecting(null);
            }
        }));
        /*
        TODO: compatibility with mining/placing the same pos or and offset
        listeners.add(new PostSendListener<>(CPacketPlayerDigging.class, e -> {
        }));
        */
    }

    public boolean isBlockingPlacement() {
        return module.sequential.getValue()
            && expecting != null
            && !timer.passed(module.seqTime.getValue());
    }

    public void setExpecting(BlockPos expecting) {
        timer.reset();
        this.expecting = expecting;
        this.crystalPos = null;
    }

}
