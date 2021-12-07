package me.earth.earthhack.impl.modules.client.editor;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.gui.hud.rewrite.HudEditorGui;

public class HudEditor extends Module {

    public HudEditor() {
        super("HudEditor", Category.Client);
    }

    @Override
    public void onEnable() {
        HudEditorGui gui = new HudEditorGui();
        gui.init();
        mc.displayGuiScreen(gui);
    }

}
