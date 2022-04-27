package me.earth.earthhack.impl.modules.render.rechams;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.*;
import me.earth.earthhack.impl.gui.visibility.PageBuilder;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.render.rechams.mode.ChamsMode;
import me.earth.earthhack.impl.modules.render.rechams.mode.ChamsPage;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.minecraft.EntityType;
import me.earth.earthhack.impl.util.render.GlShader;
import me.earth.earthhack.impl.util.render.image.GifImage;
import me.earth.earthhack.impl.util.render.image.NameableImage;
import me.earth.earthhack.impl.util.render.shader.FramebufferWrapper;
import me.earth.earthhack.impl.util.render.shader.SettingShader;
import me.earth.modeltotem.ModelTotemFileManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Tuple;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

// TODO: programmatically generate settings so that i dont have to type all of this shit out.
// TODO: maybe a more efficient method of checking settings so we don't have to do it every frame?
// TODO: shader settings
@SuppressWarnings("all")
public class ReChams extends Module
{

    public final Setting<ChamsPage> page =
            register(new EnumSetting<>("Page", ChamsPage.Players));

    /* Players */
    public final Setting<ChamsMode> playerMode =
            register(new EnumSetting<>("PlayerMode", ChamsMode.Normal));
    public final Setting<Color> color =
            register(new ColorSetting("Color", new Color(255, 255, 255, 255)));
    public final Setting<Color> wallsColor =
            register(new ColorSetting("WallsColor", new Color(255, 255, 255, 255)));
    public final Setting<Color> armorColor =
            register(new ColorSetting("ArmorColor", new Color(255, 255, 255, 255)));
    public final Setting<Color> armorWallsColor =
            register(new ColorSetting("ArmorWallsColor", new Color(255, 255, 255, 255)));
    public final Setting<Boolean> xqz =
            register(new BooleanSetting("XQZ", false));
    public final Setting<Boolean> armor =
            register(new BooleanSetting("Armor", false));
    public final Setting<Boolean> armorXQZ =
            register(new BooleanSetting("ArmorXQZ", false));
    public final Setting<Boolean> wireframe =
            register(new BooleanSetting("Wireframe", false));
    public final Setting<Color> wireframeColor =
            register(new ColorSetting("WireframeColor", new Color(255, 255, 255, 255)));
    public final Setting<Boolean> wireframeXQZ =
            register(new BooleanSetting("Wireframe", false));
    public final Setting<Color> wireframeWallsColor =
            register(new ColorSetting("WireframeColor", new Color(255, 255, 255, 255)));
    public final Setting<Boolean> lightning =
            register(new BooleanSetting("Lightning", false));
    public final Setting<Color> lightningColor =
            register(new ColorSetting("LightningColor", new Color(255, 255, 255, 255)));
    public final Setting<Boolean> xqzLightning =
            register(new BooleanSetting("XQZLightning", false));
    public final Setting<Color> wallsLightningColor =
            register(new ColorSetting("WallsLightningColor", new Color(255, 255, 255, 255)));
    public final Setting<Boolean> glint =
            register(new BooleanSetting("Glint", false));
    public final Setting<Color> glintColor =
            register(new ColorSetting("GlintColor", new Color(255, 255, 255, 255)));
    public final Setting<Boolean> xqzGlint =
            register(new BooleanSetting("XQZGlint", false));
    public final Setting<Color> wallsGlintColor =
            register(new ColorSetting("WallsGlintColor", new Color(255, 255, 255, 255)));
    public final Setting<Float> glintMultiplier =
            register(new NumberSetting<>("GlintSpeed", 1.0f, 0.0f, 5.0f));
    public final Setting<Float> glintRotate =
            register(new NumberSetting<>("GlintRotateMult", 1.0f, 0.0f, 5.0f));
    public final Setting<Float> glintScale =
            register(new NumberSetting<>("GlintScale", 2.0f, 0.1f, 5.0f));
    public final Setting<Integer> glints =
            register(new NumberSetting<>("Glints", 2, 1, 10));
    public final Setting<NameableImage> image =
            register(new ListSetting<>("Image", ModelTotemFileManager.INSTANCE.getInitialImage(), ModelTotemFileManager.INSTANCE.getImages()));
    public final Setting<NameableImage> wallsImage =
            register(new ListSetting<>("WallsImage", ModelTotemFileManager.INSTANCE.getInitialImage(), ModelTotemFileManager.INSTANCE.getImages()));
    public final Setting<Boolean> fit =
            register(new BooleanSetting("Fit", false));
    public final Setting<Boolean> fill =
            register(new BooleanSetting("Fill", false));
    public final Setting<Boolean> useGif =
            register(new BooleanSetting("UseGif", false));
    public final Setting<Boolean> useWallsGif =
            register(new BooleanSetting("UseWallsGif", false));
    public final Setting<GifImage> gif =
            register(new ListSetting<>("Gif", ModelTotemFileManager.INSTANCE.getInitialGif(), ModelTotemFileManager.INSTANCE.getGifs()));
    public final Setting<GifImage> wallsGif =
            register(new ListSetting<>("WallsGif", ModelTotemFileManager.INSTANCE.getInitialGif(), ModelTotemFileManager.INSTANCE.getGifs()));
    public final Setting<Float> mixFactor =
            register(new NumberSetting<>("ImageMixFactor", 1.0f, 0.0f, 1.0f));
    public final Setting<Float> colorMixFactor =
            register(new NumberSetting<>("ImageColorMixFactor", 1.0f, 0.0f, 1.0f));
    public final Setting<SettingShader> shader =
            register(new ListSetting<>("Shader", ModelTotemFileManager.INSTANCE.getInitialShader(), ModelTotemFileManager.INSTANCE.getShaders()));
    public final Setting<SettingShader> wallsShader =
            register(new ListSetting<>("WallsShader", ModelTotemFileManager.INSTANCE.getInitialShader(), ModelTotemFileManager.INSTANCE.getShaders()));
    public final Setting<SettingShader> framebufferShader =
            register(new ListSetting<>("FramebufferShader", ModelTotemFileManager.INSTANCE.getInitialShader(), ModelTotemFileManager.INSTANCE.getShaders()));
    public final Setting<SettingShader> wallsFramebufferShader =
            register(new ListSetting<>("WallsFramebufferShader", ModelTotemFileManager.INSTANCE.getInitialShader(), ModelTotemFileManager.INSTANCE.getShaders()));
    public final Setting<Boolean> alphaTest =
            register(new BooleanSetting("AlphaTest", false));

