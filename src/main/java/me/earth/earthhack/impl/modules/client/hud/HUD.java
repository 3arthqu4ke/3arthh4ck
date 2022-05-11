package me.earth.earthhack.impl.modules.client.hud;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.render.TextRenderer;
import me.earth.earthhack.impl.modules.client.hud.arraylist.ArrayEntry;
import me.earth.earthhack.impl.modules.client.hud.modes.*;
import me.earth.earthhack.impl.modules.client.hud.util.HUDData;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.network.ServerUtil;
import me.earth.earthhack.impl.util.render.ColorHelper;
import me.earth.earthhack.impl.util.render.ColorUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

// TODO: REWRITE?
public class HUD extends Module {
    public static final TextRenderer RENDERER = Managers.TEXT;

    //protected final Setting<RenderMode> renderMode    =
    //register(new EnumSetting<>("RenderMode", RenderMode.NORMAL));
    protected final Setting<RenderMode> renderMode =
            register(new EnumSetting<>("RenderMode", RenderMode.Normal));
    protected final Setting<HudRainbow> colorMode =
            register(new EnumSetting<>("Rainbow", HudRainbow.None));
    protected final Setting<Color> color =
            register(new ColorSetting("Color", Color.WHITE));
    protected final Setting<Boolean> logo =
            register(new BooleanSetting("Logo", true));
    protected final Setting<Boolean> coordinates =
            register(new BooleanSetting("Coordinates", true));
    protected final Setting<Boolean> armor =
            register(new BooleanSetting("Armor", true));
    protected final Setting<Modules> renderModules =
            register(new EnumSetting<>("Modules", Modules.Length));
    protected final Setting<Potions> potions =
            register(new EnumSetting<>("Potions", Potions.Move));
    protected final Setting<PotionColor> potionColor =
            register(new EnumSetting<>("PotionColor", PotionColor.Normal));
    protected final Setting<Boolean> shadow =
            register(new BooleanSetting("Shadow", true));
    protected final Setting<Boolean> ping =
            register(new BooleanSetting("Ping", false));
    protected final Setting<Boolean> speed =
            register(new BooleanSetting("Speed", false));
    protected final Setting<Boolean> fps =
            register(new BooleanSetting("FPS", false));
    protected final Setting<Boolean> tps =
            register(new BooleanSetting("TPS", false));
    protected final Setting<Boolean> currentTps =
            register(new BooleanSetting("CurrentTps", true));
    protected final Setting<Boolean> animations =
            register(new BooleanSetting("Animations", true));

    protected final List<Map.Entry<String, Module>> modules = new ArrayList<>();

    protected final Map<Module, ArrayEntry> arrayEntries = new HashMap<>();
    protected final Map<Module, ArrayEntry> removeEntries = new HashMap<>();

    protected ScaledResolution resolution = new ScaledResolution(mc);
    protected int width;
    protected int height;
    protected float animationY = 0;
    private final Map<Potion, Color> potionColorMap = new HashMap<>();

