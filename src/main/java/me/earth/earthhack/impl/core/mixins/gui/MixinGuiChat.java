package me.earth.earthhack.impl.core.mixins.gui;

import me.earth.earthhack.impl.core.ducks.gui.IGuiChat;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.pingbypass.input.Mouse;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.TabCompleter;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiChat.class)
public abstract class MixinGuiChat extends MixinGuiScreen implements IGuiChat
{
    @Shadow
    protected GuiTextField inputField;

    @Override
    public void accessSetText(String text, boolean shouldOverwrite)
    {
        if (shouldOverwrite)
        {
            this.inputField.setText(text);
        }
        else
        {
            this.inputField.writeText(text);
        }
    }

    @Inject(
        method = "drawScreen(IIF)V",
        at = @At("HEAD"))
    public void drawScreenHook(int mouseX,
                               int mouseY,
                               float partialTicks,
                               CallbackInfo callbackInfo)
    {
        Managers.COMMANDS.renderCommandGui(
                inputField.getText(),
                inputField.x,
                inputField.y);
    }

    @Redirect(
        method = "keyTyped",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/TabCompleter;complete()V"))
    protected void completerHook(TabCompleter completer)
    {
        // TODO: TabCompleter has completions and is
        //  much better than what we do...
        if (Managers.COMMANDS.onTabComplete(inputField))
        {
            completer.complete();
        }
    }

    @Inject(
        method = "mouseClicked",
        at = @At("HEAD"),
        cancellable = true)
    protected void mouseClickedHook(int mouseX,
                                    int mouseY,
                                    int mouseButton,
                                    CallbackInfo info)
    {
        if (mouseButton == 1 || mouseButton == 2)
        {
            ITextComponent tc = mc.ingameGUI
                                  .getChatGUI()
                                  .getChatComponent(Mouse.getX(), Mouse.getY());
            if (this.handleClick(tc, mouseButton))
            {
                info.cancel();
            }
        }
    }

    /* Not required anymore after the new parsing for commands.
    @Inject(
        method = "keyTyped",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiTextField;" +
                      "textboxKeyTyped(CI)Z",
            shift = At.Shift.BEFORE),
        cancellable = true)
    public void keyTypedHook(char typedChar, int keyCode, CallbackInfo info)
    {
        if (inputField.getText().startsWith(Commands.getPrefix())
                && typedChar == ' '
                && inputField.getText()
                             .charAt(inputField.getText().length() - 1) == ' ')
        {
            info.cancel();
        }
    }*/

}
