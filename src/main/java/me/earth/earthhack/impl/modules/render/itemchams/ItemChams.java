package me.earth.earthhack.impl.modules.render.itemchams;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.*;
import me.earth.earthhack.impl.gui.visibility.PageBuilder;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.GlShader;
import me.earth.earthhack.impl.util.render.image.GifImage;
import me.earth.earthhack.impl.util.render.image.NameableImage;
import me.earth.earthhack.impl.util.render.shader.FramebufferWrapper;

import java.awt.*;

public class ItemChams extends Module
{

    protected final Setting<ItemChamsPage> page =
            register(new EnumSetting<>("Page", ItemChamsPage.Glint));

    protected final Setting<Boolean> glint      =
            register(new BooleanSetting("ModifyGlint", false));
    protected final Setting<Float> scale        =
            register(new NumberSetting<>("GlintScale", 8.0f, 0.1f, 20.0f));
    protected final Setting<Float> glintMult    =
            register(new NumberSetting<>("GlintMultiplier", 1.0f, 0.1f, 10.0f));
    protected final Setting<Float> glintRotate  =
            register(new NumberSetting<>("GlintRotate", 1.0f, 0.1f, 10.0f));
    protected final Setting<Color> glintColor   =
            register(new ColorSetting("GlintColor", Color.RED));

    protected final Setting<Boolean> chams      =
            register(new BooleanSetting("Chams", false));
    protected final Setting<Boolean> blur       =
            register(new BooleanSetting("Blur", false));
    protected final Setting<Float> radius       =
            register(new NumberSetting<>("Radius", 2.0f, 0.1f, 10.0f));
    protected final Setting<Float> mix          =
            register(new NumberSetting<>("Mix", 1.0f, 0.0f, 1.0f));
    protected final Setting<Boolean> useImage   =
            register(new BooleanSetting("UseImage", false));
    public final Setting<Boolean> useGif        =
            register(new BooleanSetting("UseGif", false));
    public final Setting<GifImage> gif          =
            register(new ListSetting<>("Gif", Managers.FILES.getInitialGif(), Managers.FILES.getGifs()));
    public final Setting<NameableImage> image   =
            register(new ListSetting<>("Image", Managers.FILES.getInitialImage(), Managers.FILES.getImages()));
    protected final Setting<Float> imageMix     =
            register(new NumberSetting<>("ImageMix", 1.0f, 0.0f, 1.0f));
    protected final Setting<Boolean> rotate     =
            register(new BooleanSetting("Rotate", false));
    protected final Setting<Color> chamColor    =
            register(new ColorSetting("Color", Color.RED));

    protected final GlShader shader = new GlShader("item");

    protected final FramebufferWrapper wrapper = new FramebufferWrapper();

    protected boolean forceRender = false;

    public ItemChams()
    {
        super("ItemChams", Category.Render);
        this.listeners.add(new ListenerRenderItemPre(this));
        this.listeners.add(new ListenerRenderWorld(this));

        new PageBuilder<>(this, page)
                .addPage(p -> p == ItemChamsPage.Glint, glint, glintColor)
                .addPage(p -> p == ItemChamsPage.Chams, chams, chamColor)
                .register(Visibilities.VISIBILITY_MANAGER);
    }

    public boolean isModifyingGlint()
    {
        return glint.getValue();
    }

    public Color getGlintColor()
    {
        return glintColor.getValue();
    }

    public float getScale()
    {
        return scale.getValue();
    }

    public float getFactor()
    {
        return glintMult.getValue();
    }

    public float getGlintRotate()
    {
        return glintRotate.getValue();
    }

}
