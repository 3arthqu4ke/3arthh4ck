package me.earth.earthhack.impl.modules.player.fakeplayer;

import com.mojang.authlib.GameProfile;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.modules.player.fakeplayer.util.EntityPlayerAttack;
import me.earth.earthhack.impl.modules.player.fakeplayer.util.EntityPlayerPop;
import me.earth.earthhack.impl.modules.player.fakeplayer.util.Position;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.helpers.disabling.DisablingModule;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.thread.LookUpUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FakePlayer extends DisablingModule
{
    protected final Setting<Boolean> playRecording =
        register(new BooleanSetting("Play-Recording", false));
    protected final Setting<Boolean> record =
        register(new BooleanSetting("Record", false));
    protected final Setting<Boolean> loop =
        register(new BooleanSetting("Loop", false));
    protected final Setting<Boolean> gapple =
        register(new BooleanSetting("Gapple", true));
    protected final Setting<Integer> gappleDelay =
        register(new NumberSetting<>("Gapple-Delay", 1600, 1500, 2000));
    protected final Setting<Boolean> damage =
        register(new BooleanSetting("Damage", true));
    protected final Setting<String> name =
        register(new StringSetting("PlayerName", "FakePlayer"));

    protected final List<Position> positions = new ArrayList<>();
    protected final StopWatch timer = new StopWatch();
    protected EntityPlayerAttack fakePlayer;
    protected int index;

    public FakePlayer()
    {
        super("FakePlayer", Category.Player);
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerWorldClient(this));
        this.listeners.add(new ListenerAttack(this));
        this.listeners.add(new ListenerExplosion(this));
        this.setData(new SimpleData(this,
                "Spawns in a FakePlayer for testing purposes."));
    }

    @Override
    public String getDisplayInfo()
    {
        return record.getValue() ? "Recording"
                                 : playRecording.getValue() ? "Playing"
                                                            : null;
    }

    @Override
    protected void onEnable()
    {
        GameProfile profile = new GameProfile(new UUID(1, 1), "FakePlayer");
        if (!name.getValue().equalsIgnoreCase("FakePlayer"))
        {
            UUID uuid = LookUpUtil.getUUIDSimple(name.getValue());
            if (uuid != null)
            {
                profile = new GameProfile(uuid, name.getValue());
            }
        }
        index = 0;
        fakePlayer = (EntityPlayerAttack)
                PlayerUtil.createFakePlayerAndAddToWorld(
                        profile,
                        EntityPlayerPop::new);

        fakePlayer.setRemoteSupplier(damage::getValue);
    }

    @Override
    protected void onDisable()
    {
        PlayerUtil.removeFakePlayer(fakePlayer);
        playRecording.setValue(false);
        record.setValue(false);
        fakePlayer = null;
    }

}
