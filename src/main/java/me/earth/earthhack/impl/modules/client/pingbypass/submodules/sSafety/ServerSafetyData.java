package me.earth.earthhack.impl.modules.client.pingbypass.submodules.sSafety;

import me.earth.earthhack.api.module.data.DefaultData;

final class ServerSafetyData extends DefaultData<ServerSafety>
{
    public ServerSafetyData(ServerSafety module)
    {
        super(module);
        register("MaxDamage", "The MaxDamage that counts as unsafe. " +
                "If a crystal or a position around you can be found that" +
                " deals more damage than this to you your SafetyState" +
                " counts as \"unsafe\" which will make modules" +
                " like AutoTotem more strict.");
        register("BedCheck", "Takes beds into account. Use this on" +
                " servers where beds are common.");
        register("1.13+", "Takes changes to minecraft mechanics" +
                " after 1.13+ into account. Use on ViaVersion servers" +
                " that are on one of those version.");
        register("SafetyPlayer", "Will be less strict if no player" +
                " in range of a damaging crystal/position can be found." +
                " Be careful not to blow yourself up!");
        register("Updates", "Tick: will update each tick (~50ms), Delay:" +
                " will updated in Intervals specified by the Delay setting.");
        register("Delay", "The interval in milliseconds to check your" +
                " position with if Updates is set to Delay. The lower this " +
                "setting the higher the workload on your CPU.");
    }

    @Override
    public int getColor()
    {
        return 0xffff0000;
    }

    @Override
    public String getDescription()
    {
        return "The SafetyManager on the PingBypass.";
    }

}
