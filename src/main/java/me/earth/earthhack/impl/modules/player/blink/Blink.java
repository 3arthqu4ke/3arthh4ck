package me.earth.earthhack.impl.modules.player.blink;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.modules.player.blink.mode.PacketMode;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.helpers.disabling.DisablingModule;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;

import java.util.LinkedList;
import java.util.Queue;

//TODO: mode pulse
//TODO: auto disable stuff
public class Blink extends DisablingModule
{
    protected final Setting<PacketMode> packetMode =
            register(new EnumSetting<>("Packets", PacketMode.CPacketPlayer));
    protected final Setting<Boolean> lagDisable    =
            register(new BooleanSetting("LagDisable", false));

    protected final Queue<Packet<?>> packets = new LinkedList<>();
    protected EntityOtherPlayerMP fakePlayer;
    protected boolean shouldSend;

    public Blink()
    {
        super("Blink", Category.Player);
        this.listeners.add(new ListenerPosLook(this));
        this.listeners.add(new ListenerPacket(this));
        SimpleData data = new SimpleData(this,
            "Suppresses all movement packets you send to the server. It will look" +
            " like you don't move at all and then teleport when" +
            " you disable this module.");
        data.register(packetMode,
            "-All cancels all packets. Will cause packet spam." +
            "\n-CPacketPlayer only cancels movement packets." +
            "\nFiltered leaves some packets through, still spammy.");
        data.register(lagDisable,
                "Disable this module when the server lags you back.");
        this.setData(data);
    }

    @Override
    protected void onEnable()
    {
        if (mc.player == null)
        {
            this.disable();
            return;
        }

        fakePlayer = PlayerUtil
                .createFakePlayerAndAddToWorld(mc.player.getGameProfile());
    }

    @Override
    protected void onDisable()
    {
        PlayerUtil.removeFakePlayer(fakePlayer);

        if (shouldSend && mc.getConnection() != null)
        {
            CollectionUtil.emptyQueue(packets, p -> mc.getConnection()
                                                      .sendPacket(p));
        }
        else
        {
            packets.clear();
        }

        shouldSend = true;
    }

    @Override
    public void onShutDown()
    {
        shouldSend = false;
        super.onShutDown();
    }

    @Override
    public void onDeath()
    {
        shouldSend = false;
        super.onShutDown();
    }

    @Override
    public void onDisconnect()
    {
        shouldSend = false;
        super.onShutDown();
    }

}
