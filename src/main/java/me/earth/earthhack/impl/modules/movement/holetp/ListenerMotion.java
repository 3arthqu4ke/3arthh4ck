package me.earth.earthhack.impl.modules.movement.holetp;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.blocklag.BlockLag;
import me.earth.earthhack.impl.modules.movement.longjump.LongJump;
import me.earth.earthhack.impl.modules.movement.packetfly.PacketFly;
import me.earth.earthhack.impl.modules.movement.speed.Speed;
import me.earth.earthhack.impl.modules.movement.speed.SpeedMode;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;

final class ListenerMotion extends ModuleListener<HoleTP, MotionUpdateEvent> {
    private static final ModuleCache<PacketFly> PACKET_FLY =
            Caches.getModule(PacketFly.class);
    private static final ModuleCache<BlockLag> BLOCK_LAG =
            Caches.getModule(BlockLag.class);
    private static final ModuleCache<Speed> SPEED =
            Caches.getModule(Speed.class);
    private static final ModuleCache<LongJump> LONGJUMP =
            Caches.getModule(LongJump.class);
    private static final SettingCache<SpeedMode, EnumSetting<SpeedMode>, Speed>
            SPEED_MODE = Caches.getSetting(
                    Speed.class, Setting.class, "Mode", SpeedMode.Instant);

    public ListenerMotion(HoleTP module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (event.getStage() == Stage.POST) {
            if (PositionUtil.inLiquid(true)
                    || PositionUtil.inLiquid(false)
                    || PACKET_FLY.isEnabled()
                    || BLOCK_LAG.isEnabled()
                    || LONGJUMP.isEnabled()
                    || SPEED.isEnabled()
                        && SPEED_MODE.getValue() != SpeedMode.Instant) {
                return;
            }

            if (!mc.player.onGround) {
                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    module.jumped = true;
                }
            } else {
                module.jumped = false;
            }

            if (!module.jumped
                    && mc.player.fallDistance < 0.5
                    && module.isInHole()
                    && mc.player.posY - module.getNearestBlockBelow() > 0.8
                    && mc.player.posY - module.getNearestBlockBelow() <= 1.125) {
                if (!mc.player.onGround) {
                    module.packets++;
                }

                if (!mc.player.onGround
                        && !(mc.player.isOnLadder()
                        || mc.player.isEntityInsideOpaqueBlock())
                        && !mc.gameSettings.keyBindJump.isKeyDown()
                        && module.packets > 0) {
                    BlockPos pos = new BlockPos(mc.player.posX,
                            mc.player.posY,
                            mc.player.posZ);

                    for (double position : HoleTP.OFFSETS) {
                        mc.player.connection.sendPacket(
                                new CPacketPlayer.Position(
                                        pos.getX() + 0.5f,
                                        mc.player.posY - position,
                                        pos.getZ() + 0.5f,
                                        true));
                    }

                    mc.player.setPosition(pos.getX() + 0.5f, module.getNearestBlockBelow() + 0.1, pos.getZ() + 0.5f);
                    module.packets = 0;
                }
            }
        }
    }

}
