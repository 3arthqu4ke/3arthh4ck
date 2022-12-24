package me.earth.earthhack.impl.modules.player.freecam;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.player.freecam.mode.CamMode;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.helpers.disabling.DisablingModule;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;

//TODO: Make all BlockPlacing/Rotating modules work with this!
//TODO: add falling and stuff to fakeplayer like in the 1.8 client
public class Freecam extends DisablingModule
{
    protected final Setting<CamMode> mode =
            register(new EnumSetting<>("Mode", CamMode.Position));
    protected final Setting<Float> speed  =
            register(new NumberSetting<>("Speed", 0.5f, 0.1f, 5.0f));
    protected final Setting<Boolean> dismount =
            register(new BooleanSetting("Dismount", true));

    private EntityOtherPlayerMP fakePlayer;

    public Freecam()
    {
        super("Freecam", Category.Player);
        this.listeners.add(new ListenerPacket(this));
        this.listeners.add(new ListenerUpdate(this));
        this.listeners.add(new ListenerOverlay(this));
        this.listeners.add(new ListenerPush(this));
        this.listeners.add(new ListenerPosLook(this));
        this.listeners.add(new ListenerMove(this));
        this.listeners.add(new ListenerMotion(this));

        SimpleData data = new SimpleData(this,
                "Allows you to look around freely. This module is mostly" +
                    " meant for packet exploits, Spectate is more legit.");
        data.register(mode, "-Cancel cancels movement packets." +
                "\n-Spanish good for dupes." +
                "\n-Position very legit freecam and, unless you toggle" +
                " freecam while standing in the air, almost undetectable.");
        data.register(speed,
                "The speed you want to move with while in freecam.");
        this.setData(data);
    }

    public CamMode getMode()
    {
        return mode.getValue();
    }

    @Override
    protected void onEnable()
    {
        mc.renderChunksMany = false;
        if (mc.player == null)
        {
            this.disable();
            return;
        }

        if (dismount.getValue())
        {
            mc.player.dismountRidingEntity();
        }

        fakePlayer = PlayerUtil
                .createFakePlayerAndAddToWorld(mc.player.getGameProfile());
        fakePlayer.onGround = mc.player.onGround;
    }

    @Override
    protected void onDisable()
    {
        mc.renderChunksMany = true;
        if (mc.player == null)
        {
            return;
        }

        mc.player.setPosition(fakePlayer.posX, fakePlayer.posY, fakePlayer.posZ);
        mc.player.noClip = false;
        PlayerUtil.removeFakePlayer(fakePlayer);
        fakePlayer = null;
    }

    public EntityOtherPlayerMP getPlayer()
    {
        return fakePlayer;
    }

    public void rotate(float yaw, float pitch)
    {
        if (fakePlayer != null)
        {
            fakePlayer.rotationYawHead = yaw;
            fakePlayer.setPositionAndRotation(fakePlayer.posX,
                                              fakePlayer.posY,
                                              fakePlayer.posZ,
                                              yaw,
                                              pitch);

            fakePlayer.setPositionAndRotationDirect(fakePlayer.posX,
                                                    fakePlayer.posY,
                                                    fakePlayer.posZ,
                                                    yaw,
                                                    pitch,
                                                    3,
                                                    false);
        }
    }

}