    /* Friends */
    public final Setting<ChamsMode> friendMode =
            register(new EnumSetting<>("FriendMode", ChamsMode.Normal));
    public final Setting<Color> friendColor =
            register(new ColorSetting("F-Color", new Color(255, 255, 255, 255)));
    public final Setting<Color> friendWallColor =
            register(new ColorSetting("F-WallsColor", new Color(255, 255, 255, 255)));
    public final Setting<Color> friendArmorColor =
            register(new ColorSetting("F-ArmorColor", new Color(255, 255, 255, 255)));
    public final Setting<Color> friendArmorWallsColor =
            register(new ColorSetting("F-ArmorWallsColor", new Color(255, 255, 255, 255)));
    public final Setting<Boolean> friendXQZ =
            register(new BooleanSetting("F-XQZ", false));
    public final Setting<Boolean> friendArmor =
            register(new BooleanSetting("F-Armor", false));
    public final Setting<Boolean> friendArmorXQZ =
            register(new BooleanSetting("F-ArmorXQZ", false));
    public final Setting<Boolean> friendWireframe =
            register(new BooleanSetting("F-Wireframe", false));
    public final Setting<Color> friendWireframeColor =
            register(new ColorSetting("F-WireframeColor", new Color(255, 255, 255, 255)));
    public final Setting<Boolean> friendWireframeXQZ =
            register(new BooleanSetting("F-Wireframe", false));
    public final Setting<Color> friendWireframeWallsColor =
            register(new ColorSetting("F-WireframeColor", new Color(255, 255, 255, 255)));
    public final Setting<Boolean> friendLightning =
            register(new BooleanSetting("F-Lightning", false));
    public final Setting<Color> friendLightningColor =
            register(new ColorSetting("F-LightningColor", new Color(255, 255, 255, 255)));
    public final Setting<Boolean> friendXQZLightning =
            register(new BooleanSetting("F-XQZLightning", false));
    public final Setting<Color> friendWallsLightningColor =
            register(new ColorSetting("F-WallsLightningColor", new Color(255, 255, 255, 255)));
    public final Setting<Boolean> friendGlint =
            register(new BooleanSetting("F-Glint", false));
    public final Setting<Color> friendGlintColor =
            register(new ColorSetting("F-GlintColor", new Color(255, 255, 255, 255)));
    public final Setting<Boolean> friendXQZGlint =
            register(new BooleanSetting("F-XQZGlint", false));
    public final Setting<Color> friendWallsGlintColor =
            register(new ColorSetting("F-WallsGlintColor", new Color(255, 255, 255, 255)));
    public final Setting<Float> friendGlintMultiplier =
            register(new NumberSetting<>("F-GlintSpeed", 1.0f, 0.0f, 5.0f));
    public final Setting<Float> friendGlintRotate =
            register(new NumberSetting<>("F-GlintRotateMult", 1.0f, 0.0f, 5.0f));
    public final Setting<Float> friendGlintScale =
            register(new NumberSetting<>("F-GlintScale", 2.0f, 0.1f, 5.0f));
    public final Setting<Integer> friendGlints =
            register(new NumberSetting<>("F-Glints", 2, 1, 10));
    public final Setting<NameableImage> friendImage =
            register(new ListSetting<>("F-Image", ModelTotemFileManager.INSTANCE.getInitialImage(), ModelTotemFileManager.INSTANCE.getImages()));
    public final Setting<NameableImage> friendWallsImage =
            register(new ListSetting<>("F-WallsImage", ModelTotemFileManager.INSTANCE.getInitialImage(), ModelTotemFileManager.INSTANCE.getImages()));
    public final Setting<Boolean> friendFit =
            register(new BooleanSetting("F-Fit", false));
    public final Setting<Boolean> friendFill =
            register(new BooleanSetting("F-Fill", false));
    public final Setting<Boolean> friendUseGif =
            register(new BooleanSetting("F-UseGif", false));
    public final Setting<Boolean> friendUseWallsGif =
            register(new BooleanSetting("F-UseWallsGif", false));
    public final Setting<GifImage> friendGif =
            register(new ListSetting<>("F-Gif", ModelTotemFileManager.INSTANCE.getInitialGif(), ModelTotemFileManager.INSTANCE.getGifs()));
    public final Setting<GifImage> friendWallsGif =
            register(new ListSetting<>("F-WallsGif", ModelTotemFileManager.INSTANCE.getInitialGif(), ModelTotemFileManager.INSTANCE.getGifs()));
    public final Setting<Float> friendMixFactor =
            register(new NumberSetting<>("F-ImageMixFactor", 1.0f, 0.0f, 1.0f));
    public final Setting<Float> friendColorMixFactor =
            register(new NumberSetting<>("F-ImageColorMixFactor", 1.0f, 0.0f, 1.0f));
    public final Setting<SettingShader> friendShader =
            register(new ListSetting<>("F-Shader", ModelTotemFileManager.INSTANCE.getInitialShader(), ModelTotemFileManager.INSTANCE.getShaders()));
    public final Setting<SettingShader> friendWallsShader =
            register(new ListSetting<>("F-WallsShader", ModelTotemFileManager.INSTANCE.getInitialShader(), ModelTotemFileManager.INSTANCE.getShaders()));
    public final Setting<SettingShader> friendFramebufferShader =
            register(new ListSetting<>("F-FramebufferShader", ModelTotemFileManager.INSTANCE.getInitialShader(), ModelTotemFileManager.INSTANCE.getShaders()));
    public final Setting<SettingShader> friendWallsFramebufferShader =
            register(new ListSetting<>("F-WallsFramebufferShader", ModelTotemFileManager.INSTANCE.getInitialShader(), ModelTotemFileManager.INSTANCE.getShaders()));
    public final Setting<Boolean> friendAlphaTest =
            register(new BooleanSetting("F-AlphaTest", false));

