package me.earth.earthhack.impl.modules.misc.logger;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.modules.misc.logger.util.LoggerMode;
import me.earth.earthhack.impl.util.helpers.addable.RegisteringModule;
import me.earth.earthhack.impl.util.helpers.addable.setting.SimpleRemovingSetting;
import me.earth.earthhack.impl.util.mcp.MappingProvider;
import me.earth.earthhack.impl.util.network.PacketUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.network.Packet;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.Collectors;

public class Logger extends RegisteringModule<Boolean, SimpleRemovingSetting>
{
    protected final Setting<LoggerMode> mode =
            register(new EnumSetting<>("Mode", LoggerMode.Normal));
    protected final Setting<Boolean> incoming =
            register(new BooleanSetting("Incoming", true));
    protected final Setting<Boolean> outgoing =
            register(new BooleanSetting("Outgoing", true));
    protected final Setting<Boolean> pbCustom =
            register(new BooleanSetting("PB-Custom", true));
    protected final Setting<Boolean> c2Pb =
            register(new BooleanSetting("Client-2-PB", true));
    protected final Setting<Boolean> pb2C =
            register(new BooleanSetting("PB-2-Client", false));
    protected final Setting<Boolean> info =
            register(new BooleanSetting("Info", true));
    protected final Setting<Boolean> chat =
            register(new BooleanSetting("Chat", false));
    protected final Setting<Boolean> deobfuscate =
            register(new BooleanSetting("Deobfuscate", true));
    protected final Setting<Boolean> stackTrace =
            register(new BooleanSetting("StackTrace", false));
    protected final Setting<Boolean> statics =
            register(new BooleanSetting("Static", false));
    protected final Setting<Boolean> delay =
            register(new BooleanSetting("Delay", true));

    protected final Setting<Boolean> filter =
            registerBefore(new BooleanSetting("Filter", false), listType);
    protected final List<String> packetNames;
    protected long lastTimeOut;
    protected long lastTimeIn;
    protected boolean cancel;

    public Logger()
    {
        super("Logger",
                Category.Misc,
                "Add_Packet",
                "packet",
                SimpleRemovingSetting::new,
                s -> "Filter " + s.getName() + " packets.");

        packetNames = PacketUtil.getAllPackets()
                .stream()
                .map(MappingProvider::simpleName)
                .collect(Collectors.toList());
        this.listeners.add(new ListenerChatLog(this));
        this.listeners.add(new ListenerReceive(this));
        this.listeners.add(new ListenerSend(this));
        this.listeners.add(new ListenerCustomPbPacket(this));
        this.listeners.add(new ListenerPbReceive(this));
        this.listeners.add(new ListenerPb2Client(this));
        this.setData(new LoggerData(this));
    }

    @Override
    protected void onEnable()
    {
        cancel = false;
    }

    @Override
    public String getInput(String input, boolean add)
    {
        if (add)
        {
            String packet = getPacketStartingWith(input);
            if (packet != null)
            {
                return TextUtil.substring(packet, input.length());
            }

            return "";
        }

        return super.getInput(input, false);
    }

    private String getPacketStartingWith(String input)
    {
        for (String packet : packetNames)
        {
            if (TextUtil.startsWith(packet, input))
            {
                return packet;
            }
        }

        return null;
    }

    public void logPacket(Packet<?> packet, String message, boolean cancelled, boolean out)
    {
        logPacket(packet, message, cancelled, out, true);
    }

    public void logPacket(Packet<?> packet, String message, boolean cancelled, boolean out, boolean allowChat)
    {
        String simpleName = MappingProvider.simpleName(packet.getClass());
        if (filter.getValue() && !isValid(simpleName))
        {
            return;
        }

        StringBuilder outPut = new StringBuilder(message)
                .append(simpleName)
                .append(", cancelled : ")
                .append(cancelled);

        appendDelay(outPut, out);
        outPut.append("\n");
        appendInfo(outPut, packet);

        String s = outPut.toString();
        printChat(s, allowChat);
        Earthhack.getLogger().info(s);

        if (stackTrace.getValue())
        {
            Thread.dumpStack();
        }
    }

    private void appendDelay(StringBuilder outPut, boolean out)
    {
        if (delay.getValue())
        {
            long difference;
            long currentTime = System.currentTimeMillis();
            if (out)
            {
                difference = currentTime - lastTimeOut;
                lastTimeOut = currentTime;
            }
            else
            {
                difference = currentTime - lastTimeIn;
                lastTimeIn = currentTime;
            }

            outPut.append(", last : ")
                  .append(difference)
                  .append("ms");
        }
    }

    private void appendInfo(StringBuilder outPut, Packet<?> packet)
    {
        if (info.getValue())
        {
            try
            {
                Class<?> clazz = packet.getClass();
                while (clazz != Object.class)
                {
                    for (Field field : clazz.getDeclaredFields())
                    {
                        if (field != null)
                        {
                            if (Modifier.isStatic(field.getModifiers())
                                && !statics.getValue())
                            {
                                continue;
                            }

                            field.setAccessible(true);
                            Object obj = field.get(packet);
                            String objToString;
                            if (obj != null && obj.getClass().isArray()) {
                                StringBuilder builder = new StringBuilder("[");
                                for (int i = 0; i < Array.getLength(obj); i++) {
                                    builder.append(Array.get(obj, i));
                                    if (i < Array.getLength(obj) - 1) {
                                        builder.append(", ");
                                    }
                                }

                                objToString = builder.append("]").toString();
                            } else {
                                objToString = String.valueOf(obj);
                            }

                            outPut.append("     ")
                                  .append(getName(clazz, field))
                                  .append(" : ")
                                  .append(objToString)
                                  .append("\n");
                        }
                    }

                    clazz = clazz.getSuperclass();
                }
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void printChat(String message, boolean allowChat)
    {
        if (chat.getValue() && allowChat)
        {
            mc.addScheduledTask(() ->
            {
                cancel = true;
                try
                {
                    ChatUtil.sendMessage(message);
                }
                finally
                {
                    cancel = false;
                }
            });
        }
    }

    private String getName(Class<?> c, Field field)
    {
        if (deobfuscate.getValue())
        {
            String name = MappingProvider.field(c, field.getName());
            if (name != null)
            {
                return name;
            }
        }

        return field.getName();
    }

    public LoggerMode getMode()
    {
        return mode.getValue();
    }

}
