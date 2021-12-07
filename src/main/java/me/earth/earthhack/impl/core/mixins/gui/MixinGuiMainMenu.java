package me.earth.earthhack.impl.core.mixins.gui;

import me.earth.earthhack.impl.commands.gui.EarthhackButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMainMenu.class)
public abstract class MixinGuiMainMenu extends GuiScreen
{
    private EarthhackButton earthhackButton;

    @Inject(
        method = "initGui",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
            ordinal = 2,
            shift = At.Shift.AFTER,
            remap = false))
    private void buttonHook(CallbackInfo info)
    {
        int x = 2; int y = 0; int w = 2;
        for (GuiButton button : this.buttonList)
        {
            if (button.id == 4) // QuitButton
            {
                x = button.x;
                y = button.y;
                w = button.width;
                break;
            }
        }

        earthhackButton = this.addButton(
                new EarthhackButton(2500, x + w + 4, y));
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"), cancellable = true)
    private void actionPerformedHook(GuiButton button, CallbackInfo info)
    {
        if (button.id == earthhackButton.id)
        {
            earthhackButton.onClick(this, earthhackButton.id);
            info.cancel();
        }
    }

    @Inject(method = "confirmClicked", at = @At("HEAD"), cancellable = true)
    public void confirmClickedHook(boolean result, int id, CallbackInfo info)
    {
        if (id == earthhackButton.id)
        {
            mc.displayGuiScreen(this);
            info.cancel();
        }
    }

}
