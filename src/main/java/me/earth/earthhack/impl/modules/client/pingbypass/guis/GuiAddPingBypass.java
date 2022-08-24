package me.earth.earthhack.impl.modules.client.pingbypass.guis;

import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.pingbypass.input.Keyboard;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import java.io.IOException;

/**
 * Gui for configuring PingBypass Port and IP,
 * Code mostly from GuiAddServer.
 */
public class GuiAddPingBypass extends GuiScreen
{
    private static final SettingCache<String, StringSetting, PingBypassModule> IP =
     Caches.getSetting(PingBypassModule.class, StringSetting.class, "IP", "Proxy-IP");
    private static final SettingCache<String, StringSetting, PingBypassModule> PORT =
     Caches.getSetting(PingBypassModule.class, StringSetting.class, "Port", "0");
    private static final SettingCache<String, StringSetting, PingBypassModule> PASSWORD =
     Caches.getSetting(PingBypassModule.class, StringSetting.class, "Password", "");

    private final GuiScreen parentScreen;
    private GuiTextField serverPortField;
    private GuiTextField serverIPField;
    private GuiPasswordField passwordField;

    public GuiAddPingBypass(GuiScreen parentScreenIn)
    {
        this.parentScreen = parentScreenIn;
    }

    @Override
    public void updateScreen()
    {
        this.serverIPField.updateCursorCounter();
        this.serverPortField.updateCursorCounter();
        this.passwordField.updateCursorCounter();
    }

    @Override
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100,
                                this.height / 4 + 96 + 18, "Done"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100,
                                this.height / 4 + 120 + 18, "Cancel"));
        this.serverIPField = new GuiTextField(0, this.fontRenderer,
                                this.width / 2 - 100, 66, 200, 20);

        this.serverIPField.setFocused(true);
        this.serverIPField.setText(IP.getValue());
        this.serverPortField = new GuiTextField(1, this.fontRenderer,
                                            this.width / 2 - 100, 106, 200, 20);

        this.serverPortField.setMaxStringLength(128);
        this.serverPortField.setText(PORT.getValue());
        this.passwordField = new GuiPasswordField(2, this.fontRenderer, this.width / 2 - 100, 146, 200, 20);
        this.passwordField.setText(PASSWORD.getValue());
    }

    @Override
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.enabled)
        {
            if (button.id == 1)
            {
                mc.displayGuiScreen(parentScreen);
            }
            else if (button.id == 0)
            {
                IP.computeIfPresent(s ->
                        s.setValue(this.serverIPField.getText()));
                PORT.computeIfPresent(s ->
                        s.setValue(this.serverPortField.getText()));
                PASSWORD.computeIfPresent(s ->
                        s.setValue(this.passwordField.getText()));

                mc.displayGuiScreen(parentScreen);
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode)
    {
        this.serverIPField.textboxKeyTyped(typedChar, keyCode);
        this.serverPortField.textboxKeyTyped(typedChar, keyCode);
        this.passwordField.textboxKeyTyped(typedChar, keyCode);

        if (keyCode == 15)
        {
            this.serverIPField.setFocused(!this.serverIPField.isFocused());
            this.serverPortField.setFocused(!this.serverPortField.isFocused());
            this.passwordField.setFocused(!this.passwordField.isFocused());
        }

        if (keyCode == 28 || keyCode == 156)
        {
            this.actionPerformed(this.buttonList.get(0));
        }

        if (keyCode == 1)
        {
            mc.displayGuiScreen(parentScreen);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
            throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.serverPortField.mouseClicked(mouseX, mouseY, mouseButton);
        this.serverIPField.mouseClicked(mouseX, mouseY, mouseButton);
        this.passwordField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, "Edit PingBypass",
                this.width / 2, 17, 16777215);
        this.drawString(this.fontRenderer, "Proxy-IP",
                this.width / 2 - 100, 53, 10526880);
        this.drawString(this.fontRenderer, "Proxy-Port",
                this.width / 2 - 100, 94, 10526880);
        this.serverIPField.drawTextBox();
        this.serverPortField.drawTextBox();
        this.passwordField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

}
