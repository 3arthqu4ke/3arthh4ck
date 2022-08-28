package me.earth.earthhack.impl.gui.click.component.impl;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.earth.earthhack.api.setting.settings.ListSetting;
import me.earth.earthhack.api.util.interfaces.Nameable;
import me.earth.earthhack.impl.gui.click.component.SettingComponent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.RenderUtil;

public class ListComponent<M extends Nameable> extends SettingComponent<M, ListSetting<M>>
{

    private final ListSetting<M> setting;

    public ListComponent(ListSetting<M> setting, float posX, float posY, float offsetX, float offsetY, float width, float height)
    {
        super(setting.getName(), posX, posY, offsetX, offsetY, width, height, setting);
        this.setting = setting;
    }

    @Override
    public void moved(float posX, float posY) {
        super.moved(posX, posY);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        Managers.TEXT.drawStringWithShadow(getLabel() + ": " + ChatFormatting.GRAY + getListSetting().getValue().getName(), getFinishedX() + 5, getFinishedY() + getHeight() / 2 - (Managers.TEXT.getStringHeightI() >> 1), 0xFFFFFFFF);
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

    public ListSetting<M> getListSetting() {
        return setting;
    }

}
