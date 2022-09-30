package me.earth.earthhack.impl.modules.movement.avoid;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * {@link me.earth.earthhack.impl.core.mixins.block.MixinAvoid}
 */
public class Avoid extends Module {
    private final Setting<Boolean> cactus =
        register(new BooleanSetting("Cactus", false));
    private final Setting<Boolean> fire =
        register(new BooleanSetting("Fire", false));
    private final Setting<Boolean> lava =
        register(new BooleanSetting("Lava", false));
    private final Setting<Boolean> unloaded =
        register(new BooleanSetting("Unloaded", false));
    private final Setting<Integer> lagTime =
        register(new NumberSetting<>("LagTime", 250, 0, 1000));

    public Avoid() {
        super("Avoid", Category.Movement);
        SimpleData data = new SimpleData(
            this, "Avoids damage and unloaded chunks.");
        data.register(cactus, "Avoids damage from cacti.");
        data.register(fire, "Avoids damage from fire.");
        data.register(unloaded,
                      "Prevents you from falling into unloaded chunks.");
        data.register(lava, "Prevents you from walking into lava.");
        data.register(lagTime, "This module will not be active for this" +
            " amount of time in milliseconds after the server has" +
            " lagged you back.");
        this.setData(data);
    }

    public boolean check(BlockPos pos, World world) {
        Block block;
        Entity entity;
        return Managers.NCP.passed(lagTime.getValue())
            && (!world.isBlockLoaded(pos)
                    && unloaded.getValue()
                || (block = world.getBlockState(pos).getBlock()) == Blocks.FIRE
                    && fire.getValue()
                || (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA)
                    && (entity = PositionUtil.getPositionEntity()) != null
                    // same check for cactus/fire?
                    && !entity.isInLava()
                    && lava.getValue()
                || block == Blocks.CACTUS
                    && cactus.getValue());
    }

}
