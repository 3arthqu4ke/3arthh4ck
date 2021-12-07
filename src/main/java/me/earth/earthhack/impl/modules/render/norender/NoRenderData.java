package me.earth.earthhack.impl.modules.render.norender;

import me.earth.earthhack.api.module.data.DefaultData;

final class NoRenderData extends DefaultData<NoRender>
{
    public NoRenderData(NoRender module)
    {
        super(module);
        register(module.fire,
                "Doesn't render the fire overlay when you are on fire.");
        register(module.portal,
                "Doesn't render Portals while you are inside them.");
        register(module.pumpkin, "Doesn't render the pumpkin overlay.");
        register(module.totemPops, "Doesn't render the totempop" +
                " animation when you pop one.");
        register(module.nausea, "Doesn't render Nausea effects.");
        register(module.hurtCam, "Makes the camera not shake" +
                " when you take damage.");
        register(module.noWeather, "Doesn't render Rain.");
        register(module.barriers, "Allows you to see Barriers when on.");
        register(module.skyLight, "Prevents SkyLight lag exploits.");
        register(module.noFog, "Doesn't render fog.");
        register(module.blocks, "Allows you to look through blocks" +
                " while you are inside them.");
        register(module.advancements, "Doesn't render Advancements.");
        register(module.critParticles, "Doesn't render attack particles.");
        register(module.dynamicFov, "Removes the dynamic fov, when you " +
                "sprint or fly for example.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Don't render annoying overlays.";
    }

}
