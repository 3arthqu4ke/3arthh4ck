package me.earth.earthhack.impl.modules.combat.killaura;

import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.entity.module.EntityTypeData;

final class KillAuraData extends EntityTypeData<KillAura>
{
    public KillAuraData(KillAura module)
    {
        super(module);
        register(module.passengers, "Attacks your own Passengers.");
        register(module.targetMode, "-Closest will target the closest Player," +
                " Takes Health and Armor into account." +
                "\n-Angle will target the closest Player to where you aim.");
        register(module.prioEnemies,
                "Prioritizes Enemies over armor, health and distance.");
        register(module.range,
                "Range in which targets will be hit.");
        register(module.wallRange,
                "Range in which targets will be hit through walls.");
        register(module.swordOnly, "Only attacks if you hold a Sword or Axe.");
        register(module.delay, "If on applies 1.9+ Delays to all weapons." +
                " Otherwise applies the CPS Setting.");
        register(module.cps,
                "Clicks/second when not using Delay or using a 32k.");
        register(module.rotate, "Rotates to the targets.");
        register(module.stopSneak, "Stops sneaking when attacking.");
        register(module.stopSprint, "Stops sprinting when attacking.");
        register(module.stopShield, "Hold rightclick to block.");
        register(module.whileEating, "Allows you to hit while eating.");
        register(module.stay, "Keeps rotations. " +
                "Recommended when using Soft or Rotation-Ticks.");
        register(module.rotationTicks, "Ticks to stay rotated " +
                "towards the entity before attacking it.");
        register(module.soft, "Soft Rotations. " +
                "Maximum Angle to rotate per tick.");
        register(module.auraTeleport, "- None won't teleport." +
                "\n-Smart only teleports so the target is in Range." +
                "\n-Full teleports into the target.");
        register(module.teleportRange, "Range for teleports.");
        register(module.yTeleport, "Teleports you down/up.");
        register(module.movingTeleport, "If Off: Won't teleport you if you" +
                " hold any of the WASD keys.");
        register(module.swing, Swing.DESCRIPTION);
        register(module.tps, "Syncs your attacks with the TPS.");
        register(module.t2k, "Ignores Delay and uses the CPS Setting " +
                "when attacking with a 32k.");
        register(module.health,
                "Targets with a health lower than this will be prioritized.");
        register(module.armor, "Targets wearing armor with a durability lower" +
                " than this value will be prioritized.");
        register(module.targetRange,
                "Only players within this range will be targeted.");
        register(module.multi32k,
                "Attacks multiple targets at the same time when using 32ks.");
        register(module.packets, "Can send multiple Packets when using a 32k.");
        register(module.height, "Part of the targets Hitbox to attack. " +
                "The higher the value the higher the target will be attacked." +
                " A value of 1 means the head will be attacked.");
        register(module.ridingTeleports,
                "Allows teleports when riding an Entity.");
        register(module.efficient, "Very smart.");
        register(module.cancelEntityEquip, "Cancels SPacketEntityEquipment.");
        register(module.tpInfo,
                "Displays ArrayList info even when not attacking.");
    }

}
