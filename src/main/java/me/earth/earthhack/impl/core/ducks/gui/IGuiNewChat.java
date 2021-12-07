package me.earth.earthhack.impl.core.ducks.gui;

import net.minecraft.util.text.ITextComponent;

/**
 * Duck interface for {@link net.minecraft.client.gui.GuiNewChat}.
 */
public interface IGuiNewChat
{
    boolean replace(ITextComponent component,
                    int id,
                    boolean wrap,
                    boolean returnFirst);

    int getScrollPos();

    void setScrollPos(int pos);

    boolean getScrolled();

    void setScrolled(boolean scrolled);

    void invokeSetChatLine(ITextComponent chatComponent,
                           int chatLineId,
                           int updateCounter,
                           boolean displayOnly);

    void invokeClearChat(boolean sent);

}