    /* Enemies */
    public final Setting<ChamsMode> enemyMode =
            register(new EnumSetting<>("EnemyMode", ChamsMode.Normal));
    public final Setting<Color> enemyColor =
            register(new ColorSetting("E-Color", new Color(255, 255, 255, 255)));
    public final Setting<Color> enemyWallColor =
            register(new ColorSetting("E-WallsColor", new Color(255, 255, 255, 255)));
    public final Setting<Color> enemyArmorColor =
            register(new ColorSetting("E-ArmorColor", new Color(255, 255, 255, 255)));
    public final Setting<Color> enemyArmorWallsColor =
            register(new ColorSetting("E-ArmorWallsColor", new Color(255, 255, 255, 255)));
    public final Setting<Boolean> enemyXQZ =
            register(new BooleanSetting("E-XQZ", false));
    public final Setting<Boolean> enemyArmor =
            register(new BooleanSetting("E-Armor", false));
    public final Setting<Boolean> enemyArmorXQZ =
            register(new BooleanSetting("E-ArmorXQZ", false));
    public final Setting<Boolean> enemyWireframe =
            register(new BooleanSetting("E-Wireframe", false));
    public final Setting<Color> enemyWireframeColor =
            register(new ColorSetting("E-WireframeColor", new Color(255, 255, 255, 255)));
    public final Setting<Boolean> enemyWireframeXQZ =
            register(new BooleanSetting("E-Wireframe", false));
    public final Setting<Color> enemyWireframeWallsColor =
            register(new ColorSetting("E-WireframeColor", new Color(255, 255, 255, 255)));
    public final Setting<Boolean> enemyLightning =
            register(new BooleanSetting("E-Lightning", false));
    public final Setting<Color> enemyLightningColor =
            register(new ColorSetting("E-LightningColor", new Color(255, 255, 255, 255)));
    public final Setting<Boolean> enemyXQZLightning =
            register(new BooleanSetting("E-XQZLightning", false));
    public final Setting<Color> enemyWallsLightningColor =
            register(new ColorSetting("E-WallsLightningColor", new Color(255, 255, 255, 255)));
    public final Setting<Boolean> enemyGlint =
            register(new BooleanSetting("E-Glint", false));
    public final Setting<Color> enemyGlintColor =
            register(new ColorSetting("E-GlintColor", new Color(255, 255, 255, 255)));
    public final Setting<Boolean> enemyXQZGlint =
            register(new BooleanSetting("E-XQZGlint", false));
    public final Setting<Color> enemyWallsGlintColor =
            register(new ColorSetting("E-WallsGlintColor", new Color(255, 255, 255, 255)));
    public final Setting<Float> enemyGlintMultiplier =
            register(new NumberSetting<>("E-GlintSpeed", 1.0f, 0.0f, 5.0f));
    public final Setting<Float> enemyGlintRotate =
            register(new NumberSetting<>("E-GlintRotateMult", 1.0f, 0.0f, 5.0f));
    public final Setting<Float> enemyGlintScale =
            register(new NumberSetting<>("E-GlintScale", 2.0f, 0.1f, 5.0f));
    public final Setting<Integer> enemyGlints =
            register(new NumberSetting<>("E-Glints", 2, 1, 10));
    public final Setting<NameableImage> enemyImage =
            register(new ListSetting<>("E-Image", ModelTotemFileManager.INSTANCE.getInitialImage(), ModelTotemFileManager.INSTANCE.getImages()));
    public final Setting<NameableImage> enemyWallsImage =
            register(new ListSetting<>("E-WallsImage", ModelTotemFileManager.INSTANCE.getInitialImage(), ModelTotemFileManager.INSTANCE.getImages()));
    public final Setting<Boolean> enemyFit =
            register(new BooleanSetting("E-Fit", false));
    public final Setting<Boolean> enemyFill =
            register(new BooleanSetting("E-Fill", false));
    public final Setting<Boolean> enemyUseGif =
            register(new BooleanSetting("E-UseGif", false));
    public final Setting<Boolean> enemyUseWallsGif =
            register(new BooleanSetting("E-UseWallsGif", false));
    public final Setting<GifImage> enemyGif =
            register(new ListSetting<>("E-Gif", ModelTotemFileManager.INSTANCE.getInitialGif(), ModelTotemFileManager.INSTANCE.getGifs()));
    public final Setting<GifImage> enemyWallsGif =
            register(new ListSetting<>("E-WallsGif", ModelTotemFileManager.INSTANCE.getInitialGif(), ModelTotemFileManager.INSTANCE.getGifs()));
    public final Setting<Float> enemyMixFactor =
            register(new NumberSetting<>("E-ImageMixFactor", 1.0f, 0.0f, 1.0f));
    public final Setting<Float> enemyColorMixFactor =
            register(new NumberSetting<>("E-ImageColorMixFactor", 1.0f, 0.0f, 1.0f));
    public final Setting<SettingShader> enemyShader =
            register(new ListSetting<>("E-Shader", ModelTotemFileManager.INSTANCE.getInitialShader(), ModelTotemFileManager.INSTANCE.getShaders()));
    public final Setting<SettingShader> enemyWallsShader =
            register(new ListSetting<>("E-WallsShader", ModelTotemFileManager.INSTANCE.getInitialShader(), ModelTotemFileManager.INSTANCE.getShaders()));
    public final Setting<SettingShader> enemyFramebufferShader =
            register(new ListSetting<>("E-FramebufferShader", ModelTotemFileManager.INSTANCE.getInitialShader(), ModelTotemFileManager.INSTANCE.getShaders()));
    public final Setting<SettingShader> enemyWallsFramebufferShader =
            register(new ListSetting<>("E-WallsFramebufferShader", ModelTotemFileManager.INSTANCE.getInitialShader(), ModelTotemFileManager.INSTANCE.getShaders()));
    public final Setting<Boolean> enemyAlphaTest =
            register(new BooleanSetting("E-AlphaTest", false));