    public HUD() {
        super("HUD", Category.Client);
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerPostKey(this));
        this.setData(new HUDData(this));
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
    }

    protected void renderLogo() {
        if (logo.getValue()) {
            renderText("3arthh4ck - " + Earthhack.VERSION, 2, 2);
        }
    }

    protected void renderModules() {
        int offset = 0;
        if (potions.getValue() == Potions.Text) {
            final ArrayList<Potion> sorted = new ArrayList<>();
            for (final Potion potion : Potion.REGISTRY) {
                if (potion != null) {
                    if (mc.player.isPotionActive(potion)) {
                        sorted.add(potion);
                    }
                }
            }
            sorted.sort(Comparator.comparingDouble(potion -> -RENDERER.getStringWidth(I18n.format(potion.getName()) + (mc.player.getActivePotionEffect(potion).getAmplifier() > 0 ? " " + (mc.player.getActivePotionEffect(potion).getAmplifier() + 1) : "") + ChatFormatting.GRAY + " " + Potion.getPotionDurationString(Objects.requireNonNull(mc.player.getActivePotionEffect(potion)), 1.0F))));
            for (final Potion potion : sorted) {
                final PotionEffect effect = mc.player.getActivePotionEffect(potion);
                if (effect != null) {
                    final String label = I18n.format(potion.getName()) + (effect.getAmplifier() > 0 ? " " + (effect.getAmplifier() + 1) : "") + ChatFormatting.GRAY + " " + Potion.getPotionDurationString(effect, 1.0F);
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    final int x = width - 2 - RENDERER.getStringWidth(label);
                    renderPotionText(label, x, height - 2 - RENDERER.getStringHeight() - offset - animationY, effect.getPotion());
                    offset += RENDERER.getStringHeight() + 3;
                }
            }
        }
        if (speed.getValue()) {
            String text = "Speed " + TextColor.GRAY + MathUtil.round(Managers.SPEED.getSpeed(), 2) + " km/h";
            renderText(text, width - 2 - RENDERER.getStringWidth(text), height - 2 - RENDERER.getStringHeight() - offset - animationY);
            offset += RENDERER.getStringHeight() + 3;
        }
        if (tps.getValue()) {
            String tps = "TPS " + TextColor.GRAY + MathUtil.round(Managers.TPS.getTps(), 2);
            if (currentTps.getValue())
            {
                tps += TextColor.WHITE + " [" + TextColor.GRAY + MathUtil.round(Managers.TPS.getCurrentTps(), 2) + TextColor.WHITE + "]";
            }

            renderText(tps, width - 2 - RENDERER.getStringWidth(tps), height - 2 - RENDERER.getStringHeight() - offset - animationY);
            offset += RENDERER.getStringHeight() + 3;
        }
        if (fps.getValue()) {
            String fps = "FPS " + TextColor.GRAY + Minecraft.getDebugFPS();
            renderText(fps, width - 2 - RENDERER.getStringWidth(fps), height - 2 - RENDERER.getStringHeight() - offset - animationY);
            offset += RENDERER.getStringHeight() + 3;
        }
        if (ping.getValue()) {
            String ping = "Ping " + TextColor.GRAY + ServerUtil.getPing();
            renderText(ping, width - 2 - RENDERER.getStringWidth(ping), height - 2 - RENDERER.getStringHeight() - offset - animationY);
        }

        if (coordinates.getValue()) {
            final long x = Math.round(mc.player.posX);
            final long y = Math.round(mc.player.posY);
            final long z = Math.round(mc.player.posZ);
            final String coords = mc.player.dimension == -1 ? String.format(ChatFormatting.PREFIX_CODE + "7%s " + ChatFormatting.PREFIX_CODE + "f[%s]" + ChatFormatting.PREFIX_CODE + "8, " + ChatFormatting.PREFIX_CODE + "7%s" + ChatFormatting.PREFIX_CODE + "8, " + ChatFormatting.PREFIX_CODE + "7%s " + ChatFormatting.PREFIX_CODE + "f[%s]", x, x * 8, y, z, z * 8) : (mc.player.dimension == 0 ? String.format(ChatFormatting.PREFIX_CODE + "f%s " + ChatFormatting.PREFIX_CODE + "7[%s]" + ChatFormatting.PREFIX_CODE + "8, " + ChatFormatting.PREFIX_CODE + "f%s" + ChatFormatting.PREFIX_CODE + "8, " + ChatFormatting.PREFIX_CODE + "f%s " + ChatFormatting.PREFIX_CODE + "7[%s]",
                    x,
                    x / 8,
                    y,
                    z,
                    z / 8) : String.format(ChatFormatting.PREFIX_CODE + "f%s" + ChatFormatting.PREFIX_CODE + "8, " + ChatFormatting.PREFIX_CODE + "f%s" + ChatFormatting.PREFIX_CODE + "8, " + ChatFormatting.PREFIX_CODE + "f%s", x, y, z));
            renderText(coords, 2, height - 2 - RENDERER.getStringHeight() - animationY);
            final String dir = RotationUtil.getDirection4D(false);
            renderText(dir, 2, height - 3 - RENDERER.getStringHeight() * 2 - animationY);
        }
        renderArmor();

        if (renderModules.getValue() != Modules.None) {
            boolean move = potions.getValue() == Potions.Move && !mc.player.getActivePotionEffects().isEmpty();
            int j = move ? 2 : 0;
            int o = move ? 5 : 2;
            if (animations.getValue()) {
                for (Map.Entry<String, Module> module : modules) {
                    if (isArrayMember(module.getValue()))
                        continue;
                    getArrayEntries().put(module.getValue(), new ArrayEntry(module.getValue()));
                }
                Map<Module, ArrayEntry> arrayEntriesSorted;
                if (renderModules.getValue() == Modules.Length) {
                    arrayEntriesSorted = getArrayEntries().entrySet().stream().sorted(Comparator.comparingDouble(entry -> Managers.TEXT.getStringWidth(ModuleUtil.getHudName(entry.getKey())) * -1)).collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new));
                } else {
                    arrayEntriesSorted = getArrayEntries().entrySet().stream().sorted(Comparator.comparing(entry -> ModuleUtil.getHudName(entry.getKey()))).collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new));
                }
                for (ArrayEntry arrayEntry : arrayEntriesSorted.values()) {
                    arrayEntry.drawArrayEntry(width - 2, o + j * 10);
                    j++;
                }
                getRemoveEntries().forEach((key, value) -> getArrayEntries().remove(key));
                getRemoveEntries().clear();
            } else {
                for (Map.Entry<String, Module> module : modules) {
                    renderText(module.getKey(), width - 2 - RENDERER.getStringWidth(module.getKey()), o + j * 10);
                    j++;
                }
            }
        }
    }

    private void renderArmor() {
        if (armor.getValue()) {
            GL11.glColor4f(1.f, 1.f, 1.f, 1.f);
            int x = 15;
            RenderHelper.enableGUIStandardItemLighting();
            for (int i = 3; i >= 0; i--) {
                ItemStack stack = mc.player.inventory.armorInventory.get(i);
                if (!stack.isEmpty()) {
                    int y;
                    if (mc.player.isInsideOfMaterial(Material.WATER)
                            && mc.player.getAir() > 0
                            && !mc.player.capabilities.isCreativeMode) {
                        y = 65;
                    } else if (mc.player.getRidingEntity() != null
                            && !mc.player.capabilities.isCreativeMode) {
                        if (mc.player.getRidingEntity()
                                instanceof EntityLivingBase) {
                            EntityLivingBase entity =
                                    (EntityLivingBase) mc.player.getRidingEntity();
                            y = (int) (45
                                    + Math.ceil((entity.getMaxHealth()
                                    - 1.0F)
                                    / 20.0F)
                                    * 10);
                        } else {
                            y = 45;
                        }
                    } else if (mc.player.capabilities.isCreativeMode) {
                        y = mc.player.isRidingHorse() ? 45 : 38;
                    } else {
                        y = 55;
                    }
                    final float percent = DamageUtil.getPercent(stack) / 100.0f;
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(0.625F, 0.625F, 0.625F);
                    GlStateManager.disableDepth();
                    Managers.TEXT.drawStringWithShadow(
                            ((int) (percent * 100.0f)) + "%", (((width >> 1) + x + 1) * 1.6F), (height - y - 3) * 1.6F, ColorHelper.toColor(percent * 120.0f, 100.0f, 50.0f, 1.0f).getRGB());
                    GlStateManager.enableDepth();
                    GlStateManager.scale(1.0f, 1.0f, 1.0f);
                    GlStateManager.popMatrix();
                    GlStateManager.pushMatrix();
                    mc.getRenderItem()
                            .renderItemIntoGUI(stack,
                                    width / 2 + x,
                                    height - y);
                    mc.getRenderItem()
                            .renderItemOverlays(mc.fontRenderer,
                                    stack,
                                    width / 2 + x,
                                    height - y);
                    GlStateManager.popMatrix();
                    x += 18;
                }
            }

            RenderHelper.disableStandardItemLighting();
        }
    }

    public void renderText(String text, float x, float y) {
        String colorCode = colorMode.getValue().getColor();
        RENDERER.drawStringWithShadow(colorCode + text,
                x,
                y,
                colorMode.getValue() == HudRainbow.None
                        ? color.getValue().getRGB()
                        : (colorMode.getValue() == HudRainbow.Static ? (ColorUtil.staticRainbow((y + 1) * 0.89f, color.getValue())) : 0xffffffff));
    }

    public void renderPotionText(String text, float x, float y, Potion potion) {
        String colorCode = potionColor.getValue() == PotionColor.Normal ? "" : colorMode.getValue().getColor();
        RENDERER.drawStringWithShadow(colorCode + text,
                x,
                y,
                potionColor.getValue() == PotionColor.Normal ? potionColorMap.get(potion).getRGB() : (
                        colorMode.getValue() == HudRainbow.None
                                ? color.getValue().getRGB()
                                : (colorMode.getValue() == HudRainbow.Static ? (ColorUtil.staticRainbow((y + 1) * 0.89f, color.getValue())) : 0xffffffff)));
    }


    public Map<Module, ArrayEntry> getArrayEntries() {
        return arrayEntries;
    }

    public Map<Module, ArrayEntry> getRemoveEntries() {
        return removeEntries;
    }

    protected boolean isArrayMember(Module module) {
        return getArrayEntries().containsKey(module);
    }

}
