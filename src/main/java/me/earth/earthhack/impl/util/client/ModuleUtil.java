package me.earth.earthhack.impl.util.client;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Hidden;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.chat.ChatManager;
import me.earth.earthhack.impl.util.text.ChatIDs;
import me.earth.earthhack.impl.util.text.TextColor;

public class ModuleUtil
{
    public static String getHudName(Module module)
    {
        return module.getDisplayName()
                + (module.getDisplayInfo() == null
                    || module.isHidden() == Hidden.Info
                        ? ""
                        : TextColor.GRAY
                            + " [" + TextColor.WHITE
                            + module.getDisplayInfo()
                            + TextColor.GRAY + "]");
    }

    public static void disableRed(Module module, String message)
    {
        disable(module, TextColor.RED + message);
    }

    /**
     * Disables the given module and sends a chatmessage after.
     * The chatmessage will be colored red by default and a
     * <module.getDisplayName()> will be at the start.
     *
     * @param module the module to disable.
     * @param message the message to send afterwards.
     */
    public static void disable(Module module, String message)
    {
        module.disable();
        sendMessage(module, message);
    }

    /**
     * Calls {@link ChatManager#sendDeleteMessage(String, String, int)}
     * for the "<module.getDisplayName()> " + the message, the modules name,
     * and {@link ChatIDs#MODULE}
     *
     * @param module the module to send a message for.
     * @param message the message to send.
     */
    public static void sendMessage(Module module, String message)
    {
        sendMessage(module, message, "");
    }

    /**
     * Calls {@link ChatManager#sendDeleteMessage(String, String, int)}
     * for the "<module.getDisplayName()> " + the message, the modules name,
     * and {@link ChatIDs#MODULE}, the append String will be appended to the
     * unique word.
     *
     * @param module the module to send a message for.
     * @param message the message to send.
     * @param append gets appended to the unique word.
     */
    public static void sendMessage(Module module, String message, String append)
    {
        Managers.CHAT.sendDeleteMessage(
                "<" + module.getDisplayName() + "> " + message,
                module.getName() + append,
                ChatIDs.MODULE);
    }

    public static void sendMessageWithAquaModule(Module module, String message, String append)
    {
        Managers.CHAT.sendDeleteMessage(
            "<" + TextColor.AQUA + module.getDisplayName() + TextColor.RESET + "> " + message,
            module.getName() + append,
            ChatIDs.MODULE);
    }

}
