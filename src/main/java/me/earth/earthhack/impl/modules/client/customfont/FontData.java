package me.earth.earthhack.impl.modules.client.customfont;

import me.earth.earthhack.api.module.data.DefaultData;

final class FontData extends DefaultData<FontMod>
{
    public FontData(FontMod module)
    {
        super(module);
        register("Font", "The Font.");
        register("FontStyle", "Plain, Bold, Italic, or" +
                " All which is Bold and Italic.");
        register("FontSize", "Size the Font will be rendered in.");
        register("AntiAlias", "Smooths the edges of the font.");
        register("Metrics", "Takes sub pixel accuracy into account.");
        register("Shadow", "Font will be rendered with shadow regardless." +
                " If enabled the shadow will have less offset.");
        register("Fonts", "Click this setting to get " +
                "a list of available fonts.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Handles the CustomFont. If enabled, HUD, Nametags and Gui" +
                " will be rendered in the custom font.";
    }

}
