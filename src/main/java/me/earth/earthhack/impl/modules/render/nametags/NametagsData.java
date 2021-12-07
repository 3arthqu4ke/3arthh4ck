package me.earth.earthhack.impl.modules.render.nametags;

import me.earth.earthhack.api.module.data.DefaultData;

final class NametagsData extends DefaultData<Nametags>
{
    public NametagsData(Nametags module)
    {
        super(module);
        register(module.health, "Renders the players health.");
        register(module.ping, "Renders the players ping.");
        register(module.id, "Renders the players EntityID.");
        register(module.itemStack, "Renders the players currently" +
                " held ItemStacks name.");
        register(module.armor, "Renders the players armor.");
        register(module.gameMode, "Displays the players current gamemode.");
        register(module.durability, "Displays the Durability of the" +
                " Armor the player is currently wearing.");
        register(module.invisibles, "Renders Nametags for Invisible players.");
        register(module.pops, "Displays the players totem pops.");
        register(module.fov, "Only renders Nametags within your FOV.");
        register(module.scale, "Scale of the Nametags.");
        register(module.burrow, "Shows when players are burrowed.");
        register(module.debug, "Displays Entity-Ids for every Entity.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Displays Name, Health and Items above players.";
    }

}
