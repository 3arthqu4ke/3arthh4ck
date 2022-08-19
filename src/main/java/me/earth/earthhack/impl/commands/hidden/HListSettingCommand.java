package me.earth.earthhack.impl.commands.hidden;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.util.CommandScheduler;
import me.earth.earthhack.impl.gui.chat.factory.ComponentFactory;
import me.earth.earthhack.impl.gui.chat.util.ChatComponentUtil;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.modules.client.settings.SettingsModule;
import me.earth.earthhack.impl.util.text.ChatIDs;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.HoverEvent;

//TODO: pages when too many settings (With +- component)?
//TODO: maybe a ChatGui Handler, to update ChatLines etc?
public class HListSettingCommand extends Command
        implements Globals, CommandScheduler
{
    public HListSettingCommand()
    {
        super(new String[][]{{"hiddenlistsetting"}, {"module"}}, true);
    }

    @Override
    public void execute(String[] args)
    {
        if (args.length > 1)
        {
            Module module = Managers.MODULES.getObject(args[1]);
            if (module != null)
            {
                sendSettings(module);
            }
        }
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args)
    {
        return PossibleInputs.empty();
    }

    @Override
    public Completer onTabComplete(Completer completer)
    {
        completer.setMcComplete(true);
        return completer;
    }

    //TODO: find out how much we gotta scroll?
    private static void sendSettings(Module module)
    {
        Managers.CHAT.sendDeleteMessage(" ",
                                        module.getName() + "1",
                                        ChatIDs.CHAT_GUI);

        Managers.CHAT.sendDeleteComponent(
                new TextComponentString(module.getName()
                                        + " : "
                                        + TextColor.GRAY
                                        + module.getCategory().toString())
                        .setStyle(new Style().setHoverEvent(
                                ChatComponentUtil.setOffset(
                                    new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        new TextComponentString(
                                        module.getData().getDescription()))))),
                module.getName() + "2",
                ChatIDs.CHAT_GUI);

        for (Setting<?> setting : module.getSettings())
        {
            if (SettingsModule.shouldDisplay(setting))
            {
                ITextComponent component = ComponentFactory.create(setting);
                Managers.CHAT.sendDeleteComponent(
                    component,
                    setting.getName()
                        + module.getName(),
                    ChatIDs.CHAT_GUI);
            }
        }

        Managers.CHAT.sendDeleteMessage(" ",
                                        module.getName() + "3",
                                        ChatIDs.CHAT_GUI);

        Scheduler.getInstance().schedule(() ->
                mc.displayGuiScreen(new GuiChat()));

        SCHEDULER.submit(() -> mc.addScheduledTask(() ->
        {
            if (mc.ingameGUI != null)
            {
                mc.ingameGUI.getChatGUI().scroll(1);
            }
        }), 100);
    }

    public static String create(Module module)
    {
        return Commands.getPrefix()
                + "hiddenlistsetting "
                + module.getName();
    }

}
