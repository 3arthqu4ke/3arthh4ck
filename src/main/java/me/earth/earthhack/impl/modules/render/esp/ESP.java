package me.earth.earthhack.impl.modules.render.esp;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Complexity;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.render.esp.mode.EspMode;
import me.earth.earthhack.impl.util.minecraft.EntityType;
import me.earth.earthhack.impl.util.minecraft.PushMode;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.block.BlockChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.EXTPackedDepthStencil;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

//TODO: Cleanup!
public class ESP extends Module {
    public static boolean isRendering;
    public final Setting<EspMode> mode =
            register(new EnumSetting<>("Mode", EspMode.Outline));
    protected final Setting<Boolean> players =
            register(new BooleanSetting("Players", true));
    protected final Setting<Boolean> monsters =
            register(new BooleanSetting("Monsters", false));
    protected final Setting<Boolean> animals =
            register(new BooleanSetting("Animals", false));
    protected final Setting<Boolean> vehicles =
            register(new BooleanSetting("Vehicles", false));
    protected final Setting<Boolean> misc =
            register(new BooleanSetting("Other", false));
    protected final Setting<Boolean> items =
            register(new BooleanSetting("Items", false));
    protected final Setting<Boolean> storage =
            register(new BooleanSetting("Storage", false));
    protected final Setting<Float> storageRange =
            register(new NumberSetting<>("Storage-Range", 1000.0f, 0.0f, 1000.0f));
    protected final Setting<Float> lineWidth =
            register(new NumberSetting<>("LineWidth", 3.0f, 0.1f, 10.0f));
    protected final Setting<Boolean> hurt =
            register(new BooleanSetting("Hurt", false));
    protected final Setting<Boolean> phase =
            register(new BooleanSetting("Phase", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<PushMode> pushMode =
            register(new EnumSetting<>("PhasePushDetect", PushMode.None))
                .setComplexity(Complexity.Expert);
    public final Setting<Color> color =
            register(new ColorSetting("Color", new Color(255, 255, 255, 255)));
    public final Setting<Color> invisibleColor =
            register(new ColorSetting("InvisibleColor", new Color(180, 180, 255, 255)));
    public final Setting<Color> friendColor =
            register(new ColorSetting("FriendColor", new Color(50, 255, 50, 255)));
    public final Setting<Color> targetColor =
            register(new ColorSetting("TargetColor", new Color(255, 0, 0, 255)));
    protected final Setting<Float> scale =
            register(new NumberSetting<>("Scale", 0.003f, 0.001f, 0.01f));

    protected final ArrayList<EntityPlayer> phasing = new ArrayList<>();

    public ESP() {
        super("ESP", Category.Render);
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerModel(this));
        this.listeners.add(new ListenerRenderCrystal(this));
        this.listeners.add(new ListenerTick(this));
        this.setData(new ESPData(this));
    }

    @Override
    protected void onDisable() {
        isRendering = false;
        phasing.clear();
    }

    protected boolean isValid(Entity entity) {
        Entity renderEntity = RenderUtil.getEntity();
        return entity != null
                && !entity.isDead
                && !entity.equals(renderEntity)
                && (EntityType.isAnimal(entity) && animals.getValue()
                || EntityType.isMonster(entity) && monsters.getValue()
                || entity instanceof EntityEnderCrystal && misc.getValue()
                || entity instanceof EntityPlayer && players.getValue()
                || EntityType.isVehicle(entity) && vehicles.getValue());
    }

    protected void drawTileEntities() {
        Frustum frustum = new Frustum();
        Entity renderEntity = mc.getRenderViewEntity() == null ? mc.player : mc.getRenderViewEntity();

        try {
            double x = renderEntity.posX;
            double y = renderEntity.posY;
            double z = renderEntity.posZ;
            frustum.setPosition(x, y, z);

            for (TileEntity tileEntity : mc.world.loadedTileEntityList) {
                if ((tileEntity instanceof TileEntityChest || tileEntity instanceof TileEntityEnderChest) || tileEntity instanceof TileEntityShulkerBox) {
                    if (mc.player.getDistance(tileEntity.getPos().getX(),tileEntity.getPos().getY(),tileEntity.getPos().getZ()) > storageRange.getValue())
                        continue;
                    final double posX = tileEntity.getPos().getX() - Interpolation.getRenderPosX();
                    final double posY = tileEntity.getPos().getY() - Interpolation.getRenderPosY();
                    final double posZ = tileEntity.getPos().getZ() - Interpolation.getRenderPosZ();
                    AxisAlignedBB bb = new AxisAlignedBB(0.0625, 0.0, 0.0625, 0.94, 0.875, 0.94).offset(posX, posY, posZ);
                    if (tileEntity instanceof TileEntityChest) {
                        TileEntityChest adjacent = null;
                        if (((TileEntityChest) tileEntity).adjacentChestXNeg != null)
                            adjacent = ((TileEntityChest) tileEntity).adjacentChestXNeg;
                        if (((TileEntityChest) tileEntity).adjacentChestXPos != null)
                            adjacent = ((TileEntityChest) tileEntity).adjacentChestXPos;
                        if (((TileEntityChest) tileEntity).adjacentChestZNeg != null)
                            adjacent = ((TileEntityChest) tileEntity).adjacentChestZNeg;
                        if (((TileEntityChest) tileEntity).adjacentChestZPos != null)
                            adjacent = ((TileEntityChest) tileEntity).adjacentChestZPos;
                        if (adjacent != null)
                            bb = bb.union(new AxisAlignedBB(0.0625, 0.0, 0.0625, 0.94, 0.875, 0.94).offset(adjacent.getPos().getX() - Interpolation.getRenderPosX(), adjacent.getPos().getY() - Interpolation.getRenderPosY(), adjacent.getPos().getZ() - Interpolation.getRenderPosZ()));
                    }
                    GL11.glPushMatrix();
                    GL11.glEnable(3042);
                    GL11.glBlendFunc(770, 771);
                    GL11.glDisable(3553);
                    GL11.glEnable(2848);
                    GL11.glDisable(2929);
                    GL11.glDepthMask(false);
                    this.colorTileEntityInside(tileEntity);
                    RenderUtil.drawBox(bb);
                    this.colorTileEntity(tileEntity);
                    RenderUtil.drawOutline(bb, lineWidth.getValue());
                    GL11.glDisable(2848);
                    GL11.glEnable(3553);
                    GL11.glEnable(2929);
                    GL11.glDepthMask(true);
                    GL11.glDisable(3042);
                    RenderUtil.color(1.0f, 1.0f, 1.0f, 1.0f);
                    GL11.glPopMatrix();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void colorTileEntityInside(TileEntity tileEntity) {
        if (tileEntity instanceof TileEntityChest) {
            if (((TileEntityChest) tileEntity).getChestType() == BlockChest.Type.TRAP) {
                RenderUtil.color(new Color(250, 54, 0, 60));
            } else {
                RenderUtil.color(new Color(234, 183, 88, 60));
            }
        } else if (tileEntity instanceof TileEntityEnderChest) {
            RenderUtil.color(new Color(174, 0, 255, 60));
        } else if (tileEntity instanceof TileEntityShulkerBox) {
            RenderUtil.color(new Color(81, 140, 255, 60));
        }
    }

    protected void colorTileEntity(TileEntity tileEntity) {
        if (tileEntity instanceof TileEntityChest) {
            if (((TileEntityChest) tileEntity).getChestType() == BlockChest.Type.TRAP) {
                RenderUtil.color(new Color(250, 54, 0, 255));
            } else {
                RenderUtil.color(new Color(234, 183, 88, 255));
            }
        } else if (tileEntity instanceof TileEntityEnderChest) {
            RenderUtil.color(new Color(174, 0, 255, 255));
        }
    }

    protected Color getEntityColor(Entity entity) {
        Entity target = Managers.TARGET.getKillAura();
        Entity target1 = Managers.TARGET.getCrystal();
        EntityPlayer target2 = Managers.TARGET.getAutoCrystal();
        if (entity.equals(target) || entity.equals(target1) || entity.equals(target2)) {
            return targetColor.getValue();
        }
        if (entity instanceof EntityItem) {
            return new Color(255, 255, 255, 255);
        } else if (EntityType.isVehicle(entity) && vehicles.getValue()) {
            return new Color(200, 100, 0, 255);
        } else if (EntityType.isAnimal(entity)
                && animals.getValue()) {
            return new Color(0, 200, 0, 255);
        } else if (EntityType.isMonster(entity)
                || EntityType.isAngry(entity)
                && monsters.getValue()) {
            return new Color(200, 60, 60, 255);
        } else if (entity instanceof EntityEnderCrystal && misc.getValue()) {
            return new Color(200, 100, 200, 255);
        } else if (entity instanceof EntityPlayer && players.getValue()) {
            EntityPlayer player = (EntityPlayer) entity;
            if (player.isInvisible()) {
                return invisibleColor.getValue();
            }
            if (Managers.FRIENDS.contains(player)) {
                return friendColor.getValue();
            }
            return color.getValue();
        } else {
            return color.getValue();
        }
    }

    protected void checkSetupFBO() {
        Framebuffer fbo = mc.getFramebuffer();
        if (fbo.depthBuffer > -1) {
            setupFBO(fbo);
            fbo.depthBuffer = -1;
        }
    }

    protected void setupFBO(Framebuffer fbo) {
        EXTFramebufferObject.glDeleteRenderbuffersEXT(fbo.depthBuffer);
        int stencilDepthBufferID = EXTFramebufferObject.glGenRenderbuffersEXT();
        EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencilDepthBufferID);
        EXTFramebufferObject.glRenderbufferStorageEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, EXTPackedDepthStencil.GL_DEPTH_STENCIL_EXT, mc.displayWidth, mc.displayHeight);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_STENCIL_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencilDepthBufferID);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencilDepthBufferID);
    }

    public void renderOne(float lineWidth) {
        checkSetupFBO();
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glPolygonOffset(1.0F, -2000000F);
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GlStateManager.depthMask(false);
        GL11.glLineWidth(lineWidth);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glClearStencil(0xF);
        GL11.glStencilFunc(GL11.GL_NEVER, 1, 0xF);
        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
    }

    public void renderTwo() {
        GL11.glStencilFunc(GL11.GL_NEVER, 0, 0xF);
        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
    }

    public void renderThree() {
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
    }

    public void renderFour(Color color) {
        RenderUtil.color(color);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_LINE);
        GL11.glPolygonOffset(1.0f, -2000000f);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0f, 240.0f);
    }

    public void renderFive() {
        GL11.glPolygonOffset(1.0f, 2000000f);
        GL11.glDisable(GL11.GL_POLYGON_OFFSET_LINE);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_STENCIL_TEST);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_DONT_CARE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glPopAttrib();
        GL11.glPolygonOffset(1.0F, 2000000F);
        GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glPopMatrix();
    }


    public boolean shouldHurt() {
        return this.isEnabled() && isRendering && hurt.getValue();
    }

}
