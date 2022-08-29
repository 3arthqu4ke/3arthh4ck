// Decompiled with: CFR 0.152
// Class Version: 8
package me.earth.skycolor;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.skycolor.SkyColor;

public class SkycolorData
        extends DefaultData<SkyColor> {
    public SkycolorData(SkyColor module) {
        super(module);
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Changes the color of the fog.";
    }
}
