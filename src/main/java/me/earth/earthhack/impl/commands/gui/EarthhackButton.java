package me.earth.earthhack.impl.commands.gui;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.gui.buttons.SimpleButton;
import net.minecraft.client.gui.GuiScreen;

public class EarthhackButton extends SimpleButton implements Globals
{
    public EarthhackButton(int buttonID, int xPos, int yPos)
    {
        super(buttonID, xPos, yPos, 0, 40, 0, 60);
    }

    @Override
    public void onClick(GuiScreen parent, int id)
    {
        mc.displayGuiScreen(new CommandGui(parent, id));
    }

}