    /* Crystals */
    public final Setting<ChamsMode> crystalMode =
            register(new EnumSetting<>("CrystalMode", ChamsMode.Normal));
    public final Setting<Color> crystalColor =
            register(new ColorSetting("C-Color", new Color(255, 255, 255, 255)));
    public final Setting<Color> crystalWallColor =
            register(new ColorSetting("C-WallsColor", new Color(255, 255, 255, 255)));
    public final Setting<Boolean> crystalXQZ =
            register(new BooleanSetting("C-XQZ", false));
    public final Setting<Boolean> crystalWireframe =
            register(new BooleanSetting("C-Wireframe", false));
    public final Setting<Color> crystalWireframeColor =
            register(new ColorSetting("C-WireframeColor", new Color(255, 255, 255, 255)));
    public final Setting<Boolean> crystalWireframeXQZ =
            register(new BooleanSetting("C-Wireframe", false));
    public final Setting<Color> crystalWireframeWallsColor =
            register(new ColorSetting("C-WireframeColor", new Color(255, 255, 255, 255)));
    public final Setting<Boolean> crystalLightning =
            register(new BooleanSetting("C-Lightning", false));
    public final Setting<Color> crystalLightningColor =
            register(new ColorSetting("C-LightningColor", new Color(255, 255, 255, 255)));
    public final Setting<Boolean> crystalXQZLightning =
            register(new BooleanSetting("C-XQZLightning", false));
    public final Setting<Color> crystalWallsLightningColor =
            register(new ColorSetting("C-WallsLightningColor", new Color(255, 255, 255, 255)));
    public final Setting<Boolean> crystalGlint =
            register(new BooleanSetting("C-Glint", false));
    public final Setting<Color> crystalGlintColor =
            register(new ColorSetting("C-GlintColor", new Color(255, 255, 255, 255)));
    public final Setting<Boolean> crystalXQZGlint =
            register(new BooleanSetting("C-XQZGlint", false));
    public final Setting<Color> crystalWallsGlintColor =
            register(new ColorSetting("C-WallsGlintColor", new Color(255, 255, 255, 255)));
    public final Setting<Float> crystalGlintMultiplier =
            register(new NumberSetting<>("C-GlintSpeed", 1.0f, 0.0f, 5.0f));
    public final Setting<Float> crystalGlintRotate =
            register(new NumberSetting<>("C-GlintRotateMult", 1.0f, 0.0f, 5.0f));
    public final Setting<Float> crystalGlintScale =
            register(new NumberSetting<>("C-GlintScale", 2.0f, 0.1f, 5.0f));
    public final Setting<Integer> crystalGlints =
            register(new NumberSetting<>("C-Glints", 2, 1, 10));
    public final Setting<NameableImage> crystalImage =
            register(new ListSetting<>("C-Image", ModelTotemFileManager.INSTANCE.getInitialImage(), ModelTotemFileManager.INSTANCE.getImages()));
    public final Setting<NameableImage> crystalWallsImage =
            register(new ListSetting<>("C-WallsImage", ModelTotemFileManager.INSTANCE.getInitialImage(), ModelTotemFileManager.INSTANCE.getImages()));
    public final Setting<Boolean> crystalFit =
            register(new BooleanSetting("C-Fit", false));
    public final Setting<Boolean> crystalFill =
            register(new BooleanSetting("C-Fill", false));
    public final Setting<Boolean> crystalUseGif =
            register(new BooleanSetting("C-UseGif", false));
    public final Setting<Boolean> crystalUseWallsGif =
            register(new BooleanSetting("C-UseWallsGif", false));
    public final Setting<GifImage> crystalGif =
            register(new ListSetting<>("C-Gif", ModelTotemFileManager.INSTANCE.getInitialGif(), ModelTotemFileManager.INSTANCE.getGifs()));
    public final Setting<GifImage> crystalWallsGif =
            register(new ListSetting<>("C-WallsGif", ModelTotemFileManager.INSTANCE.getInitialGif(), ModelTotemFileManager.INSTANCE.getGifs()));
    public final Setting<Float> crystalMixFactor =
            register(new NumberSetting<>("C-ImageMixFactor", 1.0f, 0.0f, 1.0f));
    public final Setting<Float> crystalColorMixFactor =
            register(new NumberSetting<>("C-ImageColorMixFactor", 1.0f, 0.0f, 1.0f));
    public final Setting<SettingShader> crystalShader =
            register(new ListSetting<>("C-Shader", ModelTotemFileManager.INSTANCE.getInitialShader(), ModelTotemFileManager.INSTANCE.getShaders()));
    public final Setting<SettingShader> crystalWallsShader =
            register(new ListSetting<>("C-WallsShader", ModelTotemFileManager.INSTANCE.getInitialShader(), ModelTotemFileManager.INSTANCE.getShaders()));
    public final Setting<SettingShader> crystalFramebufferShader =
            register(new ListSetting<>("C-FramebufferShader", ModelTotemFileManager.INSTANCE.getInitialShader(), ModelTotemFileManager.INSTANCE.getShaders()));
    public final Setting<SettingShader> crystalWallsFramebufferShader =
            register(new ListSetting<>("C-WallsFramebufferShader", ModelTotemFileManager.INSTANCE.getInitialShader(), ModelTotemFileManager.INSTANCE.getShaders()));
    public final Setting<Boolean> crystalAlphaTest =
            register(new BooleanSetting("C-AlphaTest", false));

    /* Animals */
    public final Setting<ChamsMode> animalMode =
            register(new EnumSetting<>("AnimalMode", ChamsMode.Normal));
    public final Setting<Color> animalColor =
            register(new ColorSetting("A-Color", new Color(255, 255, 255, 255)));
    public final Setting<Color> animalWallColor =
            register(new ColorSetting("A-WallsColor", new Color(255, 255, 255, 255)));
    public final Setting<Boolean> animalXQZ =
            register(new BooleanSetting("A-XQZ", false));
    public final Setting<Boolean> animalWireframe =
            register(new BooleanSetting("A-Wireframe", false));
    public final Setting<Color> animalWireframeColor =
            register(new ColorSetting("A-WireframeColor", new Color(255, 255, 255, 255)));
    public final Setting<Boolean> animalWireframeXQZ =
            register(new BooleanSetting("A-Wireframe", false));
    public final Setting<Color> animalWireframeWallsColor =
            register(new ColorSetting("A-WireframeColor", new Color(255, 255, 255, 255)));
    public final Setting<Boolean> animalLightning =
            register(new BooleanSetting("A-Lightning", false));
    public final Setting<Color> animalLightningColor =
            register(new ColorSetting("A-LightningColor", new Color(255, 255, 255, 255)));
    public final Setting<Boolean> animalXQZLightning =
            register(new BooleanSetting("A-XQZLightning", false));
    public final Setting<Color> animalWallsLightningColor =
            register(new ColorSetting("A-WallsLightningColor", new Color(255, 255, 255, 255)));
    public final Setting<Boolean> animalGlint =
            register(new BooleanSetting("A-Glint", false));
    public final Setting<Color> animalGlintColor =
            register(new ColorSetting("A-GlintColor", new Color(255, 255, 255, 255)));
    public final Setting<Boolean> animalXQZGlint =
            register(new BooleanSetting("A-XQZGlint", false));
    public final Setting<Color> animalWallsGlintColor =
            register(new ColorSetting("A-WallsGlintColor", new Color(255, 255, 255, 255)));
    public final Setting<Float> animalGlintMultiplier =
            register(new NumberSetting<>("A-GlintSpeed", 1.0f, 0.0f, 5.0f));
    public final Setting<Float> animalGlintRotate =
            register(new NumberSetting<>("A-GlintRotateMult", 1.0f, 0.0f, 5.0f));
    public final Setting<Float> animalGlintScale =
            register(new NumberSetting<>("A-GlintScale", 2.0f, 0.1f, 5.0f));
    public final Setting<Integer> animalGlints =
            register(new NumberSetting<>("A-Glints", 2, 1, 10));
    public final Setting<String> animalImage =
            register(new StringSetting("A-Image", "None!"));
    public final Setting<String> animalWallImage =
            register(new StringSetting("A-WallsImage", "None!"));
    public final Setting<Boolean> animalFit =
            register(new BooleanSetting("A-Fit", false));
    public final Setting<Float> animalMixFactor =
            register(new NumberSetting<>("A-ImageMixFactor", 1.0f, 0.0f, 1.0f));
    public final Setting<Float> animalColorMixFactor =
            register(new NumberSetting<>("A-ImageColorMixFactor", 1.0f, 0.0f, 1.0f));

