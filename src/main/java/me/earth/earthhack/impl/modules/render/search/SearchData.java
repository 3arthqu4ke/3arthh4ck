package me.earth.earthhack.impl.modules.render.search;

import me.earth.earthhack.api.module.data.DefaultData;

final class SearchData extends DefaultData<Search>
{
    public SearchData(Search module)
    {
        super(module);
        register(module.lines, "Draws outlines around found blocks.");
        register(module.fill, "Draws boxes on found blocks.");
        register(module.tracers, "Draws tracers to found blocks.");
        register(module.softReload, "Makes the world not flicker when new " +
                "blocks are being loaded.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Searches for certain blocks in render distance.";
    }

}
