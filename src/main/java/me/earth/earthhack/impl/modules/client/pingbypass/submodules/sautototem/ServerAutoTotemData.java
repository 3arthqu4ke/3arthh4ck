package me.earth.earthhack.impl.modules.client.pingbypass.submodules.sautototem;

import me.earth.earthhack.api.module.data.DefaultData;

final class ServerAutoTotemData extends DefaultData<ServerAutoTotem>
{
    public ServerAutoTotemData(ServerAutoTotem module)
    {
        super(module);
        register("Health", "If you are outside a hole and your health is" +
                " lower than this, the autototem will switch" +
                " a totem in your offhand.");
        register("SafeHealth",
                "Same as Health, but for when you are safe.");
        register("XCarry", "If the AutoTotem should search for" +
                " totems in the XCarry.");
    }

    @Override
    public int getColor()
    {
        return 0xffff0000;
    }

    @Override
    public String getDescription()
    {
        return "An AutoTotem for the PingBypass.";
    }

}
