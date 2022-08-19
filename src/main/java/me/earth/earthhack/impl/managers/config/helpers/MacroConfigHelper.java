package me.earth.earthhack.impl.managers.config.helpers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.earth.earthhack.api.config.Jsonable;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.managers.client.macro.*;
import me.earth.earthhack.impl.managers.config.util.MacroConfig;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class MacroConfigHelper extends AbstractConfigHelper<MacroConfig>
{
    private final MacroManager manager;

    public MacroConfigHelper(MacroManager manager)
    {
        super("macro", "macros");
        this.manager = manager;
    }

    @Override
    protected MacroConfig create(String name)
    {
        return MacroConfig.create(name, manager);
    }

    @Override
    protected JsonObject toJson(MacroConfig config)
    {
        JsonObject object = new JsonObject();
        for (Macro macro : config.getMacros())
        {
            object.add(macro.getName(), Jsonable.parse(macro.toJson(), false));
        }

        return object;
    }

    @Override
    protected MacroConfig readFile(InputStream stream, String name)
    {
        MacroConfig config = new MacroConfig(name, manager);
        JsonObject object = Jsonable.PARSER
                                    .parse(new InputStreamReader(stream))
                                    .getAsJsonObject();

        for (Map.Entry<String, JsonElement> entry : object.entrySet())
        {
            JsonObject value = entry.getValue().getAsJsonObject();
            MacroType type = MacroType.fromString(
                    value.get("type").getAsString());

            Macro macro;
            switch (type)
            {
                case NORMAL:
                    macro = new Macro(entry.getKey(),
                                      Bind.none(),
                                      new String[]{});
                    break;
                case FLOW:
                    macro = new FlowMacro(entry.getKey(), Bind.none());
                    break;
                case COMBINED:
                    macro = new CombinedMacro(entry.getKey(), Bind.none());
                    break;
                case DELEGATE:
                    macro = new DelegateMacro(entry.getKey(), "");
                    break;
                default:
                    continue;
            }

            macro.fromJson(entry.getValue());
            config.add(macro);
        }

        return config;
    }

}
