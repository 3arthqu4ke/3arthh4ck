package me.earth.earthhack.impl.modules.client.pbgui;

import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.gui.click.Click;
import me.earth.earthhack.impl.modules.client.clickgui.ClickGui;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.pingbypass.PingBypass;

import java.awt.*;

public class PbGui extends ClickGui {
    private final Setting<Boolean> hint =
        register(new BooleanSetting("Hint", true));

    public PbGui() {
        super("PB-Gui");
        this.setData(new SimpleData(this, "Beautiful ClickGui by oHare for configuring the modules of the PingBypass"));
        this.color.setValue(new Color(255, 0, 0));
    }

    @Override
    protected Click newClick() {
        Click click = new Click(screen, PingBypass.MODULES);
        click.setPingBypass(hint.getValue());
        return click;
    }

}
