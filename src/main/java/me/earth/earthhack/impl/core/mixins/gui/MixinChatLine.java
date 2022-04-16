package me.earth.earthhack.impl.core.mixins.gui;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.core.ducks.gui.IChatLine;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.chat.Chat;
import me.earth.earthhack.impl.util.animation.AnimationMode;
import me.earth.earthhack.impl.util.animation.TimeAnimation;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Mixin(ChatLine.class)
public abstract class MixinChatLine implements IChatLine
{

    private static final ModuleCache<Chat> CHAT = Caches.getModule(Chat.class);
    private static final SettingCache<Boolean, BooleanSetting, Chat> TIME_STAMPS
     = Caches.getSetting(Chat.class, BooleanSetting.class, "TimeStamps", false);
    private static final SettingCache<Color, ColorSetting, Chat> COLOR
            = Caches.getSetting(Chat.class, ColorSetting.class, "TimeStampsColor", Color.WHITE);
    private static final SettingCache<Chat.Rainbow, EnumSetting<Chat.Rainbow>, Chat> RAINBOW
            = Caches.getSetting(Chat.class, Setting.class, "Rainbow", Chat.Rainbow.None);
    private static final DateFormat FORMAT = new SimpleDateFormat("k:mm");
    private static final Minecraft MC = Minecraft.getMinecraft();

    private String timeStamp;

    @Override
    public String getTimeStamp()
    {
        return timeStamp;
    }

    @Override
    @Accessor(value = "lineString")
    public abstract void setComponent(ITextComponent component);

    @Inject(
        method = "<init>",
        at = @At("RETURN"))
    public void constructorHook(int updateCounterCreatedIn,
                                ITextComponent lineStringIn,
                                int chatLineIDIn,
                                CallbackInfo ci)
    {
        StringBuilder timeStampBuilder = new StringBuilder();
        switch (RAINBOW.getValue()) {
            case None:
                Color color = COLOR.getValue();
                int hex = MathUtil.toRGBA(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
                String colorString = TextColor.CUSTOM + Integer.toHexString(hex);
                timeStampBuilder.append(colorString);
                break;
            case Horizontal:
                timeStampBuilder.append(TextColor.RAINBOW_PLUS);
                break;
            case Vertical:
                timeStampBuilder.append(TextColor.RAINBOW_MINUS);
                break;
        }

        this.timeStamp = timeStampBuilder.append("<")
                                         .append(FORMAT.format(new Date()))
                                         .append("> ")
                                         .append(TextColor.RESET)
                                         .toString();

        String t = lineStringIn.getFormattedText();
        if (CHAT.isEnabled() && TIME_STAMPS.getValue())
        {
            t = timeStamp + t;
        }

        CHAT.get().animationMap.put(
                ChatLine.class.cast(this),
                new TimeAnimation(
                    CHAT.get().time.getValue(),
                    -(MC.fontRenderer.getStringWidth(t)),
                    0,
                    false,
                    AnimationMode.LINEAR));
    }

}
