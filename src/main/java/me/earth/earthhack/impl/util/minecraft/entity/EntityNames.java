package me.earth.earthhack.impl.util.minecraft.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.*;

import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class EntityNames
{
    private static final List<Entry<Class<? extends Entity>, String>> entityNames;

    static
    {
        // Find all Entities that have no localized name for this.
        entityNames = new LinkedList<>();
        register(EntityItemFrame.class, "Item Frame");
        register(EntityEnderCrystal.class, "End Crystal");
        register(EntityMinecartEmpty.class, "Minecart");
        register(EntityMinecart.class, "Minecart");
        register(EntityMinecartFurnace.class, "Minecart with Furnace");
        register(EntityMinecartTNT.class, "Minecart with TNT");
    }

    public static void register(Class<? extends Entity> type, String name)
    {
        entityNames.add(0, new AbstractMap.SimpleEntry<>(type, name));
    }

    public static String getName(Entity entity)
    {
        for (Map.Entry<Class<? extends Entity>, String> entry : entityNames)
        {
            if (entry.getKey().isInstance(entity))
            {
                return entry.getValue();
            }
        }

        return entity.getName();
    }

}
