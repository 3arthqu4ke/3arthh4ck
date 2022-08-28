package me.earth.earthhack.impl.gui.click.component.impl;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.util.EnumHelper;
import me.earth.earthhack.impl.gui.click.component.SettingComponent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.RenderUtil;

public class EnumComponent<E extends Enum<E>> extends SettingComponent<E, EnumSetting<E>> {
    private final EnumSetting<E> enumSetting;

    public EnumComponent(EnumSetting<E> enumSetting, float posX, float posY, float offsetX, float offsetY, float width, float height) {
        super(enumSetting.getName(), posX, posY, offsetX, offsetY, width, height, enumSetting);
        this.enumSetting = enumSetting;
    }

    @Override
    public void moved(float posX, float posY) {
        super.moved(posX, posY);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        Managers.TEXT.drawStringWithShadow(getLabel() + ": " +ChatFormatting.GRAY + getEnumSetting().getValue().name(), getFinishedX() + 5, getFinishedY() + getHeight() / 2 - (Managers.TEXT.getStringHeightI() >> 1), 0xFFFFFFFF);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        final boolean hovered = RenderUtil.mouseWithinBounds(mouseX, mouseY, getFinishedX() + 5, getFinishedY() + 1, getWidth() - 10, getHeight() - 2);
        if (hovered) {
            if (mouseButton == 0) {
                getEnumSetting().setValue((E) EnumHelper.next(getEnumSetting().getValue()));
            } else if (mouseButton == 1) {
                getEnumSetting().setValue((E) EnumHelper.previous(getEnumSetting().getValue()));

            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    public EnumSetting<E> getEnumSetting() {
        return enumSetting;
    }

}
