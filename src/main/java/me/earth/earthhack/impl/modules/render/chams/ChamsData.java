package me.earth.earthhack.impl.modules.render.chams;

import me.earth.earthhack.api.module.data.DefaultData;

final class ChamsData extends DefaultData<Chams>
{
    public ChamsData(Chams module)
    {
        super(module);
        register(module.mode, "Switch between Normal and CSGO like chams.");
        register(module.self, "Render chams for yourself.");
        register(module.players, "Render chams for players.");
        register(module.animals, "Render chams for animals.");
        register(module.monsters, "Render chams for monsters.");
        register(module.texture, "Render textured chams.");
        register(module.xqz, "Renders chams through walls");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Render Entities through walls.";
    }

}
