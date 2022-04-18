package me.earth.earthhack.impl.modules.player.nohunger;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.SimpleData;
import net.minecraft.network.play.client.CPacketEntityAction;

public class NoHunger extends Module
{
    protected final Setting<Boolean> sprint =
            register(new BooleanSetting("Sprint", true));
    protected final Setting<Boolean> ground =
            register(new BooleanSetting("Ground", true));

    boolean onGround;

    public NoHunger()
    {
        super("NoHunger", Category.Player);
        this.listeners.add(new ListenerEntityAction(this));
        this.listeners.addAll(new ListenerPlayerPacket(this).getListeners());
        SimpleData data = new SimpleData(this, "Makes you not get hungry.");
        data.register(sprint, "Will cancel sprint packets you send to the server.");
        data.register(ground, "Will make the server think you are not on the" +
                " ground, which makes it no apply hunger to you. Will build" +
                " up falldamage over time so watch out.");
        this.setData(data);
    }

    @Override
    protected void onEnable()
    {
        if (sprint.getValue() && mc.player != null)
        {
            mc.player.connection.sendPacket(
                    new CPacketEntityAction(mc.player,
                            CPacketEntityAction.Action.STOP_SPRINTING));
        }
    }

    @Override
    protected void onDisable()
    {
        if (sprint.getValue()
                && mc.player != null
                && !Managers.ACTION.isSprinting()
                &&  mc.player.isSprinting())
        {
            mc.player.connection.sendPacket(
                    new CPacketEntityAction(mc.player,
                            CPacketEntityAction.Action.START_SPRINTING));
        }
    }

}
