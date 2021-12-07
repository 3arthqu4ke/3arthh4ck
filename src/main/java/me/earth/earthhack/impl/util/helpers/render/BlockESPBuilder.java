package me.earth.earthhack.impl.util.helpers.render;

import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.util.math.AxisAlignedBB;

import java.awt.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlockESPBuilder
{
    private static final Color LIGHT_WHITE = new Color(255, 255, 255, 125);

    private Supplier<Color> color   = () -> LIGHT_WHITE;
    private Supplier<Color> outline = () -> Color.white;
    private Supplier<Float> width   = () -> 1.5f;
    private Function<AxisAlignedBB, AxisAlignedBB> interpolation = bb -> bb;

    public BlockESPBuilder withColor(Setting<Color> colorSetting)
    {
        return this.withColor(colorSetting::getValue);
    }

    public BlockESPBuilder withColor(Supplier<Color> color)
    {
        this.color = color;
        return this;
    }

    public BlockESPBuilder withOutlineColor(Setting<Color> outlineColor)
    {
        return this.withOutlineColor(outlineColor::getValue);
    }

    public BlockESPBuilder withOutlineColor(Supplier<Color> outlineColor)
    {
        this.outline = outlineColor;
        return this;
    }

    public BlockESPBuilder withLineWidth(Setting<Float> lineWidth)
    {
        return withLineWidth(lineWidth::getValue);
    }

    public BlockESPBuilder withLineWidth(Supplier<Float> lineWidth)
    {
        this.width = lineWidth;
        return this;
    }

    public BlockESPBuilder withInterpolation(
            Function<AxisAlignedBB, AxisAlignedBB> interpolation)
    {
        this.interpolation = interpolation;
        return this;
    }

    public IAxisESP build()
    {
        return bb -> RenderUtil.renderBox(interpolation.apply(bb),
                                          color.get(),
                                          outline.get(),
                                          width.get());
    }

}
