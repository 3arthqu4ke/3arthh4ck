package me.earth.earthhack.impl.core.mixins.util;

import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(TextFormatting.class)
public abstract class MixinTextFormatting
{
    private static final Pattern NEW_PATTERN =
            Pattern.compile("(?i)\u00a7[0-9A-FK-ORY+-]|\u00a7Z[0-9A-F]{8}");

    @Redirect(
        method = "getTextWithoutFormattingCodes",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/regex/Pattern;matcher(Ljava/lang/CharSequence;"
                     + ")Ljava/util/regex/Matcher;",
            remap = false))
    private static Matcher patternHook(Pattern pattern, CharSequence s)
    {
        return NEW_PATTERN.matcher(s);
    }

}
