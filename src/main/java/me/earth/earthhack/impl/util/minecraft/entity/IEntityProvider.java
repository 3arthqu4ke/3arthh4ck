package me.earth.earthhack.impl.util.minecraft.entity;

import net.minecraft.entity.player.EntityPlayer;

import javax.swing.text.html.parser.Entity;
import java.util.List;

public interface IEntityProvider
{
    List<Entity> getEntities();

    List<EntityPlayer> getPlayers();

}
