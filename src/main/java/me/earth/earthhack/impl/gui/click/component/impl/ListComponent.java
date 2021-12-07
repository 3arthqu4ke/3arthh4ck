package me.earth.earthhack.impl.gui.click.component.impl;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.ListSetting;
import me.earth.earthhack.api.util.EnumHelper;
import me.earth.earthhack.api.util.interfaces.Nameable;
import me.earth.earthhack.impl.gui.click.component.Component;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.Minecraft;

public class ListComponent extends Component
{

    private final ListSetting setting;

    public ListComponent(ListSetting setting, float posX, float posY, float offsetX, float offsetY, float width, float height)
    {
        super(setting.getName(), posX, posY, offsetX, offsetY, width, height);
        this.setting = setting;
    }

    @Override
    public void moved(float posX, float posY) {
        super.moved(posX, posY);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(getLabel() + ": " + ChatFormatting.GRAY + ((Nameable) getListSetting().getValue()).getName(), getFinishedX() + 5, getFinishedY() + getHeight() / 2 - (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT >> 1), 0xFFFFFFFF);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        final boolean hovered = RenderUtil.mouseWithinBounds(mouseX, mouseY, getFinishedX() + 5, getFinishedY() + 1, getWidth() - 10, getHeight() - 2);
        if (hovered) {
            int index = getListSetting().getValues().indexOf(getListSetting().getValue());
            if (index == -1)
            {
                if (!getListSetting().getValues().isEmpty())
                {
                    index = 0;
                }
                else
                {
                    return;
                }
            }
            if (mouseButton == 0) {
                index++;
                if (index >= getListSetting().getValues().size()) index = 0;
                getListSetting().setValue(getListSetting().getValues().get(index));
            } else if (mouseButton == 1) {
                index--;
                if (index < 0) index = getListSetting().getValues().size() - 1;
                getListSetting().setValue(getListSetting().getValues().get(index));
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    public ListSetting getListSetting() {
        return setting;
    }

}
