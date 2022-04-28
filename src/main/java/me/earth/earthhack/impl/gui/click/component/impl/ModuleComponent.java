package me.earth.earthhack.impl.gui.click.component.impl;

import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.data.ModuleData;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.*;
import me.earth.earthhack.impl.gui.click.component.Component;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.clickgui.ClickGui;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class ModuleComponent extends Component {
    private static final SettingCache<Boolean, BooleanSetting, ClickGui> WHITE =
            Caches.getSetting(ClickGui.class, BooleanSetting.class, "White-Settings", true);


    private final Module module;
    private final ArrayList<Component> components = new ArrayList<>();

    public ModuleComponent(Module module, float posX, float posY, float offsetX, float offsetY, float width, float height) {
        super(module.getName(), posX, posY, offsetX, offsetY, width, height);
        this.module = module;
    }

    @Override
    public void init() {
        getComponents().clear();
        float offY = getHeight();
        ModuleData data = getModule().getData();
        if (data != null) {
            this.setDescription(data.getDescription());
        }

        if (!getModule().getSettings().isEmpty()) {
            for (Setting<?> setting : getModule().getSettings()) {
                float before = offY;
                if (setting instanceof BooleanSetting && !setting.getName().equalsIgnoreCase("enabled")) {
                    getComponents().add(new BooleanComponent((BooleanSetting) setting, getFinishedX(), getFinishedY(), 0, offY, getWidth(), 14));
                    offY += 14;
                }
                if (setting instanceof BindSetting) {
                    getComponents().add(new KeybindComponent((BindSetting) setting, getFinishedX(), getFinishedY(), 0, offY, getWidth(), 14));
                    offY += 14;
                }
                if (setting instanceof NumberSetting) {
                    getComponents().add(new NumberComponent((NumberSetting<Number>) setting, getFinishedX(), getFinishedY(), 0, offY, getWidth(), 14));
                    offY += 14;
                }
                if (setting instanceof EnumSetting) {
                    getComponents().add(new EnumComponent((EnumSetting) setting, getFinishedX(), getFinishedY(), 0, offY, getWidth(), 14));
                    offY += 14;
                }
                if (setting instanceof ColorSetting) {
                    getComponents().add(new ColorComponent((ColorSetting) setting, getFinishedX(), getFinishedY(), 0, offY, getWidth(), 14));
                    offY += 14;
                }
                if (setting instanceof StringSetting) {
                    getComponents().add(new StringComponent((StringSetting) setting, getFinishedX(), getFinishedY(), 0, offY, getWidth(), 14));
                    offY += 14;
                }
                if (setting instanceof ListSetting) {
                    getComponents().add(new ListComponent((ListSetting) setting, getFinishedX(), getFinishedY(), 0, offY, getWidth(), 14));
                    offY += 14;
                }

                // -_- lazy
                if (data != null && before != offY) {
                    String desc = (String) data.settingDescriptions().get(setting);
                    if (desc == null) {
                        desc = "A Setting (" + setting.getInitial().getClass().getSimpleName() + ").";
                    }
                    getComponents().get(getComponents().size() - 1).setDescription(desc);
                }
            }
        }

        getComponents().forEach(Component::init);
    }

    @Override
    public void moved(float posX, float posY) {
        super.moved(posX, posY);
        getComponents().forEach(component -> component.moved(getFinishedX(), getFinishedY()));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        final boolean hovered = RenderUtil.mouseWithinBounds(mouseX, mouseY, getFinishedX(), getFinishedY(), getWidth(), getHeight());

        if (hovered)
            Render2DUtil.drawRect(getFinishedX() + 1, getFinishedY() + 0.5f, getFinishedX() + getWidth() - 1, getFinishedY() + getHeight() - 0.5f, 0x66333333);
        if (getModule().isEnabled()) {
            Render2DUtil.drawRect(getFinishedX() + 1, getFinishedY() + 0.5f, getFinishedX() + getWidth() - 1, getFinishedY() + getHeight() - 0.5f, hovered ? getClickGui().get().color.getValue().brighter().getRGB() : getClickGui().get().color.getValue().getRGB());
        }
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(getLabel(), getFinishedX() + 4, getFinishedY() + getHeight() / 2 - (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT >> 1), getModule().isEnabled() ? 0xFFFFFFFF : 0xFFAAAAAA);
        if (!getComponents().isEmpty())
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(isExtended() ? getClickGui().get().close.getValue() : getClickGui().get().open.getValue(), getFinishedX() + getWidth() - 4 - Minecraft.getMinecraft().fontRenderer.getStringWidth(isExtended() ? getClickGui().get().close.getValue() : getClickGui().get().open.getValue()), getFinishedY() + getHeight() / 2 - (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT >> 1), getModule().isEnabled() ? 0xFFFFFFFF : 0xFFAAAAAA);

        if (getClickGui().get().showBind.getValue() && !getModule().getBind().toString().equalsIgnoreCase("none")) {
            GL11.glPushMatrix();
            GL11.glScalef(0.5f, 0.5f, 0.5f);
            String disString = getModule().getBind().toString().toLowerCase().replace("none", "-");
            disString = String.valueOf(disString.charAt(0)).toUpperCase() + disString.substring(1);
            if (disString.length() > 3) {
                disString = disString.substring(0, 3);
            }
            disString = "[" + disString + "]";
            float offset = getFinishedX() + getWidth() - Minecraft.getMinecraft().fontRenderer.getStringWidth(isExtended() ? getClickGui().get().close.getValue() : getClickGui().get().open.getValue());
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(disString, (offset - (Minecraft.getMinecraft().fontRenderer.getStringWidth(disString) >> 1)) * 2 - 12, (getFinishedY() + getHeight() / 1.5f - (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT >> 1)) * 2.0f, getModule().isEnabled() ? 0xFFFFFFFF : 0xFFAAAAAA);
            GL11.glScalef(1.0f, 1.0f, 1.0f);
            GL11.glPopMatrix();
        }
        if (isExtended()) {
            for (Component component : getComponents()) {
                if (component instanceof BooleanComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((BooleanComponent) component).getBooleanSetting())) {
                        component.drawScreen(mouseX, mouseY, partialTicks);
                    }
                }
                if (component instanceof KeybindComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((KeybindComponent) component).getBindSetting())) {
                        component.drawScreen(mouseX, mouseY, partialTicks);
                    }
                }
                if (component instanceof NumberComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((NumberComponent) component).getNumberSetting())) {
                        component.drawScreen(mouseX, mouseY, partialTicks);
                    }
                }
                if (component instanceof EnumComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((EnumComponent) component).getEnumSetting())) {
                        component.drawScreen(mouseX, mouseY, partialTicks);
                    }
                }
                if (component instanceof ColorComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((ColorComponent) component).getColorSetting())) {
                        component.drawScreen(mouseX, mouseY, partialTicks);
                    }
                }
                if (component instanceof StringComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((StringComponent) component).getStringSetting())) {
                        component.drawScreen(mouseX, mouseY, partialTicks);
                    }
                }
                if (component instanceof ListComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((ListComponent) component).getListSetting())) {
                        component.drawScreen(mouseX, mouseY, partialTicks);
                    }
                }
            }
            if (getModule().isEnabled()) {
                Render2DUtil.drawRect(getFinishedX() + 1.0f, getFinishedY() + getHeight() - 0.5f, getFinishedX() + 3, getFinishedY() + getHeight() + getComponentsSize(), hovered ? getClickGui().get().color.getValue().brighter().getRGB() : getClickGui().get().color.getValue().getRGB());
                Render2DUtil.drawRect(getFinishedX() + 1.0f, getFinishedY() + getHeight() + getComponentsSize(), getFinishedX() + getWidth() - 1.f, getFinishedY() + getHeight() + getComponentsSize() + 2, hovered ? getClickGui().get().color.getValue().brighter().getRGB() : getClickGui().get().color.getValue().getRGB());
                Render2DUtil.drawRect(getFinishedX() + getWidth() - 3.f, getFinishedY() + getHeight() - 0.5f, getFinishedX() + getWidth() - 1.f, getFinishedY() + getHeight() + getComponentsSize(), hovered ? getClickGui().get().color.getValue().brighter().getRGB() : getClickGui().get().color.getValue().getRGB());
            }
            Render2DUtil.drawBorderedRect(getFinishedX() + 3.0f, getFinishedY() + getHeight() - 0.5f, getFinishedX() + getWidth() - 3.f, getFinishedY() + getHeight() + getComponentsSize() + 0.5f, 0.5f, 0, WHITE.getValue() ? 0xffffffff : 0xff000000);

        }
        Render2DUtil.drawBorderedRect(getFinishedX() + 1, getFinishedY() + 0.5f, getFinishedX() + 1 + getWidth() - 2, getFinishedY() - 0.5f + getHeight() + (isExtended() ? (getComponentsSize() + 3.0f) : 0), 0.5f, 0, 0xff000000);
        updatePositions();
    }


    @Override
    public void keyTyped(char character, int keyCode) {
        super.keyTyped(character, keyCode);
        if (isExtended()) {
            for (Component component : getComponents()) {
                if (component instanceof BooleanComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((BooleanComponent) component).getBooleanSetting())) {
                        component.keyTyped(character, keyCode);
                    }
                }
                if (component instanceof KeybindComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((KeybindComponent) component).getBindSetting())) {
                        component.keyTyped(character, keyCode);
                    }
                }
                if (component instanceof NumberComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((NumberComponent) component).getNumberSetting())) {
                        component.keyTyped(character, keyCode);
                    }
                }
                if (component instanceof EnumComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((EnumComponent) component).getEnumSetting())) {
                        component.keyTyped(character, keyCode);
                    }
                }
                if (component instanceof ColorComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((ColorComponent) component).getColorSetting())) {
                        component.keyTyped(character, keyCode);
                    }
                }
                if (component instanceof StringComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((StringComponent) component).getStringSetting())) {
                        component.keyTyped(character, keyCode);
                    }
                }
                if (component instanceof ListComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((ListComponent) component).getListSetting())) {
                        component.keyTyped(character, keyCode);
                    }
                }
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        final boolean hovered = RenderUtil.mouseWithinBounds(mouseX, mouseY, getFinishedX(), getFinishedY(), getWidth(), getHeight());
        if (hovered) {
            switch (mouseButton) {
                case 0:
                    getModule().toggle();
                    break;
                case 1:
                    if (!getComponents().isEmpty())
                        setExtended(!isExtended());
                    break;
                default:
                    break;
            }
        }
        if (isExtended()) {
            for (Component component : getComponents()) {
                if (component instanceof BooleanComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((BooleanComponent) component).getBooleanSetting())) {
                        component.mouseClicked(mouseX, mouseY, mouseButton);
                    }
                }
                if (component instanceof KeybindComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((KeybindComponent) component).getBindSetting())) {
                        component.mouseClicked(mouseX, mouseY, mouseButton);
                    }
                }
                if (component instanceof NumberComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((NumberComponent) component).getNumberSetting())) {
                        component.mouseClicked(mouseX, mouseY, mouseButton);
                    }
                }
                if (component instanceof EnumComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((EnumComponent) component).getEnumSetting())) {
                        component.mouseClicked(mouseX, mouseY, mouseButton);
                    }
                }
                if (component instanceof ColorComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((ColorComponent) component).getColorSetting())) {
                        component.mouseClicked(mouseX, mouseY, mouseButton);
                    }
                }
                if (component instanceof StringComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((StringComponent) component).getStringSetting())) {
                        component.mouseClicked(mouseX, mouseY, mouseButton);
                    }
                }
                if (component instanceof ListComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((ListComponent) component).getListSetting())) {
                        component.mouseClicked(mouseX, mouseY, mouseButton);
                    }
                }
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        if (isExtended()) {
            for (Component component : getComponents()) {
                if (component instanceof BooleanComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((BooleanComponent) component).getBooleanSetting())) {
                        component.mouseReleased(mouseX, mouseY, mouseButton);
                    }
                }
                if (component instanceof KeybindComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((KeybindComponent) component).getBindSetting())) {
                        component.mouseReleased(mouseX, mouseY, mouseButton);
                    }
                }
                if (component instanceof NumberComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((NumberComponent) component).getNumberSetting())) {
                        component.mouseReleased(mouseX, mouseY, mouseButton);
                    }
                }
                if (component instanceof EnumComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((EnumComponent) component).getEnumSetting())) {
                        component.mouseReleased(mouseX, mouseY, mouseButton);
                    }
                }
                if (component instanceof ColorComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((ColorComponent) component).getColorSetting())) {
                        component.mouseReleased(mouseX, mouseY, mouseButton);
                    }
                }
                if (component instanceof StringComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((StringComponent) component).getStringSetting())) {
                        component.mouseReleased(mouseX, mouseY, mouseButton);
                    }
                }
                if (component instanceof ListComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((ListComponent) component).getListSetting())) {
                        component.mouseReleased(mouseX, mouseY, mouseButton);
                    }
                }
            }
        }
    }

    private float getComponentsSize() {
        float size = 0;
        for (Component component : getComponents()) {
            if (component instanceof BooleanComponent) {
                if (Visibilities.VISIBILITY_MANAGER.isVisible(((BooleanComponent) component).getBooleanSetting())) {
                    size += component.getHeight();
                }
            }
            if (component instanceof KeybindComponent) {
                if (Visibilities.VISIBILITY_MANAGER.isVisible(((KeybindComponent) component).getBindSetting())) {
                    size += component.getHeight();
                }
            }
            if (component instanceof NumberComponent) {
                if (Visibilities.VISIBILITY_MANAGER.isVisible(((NumberComponent) component).getNumberSetting())) {
                    size += component.getHeight();
                }
            }
            if (component instanceof EnumComponent) {
                if (Visibilities.VISIBILITY_MANAGER.isVisible(((EnumComponent) component).getEnumSetting())) {
                    size += component.getHeight();
                }
            }
            if (component instanceof ColorComponent) {
                if (Visibilities.VISIBILITY_MANAGER.isVisible(((ColorComponent) component).getColorSetting())) {
                    size += component.getHeight();
                }
            }
            if (component instanceof StringComponent) {
                if (Visibilities.VISIBILITY_MANAGER.isVisible(((StringComponent) component).getStringSetting())) {
                    size += component.getHeight();
                }
            }
            if (component instanceof ListComponent) {
                if (Visibilities.VISIBILITY_MANAGER.isVisible(((ListComponent) component).getListSetting())) {
                    size += component.getHeight();
                }
            }
        }
        return size;
    }

    private void updatePositions() {
        float offsetY = getHeight();
        for (Component component : getComponents()) {
            if (component instanceof BooleanComponent) {
                if (Visibilities.VISIBILITY_MANAGER.isVisible(((BooleanComponent) component).getBooleanSetting())) {
                    component.setOffsetY(offsetY);
                    component.moved(getPosX(), getPosY());
                    offsetY += component.getHeight();
                }
            }
            if (component instanceof KeybindComponent) {
                if (Visibilities.VISIBILITY_MANAGER.isVisible(((KeybindComponent) component).getBindSetting())) {
                    component.setOffsetY(offsetY);
                    component.moved(getPosX(), getPosY());
                    offsetY += component.getHeight();
                }
            }
            if (component instanceof NumberComponent) {
                if (Visibilities.VISIBILITY_MANAGER.isVisible(((NumberComponent) component).getNumberSetting())) {
                    component.setOffsetY(offsetY);
                    component.moved(getPosX(), getPosY());
                    offsetY += component.getHeight();
                }
            }
            if (component instanceof EnumComponent) {
                if (Visibilities.VISIBILITY_MANAGER.isVisible(((EnumComponent) component).getEnumSetting())) {
                    component.setOffsetY(offsetY);
                    component.moved(getPosX(), getPosY());
                    offsetY += component.getHeight();
                }
            }
            if (component instanceof ColorComponent) {
                if (Visibilities.VISIBILITY_MANAGER.isVisible(((ColorComponent) component).getColorSetting())) {
                    component.setOffsetY(offsetY);
                    component.moved(getPosX(), getPosY());
                    offsetY += component.getHeight();
                }
            }
            if (component instanceof StringComponent) {
                if (Visibilities.VISIBILITY_MANAGER.isVisible(((StringComponent) component).getStringSetting())) {
                    component.setOffsetY(offsetY);
                    component.moved(getPosX(), getPosY());
                    offsetY += component.getHeight();
                }
            }
            if (component instanceof ListComponent) {
                if (Visibilities.VISIBILITY_MANAGER.isVisible(((ListComponent) component).getListSetting())) {
                    component.setOffsetY(offsetY);
                    component.moved(getPosX(), getPosY());
                    offsetY += component.getHeight();
                }
            }
        }
    }

    public Module getModule() {
        return module;
    }

    public ArrayList<Component> getComponents() {
        return components;
    }
}