    /* Monsters */
    public final Setting<ChamsMode> monsterMode =
            register(new EnumSetting<>("MonsterMode", ChamsMode.Normal));
    public final Setting<Color> monsterColor =
            register(new ColorSetting("M-Color", new Color(255, 255, 255, 255)));
    public final Setting<Color> monsterWallColor =
            register(new ColorSetting("M-WallsColor", new Color(255, 255, 255, 255)));
    public final Setting<Boolean> monsterXQZ =
            register(new BooleanSetting("M-XQZ", false));
    public final Setting<Boolean> monsterWireframe =
            register(new BooleanSetting("M-Wireframe", false));
    public final Setting<Color> monsterWireframeColor =
            register(new ColorSetting("M-WireframeColor", new Color(255, 255, 255, 255)));
    public final Setting<Boolean> monsterWireframeXQZ =
            register(new BooleanSetting("M-Wireframe", false));
    public final Setting<Color> monsterWireframeWallsColor =
            register(new ColorSetting("M-WireframeColor", new Color(255, 255, 255, 255)));
    public final Setting<Boolean> monsterLightning =
            register(new BooleanSetting("M-Lightning", false));
    public final Setting<Color> monsterLightningColor =
            register(new ColorSetting("M-LightningColor", new Color(255, 255, 255, 255)));
    public final Setting<Boolean> monsterXQZLightning =
            register(new BooleanSetting("M-XQZLightning", false));
    public final Setting<Color> monsterWallsLightningColor =
            register(new ColorSetting("M-WallsLightningColor", new Color(255, 255, 255, 255)));
    public final Setting<Boolean> monsterGlint =
            register(new BooleanSetting("M-Glint", false));
    public final Setting<Color> monsterGlintColor =
            register(new ColorSetting("M-GlintColor", new Color(255, 255, 255, 255)));
    public final Setting<Boolean> monsterXQZGlint =
            register(new BooleanSetting("M-XQZGlint", false));
    public final Setting<Color> monsterWallsGlintColor =
            register(new ColorSetting("M-WallsGlintColor", new Color(255, 255, 255, 255)));
    public final Setting<Float> monsterGlintMultiplier =
            register(new NumberSetting<>("M-GlintSpeed", 1.0f, 0.0f, 5.0f));
    public final Setting<Float> monsterGlintRotate =
            register(new NumberSetting<>("M-GlintRotateMult", 1.0f, 0.0f, 5.0f));
    public final Setting<Float> monsterGlintScale =
            register(new NumberSetting<>("M-GlintScale", 2.0f, 0.1f, 5.0f));
    public final Setting<Integer> monsterGlints =
            register(new NumberSetting<>("M-Glints", 2, 1, 10));
    public final Setting<String> monsterImage =
            register(new StringSetting("M-Image", "None!"));
    public final Setting<String> monsterWallImage =
            register(new StringSetting("M-WallsImage", "None!"));
    public final Setting<Boolean> monsterFit =
            register(new BooleanSetting("M-Fit", false));
    public final Setting<Float> monsterMixFactor =
            register(new NumberSetting<>("M-ImageMixFactor", 1.0f, 0.0f, 1.0f));
    public final Setting<Float> monsterColorMixFactor =
            register(new NumberSetting<>("M-ImageColorMixFactor", 1.0f, 0.0f, 1.0f));

    public final GlShader imageShader = new GlShader("image");
    public boolean renderLayers;
    public boolean forceRenderEntities;
    public final Tuple<ChamsPage, FramebufferWrapper> playerBuffer = new Tuple<>(ChamsPage.Players, new FramebufferWrapper());
    public final Tuple<ChamsPage, FramebufferWrapper> friendBuffer = new Tuple<>(ChamsPage.Friends, new FramebufferWrapper());
    public final Tuple<ChamsPage, FramebufferWrapper> enemyBuffer = new Tuple<>(ChamsPage.Enemies, new FramebufferWrapper());
    public final Tuple<ChamsPage, FramebufferWrapper> crystalBuffer = new Tuple<>(ChamsPage.Crystals, new FramebufferWrapper());
    public final Tuple<ChamsPage, FramebufferWrapper> animalBuffer = new Tuple<>(ChamsPage.Animals, new FramebufferWrapper());
    public final Tuple<ChamsPage, FramebufferWrapper> monsterBuffer = new Tuple<>(ChamsPage.Monsters, new FramebufferWrapper());
    public final GlShader framebufferImageShader = new GlShader("framebufferimage");
    public final GlShader stencilShader = new GlShader("stencil");

    public ReChams()
    {
        super("ReChams", Category.Render);
        this.listeners.add(new ListenerRenderModelPre(this));
        this.listeners.add(new ListenerRenderModelPost(this));
        this.listeners.add(new ListenerRenderCrystalPre(this));
        this.listeners.add(new ListenerRenderCrystalPost(this));
        this.listeners.add(new ListenerRenderEntity(this));
        this.listeners.add(new ListenerRenderWorld(this));
        this.listeners.add(new ListenerPreRenderHud(this));
        this.listeners.add(new ListenerRender3D(this));
        this.listeners.add(new ListenerRenderEntityPost(this));
        this.listeners.add(new ListenerRender2D(this));
        this.listeners.add(new ListenerBeginRender(this));
        this.listeners.add(new ListenerRenderCrystalCube(this));
        this.listeners.add(new ListenerRenderArmor(this));
        new PageBuilder<>(this, page)
                .addPage(p -> p == ChamsPage.Players, playerMode, alphaTest)
                .addPage(p -> p == ChamsPage.Friends, friendMode, friendAlphaTest)
                .addPage(p -> p == ChamsPage.Enemies, enemyMode, enemyAlphaTest)
                .addPage(p -> p == ChamsPage.Crystals, crystalMode, crystalAlphaTest)
                .addPage(p -> p == ChamsPage.Animals, animalMode, animalColorMixFactor)
                .addPage(p -> p == ChamsPage.Monsters, monsterMode, monsterColorMixFactor)
                .register(Visibilities.VISIBILITY_MANAGER);
        this.setData(new SimpleData(this, "Da best in the game."));
    }

    public ChamsMode getModeFromEntity(Entity entity)
    {
        if (entity instanceof EntityPlayer)
        {
            if (Managers.FRIENDS.contains(entity))
            {
                return friendMode.getValue();
            }
            else if (Managers.ENEMIES.contains(entity))
            {
                return enemyMode.getValue();
            }
            else
            {
                return playerMode.getValue();
            }
        }
        else
        {
            if (entity instanceof EntityEnderCrystal)
            {
                return crystalMode.getValue();
            }
            else if (EntityType.isAngry(entity) || EntityType.isAnimal(entity))
            {
                return animalMode.getValue();
            }
            else if (EntityType.isMonster(entity) || EntityType.isBoss(entity))
            {
                return monsterMode.getValue();
            }
        }
        return ChamsMode.None;
    }

    public boolean shouldXQZ(Entity entity)
    {
        if (entity instanceof EntityPlayer)
        {
            if (Managers.FRIENDS.contains(entity))
            {
                return friendXQZ.getValue();
            }
            else if (Managers.ENEMIES.contains(entity))
            {
                return enemyXQZ.getValue();
            }
            else
            {
                return xqz.getValue();
            }
        }
        else
        {
            if (entity instanceof EntityEnderCrystal)
            {
                return crystalXQZ.getValue();
            }
            else if (EntityType.isAngry(entity) || EntityType.isAnimal(entity))
            {
                return animalXQZ.getValue();
            }
            else if (EntityType.isMonster(entity) || EntityType.isBoss(entity))
            {
                return monsterXQZ.getValue();
            }
        }
        return false;
    }

