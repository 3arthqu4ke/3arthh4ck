package me.earth.earthhack.impl.gui.click.component.impl;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.data.ModuleData;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.*;
import me.earth.earthhack.impl.gui.chat.factory.ComponentFactory;
import me.earth.earthhack.impl.gui.click.Click;
import me.earth.earthhack.impl.gui.click.component.Component;
import me.earth.earthhack.impl.gui.click.component.SettingComponent;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.configs.ConfigHelperModule;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.earthhack.impl.util.render.RenderUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.function.Supplier;

public class ModuleComponent extends Component {
    private final Module module;
    private final ArrayList<Component> components = new ArrayList<>();

    public ModuleComponent(Module module, float posX, float posY, float offsetX, float offsetY, float width, float height) {
        super(module.getName(), posX, posY, offsetX, offsetY, width, height);
        this.module = module;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void init() {
        getComponents().clear();
        float offY = getHeight();
        ModuleData<?> data = getModule().getData();
        if (data != null) {
            this.setDescription(data::getDescription);
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
                    getComponents().add(new EnumComponent<>((EnumSetting<?>) setting, getFinishedX(), getFinishedY(), 0, offY, getWidth(), 14));
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
                    getComponents().add(new ListComponent<>((ListSetting<?>) setting, getFinishedX(), getFinishedY(), 0, offY, getWidth(), 14));
                    offY += 14;
                }

                // -_- lazy
                if (data != null && before != offY) {
                    Supplier<String> supplier = () -> {
                        String desc = data.settingDescriptions().get(setting);
                        if (desc == null) {
                            desc = "A Setting (" + setting.getInitial().getClass().getSimpleName() + ").";
                        }

                        if (Click.CLICK_GUI.get().descNameValue.getValue()) {
                            desc = ComponentFactory.create(setting).getFormattedText() + "\n\n" + TextColor.WHITE + desc;
                        }

                        return desc;
                    };

                    getComponents().get(getComponents().size() - 1).setDescription(supplier);
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

        String label = module instanceof ConfigHelperModule && ((ConfigHelperModule) module).isDeleted() ? TextColor.RED + getLabel() : getLabel();
        Managers.TEXT.drawStringWithShadow(label, getFinishedX() + 4, getFinishedY() + getHeight() / 2 - (Managers.TEXT.getStringHeightI() >> 1), getModule().isEnabled() ? 0xFFFFFFFF : 0xFFAAAAAA);
        if (!getComponents().isEmpty())
            Managers.TEXT.drawStringWithShadow(isExtended() ? getClickGui().get().close.getValue() : getClickGui().get().open.getValue(), getFinishedX() + getWidth() - 4 - Managers.TEXT.getStringWidth(isExtended() ? getClickGui().get().close.getValue() : getClickGui().get().open.getValue()), getFinishedY() + getHeight() / 2 - (Managers.TEXT.getStringHeightI() >> 1), getModule().isEnabled() ? 0xFFFFFFFF : 0xFFAAAAAA);

        if (getClickGui().get().showBind.getValue() && !getModule().getBind().toString().equalsIgnoreCase("none")) {
            GL11.glPushMatrix();
            GL11.glScalef(0.5f, 0.5f, 0.5f);
            String disString = getModule().getBind().toString().toLowerCase().replace("none", "-");
            disString = String.valueOf(disString.charAt(0)).toUpperCase() + disString.substring(1);
            if (disString.length() > 3) {
                disString = disString.substring(0, 3);
            }
            disString = "[" + disString + "]";
            float offset = getFinishedX() + getWidth() - Managers.TEXT.getStringWidth(isExtended() ? getClickGui().get().close.getValue() : getClickGui().get().open.getValue());
            Managers.TEXT.drawStringWithShadow(disString, (offset - (Managers.TEXT.getStringWidth(disString) >> 1)) * 2 - 12, (getFinishedY() + getHeight() / 1.5f - (Managers.TEXT.getStringHeightI() >> 1)) * 2.0f, getModule().isEnabled() ? 0xFFFFFFFF : 0xFFAAAAAA);
            GL11.glScalef(1.0f, 1.0f, 1.0f);
            GL11.glPopMatrix();
        }
        if (isExtended()) {
            for (Component component : getComponents()) {
                if (component instanceof SettingComponent
                    && Visibilities.VISIBILITY_MANAGER.isVisible(((SettingComponent<?, ?>) component).getSetting())) {
                    component.drawScreen(mouseX, mouseY, partialTicks);
                }
            }
            if (getModule().isEnabled()) {
                Render2DUtil.drawRect(getFinishedX() + 1.0f, getFinishedY() + getHeight() - 0.5f, getFinishedX() + 3, getFinishedY() + getHeight() + getComponentsSize(), hovered ? getClickGui().get().color.getValue().brighter().getRGB() : getClickGui().get().color.getValue().getRGB());
                Render2DUtil.drawRect(getFinishedX() + 1.0f, getFinishedY() + getHeight() + getComponentsSize(), getFinishedX() + getWidth() - 1.f, getFinishedY() + getHeight() + getComponentsSize() + 2, hovered ? getClickGui().get().color.getValue().brighter().getRGB() : getClickGui().get().color.getValue().getRGB());
                Render2DUtil.drawRect(getFinishedX() + getWidth() - 3.f, getFinishedY() + getHeight() - 0.5f, getFinishedX() + getWidth() - 1.f, getFinishedY() + getHeight() + getComponentsSize(), hovered ? getClickGui().get().color.getValue().brighter().getRGB() : getClickGui().get().color.getValue().getRGB());
            }
            Render2DUtil.drawBorderedRect(getFinishedX() + 3.0f, getFinishedY() + getHeight() - 0.5f, getFinishedX() + getWidth() - 3.f, getFinishedY() + getHeight() + getComponentsSize() + 0.5f, 0.5f, 0, getClickGui().get().white.getValue() ? 0xffffffff : 0xff000000);

        }
        Render2DUtil.drawBorderedRect(getFinishedX() + 1, getFinishedY() + 0.5f, getFinishedX() + 1 + getWidth() - 2, getFinishedY() - 0.5f + getHeight() + (isExtended() ? (getComponentsSize() + 3.0f) : 0), 0.5f, 0, 0xff000000);
        updatePositions();
    }


    @Override
    public void keyTyped(char character, int keyCode) {
        super.keyTyped(character, keyCode);
        if (isExtended()) {
            for (Component component : getComponents()) {
                if (component instanceof SettingComponent
                    && Visibilities.VISIBILITY_MANAGER.isVisible(((SettingComponent<?, ?>) component).getSetting())) {
                    component.keyTyped(character, keyCode);
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
                if (component instanceof SettingComponent
                        && Visibilities.VISIBILITY_MANAGER.isVisible(((SettingComponent<?, ?>) component).getSetting())) {
                    component.mouseClicked(mouseX, mouseY, mouseButton);
                }
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        if (isExtended()) {
            for (Component component : getComponents()) {
                if (component instanceof SettingComponent
                        && Visibilities.VISIBILITY_MANAGER.isVisible(((SettingComponent<?, ?>) component).getSetting())) {
                    component.mouseReleased(mouseX, mouseY, mouseButton);
                }
            }
        }
    }

    private float getComponentsSize() {
        float size = 0;
        for (Component component : getComponents()) {
            if (component instanceof SettingComponent
                    && Visibilities.VISIBILITY_MANAGER.isVisible(((SettingComponent<?, ?>) component).getSetting())) {
                size += component.getHeight();
            }
        }
        return size;
    }

    private void updatePositions() {
        float offsetY = getHeight();
        for (Component component : getComponents()) {
            if (component instanceof SettingComponent
                    && Visibilities.VISIBILITY_MANAGER.isVisible(((SettingComponent<?, ?>) component).getSetting())) {
                component.setOffsetY(offsetY);
                component.moved(getPosX(), getPosY());
                offsetY += component.getHeight();
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
