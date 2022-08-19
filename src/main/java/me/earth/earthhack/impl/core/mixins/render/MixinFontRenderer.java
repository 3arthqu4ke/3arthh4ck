package me.earth.earthhack.impl.core.mixins.render;

import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.hud.HUD;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.regex.Pattern;

@Mixin(FontRenderer.class)
public abstract class MixinFontRenderer
{
    private static final SettingCache<Boolean, BooleanSetting, HUD> SHADOW =
        Caches.getSetting(HUD.class, BooleanSetting.class, "Shadow", false);

    private static final String COLOR_CODES = "0123456789abcdefklmnorzy+-p";

    private static final Pattern CUSTOM_PATTERN =
            Pattern.compile("(?i)\u00a7Z[0-9A-F]{8}");
    @Shadow
    private boolean randomStyle;
    @Shadow
    private boolean boldStyle;
    @Shadow
    private boolean italicStyle;
    @Shadow
    private boolean underlineStyle;
    @Shadow
    private boolean strikethroughStyle;
    @Shadow
    private int textColor;
    @Shadow
    protected float posX;
    @Shadow
    protected float posY;
    @Shadow
    private float alpha;

    private int skip;
    private int currentIndex;
    private boolean currentShadow;
    private String currentText;
    private boolean rainbowPlus;
    private boolean rainbowMinus;

