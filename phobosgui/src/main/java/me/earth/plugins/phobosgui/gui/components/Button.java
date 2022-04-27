package me.earth.plugins.phobosgui.gui.components;

import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.plugins.phobosgui.PhobosColorManager;
import me.earth.plugins.phobosgui.PhobosGuiModule;
import me.earth.plugins.phobosgui.PhobosTextManager;
import me.earth.plugins.phobosgui.gui.Component;
import me.earth.plugins.phobosgui.gui.PhobosGui;
import me.earth.plugins.phobosgui.util.PhobosColorUtil;
import me.earth.plugins.phobosgui.util.PhobosRenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class Button extends Item
{

    private boolean state;

    public Button(String name)
    {
        super(name);
        this.height = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (PhobosGuiModule.getInstance().rainbowRolling.getValue())
        {
            int color = PhobosColorUtil.changeAlpha(PhobosGuiModule.getInstance().colorMap.get(MathUtil.clamp((int) y, 0, PhobosTextManager.getInstance().scaledHeight)), PhobosGuiModule.getInstance().hoverAlpha.getValue());
            int color1 = PhobosColorUtil.changeAlpha(PhobosGuiModule.getInstance().colorMap.get(MathUtil.clamp((int) y + height, 0, PhobosTextManager.getInstance().scaledHeight)), PhobosGuiModule.getInstance().hoverAlpha.getValue());
            PhobosRenderUtil.drawGradientRect(x, y, width, height - 0.5f, getState() ? (!isHovering(mouseX, mouseY) ? PhobosGuiModule.getInstance().colorMap.get(MathUtil.clamp((int) y, 0, PhobosTextManager.getInstance().scaledHeight)) : color) : (!isHovering(mouseX, mouseY) ? 0x11555555 : 0x88555555), getState() ? (!isHovering(mouseX, mouseY) ? PhobosGuiModule.getInstance().colorMap.get(MathUtil.clamp((int) y + height, 0, PhobosTextManager.getInstance().scaledHeight)) : color1) : (!isHovering(mouseX, mouseY) ? 0x11555555 : 0x88555555));
        }
        else
        {
            PhobosRenderUtil.drawRect(x, y, x + width, y + height - 0.5f, getState() ? (!isHovering(mouseX, mouseY) ? PhobosColorManager.getInstance().getColorWithAlpha(PhobosGuiModule.getInstance().hoverAlpha.getValue()) : PhobosColorManager.getInstance().getColorWithAlpha(PhobosGuiModule.getInstance().alpha.getValue())) : !isHovering(mouseX, mouseY) ? 0x11555555 : 0x88555555);
        }

        PhobosTextManager.getInstance().drawStringWithShadow(getName(), x + 2.3F, y - 2F - PhobosGui.getInstance().getTextOffset(), getState() ? 0xFFFFFFFF : 0xFFAAAAAA);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (mouseButton == 0 && isHovering(mouseX, mouseY))
        {
            onMouseClick();
        }
    }

    public void onMouseClick()
    {
        state = !state;
        toggle();
        mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public void toggle() {}

    public boolean getState()
    {
        return state;
    }

    @Override
    public int getHeight()
    {
        return 14;
    }

    public boolean isHovering(int mouseX, int mouseY)
    {
        for (Component component : PhobosGui.getInstance().getComponents())
        {
            if (component.drag)
            {
                return false;
            }
        }

        return mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() && mouseY <= getY() + height;
    }
}