    public Color getColor(Entity entity)
    {
        if (entity instanceof EntityPlayer)
        {
            if (Managers.FRIENDS.contains(entity))
            {
                return friendColor.getValue();
            }
            else if (Managers.ENEMIES.contains(entity))
            {
                return enemyColor.getValue();
            }
            else
            {
                return color.getValue();
            }
        }
        else
        {
            if (entity instanceof EntityEnderCrystal)
            {
                return crystalColor.getValue();
            }
            else if (EntityType.isAngry(entity) || EntityType.isAnimal(entity))
            {
                return animalColor.getValue();
            }
            else if (EntityType.isMonster(entity) || EntityType.isBoss(entity))
            {
                return monsterColor.getValue();
            }
        }
        return Color.WHITE;
    }

    public Color getWallsColor(Entity entity)
    {
        if (entity instanceof EntityPlayer)
        {
            if (Managers.FRIENDS.contains(entity))
            {
                return friendColor.getValue();
            }
            else if (Managers.ENEMIES.contains(entity))
            {
                return enemyColor.getValue();
            }
            else
            {
                return color.getValue();
            }
        }
        else
        {
            if (entity instanceof EntityEnderCrystal)
            {
                return crystalColor.getValue();
            }
            else if (EntityType.isAngry(entity) || EntityType.isAnimal(entity))
            {
                return animalColor.getValue();
            }
            else if (EntityType.isMonster(entity) || EntityType.isBoss(entity))
            {
                return monsterColor.getValue();
            }
        }
        return Color.WHITE;
    }

    public Color getArmorColor(Entity entity)
    {
        if (entity instanceof EntityPlayer)
        {
            if (Managers.FRIENDS.contains(entity))
            {
                return friendArmorColor.getValue();
            }
            else if (Managers.ENEMIES.contains(entity))
            {
                return enemyArmorColor.getValue();
            }
            else
            {
                return armorColor.getValue();
            }
        }
        return Color.WHITE;
    }

    public Color getArmorWallsColor(Entity entity)
    {
        if (entity instanceof EntityPlayer)
        {
            if (Managers.FRIENDS.contains(entity))
            {
                return friendArmorWallsColor.getValue();
            }
            else if (Managers.ENEMIES.contains(entity))
            {
                return enemyArmorWallsColor.getValue();
            }
            else
            {
                return armorWallsColor.getValue();
            }
        }
        return Color.WHITE;
    }

    public Color getGlintColor(Entity entity)
    {
        if (entity instanceof EntityPlayer)
        {
            if (Managers.FRIENDS.contains(entity))
            {
                return friendGlintColor.getValue();
            }
            else if (Managers.ENEMIES.contains(entity))
            {
                return enemyGlintColor.getValue();
            }
            else
            {
                return glintColor.getValue();
            }
        }
        else
        {
            if (entity instanceof EntityEnderCrystal)
            {
                return crystalGlintColor.getValue();
            }
            else if (EntityType.isAngry(entity) || EntityType.isAnimal(entity))
            {
                return animalGlintColor.getValue();
            }
            else if (EntityType.isMonster(entity) || EntityType.isBoss(entity))
            {
                return monsterGlintColor.getValue();
            }
        }
        return Color.WHITE;
    }

    public Color getGlintWallsColor(Entity entity)
    {
        if (entity instanceof EntityPlayer)
        {
            if (Managers.FRIENDS.contains(entity))
            {
                return friendWallsGlintColor.getValue();
            }
            else if (Managers.ENEMIES.contains(entity))
            {
                return enemyWallsGlintColor.getValue();
            }
            else
            {
                return wallsGlintColor.getValue();
            }
        }
        else
        {
            if (entity instanceof EntityEnderCrystal)
            {
                return crystalWallsGlintColor.getValue();
            }
            else if (EntityType.isAngry(entity) || EntityType.isAnimal(entity))
            {
                return animalWallsGlintColor.getValue();
            }
            else if (EntityType.isMonster(entity) || EntityType.isBoss(entity))
            {
                return monsterWallsGlintColor.getValue();
            }
        }
        return Color.WHITE;
    }

    public Color getLightningColor(Entity entity)
    {
        if (entity instanceof EntityPlayer)
        {
            if (Managers.FRIENDS.contains(entity))
            {
                return friendLightningColor.getValue();
            }
            else if (Managers.ENEMIES.contains(entity))
            {
                return enemyLightningColor.getValue();
            }
            else
            {
                return lightningColor.getValue();
            }
        }
        else
        {
            if (entity instanceof EntityEnderCrystal)
            {
                return crystalLightningColor.getValue();
            }
            else if (EntityType.isAngry(entity) || EntityType.isAnimal(entity))
            {
                return animalLightningColor.getValue();
            }
            else if (EntityType.isMonster(entity) || EntityType.isBoss(entity))
            {
                return monsterLightningColor.getValue();
            }
        }
        return Color.WHITE;
    }

    public Color getLightningWallsColor(Entity entity)
    {
        if (entity instanceof EntityPlayer)
        {
            if (Managers.FRIENDS.contains(entity))
            {
                return friendWallsLightningColor.getValue();
            }
            else if (Managers.ENEMIES.contains(entity))
            {
                return enemyWallsLightningColor.getValue();
            }
            else
            {
                return wallsLightningColor.getValue();
            }
        }
        else
        {
            if (entity instanceof EntityEnderCrystal)
            {
                return crystalWallsLightningColor.getValue();
            }
            else if (EntityType.isAngry(entity) || EntityType.isAnimal(entity))
            {
                return animalWallsLightningColor.getValue();
            }
            else if (EntityType.isMonster(entity) || EntityType.isBoss(entity))
            {
                return monsterWallsLightningColor.getValue();
            }
        }
        return Color.WHITE;
    }

    public boolean shouldGlint(Entity entity)
    {
        if (entity instanceof EntityPlayer)
        {
            if (Managers.FRIENDS.contains(entity))
            {
                return friendGlint.getValue();
            }
            else if (Managers.ENEMIES.contains(entity))
            {
                return enemyGlint.getValue();
            }
            else
            {
                return glint.getValue();
            }
        }
        else
        {
            if (entity instanceof EntityEnderCrystal)
            {
                return crystalGlint.getValue();
            }
            else if (EntityType.isAngry(entity) || EntityType.isAnimal(entity))
            {
                return animalGlint.getValue();
            }
            else if (EntityType.isMonster(entity) || EntityType.isBoss(entity))
            {
                return monsterGlint.getValue();
            }
        }
        return false;
    }

    public boolean shouldLightning(Entity entity)
    {
        if (entity instanceof EntityPlayer)
        {
            if (Managers.FRIENDS.contains(entity))
            {
                return friendLightning.getValue();
            }
            else if (Managers.ENEMIES.contains(entity))
            {
                return enemyLightning.getValue();
            }
            else
            {
                return lightning.getValue();
            }
        }
        else
        {
            if (entity instanceof EntityEnderCrystal)
            {
                return crystalLightning.getValue();
            }
            else if (EntityType.isAngry(entity) || EntityType.isAnimal(entity))
            {
                return animalLightning.getValue();
            }
            else if (EntityType.isMonster(entity) || EntityType.isBoss(entity))
            {
                return monsterLightning.getValue();
            }
        }
        return false;
    }

