package me.earth.earthhack.impl.modules.player.sorter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.earth.earthhack.api.config.Jsonable;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.HashMap;
import java.util.Map;

// TODO: save enchants and item meta, to distinguish
//  golden apples, different pickaxes, etc.
public class InventoryLayout implements Jsonable, Globals
{
    private final Map<Integer, Item> layout = new HashMap<>();

    public void setSlot(int slot, Item item)
    {
        layout.put(slot, item);
    }

    public Item getItem(int slot)
    {
        return layout.get(slot);
    }

    @Override
    public void fromJson(JsonElement element)
    {
        JsonObject object = element.getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : object.entrySet())
        {
            int id = Integer.parseInt(entry.getKey());
            Item item = Item.getItemById(entry.getValue().getAsInt());
            layout.put(id, item);
        }
    }

    @Override
    public String toJson()
    {
        JsonObject object = new JsonObject();
        for (Map.Entry<Integer, Item> entry : layout.entrySet())
        {
            object.add(entry.getKey() + "",
                Jsonable.parse(Item.getIdFromItem(entry.getValue()) + "", false));
        }

        return object.toString();
    }

    public static InventoryLayout createFromMcPlayer()
    {
        NonNullList<ItemStack> inventory = InventoryUtil.getInventory();
        InventoryLayout layout = new InventoryLayout();
        for (int i = 0; i < inventory.size(); i++)
        {
            layout.setSlot(i, inventory.get(i).getItem());
        }

        return layout;
    }

}
