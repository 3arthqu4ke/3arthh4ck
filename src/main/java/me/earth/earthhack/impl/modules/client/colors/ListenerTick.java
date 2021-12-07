package me.earth.earthhack.impl.modules.client.colors;

import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.ColorUtil;

import java.awt.*;

final class ListenerTick extends ModuleListener<Colors, TickEvent> {

    public ListenerTick(Colors module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        Managers.MODULES.getRegistered().forEach(module1 -> module1.getSettings().stream().
                filter(setting -> setting instanceof ColorSetting &&
                        ((ColorSetting) setting).isSync()).
                forEach(setting -> ((ColorSetting) setting).setValueAlpha(Managers.COLOR.getColorSetting().getValue())));
        Managers.MODULES.getRegistered().forEach(module1 -> module1.getSettings().stream().
                filter(setting -> setting instanceof ColorSetting &&
                        ((ColorSetting) setting).isRainbow() && !((ColorSetting) setting).isSync()).
                forEach(setting ->
                        ((ColorSetting) setting).setValueAlpha(((ColorSetting) setting).isStaticRainbow() ? new Color(ColorUtil.staticRainbow(0, ((ColorSetting) setting).getStaticColor())) : ColorUtil.getRainbow((int) Math.max(((ColorSetting) setting).getRainbowSpeed() * 30.f, 30.f), 0, ((ColorSetting) setting).getRainbowSaturation() / 100.f, ((ColorSetting) setting).getRainbowBrightness() / 100.f))));
    }

}
