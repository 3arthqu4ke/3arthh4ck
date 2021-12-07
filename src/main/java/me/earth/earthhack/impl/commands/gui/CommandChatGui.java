package me.earth.earthhack.impl.commands.gui;

import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import net.minecraft.client.gui.GuiChat;

import java.io.IOException;

public class CommandChatGui extends GuiChat
{
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (keyCode == 1)
        {
            return;
        }
        else if (keyCode == 28 || keyCode == 156)
        {
            String s = this.inputField.getText().trim();

            if (!s.isEmpty())
            {
                this.sendChatMessage(s);
            }

            return;
        }

        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (!this.inputField.getText().startsWith(Commands.getPrefix()))
        {
            this.inputField.setText(Commands.getPrefix()
                                        + inputField.getText());
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public void sendChatMessage(String msg, boolean addToChat)
    {
        this.mc.ingameGUI.getChatGUI().addToSentMessages(msg);
        this.setText(Commands.getPrefix());
        Managers.COMMANDS.applyCommand(msg);
    }

    public void setText(String text)
    {
        this.inputField.setText(text);
    }
}
