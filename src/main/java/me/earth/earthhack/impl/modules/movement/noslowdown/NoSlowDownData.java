package me.earth.earthhack.impl.modules.movement.noslowdown;

import me.earth.earthhack.api.module.data.DefaultData;

final class NoSlowDownData extends DefaultData<NoSlowDown>
{
    public NoSlowDownData(NoSlowDown module)
    {
        super(module);
        register(module.guiMove, "Allows you to move while you have a Gui open."
                + " (Your Inventory, a chest etc.)");
        register(module.items,
                "Using items (like eating food) won't slow you down.");
        register(module.legit, "Needed on some servers to not get " +
                "lagged back by the AntiCheat.");
        register(module.sprint, "Development setting, should be on.");
        register(module.input, "Development setting, should be on.");
        register(module.websY,
                "Multiplier for moving vertically through webs.");
        register(module.websXZ,
                "Multiplier for moving horizontally through webs.");
        register(module.sneak, "Only apply the WebsVertical modifier" +
                " when your sneak button is pressed.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Allows you to move in Guis and makes you" +
                " move normally while eating or in webs.";
    }

}