    @Shadow
    protected abstract int renderString(String text,
                                        float x,
                                        float y,
                                        int color,
                                        boolean dropShadow);
    @Redirect(
        method = "drawString(Ljava/lang/String;FFIZ)I",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/FontRenderer;" +
                     "renderString(Ljava/lang/String;FFIZ)I"))
    public int renderStringHook(FontRenderer fontrenderer,
                                String text,
                                float x,
                                float y,
                                int color,
                                boolean dropShadow)
    {
        if (dropShadow && SHADOW.getValue())
        {
            return this.renderString(text, x - 0.4f, y - 0.4f, color, true);
        }

        return this.renderString(text, x, y, color, dropShadow);
    }

    @Inject(
        method = "renderStringAtPos",
        at = @At(value = "HEAD"))
    public void resetSkip(String text, boolean shadow, CallbackInfo info)
    {
        skip = 0;
        currentIndex = 0;
        currentText = text;
        currentShadow = shadow;
    }

    @Redirect(
        method = "renderStringAtPos",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/String;charAt(I)C",
            ordinal = 0))
    public char charAtHook(String text, int index)
    {
        currentIndex = index;
        return getCharAt(text, index);
    }

    @Redirect(
        method = "renderStringAtPos",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/String;charAt(I)C",
            ordinal = 1))
    public char charAtHook1(String text, int index)
    {
        return getCharAt(text, index);
    }

    @Redirect(
        method = "renderStringAtPos",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/String;length()I",
            ordinal = 0))
    public int lengthHook(String string)
    {
        return string.length() - skip;
    }

    @Redirect(
        method = "renderStringAtPos",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/String;length()I",
            ordinal = 1))
    public int lengthHook1(String string)
    {
        return string.length() - skip;
    }

    @Redirect(
        method = "renderStringAtPos",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/String;indexOf(I)I",
            ordinal = 0))
    @SuppressWarnings("DuplicatedCode")
    public int colorCodeHook(String colorCode, int ch)
    {
        int result = COLOR_CODES.indexOf(
                String.valueOf(currentText.charAt(currentIndex + skip + 1))
                        .toLowerCase().charAt(0));

        if (result == 22)
        {
            this.randomStyle        = false;
            this.boldStyle          = false;
            this.strikethroughStyle = false;
            this.underlineStyle     = false;
            this.italicStyle        = false;
            this.rainbowPlus        = false;
            this.rainbowMinus       = false;

            char[] h = new char[8];

            try
            {
                for (int j = 0; j < 8; j++)
                {
                    h[j] = currentText.charAt(currentIndex + skip + j + 2);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return result;
            }

            int colorcode = 0xffffffff;

            try
            {
                colorcode = (int) Long.parseLong(new String(h), 16);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            this.textColor = colorcode;
            GlStateManager.color(
                    (colorcode >> 16 & 0xFF) / 255.0f
                            / (currentShadow ? 4 : 1),
                    (colorcode >> 8 & 0xFF)  / 255.0f
                            / (currentShadow ? 4 : 1),
                    (colorcode & 0xFF)       / 255.0f
                            / (currentShadow ? 4 : 1),
                    (colorcode >> 24 & 0xFF) / 255.0f);
            skip += 8;
        }
        else if (result == 23)
        {
            this.randomStyle        = false;
            this.boldStyle          = false;
            this.strikethroughStyle = false;
            this.underlineStyle     = false;
            this.italicStyle        = false;
            this.rainbowPlus        = false;
            this.rainbowMinus       = false;

            int rainbow = Color.HSBtoRGB(Managers.COLOR.getHue(), 1.0f, 1.0f);
            GlStateManager.color(
                    (rainbow >> 16 & 0xFF) / 255.0f
                            / (currentShadow ? 4 : 1),
                    (rainbow >> 8 & 0xFF)  / 255.0f
                            / (currentShadow ? 4 : 1),
                    (rainbow & 0xFF)       / 255.0f
                            / (currentShadow ? 4 : 1),
                    (rainbow >> 24 & 0xFF) / 255.0f);
        }
        else if (result == 24)
        {
            this.randomStyle        = false;
            this.boldStyle          = false;
            this.strikethroughStyle = false;
            this.underlineStyle     = false;
            this.italicStyle        = false;
            this.rainbowPlus        = true;
            this.rainbowMinus       = false;
        }
        else if (result == 25)
        {
            this.randomStyle        = false;
            this.boldStyle          = false;
            this.strikethroughStyle = false;
            this.underlineStyle     = false;
            this.italicStyle        = false;
            this.rainbowPlus        = false;
            this.rainbowMinus       = true;
        }
        else if (result == 26) // TODO: player face
        {

        }
        else
        {
            this.rainbowPlus  = false;
            this.rainbowMinus = false;
        }

        return result;
    }

    @Inject(
        method = "resetStyles",
        at = @At("HEAD"))
    public void resetStylesHook(CallbackInfo info)
    {
        this.rainbowPlus  = false;
        this.rainbowMinus = false;
    }

    @Inject(
        method = "renderStringAtPos",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/FontRenderer;renderChar(CZ)F",
            shift = At.Shift.BEFORE,
            ordinal = 0))
    public void renderCharHook(String text, boolean shadow, CallbackInfo info)
    {
        if (this.rainbowPlus || this.rainbowMinus)
        {
            int rainbow =
                    Color.HSBtoRGB(Managers.COLOR.getHueByPosition(
                            this.rainbowMinus
                                ? this.posY
                                : this.posX),
                            1.0f,
                            1.0f);

            GlStateManager.color(
                    (rainbow >> 16 & 0xFF) / 255.0f
                            / (shadow ? 4 : 1),
                    (rainbow >> 8 & 0xFF)  / 255.0f
                            / (shadow ? 4 : 1),
                    (rainbow & 0xFF)       / 255.0f
                            / (shadow ? 4 : 1),
                    this.alpha);
        }
    }

    @ModifyVariable(
        method = "getStringWidth",
        at = @At(value="HEAD"),
        ordinal = 0)
    private String setText(String text)
    {
        return text == null
                ? null
                : CUSTOM_PATTERN.matcher(text).replaceAll(TextColor.AQUA);
    }

    private char getCharAt(String text, int index)
    {
        if (index + skip >= text.length())
        {
            return text.charAt(text.length() - 1);
        }

        return text.charAt(index + skip);
    }

}
