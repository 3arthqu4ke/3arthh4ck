package me.earth.earthhack.impl.gui.click.component;

import me.earth.earthhack.api.setting.Setting;

public class SettingComponent<V, T extends Setting<V>> extends Component {
    protected final T setting;

    public SettingComponent(String label, float posX, float posY, float offsetX,
                            float offsetY, float width, float height, T setting) {
        super(label, posX, posY, offsetX, offsetY, width, height);
        this.setting = setting;
    }

    public T getSetting() {
        return setting;
    }

}
