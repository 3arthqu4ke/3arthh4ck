package me.earth.earthhack.impl.modules.render.holeesp;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.holes.HoleObserver;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.List;

//TODO: colors etc. gradient
//TODO: Make HoleManager put 2x1s and 2x2s together so we can draw 1 bb
public class HoleESP extends Module implements HoleObserver
{
    protected final Setting<Float> range      =
            register(new NumberSetting<>("Range", 6.0f, 0.0f, 100.0f));
    protected final Setting<Integer> holes    =
            register(new NumberSetting<>("Holes", 10, 0, 1000));
    protected final Setting<Integer> safeHole =
            register(new NumberSetting<>("S-Holes", 10, 0, 1000));
    protected final Setting<Integer> wide     =
            register(new NumberSetting<>("2x1-Holes", 1, 0, 1000));
    protected final Setting<Integer> big      =
            register(new NumberSetting<>("2x2-Holes", 1, 0, 1000));
    protected final Setting<Boolean> fov      =
            register(new BooleanSetting("Fov", true));
    protected final Setting<Boolean> own      =
            register(new BooleanSetting("Own", false));
    protected final Setting<Boolean> fade      =
            register(new BooleanSetting("Fade", false));
    protected final Setting<Float> fadeRange      =
            register(new NumberSetting<>("Fade-Range", 4.0f, 0.0f, 100.0f));
    protected final Setting<Float> minFade      =
            register(new NumberSetting<>("Min-Fade", 3.0f, 0.0f, 100.0f));
    protected final Setting<Double> alphaFactor   =
            register(new NumberSetting<>("AlphaFactor", 0.3, 0.0, 1.0));

    protected final Setting<Float> height     =
            register(new NumberSetting<>("SafeHeight", 1.0f, -1.0f, 1.0f));
    protected final Setting<Float> unsafeHeight =
            register(new NumberSetting<>("UnsafeHeight", 1.0f, -1.0f, 1.0f));
    protected final Setting<Float> wideHeight     =
            register(new NumberSetting<>("2x1-Height", 0.0f, -1.0f, 1.0f));
    protected final Setting<Float> bigHeight     =
            register(new NumberSetting<>("2x2-Height", 0.0f, -1.0f, 1.0f));

    protected final Setting<Color> unsafeColor =
            register(new ColorSetting("UnsafeColor", Color.RED));
    protected final Setting<Color> safeColor =
            register(new ColorSetting("SafeColor", Color.GREEN));
    protected final Setting<Color> wideColor =
            register(new ColorSetting("2x1-Color", new Color(90, 9, 255)));
    protected final Setting<Color> bigColor =
            register(new ColorSetting("2x2-Color", new Color(0, 80, 255)));

    private final BlockPos.MutableBlockPos mPos = new BlockPos.MutableBlockPos();

    public HoleESP()
    {
        super("HoleESP", Category.Render);
        this.listeners.add(new ListenerRender(this));
        this.setData(new HoleESPData(this));
    }

    @Override
    public void onLoad()
    {
        if (this.isEnabled())
        {
            Managers.HOLES.register(this);
        }
    }

    @Override
    public void onEnable()
    {
        Managers.HOLES.register(this);
    }

    @Override
    public void onDisable()
    {
        Managers.HOLES.unregister(this);
    }

