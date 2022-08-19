package me.earth.earthhack.impl.modules.misc.packetdelay;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.util.helpers.addable.RegisteringModule;
import me.earth.earthhack.impl.util.helpers.addable.setting.SimpleRemovingSetting;
import me.earth.earthhack.impl.util.mcp.MappingProvider;
import me.earth.earthhack.impl.util.network.PacketUtil;
import me.earth.earthhack.impl.util.thread.ThreadUtil;
import net.minecraft.network.Packet;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

// TODO: Allow modules to start "sections" in which
//  the order of all packets will be maintained (will make later)
public class PacketDelay extends RegisteringModule<Boolean, SimpleRemovingSetting>
{

    protected final Setting<Integer> delay =
            register(new NumberSetting<>("Delay", 0, 0, 5000));

    public final Set<Packet<?>> packets = Collections.newSetFromMap(new ConcurrentHashMap<>());
    public final ScheduledExecutorService service = ThreadUtil.newDaemonScheduledExecutor("Packet-Delay");
    public Integer lastDelay = null;

    protected final List<String> packetNames;

    public PacketDelay()
    {
        super("PacketDelay",
                Category.Misc,
                "Add_Packet",
                "packet",
                SimpleRemovingSetting::new,
                s -> "Filter " + s.getName() + " packets.");

        packetNames = PacketUtil.getAllPackets()
                .stream()
                .map(MappingProvider::simpleName)
                .collect(Collectors.toList());
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

    protected void onEnable()
    {
        lastDelay = 0;
    }

    protected void onDisable()
    {
        lastDelay = 0;
    }

    public int getDelay()
    {
        return delay.getValue();
    }

    public boolean isPacketValid(String packet)
    {
        return isValid(packet);
    }

}
