package me.earth.plugins.phobosgui.gui.components.buttons;

import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.gui.visibility.VisibilityManager;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.plugins.phobosgui.PhobosColorManager;
import me.earth.plugins.phobosgui.PhobosGuiModule;
import me.earth.plugins.phobosgui.PhobosTextManager;
import me.earth.plugins.phobosgui.gui.PhobosGui;
import me.earth.plugins.phobosgui.gui.components.Button;
import me.earth.plugins.phobosgui.util.PhobosColorUtil;
import me.earth.plugins.phobosgui.util.PhobosRenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class BindButton extends Button
{
    private final BindSetting setting;
    public boolean isListening;

    public BindButton(BindSetting setting)
    {
        super(setting.getName());
        this.setting = setting;
        width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (PhobosGuiModule.getInstance().rainbowRolling.getValue())
        {
            int color = PhobosColorUtil.changeAlpha(PhobosGuiModule.getInstance().colorMap.get(MathUtil.clamp((int) y, 0, PhobosTextManager.getInstance().scaledHeight)), (PhobosGuiModule.getInstance()).hoverAlpha.getValue());
            int color1 = PhobosColorUtil.changeAlpha(PhobosGuiModule.getInstance().colorMap.get(MathUtil.clamp((int) y + height, 0, PhobosTextManager.getInstance().scaledHeight)), (PhobosGuiModule.getInstance()).hoverAlpha.getValue());
            PhobosRenderUtil.drawGradientRect(x, y, width + 7.4f, height - 0.5f, getState() ? (!isHovering(mouseX, mouseY) ? PhobosGuiModule.getInstance().colorMap.get(MathUtil.clamp((int) y, 0, PhobosTextManager.getInstance().scaledHeight)) : color) : (!isHovering(mouseX, mouseY) ? 0x11555555 : 0x88555555), getState() ? (!isHovering(mouseX, mouseY) ? PhobosGuiModule.getInstance().colorMap.get(MathUtil.clamp((int) y + height, 0, PhobosTextManager.getInstance().scaledHeight)) : color1) : (!isHovering(mouseX, mouseY) ? 0x11555555 : 0x88555555));
        }
        else
        {
            PhobosRenderUtil.drawRect(x, y, x + width + 7.4F, y + height - 0.5f, getState() ? (!isHovering(mouseX, mouseY) ? PhobosColorManager.getInstance().getColorWithAlpha((PhobosGuiModule.getInstance()).hoverAlpha.getValue()) : PhobosColorManager.getInstance().getColorWithAlpha((PhobosGuiModule.getInstance()).alpha.getValue())) : !isHovering(mouseX, mouseY) ? 0x11555555 : 0x88555555);
        }

        if (isListening)
        {
            PhobosTextManager.getInstance().drawStringWithShadow("Listening...", x + 2.3F, y - 1.7F - PhobosGui.getInstance().getTextOffset(), getState() ? 0xFFFFFFFF : 0xFFAAAAAA);
        }
        else
        {
            PhobosTextManager.getInstance().drawStringWithShadow(setting.getName() + " " + TextColor.GRAY + setting.getValue(), x + 2.3F, y - 1.7F - PhobosGui.getInstance().getTextOffset(), getState() ? 0xFFFFFFFF : 0xFFAAAAAA);
        }
    }

    @Override
    public void update()
    {
        this.setHidden(!Visibilities.VISIBILITY_MANAGER.isVisible(setting));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isHovering(mouseX, mouseY))
        {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode)
    {
        if(isListening)
        {
            Bind bind = Bind.fromKey(keyCode);
            if(bind.toString().equalsIgnoreCase("Escape"))
            {
                return;
            }
            else if(bind.toString().equalsIgnoreCase("Delete"))
            {
                bind = Bind.none();
            }

            setting.setValue(bind);
            super.onMouseClick();
        }
    }

    @Override
    public int getHeight()
    {
        return 14;
    }

    @Override
    public void toggle()
    {
        isListening = !isListening;
    }

    @Override
    public boolean getState()
    {
        return !isListening;
    }

}
