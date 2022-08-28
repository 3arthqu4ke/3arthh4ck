package me.earth.earthhack.impl.gui.click.component.impl;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.gui.click.component.SettingComponent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.earthhack.impl.util.render.RenderUtil;
import me.earth.earthhack.pingbypass.input.Keyboard;

public class KeybindComponent extends SettingComponent<Bind, BindSetting> {
    private final BindSetting bindSetting;
    private boolean binding;

    public KeybindComponent(BindSetting bindSetting, float posX, float posY, float offsetX, float offsetY, float width, float height) {
        super(bindSetting.getName(), posX, posY, offsetX, offsetY, width, height, bindSetting);
        this.bindSetting = bindSetting;
    }

    @Override
    public void moved(float posX, float posY) {
        super.moved(posX, posY);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        final boolean hovered = RenderUtil.mouseWithinBounds(mouseX, mouseY, getFinishedX() + 5, getFinishedY() + 1, getWidth() - 10, getHeight() - 2);
        Render2DUtil.drawBorderedRect(getFinishedX() + 4.5f, getFinishedY() + 1.0f, getFinishedX() + getWidth() - 4.5f, getFinishedY() + getHeight() - 0.5f, 0.5f, hovered ? 0x66333333:0, 0xff000000);
        Managers.TEXT.drawStringWithShadow(isBinding() ? "Press a key..." : getBindSetting().getName() + ": " + ChatFormatting.GRAY+ getBindSetting().getValue(), getFinishedX() + 6.5f, getFinishedY() + getHeight() - Managers.TEXT.getStringHeightI() - 1f, 0xFFFFFFFF);
    }

    @Override
    public void keyTyped(char character, int keyCode) {
        super.keyTyped(character, keyCode);
        if (isBinding()) {
            final Bind bind = Bind.fromKey(keyCode == Keyboard.getEscape() || keyCode == Keyboard.getSpace() || keyCode == Keyboard.getDelete() ? Keyboard.getNone() : keyCode);
            getBindSetting().setValue(bind);
            setBinding(false);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        final boolean hovered = RenderUtil.mouseWithinBounds(mouseX, mouseY, getFinishedX() + 5, getFinishedY() + 1, getWidth() - 10, getHeight() - 2);
        if (hovered && mouseButton == 0)
            setBinding(!isBinding());
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    public BindSetting getBindSetting() {
        return bindSetting;
    }

    public boolean isBinding() {
        return binding;
    }

    public void setBinding(boolean binding) {
        this.binding = binding;
    }
}