    public boolean shouldWallsGlint(Entity entity)
    {
        if (entity instanceof EntityPlayer)
        {
            if (Managers.FRIENDS.contains(entity))
            {
                return friendXQZGlint.getValue();
            }
            else if (Managers.ENEMIES.contains(entity))
            {
                return enemyXQZGlint.getValue();
            }
            else
            {
                return xqzGlint.getValue();
            }
        }
        else
        {
            if (entity instanceof EntityEnderCrystal)
            {
                return crystalXQZGlint.getValue();
            }
            else if (EntityType.isAngry(entity) || EntityType.isAnimal(entity))
            {
                return animalXQZGlint.getValue();
            }
            else if (EntityType.isMonster(entity) || EntityType.isBoss(entity))
            {
                return monsterXQZGlint.getValue();
            }
        }
        return false;
    }

    public boolean shouldWallsLightning(Entity entity)
    {
        if (entity instanceof EntityPlayer)
        {
            if (Managers.FRIENDS.contains(entity))
            {
                return friendXQZ.getValue();
            }
            else if (Managers.ENEMIES.contains(entity))
            {
                return enemyXQZ.getValue();
            }
            else
            {
                return xqz.getValue();
            }
        }
        else
        {
            if (entity instanceof EntityEnderCrystal)
            {
                return crystalXQZ.getValue();
            }
            else if (EntityType.isAngry(entity) || EntityType.isAnimal(entity))
            {
                return animalXQZ.getValue();
            }
            else if (EntityType.isMonster(entity) || EntityType.isBoss(entity))
            {
                return monsterXQZ.getValue();
            }
        }
        return false;
    }

    public boolean shouldFit(Entity entity)
    {
        return (entity instanceof EntityPlayer ? (Managers.FRIENDS.contains(entity) ? friendFit.getValue() : (Managers.ENEMIES.contains(entity) ? enemyFit.getValue() : fit.getValue())) : (entity instanceof EntityEnderCrystal ? crystalFit.getValue() : ((EntityType.isAngry(entity) || EntityType.isAnimal(entity)) ? animalFit.getValue() : ((EntityType.isMonster(entity) || EntityType.isBoss(entity)) ? monsterXQZ.getValue() : false))));
    }

    // TODO: add fill settings for more entity types!
    @SuppressWarnings("all")
    public boolean shouldFill(Entity entity)
    {
        return (entity instanceof EntityPlayer ? (Managers.FRIENDS.contains(entity) ? friendFill.getValue() : (Managers.ENEMIES.contains(entity) ? enemyFill.getValue() : fill.getValue())) : (entity instanceof EntityEnderCrystal ? crystalFill.getValue() : ((EntityType.isAngry(entity) || EntityType.isAnimal(entity)) ? false : ((EntityType.isMonster(entity) || EntityType.isBoss(entity)) ? false : false))));
    }

    @SuppressWarnings("all")
    public boolean shouldGif(Entity entity)
    {
        return (entity instanceof EntityPlayer ? (Managers.FRIENDS.contains(entity) ? friendUseGif.getValue() : (Managers.ENEMIES.contains(entity) ? enemyUseGif.getValue() : useGif.getValue())) : (entity instanceof EntityEnderCrystal ? crystalUseGif.getValue() : ((EntityType.isAngry(entity) || EntityType.isAnimal(entity)) ? false : ((EntityType.isMonster(entity) || EntityType.isBoss(entity)) ? false : false))));
    }

    public boolean shouldWallsGif(Entity entity)
    {
        return (entity instanceof EntityPlayer ? (Managers.FRIENDS.contains(entity) ? friendUseWallsGif.getValue() : (Managers.ENEMIES.contains(entity) ? enemyUseWallsGif.getValue() : useWallsGif.getValue())) : (entity instanceof EntityEnderCrystal ? crystalUseWallsGif.getValue() : ((EntityType.isAngry(entity) || EntityType.isAnimal(entity)) ? false : ((EntityType.isMonster(entity) || EntityType.isBoss(entity)) ? false : false))));
    }

    public boolean shouldGif(ChamsPage entity)
    {
        switch (entity)
        {
            case Players:
                return useGif.getValue();
            case Friends:
                return friendUseGif.getValue();
            case Enemies:
                return enemyUseGif.getValue();
            case Crystals:
                return crystalUseGif.getValue();
            default:
                return false;
        }
    }

    public boolean shouldWallsGif(ChamsPage entity)
    {
        switch (entity)
        {
            case Players:
                return useWallsGif.getValue();
            case Friends:
                return friendUseWallsGif.getValue();
            case Enemies:
                return enemyUseWallsGif.getValue();
            case Crystals:
                return crystalUseWallsGif.getValue();
            default:
                return false;
        }
    }

    public GifImage getGif(Entity entity)
    {
        return (entity instanceof EntityPlayer ? (Managers.FRIENDS.contains(entity) ? friendGif.getValue() : (Managers.ENEMIES.contains(entity) ? enemyGif.getValue() : gif.getValue())) : (entity instanceof EntityEnderCrystal ? crystalGif.getValue() : ((EntityType.isAngry(entity) || EntityType.isAnimal(entity)) ? null : ((EntityType.isMonster(entity) || EntityType.isBoss(entity)) ? null : null))));
    }

    public GifImage getWallsGif(Entity entity)
    {
        return (entity instanceof EntityPlayer ? (Managers.FRIENDS.contains(entity) ? friendWallsGif.getValue() : (Managers.ENEMIES.contains(entity) ? enemyWallsGif.getValue() : wallsGif.getValue())) : (entity instanceof EntityEnderCrystal ? crystalWallsGif.getValue() : ((EntityType.isAngry(entity) || EntityType.isAnimal(entity)) ? null : ((EntityType.isMonster(entity) || EntityType.isBoss(entity)) ? null : null))));
    }

    public NameableImage getImage(Entity entity)
    {
        return (entity instanceof EntityPlayer ? (Managers.FRIENDS.contains(entity) ? friendImage.getValue() : (Managers.ENEMIES.contains(entity) ? enemyImage.getValue() : image.getValue())) : (entity instanceof EntityEnderCrystal ? crystalImage.getValue() : ((EntityType.isAngry(entity) || EntityType.isAnimal(entity)) ? null : ((EntityType.isMonster(entity) || EntityType.isBoss(entity)) ? null : null))));
    }

    public NameableImage getWallsImage(Entity entity)
    {
        return (entity instanceof EntityPlayer ? (Managers.FRIENDS.contains(entity) ? friendWallsImage.getValue() : (Managers.ENEMIES.contains(entity) ? enemyWallsImage.getValue() : wallsImage.getValue())) : (entity instanceof EntityEnderCrystal ? crystalWallsImage.getValue() : ((EntityType.isAngry(entity) || EntityType.isAnimal(entity)) ? null : ((EntityType.isMonster(entity) || EntityType.isBoss(entity)) ? null : null))));
    }