    protected void onRender3D()
    {
        renderList(Managers.HOLES.getUnsafe(),
                   unsafeColor.getValue(),
                   unsafeHeight.getValue(),
                   holes.getValue());

        renderList(Managers.HOLES.getSafe(),
                   safeColor.getValue(),
                   height.getValue(),
                   safeHole.getValue());

        renderList(Managers.HOLES.getLongHoles(),
                   wideColor.getValue(),
                   wideHeight.getValue(),
                   wide.getValue());

        BlockPos playerPos = new BlockPos(mc.player);
        if (big.getValue() != 0 && !Managers.HOLES.getBigHoles().isEmpty())
        {
            int i = 1;
            for (BlockPos pos : Managers.HOLES.getBigHoles())
            {
                if (i > big.getValue())
                {
                    return;
                }

                if (checkPos(pos, playerPos))
                {
                    Color bC = bigColor.getValue();
                    float bH = bigHeight.getValue();

                    if (fade.getValue())
                    {
                        double distance = mc.player.getDistanceSq(
                                pos.getX() + 1, pos.getY(), pos.getZ() + 1);
                        double alpha = (MathUtil.square(fadeRange.getValue())
                                + MathUtil.square(minFade.getValue())
                                - distance)
                                / MathUtil.square(fadeRange.getValue());

                        if (alpha > 0 && alpha < 1)
                        {
                            int alphaInt = MathUtil.clamp((int) (alpha * 255), 0, 255);
                            Color bC1 = new Color(bC.getRed(),
                                                     bC.getGreen(),
                                                     bC.getBlue(),
                                                     alphaInt);

                            int boxInt = (int) (alphaInt * alphaFactor.getValue());
                            RenderUtil.renderBox(pos, bC1, bH, boxInt);
                            mPos.setPos(pos.getX(), pos.getY(), pos.getZ() + 1);
                            RenderUtil.renderBox(mPos, bC1, bH, boxInt);
                            mPos.setPos(pos.getX() + 1, pos.getY(), pos.getZ());
                            RenderUtil.renderBox(mPos, bC1, bH, boxInt);
                            mPos.setPos(pos.getX() + 1, pos.getY(), pos.getZ() + 1);
                            RenderUtil.renderBox(mPos, bC1, bH, boxInt);
                        }
                        else if (alpha < 0)
                        {
                            continue;
                        }
                    }

                    RenderUtil.renderBox(pos, bC, bH);
                    mPos.setPos(pos.getX(), pos.getY(), pos.getZ() + 1);
                    RenderUtil.renderBox(mPos, bC, bH);
                    mPos.setPos(pos.getX() + 1, pos.getY(), pos.getZ());
                    RenderUtil.renderBox(mPos, bC, bH);
                    mPos.setPos(pos.getX() + 1, pos.getY(), pos.getZ() + 1);
                    RenderUtil.renderBox(mPos, bC, bH);
                    i++;
                }
            }
        }
    }

    private void renderList(List<BlockPos> positions,
                            Color color,
                            float height,
                            int max)
    {
        BlockPos playerPos = new BlockPos(mc.player);
        if (max != 0 && !positions.isEmpty())
        {
            int i = 1;
            for (BlockPos pos : positions)
            {
                if (i > max)
                {
                    return;
                }

                if (checkPos(pos, playerPos))
                {
                    if (fade.getValue())
                    {
                        double alpha = (MathUtil.square(fadeRange.getValue())
                                        + MathUtil.square(minFade.getValue())
                                        - mc.player.getDistanceSq(pos))
                                        / MathUtil.square(fadeRange.getValue());

                        if (alpha > 0 && alpha < 1)
                        {
                            int alphaInt =
                                MathUtil.clamp((int) (alpha * 255), 0, 255);
                            Color color1 = new Color(color.getRed(),
                                                     color.getGreen(),
                                                     color.getBlue(),
                                                     alphaInt);
                            RenderUtil.renderBox(pos,
                                    color1, height, (int) (alphaInt * alphaFactor.getValue()));
                        }
                        else if (alpha >= 1)
                        {
                            RenderUtil.renderBox(pos, color, height);
                            continue;
                        }

                        continue;
                    }

                    RenderUtil.renderBox(pos, color, height);
                    i++;
                }
            }
        }
    }

    private boolean checkPos(BlockPos pos, BlockPos playerPos)
    {
        return (!fov.getValue() || RotationUtil.inFov(pos))
                && (own.getValue() || !pos.equals(playerPos));
    }

    @Override
    public double getRange()
    {
        return range.getValue();
    }

    @Override
    public int getSafeHoles()
    {
        return safeHole.getValue();
    }

    @Override
    public int getUnsafeHoles()
    {
        return holes.getValue();
    }

    @Override
    public int get2x1Holes()
    {
        return wide.getValue();
    }

    @Override
    public int get2x2Holes()
    {
        return big.getValue();
    }

}
