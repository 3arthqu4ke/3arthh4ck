package me.earth.earthhack.impl.modules.client.pingbypass.submodules.sautocrystal;

import me.earth.earthhack.api.module.data.DefaultData;

final class ServerAutoCrystalData extends DefaultData<ServerAutoCrystal>
{
    public ServerAutoCrystalData(ServerAutoCrystal module)
    {
        super(module);
        register("Place", "Decides if we place crystals or not.");
        register("Target", "Which Players to " +
                "target. \n-Closest targets the closest player \n-Damage " +
                "targets the player we can damage most(CPU intensive)");
        register("PlaceRange",
                "Max distance from you to the position to place on.");
        register("PlaceTrace",
                "Max distance through walls, to the position " +
                "to place on. (Most servers allow the same wall- and " +
                "normal range for placing crystals)");
        register("MinDamage",
                "Minimum damage a crystal we place has to deal to the enemy.");
        register("PlaceDelay",
                "Delay (in ms) between each time we place a crystal.");
        register("MaxSelfPlace",
                "Maximum damage a crystal we place can deal to us.");
        register(("FacePlace"),
                "If the targets health is below this value we ignore" +
                    " MinDamage and faceplace him.");
        register(("MultiPlace"), "Maximum amount of crystals dealing" +
                " damage that can exist at the same time.");
        register(("CountMin"), "If off : only count crystals that" +
                " deal more than minDamage towards the MultiPlace value." +
                " Recommended On if you have higher Ping.");
        register(("AntiSurround"), "Places on positions that " +
                "already have crystals on them to speed up the AutoCrystal." +
                " (Has similar effects to SetDead)");
        register(("1.13+"), "After version 1.13 it's possible to " +
                "place crystals in 1 block high spaces. Use for " +
                "ViaVersion servers.");
        register(("Attack"), "When to attack: -Always well, " +
                "attacks always -BreakSlot Only attack if we are holding" +
                " a crystal, this is very recommended, since you" +
                " can leave the AutoCrystal on at all times -Calc " +
                "Same as breakslot but will show an ESP.");
        register(("Break"), "Decides if AutoCrystal attacks crystals or not.");
        register(("BreakRange"), "Max distance between you" +
                " and the crystal you want to break.");
        register(("BreakTrace"), "Max distance through walls" +
                " between you and the crystal you want to break." +
                " Most servers are a lot more strict with this " +
                "than with place wall range. For Orientation:" +
                " Vanilla servers have a wall range of 3 blocks.");
        register(("MinBreakDmg"), "Minimum damage that a crystal" +
                " has to deal to the enemy to be attacked by us.");
        register(("SlowBreak"), "Crystals dealing damage that lies " +
                "between MinBreakDmg and this value will be broken slowly" +
                " with the given SlowDelay.");
        register(("SlowDelay"), "Delay that crystals that deal " +
                "less Damage than SlowBreak get blown up with.");
        register(("BreakDelay"), "Delay between each time we attack" +
                " a crystal.");
        register(("MaxSelfBreak"), "Maximum damage a crystal we break" +
                " can deal to us.");
        register(("Instant"), "Attacks crystals immediately when they" +
                " spawn. Can speed up AutoCrystal by up to 100%." +
                " (Previously known as Predict)");
        register(("Rotate"), "Some AntiCheats require you to look " +
                "at the positions you place/break on : -None don't" +
                " rotate -Break only rotate for breaking crystals" +
                " -Place only rotate for placing crystals -All rotate" +
                " for both placing/breaking");
        register(("Stay"), "Keeps the rotations to a position from " +
                "placement til attacking.");
        register(("MultiThread"), "Especially Target - Damage can" +
                "be heavy for the CPU, this will transfer the calculations" +
                " to another Thread which will make the AutoCrystal eat " +
                "up less FPS. It's possible to rotate using this, but not" +
                " recommended.");
        register(("Suicide"), "Only recommended if you run around " +
                "with 20 Totem kits. Goes all out and will ignore damage" +
                " dealt to you.");
        register(("Range"), "Distance from crystal to target.");
        register(("Override"), "Ignore MinBreakDmg and MinPlaceDmg if w" +
                "e can deal lethal damage to the target.");
        register(("MinFace"), "MinDamage for Faceplacing.");
        register(("AntiFriendPop"), "Calculates damage dealt to friends.");
        register(("AutoSwitch"), "Automatically switches to crystals: " +
                "-None never switch -Bind use a bind to toggle switching o" +
                "n and off -Always always switch.");
        register(("MainHand"), "If On, AutoSwitch will switch to the main" +
                " hand, if Off, to the off hand if the Offhand module is " +
                "enabled.");
        register(("SwitchBind"), "The bind for AutoSwitch Mode Bind.");
        register(("SwitchBack"), "If the SwitchBind is pressed again, while" +
                " we are holding crystals in Offhand -> switch to" +
                " Totems in Offhand.");
        register(("Swing"), "Determines whether and with which Arm " +
                "to swing with.");
        register(("CombinedTrace"), "Prevents placing Crystals that don't li" +
                "e within the break wall range.");
        register(("SetDead"), "Removes crystals after we attacked them (Client"
                + " sided), which can speed up AutoCrystal. Similar effects to "
                + "AntiSurround, but will send less attack packets.");
        register(("Cooldown"), "Most servers have a cooldown within which " +
                "you can't attack entities after you switched your mainhand" +
                " slot. Attacking crystals during this time can cause" +
                " the AntiCheat to flag you. This setting prevents " +
                "that by waiting for the given delay (ms).");
        register(("PartialTicks"), "Only touch if you read the code. " +
                "Required when you want to use Multithreading and Rotate.");
        register(("FallBack"), "Due to the Damage calculation crystals" +
                " that deal no damage to you and the enemy can block" +
                " high damaging positions. This setting will cause the" +
                " AutoCrystal to break such Crystals.");
        register(("FB-Dmg"), "Max Damage a FallBack crystal can deal to you.");
        register(("SoundRemove"), "Explosion sounds arrive before the " +
                "actual Explosion at our client. It can be used to improve" +
                " AutoCrystal speeds.");
        register(("HoldFP"), "Faceplaces while you hold the left Mouse button.");
        register(("MultiTask"), "Recommended On. If Off: won't place" +
                " in ticks that we attacked a crystal in.");
        register(("ThreadMode"), "The entry point for starting a " +
                "Thread when MultiThreading. Recommended ones would" +
                " be Pre and Delay.");
        register(("ThreadDelay"), "Delay between each Thread when ThreadMode" +
                " is Delay. Low thread delays can be intensive.");
        register(("ID-Predict"), "Purely a fun setting. Drastically" +
                " increases your chances of getting kicked, but can" +
                " reach the theoretical speed limit of up to 20 " +
                "crystals/second.");
        register("NoOffhandParticles", "Blocks the particles that" +
                " appear when attacking crystals with a weapon " +
                "in your mainhand.");
    }

    @Override
    public int getColor()
    {
        return 0xffff0000;
    }

    @Override
    public String getDescription()
    {
        return "An AutoCrystal for the PingBypass.";
    }

}
