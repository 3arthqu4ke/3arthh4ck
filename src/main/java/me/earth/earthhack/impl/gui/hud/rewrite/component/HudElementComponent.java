package me.earth.earthhack.impl.gui.hud.rewrite.component;

import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.data.ModuleData;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.*;
import me.earth.earthhack.impl.gui.click.component.Component;
import me.earth.earthhack.impl.gui.click.component.impl.*;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.clickgui.ClickGui;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;

// TODO: Again, maybe generify these classes in the future. Making HudElement a subclass of Module is messy.
public class HudElementComponent extends Component {

    private static final SettingCache<Boolean, BooleanSetting, ClickGui> WHITE =
            Caches.getSetting(ClickGui.class, BooleanSetting.class, "White-Settings", true);

    private final HudElement element;
    private final ArrayList<Component> components = new ArrayList<>();

    public HudElementComponent(HudElement element, float posX, float posY, float offsetX, float offsetY, float width, float height) {
        super(element.getName(), posX, posY, offsetX, offsetY, width, height);
        this.element = element;
    }

    @Override
    public void init() {
        getComponents().clear();
        float offY = getHeight();
        ModuleData<HudElement> data = /*getElement().getData()*/ null; // TODO
        if (data != null)
        {
            this.setDescription(data.getDescription());
        }

        if (!getElement().getSettings().isEmpty()) {
            for (Setting<?> setting : getElement().getSettings()) {
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

                // -_- lazy
                if (data != null && before != offY)
                {
                    String desc = data.settingDescriptions().get(setting);
                    if (desc == null)
                    {
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
        if (getElement().isEnabled()) {
            Render2DUtil.drawRect(getFinishedX() + 1, getFinishedY() + 0.5f, getFinishedX() + getWidth() - 1, getFinishedY() + getHeight() - 0.5f, hovered ? getClickGui().get().color.getValue().brighter().getRGB() : getClickGui().get().color.getValue().getRGB());
        }
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(getLabel(), getFinishedX() + 4, getFinishedY() + getHeight() / 2 - (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT >> 1), getElement().isEnabled() ? 0xFFFFFFFF : 0xFFAAAAAA);
        if (!getComponents().isEmpty())
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(isExtended() ? getClickGui().get().close.getValue() : getClickGui().get().open.getValue(), getFinishedX() + getWidth() - 4 - Minecraft.getMinecraft().fontRenderer.getStringWidth(isExtended() ? getClickGui().get().close.getValue() : getClickGui().get().open.getValue()), getFinishedY() + getHeight() / 2 - (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT >> 1), getElement().isEnabled() ? 0xFFFFFFFF : 0xFFAAAAAA);
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
            }
            if (getElement().isEnabled()) {
                Render2DUtil.drawRect(getFinishedX() + 1.0f, getFinishedY() + getHeight() - 0.5f, getFinishedX() + 3, getFinishedY() + getHeight() + getComponentsSize(), hovered ? getClickGui().get().color.getValue().brighter().getRGB() : getClickGui().get().color.getValue().getRGB());
                Render2DUtil.drawRect(getFinishedX() + 1.0f, getFinishedY() + getHeight() + getComponentsSize(), getFinishedX() + getWidth() - 1.f, getFinishedY() + getHeight() + getComponentsSize() + 2, hovered ? getClickGui().get().color.getValue().brighter().getRGB() : getClickGui().get().color.getValue().getRGB());
                Render2DUtil.drawRect(getFinishedX() + getWidth() - 3.f, getFinishedY() + getHeight() - 0.5f, getFinishedX() + getWidth() - 1.f, getFinishedY() + getHeight() + getComponentsSize(), hovered ? getClickGui().get().color.getValue().brighter().getRGB() : getClickGui().get().color.getValue().getRGB());
            }
            Render2DUtil.drawBorderedRect(getFinishedX() + 3.0f, getFinishedY() + getHeight() - 0.5f, getFinishedX() + getWidth() - 3.f, getFinishedY() + getHeight() + getComponentsSize() + 0.5f, 0.5f, 0, WHITE.getValue() ? 0xffffffff :  0xff000000);

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
                    getElement().toggle(); // TODO
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
        }
    }

    public ArrayList<Component> getComponents() {
        return components;
    }

    public HudElement getElement() {
        return element;
    }
}
