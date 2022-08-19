package me.earth.earthhack.impl.util.text;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.pingbypass.PingBypass;
import me.earth.earthhack.pingbypass.protocol.s2c.S2CChatPacket;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.util.Random;
import java.util.function.Consumer;

public class ChatUtil implements Globals
{
    private static final Random RND = new Random();

    public static void sendMessage(String message)
    {
        sendMessage(message, 0);
    }

    public static void sendMessage(String message, int id)
    {
        sendComponent(new TextComponentString(
                message == null ? "null" : message), id);
    }

    public static void deleteMessage(int id)
    {
        applyIfPresent(g -> g.deleteChatLine(id));
    }

    public static void sendComponent(ITextComponent component)
    {
        sendComponent(component, 0);
    }

    public static void sendComponent(ITextComponent c, int id)
    {
        applyIfPresent(g -> {
            if (PingBypass.isServer()) {
                TextComponentString string = new TextComponentString("<" + TextColor.DARK_RED + "PingBypass" + TextColor.WHITE + "> ");
                string.appendSibling(c);
                PingBypass.sendPacket(new S2CChatPacket(string, ChatType.SYSTEM, id));
            }

            g.printChatMessageWithOptionalDeletion(c, id);
        });
    }

    public static void applyIfPresent(Consumer<GuiNewChat> consumer)
    {
        GuiNewChat chat = getChatGui();
        if (chat != null)
        {
            consumer.accept(chat);
        }
    }

    public static GuiNewChat getChatGui()
    {
        if (mc.ingameGUI != null) // Really unnecessary tbh
        {
            return mc.ingameGUI.getChatGUI();
        }

        return null;
    }

    public static void sendMessageScheduled(String message)
    {
        mc.addScheduledTask(() -> sendMessage(message));
    }

    public static String generateRandomHexSuffix(int places)
    {
        return "[" + Integer.toHexString((RND.nextInt() + 11)
                * RND.nextInt()).substring(0, places) + "]";
    }

}
