package me.earth.plugins.phobosgui.gui.components.buttons;

import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.plugins.phobosgui.PhobosColorManager;
import me.earth.plugins.phobosgui.PhobosGuiModule;
import me.earth.plugins.phobosgui.PhobosTextManager;
import me.earth.plugins.phobosgui.gui.PhobosGui;
import me.earth.plugins.phobosgui.gui.components.Button;
import me.earth.plugins.phobosgui.util.PhobosColorUtil;
import me.earth.plugins.phobosgui.util.PhobosRenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;

public class StringButton extends Button
{

    private StringSetting setting;
    public boolean isListening;
    private CurrentString currentString = new CurrentString("");

    public StringButton(StringSetting setting)
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
            PhobosRenderUtil.drawRect(x, y, x + width + 7.4F, y + height - 0.5f, getState() ? (!isHovering(mouseX, mouseY) ? PhobosColorManager.getInstance().getColorWithAlpha(((PhobosGuiModule.getInstance()).hoverAlpha.getValue())) : PhobosColorManager.getInstance().getColorWithAlpha(((PhobosGuiModule.getInstance()).alpha.getValue()))) : !isHovering(mouseX, mouseY) ? 0x11555555 : 0x88555555);
        }

        if(isListening)
        {
            PhobosTextManager.getInstance().drawStringWithShadow(currentString.getString() + PhobosTextManager.getInstance().getIdleSign(), x + 2.3F, y - 1.7F - PhobosGui.getInstance().getTextOffset(), getState() ? 0xFFFFFFFF : 0xFFAAAAAA);
        }
        else
        {
            PhobosTextManager.getInstance().drawStringWithShadow((/*setting.shouldRenderName() ? setting.getName() + " " + TextColor.GRAY :*/ "") + setting.getValue(), x + 2.3F, y - 1.7F - PhobosGui.getInstance().getTextOffset(), getState() ? 0xFFFFFFFF : 0xFFAAAAAA);
        }
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

    //TODO: CHINESE
    @Override
    public void onKeyTyped(char typedChar, int keyCode)
    {
        if(isListening)
        {
            /*switch (keyCode) {
                case 1:
                    break;
                case 28:
                    enterString();
                    break;
                case 14:
                    setString(removeLastChar(currentString.getString()));
                    break;
                default:
                    if(ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                        setString(currentString.getString() + typedChar);
                    }
            }*/
            if (keyCode == Keyboard.KEY_ESCAPE)
            {
                return;
            }
            else if (keyCode == Keyboard.KEY_RETURN)
            {
                enterString();
            }
            else if (keyCode == Keyboard.KEY_BACK)
            {
                setString(removeLastChar(currentString.getString()));
            }
            else if (keyCode == Keyboard.KEY_V && (Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)))
            {
                try
                {
                    setString(currentString.getString() + Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                if(ChatAllowedCharacters.isAllowedCharacter(typedChar))
                {
                    setString(currentString.getString() + typedChar);
                }
            }
        }
    }

    @Override
    public void update()
    {
        this.setHidden(!Visibilities.VISIBILITY_MANAGER.isVisible(setting));
    }

    private void enterString()
    {
        if(currentString.getString().isEmpty())
        {
            setting.setValue(setting.getInitial());
        }
        else
        {
            setting.setValue(currentString.getString());
        }

        setString("");
        super.onMouseClick();
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

    public void setString(String newString)
    {
        this.currentString = new CurrentString(newString);
    }

    public static String removeLastChar(String str)
    {
        String output = "";
        if (str != null && str.length() > 0)
        {
            output = str.substring(0, str.length() - 1);
        }
        return output;
    }

    public static class CurrentString
    {
        private final String string;

        public CurrentString(String string)
        {
            this.string = string;
        }

        public String getString()
        {
            return this.string;
        }
    }

}
