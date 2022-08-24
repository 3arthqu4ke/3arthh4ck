package me.earth.earthhack.impl.core.mixins.render.entity;

import com.google.common.base.Predicate;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.core.ducks.entity.IEntityRenderer;
import me.earth.earthhack.impl.event.events.misc.ReachEvent;
import me.earth.earthhack.impl.event.events.render.*;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.management.Management;
import me.earth.earthhack.impl.modules.player.blocktweaks.BlockTweaks;
import me.earth.earthhack.impl.modules.player.raytrace.RayTrace;
import me.earth.earthhack.impl.modules.player.spectate.Spectate;
import me.earth.earthhack.impl.modules.render.ambience.Ambience;
import me.earth.earthhack.impl.modules.render.blockhighlight.BlockHighlight;
import me.earth.earthhack.impl.modules.render.esp.ESP;
import me.earth.earthhack.impl.modules.render.norender.NoRender;
import me.earth.earthhack.impl.modules.render.viewclip.CameraClip;
import me.earth.earthhack.impl.modules.render.weather.Weather;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.Vector3f;
import me.earth.earthhack.impl.util.math.raytrace.RayTracer;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.misc.MutableWrapper;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.glu.Project;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.nio.FloatBuffer;
import java.util.Collections;
import java.util.List;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer implements IEntityRenderer {
    private static final ModuleCache<NoRender>
            NO_RENDER = Caches.getModule(NoRender.class);
    private static final ModuleCache<BlockHighlight>
            BLOCK_HIGHLIGHT = Caches.getModule(BlockHighlight.class);
    private static final ModuleCache<BlockTweaks>
            BLOCK_TWEAKS = Caches.getModule(BlockTweaks.class);
    private static final ModuleCache<CameraClip>
            CAMERA_CLIP = Caches.getModule(CameraClip.class);
    private static final ModuleCache<Weather>
            WEATHER = Caches.getModule(Weather.class);
    private static final ModuleCache<RayTrace>
            RAYTRACE = Caches.getModule(RayTrace.class);
    private static final ModuleCache<Spectate>
            SPECTATE = Caches.getModule(Spectate.class);
    private static final ModuleCache<Management>
            MANAGEMENT = Caches.getModule(Management.class);
    private static final ModuleCache<Ambience> 
            AMBIENCE = Caches.getModule(Ambience.class);

    private static final SettingCache<Boolean, BooleanSetting, CameraClip>
            EXTEND = Caches.getSetting(CameraClip.class,
            BooleanSetting.class,
            "Extend",
            false);

    private static final SettingCache<Double, NumberSetting<Double>, CameraClip>
            DISTANCE = Caches.getSetting(CameraClip.class,
            Setting.class,
            "Distance",
            10.0);

    @Shadow
    @Final
    private Minecraft mc;

    @Shadow
    private ItemStack itemActivationItem;

    @Shadow
    protected abstract void orientCamera(float partialTicks);

    @Shadow
    protected abstract void setupCameraTransform(float partialTicks, int pass);

    @Shadow protected abstract FloatBuffer setFogColorBuffer(float red, float green, float blue, float alpha);

    @Shadow protected abstract void renderHand(float partialTicks, int pass);

    @Shadow @Final private int[] lightmapColors;
    private float lastReach;

    @Override
    public void invokeOrientCamera(float partialTicks) {
        this.orientCamera(partialTicks);
    }

    @Override
    public void invokeSetupCameraTransform(float partialTicks, int pass) {
        setupCameraTransform(partialTicks, pass);
    }

    @Override
    public void invokeRenderHand(float partialTicks, int pass) {
        renderHand(partialTicks, pass);
    }

    @Override
    @Accessor(value = "lightmapUpdateNeeded")
    public abstract void setLightmapUpdateNeeded(boolean needed);

    @Redirect(
        method = "setupCameraTransform",
        at = @At(
            value = "INVOKE",
            target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V",
            remap = false))
    public void onSetupCameraTransform(final float fovy, final float aspect, final float zNear, final float zFar) {
        final AspectRatioEvent event = new AspectRatioEvent(mc.displayWidth / (float) mc.displayHeight);
        Bus.EVENT_BUS.post(event);
        Project.gluPerspective(fovy, event.getAspectRatio(), zNear, zFar);
    }

    @Redirect(
        method = "renderWorldPass",
        at = @At(
            value = "INVOKE",
            target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V",
            remap = false))
    public void onRenderWorldPass(final float fovy, final float aspect, final float zNear, final float zFar) {
        final AspectRatioEvent event = new AspectRatioEvent(mc.displayWidth / (float) mc.displayHeight);
        Bus.EVENT_BUS.post(event);
        Project.gluPerspective(fovy, event.getAspectRatio(), zNear, zFar);
        /*Project.gluPerspective(fovy, event.getAspectRatio(), zNear, zFar);
        final GLUProjection projection = GLUProjection.getInstance();
        final IntBuffer viewPort = GLAllocation.createDirectIntBuffer(16);
        final FloatBuffer modelView = GLAllocation.createDirectFloatBuffer(16);
        final FloatBuffer projectionPort = GLAllocation.createDirectFloatBuffer(16);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelView);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projectionPort);
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewPort);
        final ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        projection.updateMatrices(viewPort, modelView, projectionPort, scaledResolution.getScaledWidth() / (double) Minecraft.getMinecraft().displayWidth,
                scaledResolution.getScaledHeight() / (double) Minecraft.getMinecraft().displayHeight);*/
    }

    @Redirect(
        method = "renderCloudsCheck",
        at = @At(
            value = "INVOKE",
            target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V", remap = false))
    public void onRenderCloudsCheck(final float fovy, final float aspect, final float zNear, final float zFar) {
        final AspectRatioEvent event = new AspectRatioEvent(mc.displayWidth / (float) mc.displayHeight);
        Bus.EVENT_BUS.post(event);
        Project.gluPerspective(fovy, event.getAspectRatio(), zNear, zFar);
    }

    @Inject(
            method = "renderItemActivation",
            at = @At("HEAD"),
            cancellable = true)
    public void renderItemActivationHook(CallbackInfo info) {
        if (this.itemActivationItem != null
                && NO_RENDER.returnIfPresent(NoRender::noTotems, false)
                && this.itemActivationItem.getItem() == Items.TOTEM_OF_UNDYING) {
            info.cancel();
        }
    }

    /**
     * target = {@link GlStateManager#clear(int)}
     */
    @Inject(
            method = "renderWorldPass",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/client/renderer/GlStateManager" +
                            ".clear(I)V",
                    ordinal = 1,
                    shift = At.Shift.AFTER))
    public void renderWorldPassHook(int pass,
                                     float partialTicks,
                                     long finishTimeNano,
                                     CallbackInfo info) {
        if (Display.isActive() || Display.isVisible()) {
            Bus.EVENT_BUS.post(new Render3DEvent(partialTicks));
        }
    }

    @Inject(
            method = "renderWorldPass",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/RenderGlobal;renderEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/renderer/culling/ICamera;F)V",
                    shift = At.Shift.AFTER
            )
    )
    public void renderEntitiesHook(int pass,
                                    float partialTicks,
                                    long finishTimeNano,
                                    CallbackInfo info)
    {
        Bus.EVENT_BUS.post(new RenderEntitiesEvent());
    }

    /**
     * target = {@link EntityPlayerSP#prevTimeInPortal}
     */
    @Redirect(
            method = "setupCameraTransform",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/entity/EntityPlayerSP;" +
                            "prevTimeInPortal:F"))
    public float prevTimeInPortalHook(EntityPlayerSP entityPlayerSP) {
        if (NO_RENDER.returnIfPresent(NoRender::noNausea, false)) {
            return -3.4028235E38f;
        }

        return entityPlayerSP.prevTimeInPortal;
    }

    @Inject(
            method = "setupFog",
            at = @At(value = "RETURN"),
            cancellable = true)
    public void setupFogHook(int startCoords,
                             float partialTicks,
                             CallbackInfo info) {
        // TODO: Fix this
        if (NO_RENDER.returnIfPresent(NoRender::noFog, false)) {
            GlStateManager.setFogDensity(0.0f);
        }
    }

    @Inject(
            method = "hurtCameraEffect",
            at = @At("HEAD"),
            cancellable = true)
    public void hurtCameraEffectHook(float ticks, CallbackInfo info) {
        if (NO_RENDER.returnIfPresent(NoRender::noHurtCam, false)
                || ESP.isRendering) {
            info.cancel();
        }
    }

    /**
     * target = {@link WorldClient#getEntitiesInAABBexcluding(Entity,
     * AxisAlignedBB, Predicate)}
     */
    @SuppressWarnings("Guava")
    @Redirect(
            method = "getMouseOver",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/WorldClient;" +
                            "getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;" +
                            "Lnet/minecraft/util/math/AxisAlignedBB;" +
                            "Lcom/google/common/base/Predicate;)Ljava/util/List;"))
    public List<Entity> getEntitiesInAABBexcludingHook(
            WorldClient worldClient,
            Entity entityIn,
            AxisAlignedBB boundingBox,
            Predicate<? super Entity> predicate) {
        if (BLOCK_TWEAKS.isEnabled() && BLOCK_TWEAKS.get().noMiningTrace()) {
            return Collections.emptyList();
        }

        try {
            Predicate<? super Entity> p = e ->
                    predicate.test(e) && !e.equals(mc.player);
            return worldClient.getEntitiesInAABBexcluding(entityIn,
                    boundingBox,
                    p);
        } catch (Exception e) {
            Earthhack.getLogger().warn("It's that Exception again...");
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    /**
     * target = {@link PlayerControllerMP#getBlockReachDistance()}
     */
    @Redirect(
            method = "getMouseOver",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/client/multiplayer/PlayerControllerMP" +
                            ".getBlockReachDistance()F"))
    public float getBlockReachDistanceHook(PlayerControllerMP controller) {
        ReachEvent event = new ReachEvent(controller.getBlockReachDistance(),
                0.0f);

        Bus.EVENT_BUS.post(event);

        if (event.isCancelled()) {
            lastReach = event.getReach();
        } else {
            lastReach = 0.0f;
        }

        return this.mc.playerController.getBlockReachDistance() + lastReach;
    }

    /**
     * target = {@link Vec3d#distanceTo(Vec3d)}
     */
    @Redirect(
            method = "getMouseOver",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/util/math/Vec3d" +
                            ".distanceTo(Lnet/minecraft/util/math/Vec3d;)D"))
    public double distanceToHook(Vec3d vec3d, Vec3d vec3d1) {
        return vec3d.distanceTo(vec3d1) - this.lastReach;
    }

    @ModifyVariable(
            method = "orientCamera",
            ordinal = 3,
            at = @At(
                    value = "STORE",
                    ordinal = 0),
            require = 1)
    public double changeCameraDistanceHook(double range) {
        return CAMERA_CLIP.isEnabled() && EXTEND.getValue()
                ? DISTANCE.getValue()
                : range;
    }

    @ModifyVariable(
            method = "orientCamera",
            ordinal = 7,
            at = @At(
                    value = "STORE",
                    ordinal = 0),
            require = 1)
    public double orientCameraHook(double range) {
        return CAMERA_CLIP.isEnabled()
                ? EXTEND.getValue()
                ? DISTANCE.getValue()
                : 4.0
                : range;
    }

    /**
     * target = {@link RenderGlobal#drawSelectionBox(EntityPlayer,
     * RayTraceResult, int, float)}
     */
    @Redirect(
            method = "renderWorldPass",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/RenderGlobal;" +
                            "drawSelectionBox(" +
                            "Lnet/minecraft/entity/player/EntityPlayer;" +
                            "Lnet/minecraft/util/math/RayTraceResult;IF)V"))
    public void drawSelectionBoxHook(RenderGlobal renderGlobal,
                                     EntityPlayer player,
                                     RayTraceResult movingObjectPositionIn,
                                     int execute,
                                     float partialTicks) {
        if (!BLOCK_HIGHLIGHT.isEnabled()) {
            renderGlobal.drawSelectionBox(player,
                    movingObjectPositionIn,
                    execute,
                    partialTicks);
        }
    }

    @Inject(
            method = "renderWorldPass",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/EntityRenderer;renderRainSnow(F)V"))
    public void weatherHook(int pass, float partialTicks, long finishTimeNano, CallbackInfo ci) {
        if (WEATHER.isEnabled()) {
            WEATHER.get().render(partialTicks);
        }
    }

    @Redirect(
            method = "getMouseOver",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;rayTrace(DF)Lnet/minecraft/util/math/RayTraceResult;"))
    private RayTraceResult getMouseOverHook(Entity entity,
                                            double blockReachDistance,
                                            float partialTicks) {
        if (RAYTRACE.returnIfPresent(RayTrace::isActive, false)) {
            Vec3d start = entity.getPositionEyes(partialTicks);
            Vec3d look = entity.getLook(partialTicks);
            Vec3d end = start.add(look.x * blockReachDistance,
                    look.y * blockReachDistance,
                    look.z * blockReachDistance);
            // TODO: make this better!
            if (RAYTRACE.returnIfPresent(RayTrace::liquidCrystalPlace, false)
                    && (mc.player.isInsideOfMaterial(Material.WATER)
                    || mc.player.isInsideOfMaterial(Material.LAVA))
                    && InventoryUtil.isHolding(Blocks.OBSIDIAN))
            {
                MutableWrapper<BlockPos> opposite = new MutableWrapper<>();
                RayTraceResult result = traceInLiquid(start, end, opposite, true);
                if (result != null
                        && result.typeOfHit == RayTraceResult.Type.BLOCK) {
                    if (result.getBlockPos().equals(opposite.get())) {
                        result.sideHit = result.sideHit.getOpposite();
                    }

                    return result;
                }
                else
                {
                    result = traceInLiquid(start, end, opposite, false);
                    if (result != null
                            && result.typeOfHit == RayTraceResult.Type.BLOCK)
                    {
                        if (result.getBlockPos().equals(opposite.get()))
                        {
                            result.sideHit = result.sideHit.getOpposite();
                        }

                        return result;
                    }
                }
            }

            if (RAYTRACE.returnIfPresent(RayTrace::phaseCheck, false)) {
                // TODO: trace to first Air Block!
                return RayTracer.traceTri(
                        mc.world, mc.world, start, end, false, false, true,
                        (b, p, ef) ->
                        {
                            AxisAlignedBB bb =
                                    mc.world
                                      .getBlockState(p)
                                      .getBoundingBox(mc.world, p)
                                      .offset(p);

                            if (RotationUtil.getRotationPlayer()
                                    .getEntityBoundingBox()
                                    .intersects(bb)
                                    && p.getY() > mc.player.posY + 0.25)
                            {
                                return false;
                            }

                            if (ef == null) {
                                return true;
                            }

                            for (Entity e : mc.world.getEntitiesWithinAABB(
                                    Entity.class, new AxisAlignedBB(p.offset(ef))))
                            {
                                if (e == null
                                        || e.isDead
                                        || mc.player.equals(e)
                                        || RotationUtil.getRotationPlayer()
                                        .equals(e))
                                {
                                    continue;
                                }

                                return false;
                            }

                            return true;
                        });
            }
        }

        return entity.rayTrace(blockReachDistance, partialTicks);
    }

    @Redirect(
            method = "updateCameraAndRender",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/entity/EntityPlayerSP;turn(FF)V"))
    public void turnHook(EntityPlayerSP entityPlayerSP, float yaw, float pitch) {
        if (SPECTATE.isEnabled()) {
            if (SPECTATE.get().shouldTurn()) {
                EntityPlayer spectate = SPECTATE.get().getRender();
                if (spectate != null) {
                    spectate.turn(yaw, pitch);
                    spectate.rotationYawHead = spectate.rotationYaw;
                }
            }

            return;
        }

        entityPlayerSP.turn(yaw, pitch);
    }

    // Maybe modifyArgs in the future to reduce the amount of calls?
    @Inject(method = "setupFogColor", at = @At("HEAD"), cancellable = true)
    public void setupFogColoHook(boolean black, CallbackInfo ci) {
        if (MANAGEMENT.get().isUsingCustomFogColor())
        {
            ci.cancel();
            Color fogColor = MANAGEMENT.get().getCustomFogColor();
            GlStateManager.glFog(2918, setFogColorBuffer(fogColor.getRed() / 255.0f, fogColor.getGreen() / 255.0f, fogColor.getBlue() / 255.0f, fogColor.getAlpha() / 255.0f));
        }
    }

    @Redirect(
            method = "renderHand",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/ItemRenderer;renderItemInFirstPerson(F)V"
            )
    )
    public void drHook(ItemRenderer itemRenderer, float partialTicks)
    {
        BeginRenderHandEvent event = new BeginRenderHandEvent();
        Bus.EVENT_BUS.post(event);
        if (!event.isCancelled()) itemRenderer.renderItemInFirstPerson(partialTicks);
    }

    @Inject(
            method = "updateCameraAndRender",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/EntityRenderer;setupOverlayRendering()V",
                    ordinal = 0
            )
    )
    public void nurseHook(float partialTicks, long nanoTime, CallbackInfo ci)
    {
        final PreRenderHudEvent event = new PreRenderHudEvent();
        Bus.EVENT_BUS.post(event);
    }

    @Inject(
            method = "renderHand",
            at = @At(
                    value = "HEAD"
            )
    )
    public void preRenderHandHook(float partialTicks, int pass, CallbackInfo info)
    {
        final PreRenderHandEvent event = new PreRenderHandEvent();
        Bus.EVENT_BUS.post(event);
    }

    @Inject(method = "renderWorld", at = @At("RETURN"))
    public void renderWorldHook(CallbackInfo info)
    {
        final int guiScale = mc.gameSettings.guiScale;
        mc.gameSettings.guiScale = 1;
        Bus.EVENT_BUS.post(new WorldRenderEvent());
        mc.gameSettings.guiScale = guiScale;
    }

    @Redirect(
            method = "renderItemActivation",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/RenderItem;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;)V"
            )
    )
    public void renderItemHook(RenderItem item, ItemStack stack, ItemCameraTransforms.TransformType type)
    {
        RenderItemActivationEvent event = new RenderItemActivationEvent(item, stack, type);
        Bus.EVENT_BUS.post(event);
        if (!event.isCancelled()) event.getRenderItem().renderItem(stack, type);
    }

    // this could also be used for colored lights i think?
    @Inject(
            method = "updateLightmap",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/texture/DynamicTexture;updateDynamicTexture()V",
                    shift = At.Shift.BEFORE
            )
    )
    public void updateTextureHook(float partialTicks, CallbackInfo ci)
    {
        if (AMBIENCE.isEnabled())
        {
            for (int i = 0; i < lightmapColors.length; i++)
            {
                Color ambientColor = AMBIENCE.get().getColor();
                int alpha = ambientColor.getAlpha();
                float modifier = alpha / 255.0f;
                int color = lightmapColors[i];
                int[] bgr = MathUtil.toRGBAArray(color);
                /*int red = (bgr[2] + ambientColor.getRed()) / 2; // half-half mix of both colors
                int green = (bgr[1] + ambientColor.getGreen()) / 2;
                int blue = (bgr[0] + ambientColor.getBlue()) / 2;*/
                Vector3f values = new Vector3f(bgr[2] / 255.0f, bgr[1] / 255.0f, bgr[0] / 255.0f);
                Vector3f newValues = new Vector3f(ambientColor.getRed() / 255.0f, ambientColor.getGreen() / 255.0f, ambientColor.getBlue() / 255.0f);
                Vector3f finalValues = MathUtil.mix(values, newValues, modifier);

                /*int red = (int) (((bgr[2] * (1 - modifier)) + (ambientColor.getRed() * modifier)) / 2.0f); // half-half mix of both colors
                int green = (int) (((bgr[1] * (1 - modifier)) + (ambientColor.getGreen() * modifier)) / 2.0f);
                int blue = (int) (((bgr[0] * (1 - modifier)) + (ambientColor.getBlue() * modifier)) / 2.0f);*/
                // lightmapColors[i] = MathUtil.toRGB(red, green, blue);
                int red = (int) (finalValues.x * 255);
                int green = (int) (finalValues.y * 255);
                int blue = (int) (finalValues.z * 255);
                lightmapColors[i] = -16777216 | red << 16 | green << 8 | blue;
            }
        }
    }

    private RayTraceResult traceInLiquid(Vec3d start,
                                         Vec3d end,
                                         MutableWrapper<BlockPos> opposite,
                                         boolean air)
    {
         return RayTracer.traceTri(
                 mc.world, mc.world, start, end, false, false, true,
        (b, p, ef) ->
        {
            if (ef == null
                    || RotationUtil.getRotationPlayer()
                    .getEntityBoundingBox()
                    .intersects(new AxisAlignedBB(p)))
            {
                return false;
            }

            // TODO: 1.13+ mechanics
            BlockPos pos = p.offset(ef);
            BlockPos up = pos.up();
            if ((!air || mc.world.getBlockState(up)
                        .getBlock() == Blocks.AIR
                    && mc.world.getBlockState(up.up())
                               .getBlock() == Blocks.AIR)
                    && mc.world.getEntitiesWithinAABBExcludingEntity(
                            null,
                            new AxisAlignedBB(up.getX(),
                                    up.getY(),
                                    up.getZ(),
                                    up.getX() + 1.0,
                                    up.getY() + 2.0,
                                    up.getZ() + 1.0))
                    .isEmpty())
            {
                return true;
            }

            pos = p.offset(ef.getOpposite());
            up = pos.up();
            if ((!air || mc.world.getBlockState(up)
                                 .getBlock() == Blocks.AIR
                    && mc.world.getBlockState(up.up())
                               .getBlock() == Blocks.AIR)
                    && mc.world.getEntitiesWithinAABBExcludingEntity(
                            null,
                            new AxisAlignedBB(up.getX(),
                                    up.getY(),
                                    up.getZ(),
                                    up.getX() + 1.0,
                                    up.getY() + 2.0,
                                    up.getZ() + 1.0))
                    .isEmpty())
            {
                opposite.set(p);
                return true;
            }

            return !(b instanceof BlockLiquid)
                    && !(b instanceof BlockAir);
        }, (b, p, ef) -> true);
    }

}
