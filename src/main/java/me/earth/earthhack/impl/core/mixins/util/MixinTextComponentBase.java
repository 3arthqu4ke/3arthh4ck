package me.earth.earthhack.impl.core.mixins.util;

import me.earth.earthhack.impl.core.ducks.util.ITextComponentBase;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(TextComponentBase.class)
public abstract class MixinTextComponentBase
        implements ITextComponentBase, ITextComponent
{
    private Supplier<String> hookFormat;
    private Supplier<String> hookUnFormat;

    @Override
    public void setFormattingHook(Supplier<String> hook)
    {
        this.hookFormat = hook;
    }

    @Override
    public void setUnFormattedHook(Supplier<String> hook)
    {
        this.hookUnFormat = hook;
    }

    @Override
    public ITextComponent copyNoSiblings()
    {
        ITextComponent copy = this.createCopy();
        copy.getSiblings().clear();

        return copy;
    }

    @Inject(
        method = "getFormattedText",
        at = @At("HEAD"),
        cancellable = true)
    public void getFormattedTextHook(CallbackInfoReturnable<String> info)
    {
        if (hookFormat != null)
        {
            info.setReturnValue(hookFormat.get());
        }
    }

    @Inject(
        method = "getUnformattedText",
        at = @At("HEAD"),
        cancellable = true)
    public void getUnformattedTextHook(CallbackInfoReturnable<String> info)
    {
        if (hookUnFormat != null)
        {
            info.setReturnValue(hookUnFormat.get());
        }
    }

}
