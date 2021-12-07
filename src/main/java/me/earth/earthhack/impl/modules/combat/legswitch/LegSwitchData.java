package me.earth.earthhack.impl.modules.combat.legswitch;

import me.earth.earthhack.api.module.data.DefaultData;

final class LegSwitchData extends DefaultData<LegSwitch>
{
    public LegSwitchData(LegSwitch module)
    {
        super(module);
        register(module.delay, "Delay between between a PlaceBreakProcess.");
        register(module.closest, "Targets only the closest player.");
        register(module.rotate, "Type of Rotation.");
        register(module.minDamage, "Min Damage a crystal needs to deal.");
        register(module.maxSelfDamage,
                "Maximum damage a crystal can deal to you.");
        register(module.placeRange,
                "Range in which crystals should be placed.");
        register(module.placeTrace,
                "Range that crystals are placed through walls with.");
        register(module.breakRange,
                "Only crystals within this range will be broken.");
        register(module.breakTrace, "Only crystals within this range will" +
                " be broken through walls.");
        register(module.combinedTrace, "Break/PlaceTrace. Keep this at" +
                " the BreakTraces value.");
        register(module.instant, "Instantly break Crystals.");
        register(module.setDead, "Removes crystals after they've" +
                " been attacked.");
        register(module.requireMid, "Takes the middle block between the 2" +
                " crystals into account. (Dev Setting)");
        register(module.soundRemove, "Uses SoundPackets to remove Crystals.");
        register(module.soundStart, "Dev Setting don't touch!");
        register(module.newVer, "Takes 1.13+ mechanics into account." +
                " For ViaVersion servers.");
        register(module.rotationPacket, "Produces an extra Rotation Packet." +
                " Might lag you back.");
        register(module.coolDown, "Required for some servers where you can't" +
                " attack instantly after you switched your mainhand item.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Attempts to bypass Minecraft mechanics that stop Crystals" +
                " from blocking Surround. Will deal less damage.";
    }

}
