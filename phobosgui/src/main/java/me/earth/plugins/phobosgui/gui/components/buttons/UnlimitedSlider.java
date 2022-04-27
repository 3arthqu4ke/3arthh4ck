package me.earth.plugins.phobosgui.gui.components.buttons;

import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.gui.chat.util.IncrementationUtil;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
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

public class UnlimitedSlider<N extends Number> extends Button
{
    public NumberSetting<N> setting;

    public UnlimitedSlider(NumberSetting<N> setting)
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
            PhobosRenderUtil.drawGradientRect((int) x, (int) y, width + 7.4f, height, color, color1);
        }
        else
        {
            PhobosRenderUtil.drawRect(x, y, x + width + 7.4F, y + height - 0.5f, !isHovering(mouseX, mouseY) ? PhobosColorManager.getInstance().getColorWithAlpha((PhobosGuiModule.getInstance()).hoverAlpha.getValue()) : PhobosColorManager.getInstance().getColorWithAlpha((PhobosGuiModule.getInstance()).alpha.getValue()));
        }

        PhobosTextManager.getInstance().drawStringWithShadow(" - " + setting.getName() + " " + TextColor.GRAY + setting.getValue() + TextColor.RESET + " +", x + 2.3F, y - 1.7F - PhobosGui.getInstance().getTextOffset(), getState() ? 0xFFFFFFFF : 0xFFAAAAAA);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isHovering(mouseX, mouseY))
        {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            if(isRight(mouseX))
            {
                if (setting.getValue() instanceof Double || setting.getValue() instanceof Float)
                {
                    Double d = setting.getValue().doubleValue() + 0.1;
                    setting.setValue(setting.numberToValue(d));
                }
                else
                {
                    long l = setting.getValue().longValue() + 1;
                    setting.setValue(setting.numberToValue(l));
                }
            }
            else
            {
                if (setting.getValue() instanceof Double || setting.getValue() instanceof Float)
                {
                    Double d = setting.getValue().doubleValue() - 0.1;
                    setting.setValue(setting.numberToValue(d));
                }
                else
                {
                    long l = setting.getValue().longValue() - 1;
                    setting.setValue(setting.numberToValue(l));
                }
            }
        }
    }

    @Override
    public void update()
    {
        this.setHidden(!Visibilities.VISIBILITY_MANAGER.isVisible(setting));
    }

    @Override
    public int getHeight()
    {
        return 14;
    }

    @Override
    public boolean getState()
    {
        return true;
    }

    public boolean isRight(int x)
    {
        return x > this.x + ((width + 7.4F) / 2);
    }

}

