package me.earth.earthhack.impl.modules.movement.stairs;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.block.BlockStairs;
import net.minecraft.util.math.BlockPos;

public class Stairs extends Module {
    private final Setting<Integer> delay =
        register(new NumberSetting<>("Delay", 100, 0, 1000));
    private final Setting<Boolean> whileSneaking =
        register(new BooleanSetting("WhileSneaking", false));

    private final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
    private final StopWatch timer = new StopWatch();
    private double currentY;
    private double lastY;

    public Stairs() {
        super("Stairs", Category.Movement);
        SimpleData data = new SimpleData(this, "Makes you faster on stairs.");
        data.register(delay, "Delay in milliseconds between jumps.");
        this.setData(data);
        this.listeners.add(new LambdaListener<>(UpdateEvent.class, e -> {
            if (mc.player.posY != currentY || timer.passed(100)) {
                if (currentY != lastY) {
                    lastY = currentY;
                }

                currentY = mc.player.posY;
            }

            if (timer.passed(delay.getValue())
                && mc.player.onGround
                && mc.player.moveForward > 0
                && lastY < currentY
                && !mc.player.isSpectator()
                && !mc.player.isRiding()
                && !mc.player.isOnLadder()
                && (!mc.player.isSneaking() || whileSneaking.getValue())
                && checkForStairs()) {
                mc.player.jump();
                timer.reset();
            }
        }));
    }

    private boolean checkForStairs() {
        pos.setPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        if (mc.world.getBlockState(pos).getBlock() instanceof BlockStairs) {
            return true;
        }

        pos.setPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ);
        return mc.world.getBlockState(pos).getBlock() instanceof BlockStairs;
    }

}
