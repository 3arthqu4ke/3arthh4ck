package me.earth.hudplugin;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.module.util.Hidden;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.events.render.Render2DEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.hud.modes.HudRainbow;
import me.earth.earthhack.impl.modules.misc.tooltips.ToolTips;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.network.ServerUtil;
import me.earth.earthhack.impl.util.render.ColorUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.hudplugin.mixin.IMinecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HUDModule extends Module
{
    private static final ModuleCache<ToolTips> TOOL_TIPS = Caches.getModule(ToolTips.class);
    private static final SettingCache<Boolean, BooleanSetting, ToolTips> SHULKER_SPY =
            Caches.getSetting(ToolTips.class, BooleanSetting.class, "ShulkerSpy", false);

    protected final Setting<HudRainbow> colorMode    =
            register(new EnumSetting<>("Rainbow", HudRainbow.None));
    protected final Setting<Color> color =
            register(new ColorSetting("Color", Color.WHITE));
    private final Setting<Boolean> renderingUp = register(new BooleanSetting("RenderingUp", false));
    private final Setting<WaterMark> watermark = register(new EnumSetting<>("Logo", WaterMark.NONE));
    private final Setting<String> customWatermark = register(new StringSetting("WatermarkName", "3arthh4ck"));
    private final Setting<Boolean> modeVer = register(new BooleanSetting("Version", false));
    private final Setting<Boolean> arrayList = register(new BooleanSetting("ActiveModules", false));
    public Setting<Integer> animationHorizontalTime = register(new NumberSetting<>("AnimationHTime", 500, 1, 1000));
    private final Setting<Boolean> alphabeticalSorting = register(new BooleanSetting("AlphabeticalSorting", false));
    private final Setting<Boolean> serverBrand = register(new BooleanSetting("ServerBrand", false));
    private final Setting<Boolean> ping = register(new BooleanSetting("Ping", false));
    private final Setting<Boolean> tps = register(new BooleanSetting("TPS", false));
    private final Setting<Boolean> fps = register(new BooleanSetting("FPS", false));
    private final Setting<Boolean> coords = register(new BooleanSetting("Coords", false));
    private final Setting<Boolean> direction = register(new BooleanSetting("Direction", false));
    private final Setting<Boolean> speed = register(new BooleanSetting("Speed", false));
    private final Setting<Boolean> potions = register(new BooleanSetting("Potions", false));
    private final Setting<Boolean> altPotionsColors = register(new BooleanSetting("AltPotionColors", false));
    public Setting<Boolean> textRadar = register(new BooleanSetting("TextRadar", false));
    private final Setting<Boolean> armor = register(new BooleanSetting("Armor", false));
    private final Setting<Boolean> durability = register(new BooleanSetting("Durability", false));
    private final Setting<Boolean> percent = register(new BooleanSetting("Percent", true));
    private final Setting<Boolean> totems = register(new BooleanSetting("Totems", false));
    private final Setting<Greeter> greeter = register(new EnumSetting<>("Greeter", Greeter.NONE));
    private final Setting<String> spoofGreeter = register(new StringSetting("GreeterName", "3arthqu4ke"));
    public Setting<Boolean> time = register(new BooleanSetting("Time", false));
    private final Setting<LagNotify> lag = register(new EnumSetting<>("Lag", LagNotify.GRAY));
    private final Setting<Boolean> hitMarkers = register(new BooleanSetting("HitMarkers", true));
    private final Setting<Boolean> grayNess = register(new BooleanSetting("FutureColour", true));
    public Setting<Boolean> potions1 = register(new BooleanSetting("LevelPotions", false));
    public Setting<Boolean> MS = register(new BooleanSetting("ms", false));

    private static HUDModule INSTANCE = new HUDModule();

    public static String theServerBrand;

    private Map<String, Integer> players = new HashMap<>();

    private Map<Potion, Color> potionColorMap = new HashMap<>();

    private static final ResourceLocation box = new ResourceLocation("textures/gui/container/shulker_box.png");
    private static final ItemStack totem = new ItemStack(Items.TOTEM_OF_UNDYING);
    private boolean shouldIncrement;
    private int hitMarkerTimer;
    private final StopWatch timer = new StopWatch();
    private final StopWatch moduleTimer = new StopWatch();
    public Map<Integer, Integer> colorMap = new HashMap<>();

    private static final ResourceLocation codHitmarker = new ResourceLocation("earthhack", "cod_hitmarker");
    private static final ResourceLocation csgoHitmarker = new ResourceLocation("earthhack","csgo_hitmarker");
    public static final SoundEvent COD_EVENT = new SoundEvent(codHitmarker);
    public static final SoundEvent CSGO_EVENT = new SoundEvent(csgoHitmarker);

    private final Map<Module, Boolean> slidingMap = new HashMap<>();
    private final Map<Module, Integer> arrayListOffset = new HashMap<>();
    private List<Module> sortedModules = new ArrayList<>();
    private ScaledResolution resolution;

    public HUDModule() {
        super("Phobos-HUD", Category.Client);
        setInstance();
        potionColorMap.put(MobEffects.SPEED, new Color(124, 175, 198));
        potionColorMap.put(MobEffects.SLOWNESS, new Color(90, 108, 129));
        potionColorMap.put(MobEffects.HASTE, new Color(217, 192, 67));
        potionColorMap.put(MobEffects.MINING_FATIGUE, new Color(74, 66, 23));
        potionColorMap.put(MobEffects.STRENGTH, new Color(147, 36, 35));
        potionColorMap.put(MobEffects.INSTANT_HEALTH, new Color(67, 10, 9));
        potionColorMap.put(MobEffects.INSTANT_DAMAGE, new Color(67, 10, 9));
        potionColorMap.put(MobEffects.JUMP_BOOST, new Color(34, 255, 76));
        potionColorMap.put(MobEffects.NAUSEA, new Color(85, 29, 74));
        potionColorMap.put(MobEffects.REGENERATION, new Color(205, 92, 171));
        potionColorMap.put(MobEffects.RESISTANCE, new Color(153, 69, 58));
        potionColorMap.put(MobEffects.FIRE_RESISTANCE, new Color(228, 154, 58));
        potionColorMap.put(MobEffects.WATER_BREATHING, new Color(46, 82, 153));
        potionColorMap.put(MobEffects.INVISIBILITY, new Color(127, 131, 146));
        potionColorMap.put(MobEffects.BLINDNESS, new Color(31, 31, 35));
        potionColorMap.put(MobEffects.NIGHT_VISION, new Color(31, 31, 161));
        potionColorMap.put(MobEffects.HUNGER, new Color(88, 118, 83));
        potionColorMap.put(MobEffects.WEAKNESS, new Color(72, 77, 72));
        potionColorMap.put(MobEffects.POISON, new Color(78, 147, 49));
        potionColorMap.put(MobEffects.WITHER, new Color(53, 42, 39));
        potionColorMap.put(MobEffects.HEALTH_BOOST, new Color(248, 125, 35));
        potionColorMap.put(MobEffects.ABSORPTION, new Color(37, 82, 165));
        potionColorMap.put(MobEffects.SATURATION, new Color(248, 36, 35));
        potionColorMap.put(MobEffects.GLOWING, new Color(148, 160, 97));
        potionColorMap.put(MobEffects.LEVITATION, new Color(206, 255, 255));
        potionColorMap.put(MobEffects.LUCK, new Color(51, 153, 0));
        potionColorMap.put(MobEffects.UNLUCK, new Color(192, 164, 77));
        resolution = new ScaledResolution(mc);
        this.listeners.add(new EventListener<UpdateEvent>(UpdateEvent.class)
        {
            @Override
            public void invoke(UpdateEvent tickEvent)
            {
                onTick();
            }
        });
        this.listeners.add(new EventListener<Render2DEvent>(Render2DEvent.class)
        {
            @Override
            public void invoke(Render2DEvent event)
            {
                onRender2D(event);
            }
        });
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static HUDModule getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HUDModule();
        }
        return INSTANCE;
    }

    public void renderText(String text, float x, float y) {
        String colorCode = colorMode.getValue().getColor();
        Managers.TEXT.drawStringWithShadow(colorCode + text,
                x,
                y,
                colorMode.getValue() == HudRainbow.None
                        ? color.getValue().getRGB()
                        : 0xffffffff);
    }

    public void onTick() {
        resolution = new ScaledResolution(mc);
        Comparator<Module> comparator = alphabeticalSorting.getValue()
                ? Comparator.comparing(Module::getDisplayName)
                : Comparator.comparing(module -> Managers.TEXT.getStringWidth(ModuleUtil.getHudName(module)) * -1);

        sortedModules = Managers
                    .MODULES
                    .getRegistered()
                    .stream()
                    .filter(m -> m.isEnabled() && m.isHidden() != Hidden.Hidden)
                    .sorted(comparator)
                    .collect(Collectors.toList());

        for (Module module : sortedModules) {
            if (!module.isEnabled())
            {
                Integer arraylistOffset = arrayListOffset.get(module);
                if (arraylistOffset == null || arraylistOffset == 0)
                {
                    slidingMap.put(module, true);
                }
            }
        }

        this.players = getTextRadarPlayers();

        if (shouldIncrement) {
            hitMarkerTimer++;
        }

        if (hitMarkerTimer == 10) {
            hitMarkerTimer = 0;
            shouldIncrement = false;
        }
    }

    public void onRender2D(Render2DEvent event) {
        int width = resolution.getScaledWidth();
        int height = resolution.getScaledHeight();

        GlStateManager.pushMatrix();

        String grayString = (grayNess.getValue() ? TextColor.GRAY : "");

        switch (watermark.getValue()) {
            case PHOBOS:
                renderText("Phobos" + (modeVer.getValue() ? " v" + Earthhack.VERSION : ""), 2, 2);
                break;
            case EARTH:
                renderText("3arthh4ck" + (modeVer.getValue() ? " v" + Earthhack.VERSION : ""), 2, 2);
                break;
            case CUSTOM:
                renderText(customWatermark.getValue() + (modeVer.getValue() ? " v" + Earthhack.VERSION : ""), 2, 2);
            default:
        }

        if (textRadar.getValue()) {
            drawTextRadar(0);
        }

        int j = renderingUp.getValue() ? 0 : (mc.currentScreen instanceof GuiChat ? 14 : 0);
        if (arrayList.getValue()) {
            if (renderingUp.getValue()) {
                for (int i = 0; i < sortedModules.size(); i++) {
                    Module module = sortedModules.get(i);
                    String text = ModuleUtil.getHudName(module);
                    renderText(text, width - 2 - Managers.TEXT.getStringWidth(text) + (animationHorizontalTime.getValue() == 1 ? 0 : arrayListOffset.getOrDefault(module, 0)), 2 + j * 10);
                    j++;
                }
            } else {
                for (int i = 0; i < sortedModules.size(); i++) {
                    Module module = sortedModules.get(i);
                    String text = ModuleUtil.getHudName(module);
                    renderText(text, width - 2 - Managers.TEXT.getStringWidth(text) + (animationHorizontalTime.getValue() == 1 ? 0 : arrayListOffset.getOrDefault(module, 0)), height - (j += 10));
                }
            }
        }

        int i = !renderingUp.getValue() ? 0 : (mc.currentScreen instanceof GuiChat) ? 0 : 0;
        if (renderingUp.getValue()) {
            if (serverBrand.getValue()) {
                String text = grayString + "Server brand " + TextColor.WHITE + theServerBrand;
                renderText(text, width - (Managers.TEXT.getStringWidth(text) + 2), height - 2 - (i += 10));
            }
            if (potions.getValue()) {
                for (PotionEffect effect : PotionManager.INSTANCE.getOwnPotions()) {
                    String text = altPotionsColors.getValue() ? PotionManager.INSTANCE.getPotionString(effect) : PotionManager.INSTANCE.getColoredPotionString(effect);
                    renderText(text, width - (Managers.TEXT.getStringWidth(text) + 2), height - 2 - (i += 10));
                }
            }
            if (speed.getValue()) {
                String text = grayString + "Speed " + TextColor.WHITE + MathUtil.round(Managers.SPEED.getSpeed(), 2) + " km/h";
                renderText(text, width - (Managers.TEXT.getStringWidth(text) + 2), height - 2 - (i += 10));
            }
            if (time.getValue()) {
                String text = grayString + "Time " + TextColor.WHITE + (new SimpleDateFormat("h:mm a").format(new Date()));
                renderText(text, width - (Managers.TEXT.getStringWidth(text) + 2), height - 2 - (i += 10));
            }
            if (durability.getValue()) {
                int itemDamage = mc.player.getHeldItemMainhand().getMaxDamage() - mc.player.getHeldItemMainhand().getItemDamage();
                if(itemDamage > 0) {
                    String text = grayString + "Durability " + TextColor.GREEN + itemDamage;
                    renderText(text, width - (Managers.TEXT.getStringWidth(text) + 2), height - 2 - (i += 10));
                }
            }
            if (tps.getValue()) {
                String text = grayString + "TPS " + TextColor.WHITE + MathUtil.round(Managers.TPS.getTps(), 2);
                renderText(text, width - (Managers.TEXT.getStringWidth(text) + 2), height - 2 - (i += 10));
            }
            String fpsText = grayString + "FPS " + TextColor.WHITE + IMinecraft.getDebugFPS();
            String text = grayString + "Ping " + TextColor.WHITE + ServerUtil.getPing() + (MS.getValue() ? "ms" : "");
            if (Managers.TEXT.getStringWidth(text) > Managers.TEXT.getStringWidth(fpsText)) {
                if (ping.getValue()) {
                    renderText(text, width - (Managers.TEXT.getStringWidth(text) + 2), height - 2 - (i += 10));
                }
                if (fps.getValue()) {
                    renderText(fpsText, width - (Managers.TEXT.getStringWidth(fpsText) + 2), height - 2 - (i += 10));
                }
            } else {
                if (fps.getValue()) {
                    renderText(fpsText, width - (Managers.TEXT.getStringWidth(fpsText) + 2), height - 2 - (i += 10));
                }
                if (ping.getValue()) {
                    renderText(text, width - (Managers.TEXT.getStringWidth(text) + 2), height - 2 - (i += 10));
                }
            }
        } else {
            if (serverBrand.getValue()) {
                String text = grayString + "Server brand " + TextColor.WHITE + theServerBrand;
                renderText(text, width - (Managers.TEXT.getStringWidth(text) + 2), 2 + i++ * 10);
            }
            if (potions.getValue()) {
                for (PotionEffect effect : PotionManager.INSTANCE.getOwnPotions()) {
                    String text = altPotionsColors.getValue() ? PotionManager.INSTANCE.getPotionString(effect) : PotionManager.INSTANCE.getColoredPotionString(effect);
                    renderText(text, width - (Managers.TEXT.getStringWidth(text) + 2), 2 + i++ * 10);
                }
            }
            if (speed.getValue()) {
                String text = grayString + "Speed " + TextColor.WHITE + MathUtil.round(Managers.SPEED.getSpeed(), 2) + " km/h";
                renderText(text, width - (Managers.TEXT.getStringWidth(text) + 2), 2 + i++ * 10);
            }
            if (time.getValue()) {
                String text = grayString + "Time " + TextColor.WHITE + (new SimpleDateFormat("h:mm a").format(new Date()));
                renderText(text, width - (Managers.TEXT.getStringWidth(text) + 2), 2 + i++ * 10);
            }
            if (durability.getValue()) {
                int itemDamage = mc.player.getHeldItemMainhand().getMaxDamage() - mc.player.getHeldItemMainhand().getItemDamage();
                if(itemDamage > 0) {
                    String text = grayString + "Durability " + TextColor.GREEN + itemDamage;
                    renderText(text, width - (Managers.TEXT.getStringWidth(text) + 2), 2 + i++ * 10);
                }
            }
            if (tps.getValue()) {
                String text = grayString + "TPS " + TextColor.WHITE + MathUtil.round(Managers.TPS.getTps(), 2);
                renderText(text, width - (Managers.TEXT.getStringWidth(text) + 2), 2 + i++ * 10);
            }
            String fpsText = grayString + "FPS " + TextColor.WHITE + IMinecraft.getDebugFPS();
            String text = grayString + "Ping " + TextColor.WHITE + ServerUtil.getPing();
            if (Managers.TEXT.getStringWidth(text) > Managers.TEXT.getStringWidth(fpsText)) {
                if (ping.getValue()) {
                    renderText(text, width - (Managers.TEXT.getStringWidth(text) + 2), 2 + i++ * 10);
                }
                if (fps.getValue()) {
                    renderText(fpsText, width - (Managers.TEXT.getStringWidth(fpsText) + 2), 2 + i++ * 10);
                }
            } else {
                if (fps.getValue()) {
                    renderText(fpsText, width - (Managers.TEXT.getStringWidth(fpsText) + 2), 2 + i++ * 10);
                }
                if (ping.getValue()) {
                    renderText(text, width - (Managers.TEXT.getStringWidth(text) + 2), 2 + i++ * 10);
                }
            }
        }

        boolean inHell = (mc.world.getBiome(mc.player.getPosition()).getBiomeName().equals("Hell"));

        int posX = (int) mc.player.posX;
        int posY = (int) mc.player.posY;
        int posZ = (int) mc.player.posZ;

        float nether = !inHell ? 0.125f : 8;
        int hposX = (int) (mc.player.posX * nether);
        int hposZ = (int) (mc.player.posZ * nether);

        i = mc.currentScreen instanceof GuiChat ? 14 : 0;
        String coordinates = grayString + "XYZ " + TextColor.WHITE + posX + ", " + posY + ", " + posZ + " " + TextColor.RESET + grayString + "[" + TextColor.WHITE + hposX + ", " + hposZ + TextColor.RESET + grayString + "]";
        String text = (direction.getValue() ? getDirection4D(true) + " " : "") + (coords.getValue() ? coordinates : "") + "";

        renderText(text, 2, height - (i += 10));

        if (armor.getValue()) {
            renderArmorHUD(percent.getValue());
        }

        if (totems.getValue()) {
            renderTotemHUD();
        }

        if (greeter.getValue() != Greeter.NONE) {
            renderGreeter();
        }

        if (lag.getValue() != LagNotify.NONE) {
            renderLag();
        }

        if (hitMarkers.getValue() && hitMarkerTimer > 0) {
            drawHitMarkers();
        }
        GlStateManager.popMatrix();
    }

    public static int getDirection4D() {
        return MathHelper.floor((mc.player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
    }

    public static String getDirection4D(boolean northRed) {
        int dirnumber = getDirection4D();
        if(dirnumber == 0){
            return "South (+Z)";
        }
        if(dirnumber == 1){
            return "West (-X)";
        }
        if(dirnumber == 2){
            return (northRed ? TextColor.RED : "") + "North (-Z)";
        }
        if(dirnumber == 3){
            return "East (+X)";
        }
        return "Loading...";
    }

    public static Map<String, Integer> getTextRadarPlayers() {
        Map<String, Integer> output = new HashMap<>();
        DecimalFormat dfHealth = new DecimalFormat("#.#");
        dfHealth.setRoundingMode(RoundingMode.CEILING);
        DecimalFormat dfDistance = new DecimalFormat("#.#");
        dfDistance.setRoundingMode(RoundingMode.CEILING);
        StringBuilder healthSB = new StringBuilder();
        StringBuilder distanceSB = new StringBuilder();
        for(EntityPlayer player : mc.world.playerEntities) {
            if(player.isInvisible()) {
                continue;
            }

            if (player.getName().equals(mc.player.getName())) {
                continue;
            }
            int hpRaw = (int) EntityUtil.getHealth(player);
            String hp = dfHealth.format(hpRaw);
            healthSB.append(TextColor.SECTIONSIGN);
            if (hpRaw >= 20) {
                healthSB.append("a");
            } else if (hpRaw >= 10) {
                healthSB.append("e");
            } else if (hpRaw >= 5) {
                healthSB.append("6");
            } else {
                healthSB.append("c");
            }
            healthSB.append(hp);
            int distanceInt = (int) mc.player.getDistance(player);
            String distance = dfDistance.format(distanceInt);
            distanceSB.append(TextColor.SECTIONSIGN);
            if (distanceInt >= 25) {
                distanceSB.append("a");
            } else if (distanceInt > 10) {
                distanceSB.append("6");
            } else if (distanceInt >= 50) {
                distanceSB.append("7"); //TODO: Always false!!!
            } else {
                distanceSB.append("c");
            }
            distanceSB.append(distance);
            output.put(healthSB.toString() + " " + (Managers.FRIENDS.contains(player) ? TextColor.AQUA : Managers.ENEMIES.contains(player) ? TextColor.RED : TextColor.RESET) + player.getName() + " " + distanceSB.toString() + " " + TextColor.WHITE + Managers.COMBAT.getPops(player) + PotionManager.INSTANCE.getTextRadarPotion(player), (int) mc.player.getDistance(player));

            healthSB.setLength(0);
            distanceSB.setLength(0);
        }

        if (!output.isEmpty()) {
            output = sortByValue(output, false);
        }
        return output;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, boolean descending) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        if(descending) {
            list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        } else {
            list.sort(Map.Entry.comparingByValue());
        }

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public String getTotemPopString(EntityPlayer player) {
        int pops = Managers.COMBAT.getPops(player);
        return TextColor.WHITE + (pops <= 0 ? "" : "-" + pops + " ");
    }

    public void renderGreeter() {
        int width = resolution.getScaledWidth();
        String text = "";
        switch (greeter.getValue()) {
            case TIME:
                text += getTimeOfDay() + mc.player.getDisplayNameString();
                break;
            case CHRISTMAS:
                text += "Merry Christmas " + mc.player.getDisplayNameString() + " :^)";
                break;
            case LONG:
                text += "Welcome to Phobos.eu " + mc.player.getDisplayNameString() + " :^)";
                break;
            case CUSTOM:
                text += spoofGreeter.getValue();
                break;
            default:
                text += "Welcome " + mc.player.getDisplayNameString();
        }
        renderText(text, (width / 2.0f) - (Managers.TEXT.getStringWidth(text) / 2.0f) + 2, 2);
    }

    public static String getTimeOfDay() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay < 12){
            return "Good Morning ";
        } else if(timeOfDay < 16){
            return "Good Afternoon ";
        } else if(timeOfDay < 21){
            return "Good Evening ";
        } else {
            return "Good Night ";
        }
    }

    public void renderLag() {
        int width = resolution.getScaledWidth();
        if (Managers.SERVER.lastResponse() > 1500) {
            String text = (lag.getValue() == LagNotify.GRAY ? TextColor.GRAY : TextColor.RED) + "Server not responding: " + MathUtil.round((Managers.SERVER.lastResponse() / 1000.0f), 1) + "s.";
            renderText(text, (width / 2.0f) - (Managers.TEXT.getStringWidth(text) / 2.0f) + 2, 20);
        }
    }

    public void renderArrayList() {

    }

    public void renderTotemHUD() {
        int width = resolution.getScaledWidth();
        int height = resolution.getScaledHeight();
        int totems = mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        if (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING)
            totems += mc.player.getHeldItemOffhand().getCount();
        if (totems > 0) {
            GlStateManager.enableTexture2D();
            int i = width / 2;
            int iteration = 0;
            int y = height - 55 - (mc.player.isInWater() && mc.playerController.gameIsSurvivalOrAdventure() ? 10 : 0);
            int x = i - 189 + 9 * 20 + 2;
            GlStateManager.enableDepth();
            mc.getRenderItem().zLevel = 200F;
            mc.getRenderItem().renderItemAndEffectIntoGUI(totem, x, y);
            mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, totem, x, y, "");
            mc.getRenderItem().zLevel = 0F;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            renderText(totems + "", x + 19 - 2 - Managers.TEXT.getStringWidth(totems + ""), y + 9);
            //mc.fontRenderer.drawStringWithShadow(totems + "", x + 19 - 2 - mc.fontRenderer.getStringWidth(totems + ""), y + 9, 0xffffff);
            GlStateManager.enableDepth();
            GlStateManager.disableLighting();
        }
    }

    public void renderArmorHUD(boolean percent) {
        int width = resolution.getScaledWidth();
        int height = resolution.getScaledHeight();
        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();

        int i = width / 2;
        int iteration = 0;
        int y = height - 55 - (mc.player.isInWater() && mc.playerController.gameIsSurvivalOrAdventure() ? 10 : 0);
        for (ItemStack is : mc.player.inventory.armorInventory) {
            iteration++;
            if (is.isEmpty()) continue;
            int x = i - 90 + (9 - iteration) * 20 + 2;
            mc.getRenderItem().zLevel = 200F;
            mc.getRenderItem().renderItemAndEffectIntoGUI(is, x, y);
            mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, is, x, y, "");
            mc.getRenderItem().zLevel = 0F;
            String s = is.getCount() > 1 ? is.getCount() + "" : "";
            renderText(s, x + 19 - 2 - Managers.TEXT.getStringWidth(s), y + 9);
            //mc.fontRenderer.drawStringWithShadow(s, x + 19 - 2 - mc.fontRenderer.getStringWidth(s), y + 9, 0xffffff);

            if (percent) {
                int dmg = 0;
                int itemDurability = is.getMaxDamage() - is.getItemDamage();
                float green = ((float) is.getMaxDamage() - (float) is.getItemDamage()) / (float) is.getMaxDamage();
                float red = 1 - green;
                if (percent) { //(for percent was parted in old phobos)
                    dmg = 100 - (int) (red * 100);
                } else {
                    dmg = itemDurability;
                }
                Managers.TEXT.drawStringWithShadow(dmg + "", x + 8 - Managers.TEXT.getStringWidth(dmg + "") / 2, y - 11, ColorUtil.toARGB((int) (red * 255), (int) (green * 255), 0));
            }
        }

        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    public void drawHitMarkers() {
        //RenderUtil.drawLine(2, 2, 100, 100, 1, ColorUtil.toRGBA(255, 255, 255, 255));
        ScaledResolution resolution = new ScaledResolution(mc);
        drawLine(resolution.getScaledWidth() / 2f - 4, resolution.getScaledHeight() / 2f - 4, resolution.getScaledWidth() / 2f - 8, resolution.getScaledHeight() / 2f - 8, 1f, 0xffffffff);
        drawLine(resolution.getScaledWidth() / 2f + 4, resolution.getScaledHeight() / 2f - 4, resolution.getScaledWidth() / 2f + 8, resolution.getScaledHeight() / 2f - 8, 1f, 0xffffffff);
        drawLine(resolution.getScaledWidth() / 2f - 4, resolution.getScaledHeight() / 2f + 4, resolution.getScaledWidth() / 2f - 8, resolution.getScaledHeight() / 2f + 8, 1f, 0xffffffff);
        drawLine(resolution.getScaledWidth() / 2f + 4, resolution.getScaledHeight() / 2f + 4, resolution.getScaledWidth() / 2f + 8, resolution.getScaledHeight() / 2f + 8, 1f, 0xffffffff);
    }

    public static void drawLine(float x, float y, float x1, float y1, float thickness, int hex) {
        float red = (hex >> 16 & 0xFF) / 255.0F;
        float green = (hex >> 8 & 0xFF) / 255.0F;
        float blue = (hex & 0xFF) / 255.0F;
        float alpha = (hex >> 24 & 0xFF) / 255.0F;

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GL11.glLineWidth(thickness);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double) x, (double) y, (double) 0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos((double) x1, (double) y1, (double) 0).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    public void drawTextRadar(int yOffset) {
        if (!this.players.isEmpty()) {
            int y = (int) (Managers.TEXT.getStringHeight() + 7 + yOffset);
            for (Map.Entry<String, Integer> player : this.players.entrySet()) {
                String text = player.getKey() + " ";
                int textheight = (int) (Managers.TEXT.getStringHeight() + 1);
                renderText(text, 2, y);
                y += textheight;
            }
        }
    }

    public enum Greeter {
        NONE,
        NAME,
        TIME,
        CHRISTMAS,
        LONG,
        CUSTOM
    }

    public enum LagNotify {
        NONE,
        RED,
        GRAY
    }

    public enum WaterMark {
        NONE,
        PHOBOS,
        EARTH,
        CUSTOM
    }

    public enum Sound {
        NONE,
        COD,
        CSGO
    }
}

