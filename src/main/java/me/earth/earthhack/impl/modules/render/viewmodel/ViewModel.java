package me.earth.earthhack.impl.modules.render.viewmodel;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.core.mixins.render.MixinItemRenderer;
import net.minecraft.util.EnumHand;

/**
 * {@link MixinItemRenderer}.
 */
public class ViewModel extends Module
{
    public static final float[] DEFAULT_SCALE =
            new float[]{1.0f, 1.0f, 1.0f};
    public static final float[] DEFAULT_TRANSLATION =
            new float[]{0.0f, 0.0f, 0.0f, 0.0f};

    protected final Setting<Float> offX  =
            register(new NumberSetting<>("OffHand-X", 0.0f, -10.0f, 10.0f));
    protected final Setting<Float> offY  =
            register(new NumberSetting<>("OffHand-Y", 0.0f, -10.0f, 10.0f));
    protected final Setting<Float> mainX =
            register(new NumberSetting<>("MainHand-X", 0.0f, -10.0f, 10.0f));
    protected final Setting<Float> mainY =
            register(new NumberSetting<>("MainHand-Y", 0.0f, -10.0f, 10.0f));
    protected final Setting<Float> xScale =
            register(new NumberSetting<>("X-Scale", 1.0f, 0.0f, 10.0f));
    protected final Setting<Float> yScale =
            register(new NumberSetting<>("Y-Scale", 1.0f, 0.0f, 10.0f));
    protected final Setting<Float> zScale =
            register(new NumberSetting<>("Z-Scale", 1.0f, 0.0f, 10.0f));
    protected final Setting<Float> angleTranslate =
            register(new NumberSetting<>("Angle-Translate", 0.0f, -360.0f, 360.0f));
    protected final Setting<Float> xTranslate =
            register(new NumberSetting<>("X-Translate", 1.0f, -10.0f, 10.0f));
    protected final Setting<Float> yTranslate =
            register(new NumberSetting<>("Y-Translate", 1.0f, -10.0f, 10.0f));
    protected final Setting<Float> zTranslate =
            register(new NumberSetting<>("Z-Translate", 1.0f, -10.0f, 10.0f));

    public ViewModel()
    {
        super("ViewModel", Category.Render);
        this.setData(new ViewModelData(this));
    }

    public float getX(EnumHand hand)
    {
        if (!this.isEnabled())
        {
            return 0.0f;
        }

        return hand == EnumHand.MAIN_HAND ? mainX.getValue() : offX.getValue();
    }

    public float getY(EnumHand hand)
    {
        if (!this.isEnabled())
        {
            return 0.0f;
        }

        return hand == EnumHand.MAIN_HAND ? mainY.getValue() : offY.getValue();
    }

    public float[] getScale()
    {
        if (!this.isEnabled())
        {
            return DEFAULT_SCALE;
        }

        return new float[]
                {xScale.getValue(), yScale.getValue(), zScale.getValue()};
    }

    public float[] getTranslation()
    {
        if (!this.isEnabled())
        {
            return DEFAULT_TRANSLATION;
        }

        return new float[]
            {angleTranslate.getValue(), xTranslate.getValue(), yTranslate.getValue(), zTranslate.getValue()};
    }

}
