package me.earth.plugins.phobosgui.gui.components.buttons;

import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.plugins.phobosgui.PhobosColorManager;
import me.earth.plugins.phobosgui.PhobosGuiModule;
import me.earth.plugins.phobosgui.PhobosTextManager;
import me.earth.plugins.phobosgui.gui.Component;
import me.earth.plugins.phobosgui.gui.PhobosGui;
import me.earth.plugins.phobosgui.gui.components.Button;
import me.earth.plugins.phobosgui.util.PhobosColorUtil;
import me.earth.plugins.phobosgui.util.PhobosRenderUtil;
import org.lwjgl.input.Mouse;

public class Slider<N extends Number> extends Button
{

    public NumberSetting<N> setting;
    private final Number min;
    private final Number max;
    private final int difference;

    public Slider(NumberSetting<N> setting)
    {
        super(setting.getName());
        this.setting = setting;
        this.min = setting.getMin();
        this.max = setting.getMax();
        this.difference = max.intValue() - min.intValue();
        width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        dragSetting(mouseX, mouseY);
        PhobosRenderUtil.drawRect(x, y, x + width + 7.4F, y + height - 0.5f, !isHovering(mouseX, mouseY) ? 0x11555555 : 0x88555555);
        if (PhobosGuiModule.getInstance().rainbowRolling.getValue())
        {
            int color = PhobosColorUtil.changeAlpha(PhobosGuiModule.getInstance().colorMap.get(MathUtil.clamp((int) y, 0, PhobosTextManager.getInstance().scaledHeight)), PhobosGuiModule.getInstance().hoverAlpha.getValue());
            int color1 = PhobosColorUtil.changeAlpha(PhobosGuiModule.getInstance().colorMap.get(MathUtil.clamp((int) y + height, 0, PhobosTextManager.getInstance().scaledHeight)), PhobosGuiModule.getInstance().hoverAlpha.getValue());
            PhobosRenderUtil.drawGradientRect(x, y, (setting.getValue()).floatValue() <= min.floatValue() ? 0 : (width + 7.4F) * partialMultiplier(), height - 0.5f, !isHovering(mouseX, mouseY) ? PhobosGuiModule.getInstance().colorMap.get(MathUtil.clamp((int) y, 0, PhobosTextManager.getInstance().scaledHeight)) : color, !isHovering(mouseX, mouseY) ? PhobosGuiModule.getInstance().colorMap.get(MathUtil.clamp((int) y, 0, PhobosTextManager.getInstance().scaledHeight)) : color1);
        }
        else
        {
            PhobosRenderUtil.drawRect(x, y, (setting.getValue()).floatValue() <= min.floatValue() ? x : x + (width + 7.4F) * partialMultiplier(), y + height - 0.5f, !isHovering(mouseX, mouseY) ? PhobosColorManager.getInstance().getColorWithAlpha(PhobosGuiModule.getInstance().hoverAlpha.getValue()) : PhobosColorManager.getInstance().getColorWithAlpha((PhobosGuiModule.getInstance().alpha.getValue())));
        }

        PhobosTextManager.getInstance().drawStringWithShadow(getName() + " " + TextColor.GRAY + (setting.getValue() instanceof Float ? (setting.getValue()) : (setting.getValue()).doubleValue()), x + 2.3F, y - 1.7F - PhobosGui.getInstance().getTextOffset(), 0xFFFFFFFF);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isHovering(mouseX, mouseY))
        {
            setSettingFromX(mouseX);
        }
    }

    @Override
    public boolean isHovering(int mouseX, int mouseY) {
        for (Component component : PhobosGui.getInstance().getComponents())
        {
            if (component.drag)
            {
                return false;
            }
        }
        return mouseX >= getX() && mouseX <= getX() + getWidth() + 8 && mouseY >= getY() && mouseY <= getY() + height;
    }

    @Override
    public void update()
    {
        this.setHidden(!Visibilities.VISIBILITY_MANAGER.isVisible(setting));
    }

    private void dragSetting(int mouseX, int mouseY)
    {
        if(isHovering(mouseX, mouseY) && Mouse.isButtonDown(0))
        {
            setSettingFromX(mouseX);
        }
    }

    @Override
    public int getHeight()
    {
        return 14;
    }

    private void setSettingFromX(int mouseX)
    {
        double percent = (mouseX - x) / (width + 7.4F);
        double result = setting.getMin().doubleValue() + (difference * percent);
        setting.setValue(setting.numberToValue(result));
    }

    private float middle()
    {
        return max.floatValue() - min.floatValue();
    }

    private float part()
    {
        return setting.getValue().floatValue() - min.floatValue();
    }

    private float partialMultiplier()
    {
        return part() / middle();
    }
}
