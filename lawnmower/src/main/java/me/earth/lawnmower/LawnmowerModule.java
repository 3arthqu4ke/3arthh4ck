package me.earth.lawnmower;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.geocache.Sphere;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;

public class LawnmowerModule extends Module {
    private final Setting<Double> range = register(new NumberSetting<>("Range", 6.0, 0.1, 10.0));
    private final Setting<Integer> blocksTick = register(new NumberSetting<>("Break/Tick", 20, 1, 1_000));
    private final Setting<Boolean> rotate = register(new BooleanSetting("Rotate", false));
    private BlockPos pos;

    public LawnmowerModule() {
        super("Lawnmower", Category.Misc);
        this.setData(new SimpleData(this, "Mows the lawn."));
        this.listeners.add(new LambdaListener<>(MotionUpdateEvent.class, 10_000_000, e -> {
            if (e.getStage() == Stage.PRE) {
                pos = null;
                BlockPos middle = PositionUtil.getPosition(RotationUtil.getRotationPlayer());
                int maxRadius = Sphere.getRadius(range.getValue());
                int breaks = 0;
                boolean rotationsSet = false;
                BlockPos.MutableBlockPos mutPos = new BlockPos.MutableBlockPos();
                for (int i = 1; i < maxRadius; i++) {
                    Vec3i vec3i = Sphere.get(i);
                    mutPos.setPos(middle.getX() + vec3i.getX(), middle.getY() + vec3i.getY(), middle.getZ() + vec3i.getZ());
                    IBlockState state = mc.world.getBlockState(mutPos);
                    if (state.getBlock() == Blocks.TALLGRASS
                        || state.getBlock() == Blocks.DOUBLE_PLANT
                            && (getType(mutPos, state, mc.world, state.getBlock()) == BlockDoublePlant.EnumPlantType.GRASS
                                    || getType(mutPos, state, mc.world, state.getBlock()) == BlockDoublePlant.EnumPlantType.FERN)) {
                        breaks++;
                        if (rotate.getValue() && !RotationUtil.isLegit(pos)) {
                            if (!rotationsSet) {
                                float[] rotations = RotationUtil.getRotationsToTopMiddle(mutPos);
                                e.setYaw(rotations[0]);
                                e.setPitch(rotations[1]);
                                rotationsSet = true;
                                pos = mutPos.toImmutable();
                            }
                        } else {
                            BlockPos immutable = mutPos.toImmutable();
                            mc.playerController.onPlayerDamageBlock(immutable, RayTraceUtil.getFacing(mc.player, immutable, true));
                            mc.player.swingArm(EnumHand.MAIN_HAND);
                        }

                        if (breaks >= blocksTick.getValue()) {
                            break;
                        }
                    }
                }
            } else {
                BlockPos p = pos;
                if (p != null) {
                    mc.playerController.onPlayerDamageBlock(p, RayTraceUtil.getFacing(mc.player, p, true));
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                }
            }
        }));
    }

    @Override
    protected void onEnable() {
        pos = null;
    }

    private BlockDoublePlant.EnumPlantType getType(BlockPos pos, IBlockState state, IBlockAccess access, Block block) {
        if (state.getBlock() == block) {
            state = state.getActualState(access, pos);
            return state.getValue(BlockDoublePlant.VARIANT);
        } else {
            return BlockDoublePlant.EnumPlantType.FERN;
        }
    }

}
