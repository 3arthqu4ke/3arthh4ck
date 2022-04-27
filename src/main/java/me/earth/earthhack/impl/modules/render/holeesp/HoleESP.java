package me.earth.earthhack.impl.modules.render.holeesp;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.holes.HoleObserver;
import me.earth.earthhack.impl.modules.render.holeesp.invalidation.Hole;
import me.earth.earthhack.impl.modules.render.holeesp.invalidation.InvalidationConfig;
import me.earth.earthhack.impl.modules.render.holeesp.invalidation.InvalidationHoleManager;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.render.RenderUtil;
import me.earth.earthhack.impl.util.render.mutables.BBRender;
import me.earth.earthhack.impl.util.render.mutables.MutableBB;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.List;

//TODO: colors etc. gradient
//TODO: Make HoleManager put 2x1s and 2x2s together so we can draw 1 bb
public class HoleESP extends Module implements HoleObserver, InvalidationConfig
{
    protected final Setting<CalcMode> mode =
            register(new EnumSetting<>("Mode", CalcMode.Polling));

    protected final Setting<Float> range =
            register(new NumberSetting<>("Range", 6.0f, 0.0f, 100.0f));
    protected final Setting<Integer> holes =
            register(new NumberSetting<>("Holes", 10, 0, 1000));
    protected final Setting<Integer> safeHole =
            register(new NumberSetting<>("S-Holes", 10, 0, 1000));
    protected final Setting<Integer> wide =
            register(new NumberSetting<>("2x1-Holes", 1, 0, 1000));
    protected final Setting<Integer> big =
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

    protected final Setting<Boolean> async =
            register(new BooleanSetting("Async", true));
    protected final Setting<Integer> chunk_height =
            register(new NumberSetting<>("Height", 256, 0, 256));
    protected final Setting<Boolean> limit =
            register(new BooleanSetting("Limit", true));
    protected final Setting<Integer> sort_time =
            register(new NumberSetting<>("SortTime", 100, 0, 10_000));
    protected final Setting<Integer> remove_time =
            register(new NumberSetting<>("RemoveTime", 5000, 0, 60_000));

    protected final BlockPos.MutableBlockPos mPos = new BlockPos.MutableBlockPos();
    protected final InvalidationHoleManager invalidationHoleManager = new InvalidationHoleManager(this);
    protected final MutableBB bb = new MutableBB();

    public HoleESP()
    {
        super("HoleESP", Category.Render);
        this.listeners.add(new ListenerRender(this));
        this.listeners.addAll(invalidationHoleManager.getListeners());
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
        invalidationHoleManager.get1x1().clear();
        invalidationHoleManager.getHoles().clear();
        invalidationHoleManager.get1x1Unsafe().clear();
        invalidationHoleManager.get2x1().clear();
        invalidationHoleManager.get2x2().clear();
    }

    public void renderListNew(List<Hole> holes,
                              Color color,
                              float height,
                              int max)
    {
        BlockPos playerPos = new BlockPos(mc.player);
        float rangeSq = MathUtil.square(range.getValue());
        if (max != 0 && !holes.isEmpty())
        {
            int i = 1;
            for (Hole hole : holes)
            {
                if (i > max)
                {
                    return;
                }

                if (mc.player.getDistanceSq(hole.getX(), hole.getY(), hole.getZ()) < rangeSq
                        && checkPos(hole, playerPos))
                {
                    bb.setBB(
                            hole.getX() - mc.getRenderManager().viewerPosX,
                            hole.getY() - mc.getRenderManager().viewerPosY,
                            hole.getZ() - mc.getRenderManager().viewerPosZ,
                            hole.getMaxX() - mc.getRenderManager().viewerPosX,
                            hole.getY() + height - mc.getRenderManager().viewerPosY,
                            hole.getMaxZ() - mc.getRenderManager().viewerPosZ);
                    if (fade.getValue())
                    {
                        double alpha = (MathUtil.square(fadeRange.getValue())
                                + MathUtil.square(minFade.getValue())
                                - mc.player.getDistanceSq(hole.getX(), hole.getY(),
                                hole.getZ()))
                                / MathUtil.square(fadeRange.getValue());

                        if (alpha > 0 && alpha < 1)
                        {
                            int alphaInt =
                                    MathUtil.clamp((int) (alpha * 255), 0, 255);
                            // TODO: mutable color or something?
                            BBRender.renderBox(bb,
                                    new Color(color.getRed(),
                                            color.getGreen(),
                                            color.getBlue(),
                                            (int) (alphaInt * alphaFactor.getValue())),
                                    new Color(color.getRed(),
                                            color.getGreen(),
                                            color.getBlue(),
                                            alphaInt),
                                    1.5f);
                        }
                        else if (alpha >= 1)
                        {
                            BBRender.renderBox(bb,
                                    color,
                                    1.5f);
                            continue;
                        }

                        continue;
                    }

                    BBRender.renderBox(bb, color, 1.5f);
                    i++;
                }
            }
        }
    }

    public void renderListOld(List<BlockPos> positions,
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

    protected boolean checkPos(BlockPos pos, BlockPos playerPos)
    {
        return (!fov.getValue() || RotationUtil.inFov(pos))
                && (own.getValue() || !pos.equals(playerPos));
    }

    protected boolean checkPos(Hole hole, BlockPos playerPos)
    {
        // TODO: improve FOV to use mutable vec3ds
        // TODO: improve FOV in general
        return hole.isValid() && (!fov.getValue() || RotationUtil.inFov(hole.getX(), hole.getY(), hole.getZ()))
                && (own.getValue() || playerPos.getX() != hole.getX() || playerPos.getY() != hole.getY() || playerPos.getZ() != hole.getZ());
    }

    @Override
    public boolean isThisHoleObserverActive()
    {
        return !isUsingInvalidationHoleManager();
    }

    @Override
    public boolean isUsingInvalidationHoleManager()
    {
        return mode.getValue() == CalcMode.Invalidation;
    }

    @Override
    public boolean shouldCalcChunksAsnyc()
    {
        return async.getValue();
    }

    @Override
    public boolean limitChunkThreads()
    {
        return limit.getValue();
    }

    @Override
    public int getHeight()
    {
        return chunk_height.getValue();
    }

    @Override
    public int getSortTime()
    {
        return sort_time.getValue();
    }

    @Override
    public int getRemoveTime()
    {
        return remove_time.getValue();
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

    public enum CalcMode
    {
        Polling,
        Invalidation
    }

}
