package me.earth.earthhack.impl.managers.chat;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.register.Register;
import me.earth.earthhack.api.register.Registrable;
import me.earth.earthhack.api.register.exception.AlreadyRegisteredException;
import me.earth.earthhack.api.register.exception.CantUnregisterException;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.commands.*;
import me.earth.earthhack.impl.commands.hidden.FailCommand;
import me.earth.earthhack.impl.commands.hidden.HListSettingCommand;
import me.earth.earthhack.impl.commands.hidden.HModulesCommand;
import me.earth.earthhack.impl.commands.hidden.HSettingCommand;
import me.earth.earthhack.impl.commands.packet.PacketCommandImpl;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.network.play.client.CPacketChatMessage;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * {@link net.minecraft.client.gui.GuiChat}
 * {@link me.earth.earthhack.impl.core.mixins.gui.MixinGuiChat}
 */
public class CommandManager extends SubscriberImpl
        implements Globals, Register<Command>
{
    private static final Command MODULE_COMMAND = new ModuleCommand();
    private static final Command FAIL_COMMAND   = new FailCommand();

    private final Set<Command> commands = new LinkedHashSet<>();
    private final Set<Command> hidden   = new LinkedHashSet<>();
    private String concatenated;
    private String lastMessage;

    public CommandManager()
    {
        this.listeners.add(
            new EventListener<PacketEvent.Send<CPacketChatMessage>>
                    (PacketEvent.Send.class, CPacketChatMessage.class)
            {
                @Override
                public void invoke(PacketEvent.Send<CPacketChatMessage> event)
                {
                    if (event.getPacket()
                             .getMessage()
                             .startsWith(Commands.getPrefix()))
                    {
                        applyCommand(event.getPacket().getMessage());
                        if (!event.getPacket()
                                  .getMessage()
                                  .toLowerCase()
                                  .startsWith(Commands.getPrefix() + "last ")
                            && !event.getPacket()
                                     .getMessage()
                                     .equalsIgnoreCase(Commands.getPrefix()
                                                            + "last"))
                        {
                            lastMessage = event.getPacket().getMessage();
                        }

                        event.setCancelled(true);
                    }
                }
            });
    }

    public void init()
    {
        Earthhack.getLogger().info("Initializing Commands.");

        commands.add(new ConfigCommand());
        commands.add(new FontCommand());
        commands.add(new FriendCommand());
        commands.add(new EnemyCommand());
        commands.add(new HelpCommand());
        commands.add(new HexCommand());
        commands.add(new HistoryCommand());
        commands.add(new MacroCommand());
        commands.add(new LastCommand());
        commands.add(new ModuleListCommand());
        commands.add(new PeekCommand());
        commands.add(new PrefixCommand());
        commands.add(new ToggleCommand());
        commands.add(new TimesCommand());
        commands.add(new PluginCommand());
        commands.add(new SayCommand());
        commands.add(new GameModeCommand());
        commands.add(new JavaScriptCommand());
        commands.add(new KitCommand());
        commands.add(new Thirty2kCommand());
        commands.add(new BindCommand());
        commands.add(new ResetCommand());
        commands.add(new PbModuleCommand());
        commands.add(new PbSyncCommand());
        commands.add(new PrintCommand());
        commands.add(new ProxyCommand());
        commands.add(new QuitCommand());
        commands.add(new ConnectCommand());
        commands.add(new DisconnectCommand());
        commands.add(new VClipCommand());
        commands.add(new HClipCommand());
        commands.add(new GiveCommand());
        commands.add(new DumpStackCommand());
        commands.add(new EnchantCommand());
        commands.add(new ShrugCommand());
        commands.add(new EntityDesyncCommand());
        commands.add(new SoundCommand());
        commands.add(new FolderCommand());
        commands.add(new PacketCommandImpl());
        commands.add(new PresetCommand());
        commands.add(new BookCommand());
        commands.add(new ReloadResourceCommand());

        hidden.add(new HListSettingCommand());
        hidden.add(new HModulesCommand());
        hidden.add(new HSettingCommand());

        setupAndConcatenate();
    }

    @Override
    public void register(Command command) throws AlreadyRegisteredException
    {
        if (command.isHidden())
        {
            hidden.add(command);
        }
        else
        {
            commands.add(command);
        }

        if (command instanceof Registrable)
        {
            ((Registrable) command).onRegister();
        }

        setupAndConcatenate();
    }

    @Override
    public void unregister(Command command) throws CantUnregisterException
    {
        if (command instanceof Registrable)
        {
            ((Registrable) command).onUnRegister();
        }

        hidden.remove(command);
        commands.remove(command);
        setupAndConcatenate();
    }

    @Override
    public Command getObject(String name)
    {
        Command command = CommandUtil.getNameableStartingWith(name, commands);
        if (command == null || !command.getName().equalsIgnoreCase(name))
        {
            command = CommandUtil.getNameableStartingWith(name, hidden);
            if (command != null && !command.getName().equalsIgnoreCase(name))
            {
                return null;
            }
        }

        return command;
    }

    @Override
    public <C extends Command> C getByClass(Class<C> clazz)
    {
        C command = CollectionUtil.getByClass(clazz, commands);
        if (command == null)
        {
            command = CollectionUtil.getByClass(clazz, hidden);
        }

        return command;
    }

    @Override
    public Collection<Command> getRegistered()
    {
        return commands;
    }

    public String getLastCommand()
    {
        return lastMessage;
    }

    /**
     * Ensures that the last command is always
     * the {@link ModuleCommand} and reConcatenates
     * for {@link CommandManager#getConcatenatedCommands()}.
     */
    private void setupAndConcatenate()
    {
        commands.remove(MODULE_COMMAND);
        commands.add(MODULE_COMMAND);
        concatenated = concatenateCommands();
    }

    public void renderCommandGui(String message, int x, int y)
    {
        if (message != null
                && message.startsWith(Commands.getPrefix()))
        {
            String[] array = createArray(message);
            String possible = getCommandForMessage(array)
                                    .getPossibleInputs(array)
                                    .getFullText();

            int width = x + mc.fontRenderer.getStringWidth(message.trim());
            mc.fontRenderer.drawString(possible, width, y, 0xffffffff, true);
        }
    }

    public boolean onTabComplete(GuiTextField inputField)
    {
        if (inputField.getText().startsWith(Commands.getPrefix()))
        {
            String[] array = createArray(inputField.getText());
            Completer completer = getCommandForMessage(array)
                    .onTabComplete(new Completer(inputField.getText(), array));

            inputField.setText(completer.getResult());
            return completer.shouldMcComplete();
        }

        return true;
    }

    // TODO: WHY REQUIRE PREFIX HERE IM SO RETARDED WTF
    public void applyCommand(String message)
    {
        if (message != null && message.length() > 1)
        {
            applyCommandNoPrefix(removePrefix(message));
        }
    }

    public void applyCommandNoPrefix(String message)
    {
        if (message != null && message.length() > 1)
        {
            // String[] commandSplit = message.split(";"); TODO this
            // for (String s : commandSplit)
            String[] array = createArrayNoPrefix(message);
            executeArgs(array);
        }
    }

    public void executeArgs(String... args)
    {
        Command command = getCommandForMessage(args);
        if (command.equals(FAIL_COMMAND))
        {
            command = getHiddenCommand(args);
        }

        command.execute(args);
    }

    public String getConcatenatedCommands()
    {
        return concatenated;
    }

    public Command getCommandForMessage(String[] array)
    {
        if (array == null || array.length == 0)
        {
            return FAIL_COMMAND;
        }

        for (Command command : commands)
        {
            if (command.fits(array))
            {
                return command;
            }
        }

        return FAIL_COMMAND;
    }

    public String[] createArray(String message)
    {
        String noPrefix = removePrefix(message);
        return CommandUtil.toArgs(noPrefix);
    }

    public String removePrefix(String message) {
        return message.substring(Commands.getPrefix().length());
    }

    public String[] createArrayNoPrefix(String message)
    {
        return CommandUtil.toArgs(message);
    }

    private Command getHiddenCommand(String[] array)
    {
        for (Command command : hidden)
        {
            if (command.fits(array))
            {
                return command;
            }
        }

        return FAIL_COMMAND;
    }

    private String concatenateCommands()
    {
        StringBuilder builder = new StringBuilder();

        Iterator<Command> itr = commands.iterator();
        while (itr.hasNext())
        {
            builder.append(itr.next().getName().toLowerCase());
            if (itr.hasNext())
            {
                builder.append(", ");
            }
        }

        return builder.toString();
    }

}
