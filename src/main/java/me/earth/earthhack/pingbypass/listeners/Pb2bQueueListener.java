package me.earth.earthhack.pingbypass.listeners;

import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.listeners.ReceiveListener;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.play.server.SPacketPlayerListHeaderFooter;
import net.minecraft.util.text.ITextComponent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Pb2bQueueListener extends SubscriberImpl implements Globals {
    private static final Pattern REGEX =
        Pattern.compile("Position in queue: ([0-9]+)");

    private int position = -1;

    public Pb2bQueueListener() {
        this.listeners.add(new ReceiveListener<>(SPacketPlayerListHeaderFooter.class, e -> {
            if (isOn2b2t()) {
                SPacketPlayerListHeaderFooter p = e.getPacket();
                ITextComponent header = p.getHeader();
                //noinspection ConstantConditions
                if (header != null) {
                    Matcher matcher = REGEX.matcher(header.getUnformattedText());
                    if (matcher.find()) {
                        String group = matcher.group(1);
                        try {
                            position = Integer.parseInt(group);
                            return;
                        } catch (NumberFormatException ignored) {

                        }
                    }
                }
            }

            position = -1;
        }));
    }

    public int getPosition() {
        return position;
    }

    public boolean isOn2b2t() {
        ServerData data = mc.getCurrentServerData();
        return data != null && ("2b2t.org".equalsIgnoreCase(data.serverIP) || "connect.2b2t.org.".equalsIgnoreCase(data.serverIP));
    }

}
