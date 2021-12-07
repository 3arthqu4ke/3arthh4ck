package me.earth.earthhack.impl.modules.client.tab;

import me.earth.earthhack.impl.commands.gui.ExitButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

public class GuiScreenTab extends GuiScreen
{
    private final TabModule module;

    public GuiScreenTab(TabModule module)
    {
        this.module = module;
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return module.pause.getValue();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (keyCode == module.getBind().getKey())
        {
            module.disable();
            return;
        }

        super.keyTyped(typedChar, keyCode);
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public void setWorldAndResolution(Minecraft mc, int width, int height)
    {
        super.setWorldAndResolution(mc, width, height);
        this.buttonList.clear();
        this.buttonList.add(new ExitButton(0, this.width - 24, 5));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (mc.world == null)
        {
            this.drawDefaultBackground();
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.id == 0)
        {
            module.disable();
        }
    }

}
