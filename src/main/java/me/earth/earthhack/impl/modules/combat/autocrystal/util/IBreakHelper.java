package me.earth.earthhack.impl.modules.combat.autocrystal.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface IBreakHelper<T extends CrystalData>
{
    BreakData<T> newData(Collection<T> data);

    BreakData<T> getData(Collection<T> dataSet,
                         List<Entity> entities,
                         List<EntityPlayer> players,
                         List<EntityPlayer> friends);
}
