package me.earth.earthhack.impl.modules.movement.flight;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.movement.flight.mode.FlightMode;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.potion.PotionEffect;

//TODO: more modes and check bugs idk
public class Flight extends Module {
    protected final Setting<FlightMode> mode =
            register(new EnumSetting<>("Mode", FlightMode.Normal));
    protected final Setting<Double> speed =
            register(new NumberSetting<>("Speed", 2.5, 0.0, 50.0));
    protected final Setting<Boolean> animation =
            register(new BooleanSetting("Animation", true));
    protected final Setting<Boolean> damage =
            register(new BooleanSetting("Damage", false));
    protected final Setting<Boolean> antiKick =
            register(new BooleanSetting("AntiKick", true));
    protected final Setting<Boolean> glide =
            register(new BooleanSetting("Glide", true));
    protected final Setting<Double> glideSpeed =
            register(new NumberSetting<>("Glide-Speed", 0.03126, -2.0, 2.0));
    protected final Setting<Double> aacY =
            register(new NumberSetting<>("AAC-Y", 0.83, 0.0, 10.0));

    protected int counter;
    protected int antiCounter;

    protected int constantiamStage;
    protected int constantiamTicks;
    protected double moveSpeed;
    protected int stage;
    protected int ticks;
    protected double y;

    protected int constNewStage;
    protected int constNewTicks;
    protected double constNewOffset;
    protected double constY;
    protected double constMovementSpeed;
    protected double lastDist;

    protected boolean clipped;
    protected int oHareCounter, oHareLevel;
    protected double oHareMoveSpeed, oHareLastDist;

    public Flight() {
        super("Flight", Category.Movement);
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerOnground(this));
        this.listeners.add(new ListenerMove(this));
        this.listeners.add(new ListenerMotion(this));
        this.listeners.addAll(new ListenerPlayerPacket(this).getListeners());
        this.setData(new FlightData(this));
    }

    @Override
    public String getDisplayInfo() {
        return mode.getValue().toString();
    }

    @Override
    protected void onEnable() {
        constantiamStage = 0;
        constantiamTicks = 0;
        moveSpeed = 0.0;

        constNewStage = 0;
        constNewTicks = 0;
        constY = 0.0d;
        constMovementSpeed = 0.0;
        oHareLevel = 1;
        oHareMoveSpeed = 0.1D;
        oHareLastDist = 0.0D;

        if (damage.getValue()) {
            damage();
        }

        if (mode.getValue() == FlightMode.Constantiam) {
            PacketUtil.doY(mc.player.posY + 0.22534, false);
            PacketUtil.doY(mc.player.posY + 0.04534, false);
        }

        if (mode.getValue() == FlightMode.ConstoHareFast && mc.player != null && mc.player.onGround) {
            mc.player.motionY = 0.40245f;
        }
    }

    @Override
    protected void onDisable() {
        if (mode.getValue() == FlightMode.Jump && mc.player != null) {
            counter = 0;
            mc.player.jumpMovementFactor = 0.02F;
        }

        if (mode.getValue() == FlightMode.ConstantiamNew) {
            mc.player.setPosition(mc.player.posX, mc.player.posY + constY, mc.player.posZ);
        }
        if (mode.getValue() == FlightMode.ConstoHare && mc.player != null) {
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
            oHareLevel = 0;
            oHareCounter = 0;
            oHareMoveSpeed = 0.1D;
            oHareLastDist = 0.0D;
        }
    }

    public static void damage() {
        double offset = 0.0625;
        if (mc.player != null && mc.player.onGround) {
            for (int i = 0; i <= (4 / offset); i++) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX,
                        mc.player.posY + offset, mc.player.posZ, false));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX,
                        mc.player.posY, mc.player.posZ, (i == (4 / offset))));
            }
        }
    }

    public static float getMaxFallDist() {
        PotionEffect potioneffect = mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST);
        int f = potioneffect != null ? potioneffect.getAmplifier() + 1 : 0;
        return (float) (mc.player.getMaxFallHeight() + f);
    }

}
