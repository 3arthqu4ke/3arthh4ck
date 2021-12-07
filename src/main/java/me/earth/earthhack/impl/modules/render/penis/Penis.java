package me.earth.earthhack.impl.modules.render.penis;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.Interpolation;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

import java.awt.*;

public class Penis extends Module {
    protected final Setting<Float> selfLength =
            register(new NumberSetting<>("SelfLength", 0.8f, 0.1f, 2.0f));
    protected final Setting<Float> friendLength =
            register(new NumberSetting<>("FriendLength", 0.8f, 0.1f, 2.0f));
    protected final Setting<Float> enemyLength =
            register(new NumberSetting<>("EnemyLength", 0.4f, 0.1f, 2.0f));
    protected final Setting<Boolean> uncircumcised =
            register(new BooleanSetting("Uncircumcised", false));
    public final Setting<Color> selfShaftColor =
            register(new ColorSetting("SelfShaftColor", new Color(95, 67, 63, 255)));
    public final Setting<Color> selfTipColor =
            register(new ColorSetting("SelfTipColor", new Color(160, 99, 98, 255)));
    public final Setting<Color> friendShaftColor =
            register(new ColorSetting("FriendShaftColor", new Color(95, 67, 63, 255)));
    public final Setting<Color> friendTipColor =
            register(new ColorSetting("FriendTipColor", new Color(160, 99, 98, 255)));
    public final Setting<Color> enemyShaftColor =
            register(new ColorSetting("EnemyShaftColor", new Color(95, 67, 63, 255)));
    public final Setting<Color> enemyTipColor =
            register(new ColorSetting("EnemyTipColor", new Color(160, 99, 98, 255)));
    private final Cylinder shaft = new Cylinder();
    private final Sphere ball = new Sphere();
    private final Sphere tip = new Sphere();

    public Penis() {
        super("Penis", Category.Render);
        this.listeners.add(new ListenerRender(this));
        this.setData(new PenisData(this));
        shaft.setDrawStyle(GLU.GLU_FILL);
        ball.setDrawStyle(GLU.GLU_FILL);
        tip.setDrawStyle(GLU.GLU_FILL);
    }

    protected void onRender3D() {
        for (final EntityPlayer player : mc.world.playerEntities) {
            final Vec3d interpolateEntity = Interpolation.interpolateEntity(player);
            drawPenis(player, interpolateEntity.x, interpolateEntity.y, interpolateEntity.z);
        }
    }

    protected void drawPenis(EntityPlayer player, double x, double y, double z) {
        final float length =  player == mc.player ? selfLength.getValue() : (Managers.FRIENDS.contains(player) ? friendLength.getValue() : enemyLength.getValue());
        final Color shaftColor = player == mc.player ? selfShaftColor.getValue() : (Managers.FRIENDS.contains(player) ? friendShaftColor.getValue() : enemyShaftColor.getValue());
        final Color tipColor = player == mc.player ? selfTipColor.getValue() : (Managers.FRIENDS.contains(player) ? friendTipColor.getValue() : enemyTipColor.getValue());
        GL11.glPushMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(2896);
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(2929);
        GL11.glEnable(2848);
        GL11.glDepthMask(false);
        GL11.glTranslated(x, y, z);
        GL11.glRotatef(-player.rotationYaw, 0.0f, player.height, 0.0f);
        GL11.glTranslated(-x, -y, -z);
        GL11.glTranslated(x, y + player.height / 2.0f - 0.22499999403953552, z);
        GL11.glColor4f(shaftColor.getRed() / 255.f,shaftColor.getGreen() / 255.f,shaftColor.getBlue() / 255.f, 1.0f);
        GL11.glTranslated(0.0, 0.0, 0.07500000298023224);
        shaft.draw(0.1f, 0.11f,length, 25, 20);
        GL11.glColor4f(shaftColor.getRed() / 255.f,shaftColor.getGreen() / 255.f,shaftColor.getBlue() / 255.f, 1.0f);
        GL11.glTranslated(0.0, 0.0, 0.02500000298023223);
        GL11.glTranslated(-0.09000000074505805, 0.0, 0.0);
        ball.draw(0.14f, 10, 20);
        GL11.glTranslated(0.16000000149011612, 0.0, 0.0);
        ball.draw(0.14f, 10, 20);
        GL11.glTranslated(-0.07000000074505806, 0.0,  length - (uncircumcised.getValue() ? 0.15 : 0));
        GL11.glColor4f(tipColor.getRed() / 255.f,tipColor.getGreen() / 255.f,tipColor.getBlue() / 255.f, 1.0f);
        tip.draw(0.13f, 15, 20);
        GL11.glDepthMask(true);
        GL11.glDisable(2848);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glEnable(2896);
        GL11.glEnable(3553);
        RenderHelper.enableStandardItemLighting();
        GL11.glPopMatrix();
        GL11.glColor4f(1,1,1,1);
    }
}
