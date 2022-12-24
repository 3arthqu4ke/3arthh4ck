package me.earth.earthhack.impl.modules.player.sorter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.earth.earthhack.api.config.Jsonable;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.event.events.client.ShutDownEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.managers.config.helpers.CurrentConfig;
import me.earth.earthhack.impl.managers.config.util.JsonPathWriter;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.helpers.addable.LoadableModule;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.misc.FileUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sorter extends LoadableModule
{
    public static final String PATH = "earthhack/sorter";
    public static final String JSON_PATH = "earthhack/sorter/Sorter.json";

    protected final Setting<Boolean> virtualHotbar =
        register(new BooleanSetting("VirtualHotbar", false));
    protected final Setting<Boolean> sort =
        register(new BooleanSetting("Sort", false));
    protected final Setting<Integer> delay =
        register(new NumberSetting<>("Delay", 500, 0, 5000));
    protected final Setting<Integer> globalDelay =
        register(new NumberSetting<>("Global-Delay", 500, 0, 5000));
    protected final Setting<Boolean> sortInLoot =
        register(new BooleanSetting("SortInLoot", false));
    protected final Setting<Boolean> sortInInv =
        register(new BooleanSetting("SortInInventory", false));
    protected final Setting<Boolean> ensureHotbar =
        register(new BooleanSetting("EnsureHotbarSorting", false));

    protected final StopWatch timer = new StopWatch();

    protected final Map<String, InventoryLayout> layouts = new HashMap<>();
    protected Map<Integer, Integer> reverse = null;
    protected Map<Integer, Integer> mapping = null;
    protected InventoryLayout current;
    protected String currentLayout;

    public Sorter()
    {
        super("Sorter", Category.Player, "Add_Layout", "layout");
        Bus.EVENT_BUS.register(
            new LambdaListener<>(ShutDownEvent.class, e -> this.saveConfig()));
        this.listeners.add(new ListenerMotion(this));
        this.unregister(super.listType);
    }

    public boolean scroll(int direction)
    {
        if (!this.isEnabled() || !virtualHotbar.getValue())
        {
            return false;
        }

        Map<Integer, Integer> mapping = this.mapping;
        Map<Integer, Integer> reverse = this.reverse;
        if (mapping == null || reverse == null)
        {
            return false;
        }

        if (direction > 0)
        {
            direction = 1;
        }

        if (direction < 0)
        {
            direction = -1;
        }

        int c = mc.player.inventory.currentItem;
        int curr = reverse.getOrDefault(c, c);
        curr -= direction;
        if (curr < 0)
        {
            curr = 8;
        }
        else if (curr > 8)
        {
            curr = 0;
        }

        mc.player.inventory.currentItem = mapping.getOrDefault(curr, curr);
        return true;
    }

    public void updateMapping()
    {
        if (!this.isEnabled() || !virtualHotbar.getValue())
        {
            return;
        }

        InventoryLayout current = this.current;
        if (current == null)
        {
            return;
        }

        Map<Integer, Integer> mapping = new HashMap<>(14, 0.75f);
        Map<Integer, Integer> reverse = new HashMap<>(14, 0.75f);
        List<Integer> empty = new ArrayList<>();
        for (int i = 0; i < 9; i++)
        {
            Item item = current.getItem(InventoryUtil.hotbarToInventory(i));
            if (item == Items.AIR)
            {
                empty.add(i);
                continue;
            }

            boolean notFound = true;
            for (int j = 0; j < 9; j++)
            {
                if (!mapping.containsValue(j)
                    && item == mc.player.inventory.getStackInSlot(j).getItem())
                {
                    mapping.put(i, j);
                    reverse.put(j, i);
                    notFound = false;
                    break;
                }
            }

            if (notFound)
            {
                empty.add(i);
            }
        }

        for (int i : empty)
        {
            for (int j = 0; j < 9; j++)
            {
                if (!mapping.containsValue(j))
                {
                    mapping.put(i, j);
                    reverse.put(j, i);
                    break;
                }
            }
        }

        this.reverse = reverse;
        this.mapping = mapping;
    }

    public int getReverseMapping(int slot)
    {
        return getMapping(slot, this.reverse);
    }

    public int getHotbarMapping(int slot)
    {
        return getMapping(slot, this.mapping);
    }

    private int getMapping(int slot, Map<Integer, Integer> mapping)
    {
        if (!this.isEnabled() || !virtualHotbar.getValue() || mapping == null)
        {
            return slot;
        }

        return mapping.getOrDefault(slot, slot);
    }

    @Override
    protected void onLoad()
    {
        loadConfig();
        currentLayout = CurrentConfig.getInstance().get("sorter");
        if (currentLayout == null)
        {
            currentLayout = layouts.keySet().stream().findFirst().orElse(null);
        }

        if (currentLayout != null)
        {
            current = layouts.get(currentLayout);
        }
    }

    @Override
    public void add(String string)
    {
        InventoryLayout layout = InventoryLayout.createFromMcPlayer();
        layouts.put(string.toLowerCase(), layout);
        load(string.toLowerCase());
        ModuleUtil.sendMessage(
            this, TextColor.GREEN + "Created new layout "
                + TextColor.AQUA + string + TextColor.GREEN + ".", "add");
    }

    @Override
    public void del(String string)
    {
        layouts.remove(string.toLowerCase());
        if (string.equalsIgnoreCase(currentLayout))
        {
            currentLayout = layouts.keySet().stream().findFirst().orElse(null);
            ModuleUtil.sendMessage(
                    this,
                    TextColor.GREEN + "Deleted layout " + TextColor.RED
                    + string + TextColor.GREEN
                    + (currentLayout == null
                        ? ", no replacement found."
                        : (", now active: " + TextColor.AQUA
                            + currentLayout + TextColor.GREEN + ".")),
                    "del");

            load(currentLayout);
            if (currentLayout == null)
            {
                current = null;
                reverse = null;
                mapping = null;
            }
        }
        else
        {
            ModuleUtil.sendMessage(
                this, TextColor.GREEN + "Deleted layout " + TextColor.RED +
                    string + TextColor.GREEN + ".", "del");
        }
    }

    @Override
    protected void load(String string, boolean noArgGiven)
    {
        if (noArgGiven)
        {
            ModuleUtil.sendMessage(
                this, TextColor.RED
                    + "Please specify a Layout to load!");
            return;
        }

        if (load(string))
        {
            ModuleUtil.sendMessage(
                this, TextColor.RED
                    + "Couldn't find layout "
                    + TextColor.WHITE
                    + string
                    + TextColor.RED
                    + "!");
        }
        else
        {
            ModuleUtil.sendMessage(
                this, TextColor.GREEN + "Layout "
                    + TextColor.WHITE + string + TextColor.GREEN
                    + " loaded successfully.");
        }
    }

    @Override
    protected String getLoadableStartingWith(String string)
    {
        string = string.toLowerCase();
        for (String s : layouts.keySet())
        {
            if (s.toLowerCase().startsWith(string.toLowerCase()))
            {
                return s;
            }
        }

        return null;
    }

    @Override
    public boolean execute(String[] args)
    {
        if (args.length > 1
            && args[1].equalsIgnoreCase("add")
            && mc.player == null)
        {
            ChatUtil.sendMessage(TextColor.RED
                + "You need to be in game to add a new Inventory Layout.");
            return true;
        }

        return super.execute(args);
    }

    @Override
    public String getInput(String input, boolean add)
    {
        String s = getLoadableStartingWith(input);
        if (s == null)
        {
            return "";
        }

        return TextUtil.substring(s, input.length());
    }

    public boolean load(String layout)
    {
        InventoryLayout l = layouts.get(layout);
        if (l == null)
        {
            return true;
        }

        currentLayout = layout;
        current = l;
        return false;
    }

    public void loadConfig()
    {
        layouts.clear();
        FileUtil.createDirectory(Paths.get(PATH));
        Path path = Paths.get(JSON_PATH);
        if (!Files.exists(path))
        {
            return;
        }

        try (InputStream stream = Files.newInputStream(path))
        {
            JsonObject object = Jsonable.PARSER
                                        .parse(new InputStreamReader(stream))
                                        .getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : object.entrySet())
            {
                InventoryLayout layout = new InventoryLayout();
                layout.fromJson(entry.getValue());
                layouts.put(entry.getKey().toLowerCase(), layout);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void saveConfig()
    {
        if (currentLayout != null)
        {
            CurrentConfig.getInstance().set("sorter", currentLayout);
        }

        FileUtil.createDirectory(Paths.get(PATH));
        JsonObject object = new JsonObject();
        for (Map.Entry<String, InventoryLayout> entry : layouts.entrySet())
        {
            object.add(entry.getKey().toLowerCase(),
                       Jsonable.parse(entry.getValue().toJson(), false));
        }

        try
        {
            JsonPathWriter.write(Paths.get(JSON_PATH), object);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