    public GifImage getGif(ChamsPage page)
    {
        switch (page)
        {
            case Players:
                return gif.getValue();
            case Enemies:
                return enemyGif.getValue();
            case Friends:
                return friendGif.getValue();
            case Crystals:
                return crystalGif.getValue();
            default:
                return null;
        }
    }

    public GifImage getWallsGif(ChamsPage page)
    {
        switch (page)
        {
            case Players:
                return wallsGif.getValue();
            case Enemies:
                return enemyWallsGif.getValue();
            case Friends:
                return friendWallsGif.getValue();
            case Crystals:
                return crystalWallsGif.getValue();
            default:
                return null;
        }
    }

    public NameableImage getImage(ChamsPage page)
    {
        switch (page)
        {
            case Players:
                return image.getValue();
            case Enemies:
                return enemyImage.getValue();
            case Friends:
                return friendImage.getValue();
            case Crystals:
                return crystalImage.getValue();
            default:
                return null;
        }
    }

    public NameableImage getWallsImage(ChamsPage page)
    {
        switch (page)
        {
            case Players:
                return wallsImage.getValue();
            case Enemies:
                return enemyWallsImage.getValue();
            case Friends:
                return friendWallsImage.getValue();
            case Crystals:
                return crystalWallsImage.getValue();
            default:
                return null;
        }
    }

    public float getMixFactor(Entity entity)
    {
        return (entity instanceof EntityPlayer ? (Managers.FRIENDS.contains(entity) ? friendMixFactor.getValue() : (Managers.ENEMIES.contains(entity) ? enemyMixFactor.getValue() : mixFactor.getValue())) : (entity instanceof EntityEnderCrystal ? crystalMixFactor.getValue() : ((EntityType.isAngry(entity) || EntityType.isAnimal(entity)) ? animalMixFactor.getValue() : ((EntityType.isMonster(entity) || EntityType.isBoss(entity)) ? monsterMixFactor.getValue() : 1.0f))));
    }

    public float getColorMixFactor(Entity entity)
    {
        return (entity instanceof EntityPlayer ? (Managers.FRIENDS.contains(entity) ? friendColorMixFactor.getValue() : (Managers.ENEMIES.contains(entity) ? enemyColorMixFactor.getValue() : colorMixFactor.getValue())) : (entity instanceof EntityEnderCrystal ? crystalColorMixFactor.getValue() : ((EntityType.isAngry(entity) || EntityType.isAnimal(entity)) ? animalColorMixFactor.getValue() : ((EntityType.isMonster(entity) || EntityType.isBoss(entity)) ? monsterColorMixFactor.getValue() : 0.0f))));
    }

    public boolean shouldAlphaTest(Entity entity)
    {
        return (entity instanceof EntityPlayer ? (Managers.FRIENDS.contains(entity) ? friendAlphaTest.getValue() : (Managers.ENEMIES.contains(entity) ? enemyAlphaTest.getValue() : alphaTest.getValue())) : (entity instanceof EntityEnderCrystal ? crystalAlphaTest.getValue() : ((EntityType.isAngry(entity) || EntityType.isAnimal(entity)) ? false : ((EntityType.isMonster(entity) || EntityType.isBoss(entity)) ? false : false))));
    }

    public ChamsMode[] getAllCurrentModes()
    {
        ChamsMode[] toReturn = new ChamsMode[6];
        toReturn[0] = playerMode.getValue();
        toReturn[1] = friendMode.getValue();
        toReturn[2] = enemyMode.getValue();
        toReturn[3] = crystalMode.getValue();
        toReturn[4] = animalMode.getValue();
        toReturn[5] = monsterMode.getValue();
        return toReturn;
    }

    public Tuple<ChamsPage, FramebufferWrapper> getFrameBufferFromEntity(Entity entity)
    {
        return (entity instanceof EntityPlayer ? (Managers.FRIENDS.contains(entity) ? friendBuffer : (Managers.ENEMIES.contains(entity) ? enemyBuffer : playerBuffer)) : (entity instanceof EntityEnderCrystal ? crystalBuffer : ((EntityType.isAngry(entity) || EntityType.isAnimal(entity)) ? animalBuffer : ((EntityType.isMonster(entity) || EntityType.isBoss(entity)) ? monsterBuffer : null))));
    }

    public List<Tuple<ChamsPage, FramebufferWrapper>> getFramebuffers()
    {
        List<Tuple<ChamsPage, FramebufferWrapper>> toReturn = new ArrayList<>();
        toReturn.add(playerBuffer);
        toReturn.add(friendBuffer);
        toReturn.add(enemyBuffer);
        toReturn.add(crystalBuffer);
        toReturn.add(animalBuffer);
        toReturn.add(monsterBuffer);
        return toReturn;
    }

    public List<Tuple<ChamsPage, FramebufferWrapper>> getPlayerBuffers()
    {
        List<Tuple<ChamsPage, FramebufferWrapper>> toReturn = new ArrayList<>();
        toReturn.add(playerBuffer);
        toReturn.add(friendBuffer);
        toReturn.add(enemyBuffer);
        return toReturn;
    }

    public List<Tuple<ChamsPage, FramebufferWrapper>> getFramebuffersFromMode(ChamsMode mode)
    {
        List<Tuple<ChamsPage, FramebufferWrapper>> toReturn = new ArrayList<>();
        if (crystalMode.getValue() == mode) toReturn.add(crystalBuffer);
        if (playerMode.getValue() == mode) toReturn.add(playerBuffer);
        if (friendMode.getValue() == mode) toReturn.add(friendBuffer);
        if (enemyMode.getValue() == mode) toReturn.add(enemyBuffer);
        if (animalMode.getValue() == mode) toReturn.add(animalBuffer);
        if (monsterMode.getValue() == mode) toReturn.add(monsterBuffer);
        return toReturn;
    }

    public List<ChamsPage> getPagesFromMode(ChamsMode mode)
    {
        List<ChamsPage> toReturn = new ArrayList<>();
        if (crystalMode.getValue() == mode) toReturn.add(ChamsPage.Crystals);
        if (playerMode.getValue() == mode) toReturn.add(ChamsPage.Players);
        if (friendMode.getValue() == mode) toReturn.add(ChamsPage.Friends);
        if (enemyMode.getValue() == mode) toReturn.add(ChamsPage.Enemies);
        if (animalMode.getValue() == mode) toReturn.add(ChamsPage.Animals);
        if (monsterMode.getValue() == mode) toReturn.add(ChamsPage.Monsters);
        return toReturn;
    }

    public Predicate<Entity> getEntityPredicate(ChamsMode mode)
    {
        return entity ->
        {
            return mode == getModeFromEntity(entity);
        };
    }

    public boolean isValid(Entity entity, ChamsMode mode)
    {
        return getEntityPredicate(mode).test(entity);
    }

}
