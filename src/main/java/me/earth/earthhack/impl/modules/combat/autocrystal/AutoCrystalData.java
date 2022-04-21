package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.SwingType;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.Target;

/*
     StringBuilder builder = new StringBuilder("\n");
     for (Field field : module.getClass().getDeclaredFields())
     {
         if (field.getType().isAssignableFrom(Setting.class))
         {
             field.setAccessible(true);
             builder.append("register(module.")
                    .append(field.getName())
                    .append(", \"\");\n");
         }
     }

     Earthhack.getLogger().info(builder.toString());
 */
public class AutoCrystalData extends DefaultData<AutoCrystal>
{
    public AutoCrystalData(AutoCrystal module)
    {
        super(module);
        this.presets.add(new EarthPreset(module));
        register(module.pages, "Structures the Settings in the ClickGui.");
        register(module.place, "If you want to place Crystals or not.");
        register(module.targetMode, Target.DESCRIPTION);
        register(module.placeRange, "Range to the Block you want to place on.");
        register(module.placeTrace, "Range, through walls, to the Block" +
           " you want to place on.");
        register(module.minDamage, "Minimum Damage a Crystal" +
           " should deal to an Enemy.");
        register(module.placeDelay,
            "Delay in ms between each Crystal-Placement.");
        register(module.maxSelfPlace,
            "Maximum Damage a crystal you place should deal to you.");
        register(module.multiPlace, "New crystals will only be placed " +
            "if there's less valid crystals than this value in range.");
        register(module.slowPlaceDmg, "If the damage you deal is lower " +
            "than this value the SlowPlaceDelay will be applied instead" +
            " of the normal PlaceDelay.");
        register(module.slowPlaceDelay,
            "Delay for placing less damaging Crystals.");
        register(module.override, "Places crystals that exceed " +
            "the MaxSelfPlace value, but only if they can kill an Enemy.");
        register(module.newVer,
            "Takes 1.13+ Mechanics (1 high spaces into account).");
        register(module.newVerEntities, "This is actually not a 1.13+ " +
            "mechanic, but Crystalpvp.cc allows you to place crystals " +
            "underneath Entities.");
        register(module.placeSwing, "-None, won't swing when placing.\n" +
            "-Pre, will swing before you place a crystal.\n" +
            "-Post, will swing after you placed a crystal (Vanilla).");
        register(module.smartTrace, "Only for really strict RayTrace Servers." +
                " Has to make complicated Calculations.");
        register(module.fallbackTrace, "Will place upwards if it has to.");
        register(module.simulatePlace, "Will spawn FakeCrystals, which live" +
            " for this amount of ticks. A value of 0 means this is Off.");
        register(module.attackMode, "-Always, Always attacks crystals.\n" +
            "-Crystal, Only attacks when you are holding a crystal.\n" +
            "-Calc, Similar to Crystal, but will show an ESP " +
            "when you aren't holding a crystal.");

        register(module.attack, "If you want to attack crystals.");
        register(module.breakRange,
            "Only Crystals within this range will be attacked.");
        register(module.breakDelay, "Delay between two attacks.");
        register(module.breakTrace, "Crystals will only be attacked through " +
            "walls, if they lie within this range.");
        register(module.minBreakDamage,
            "Minimum Damage a crystal needs to deal in order to be broken.");
        register(module.maxSelfBreak,
            "Maximum Damage a crystal should deal to you.");
        register(module.slowBreakDamage, "If a Crystal deals less than this" +
            " Damage the SlowBreakDelay will be applied instead of the normal" +
            " BreakDelay.");
        register(module.slowBreakDelay, "Delay for less damaging crystals.");
        register(module.instant, "Attacks Crystals in the moment they spawn.");
        register(module.asyncCalc, "For Instant: Normally Instant only" +
            " attacks Crystals we've placed, this will also calculate the " +
            "crystals we haven't placed in the moment they spawn.");
        register(module.alwaysCalc, "Ignores if we placed the crystal or not," +
            " will always apply the Async-Calc.");
        register(module.packets, "Amount of crystals to attack per tick.");
        register(module.overrideBreak, "Similar to OverridePlace, will attack" +
            " crystals that deal more than the MaxSelfBreak, but only" +
            " if they can kill an enemy.");
        register(module.antiWeakness, "-None, AntiWeakness is off, you can " +
            "only attack crystals if you hold a good tool in your hand.\n" +
            "-Switch, will switch to a damaging Tool and back" +
            " (Currently requires Cooldown 0).");
        register(module.instantAntiWeak, "Allows Instant to use AntiWeakness.");
        register(module.efficient, "Only accepts Crystals that deal more " +
            "damage to the enemy than to you.");
        register(module.manually, "Will attack crystals that you place " +
            "manually, regardless of how much damage they deal" +
            " (This can kill you).");
        register(module.manualDelay,
            "Delay for Breaking manually placed crystals.");
        register(module.breakSwing, "-None won't swing when attacking" +
            " crystals (Some servers flag that).\n-Pre will swing before you" +
            " attack a crystal.\n-Post will swing after (Vanilla).");

        register(module.rotate, "-None, no Rotations.\n-Break, only rotate" +
            "s to break crystals.\n-Place, only rotates to place crystals\n" +
            "-All, rotates for both Placing and Breaking.");
        register(module.rotateMode, "-Normal, Normal Rotations.\n-Smooth, Smooth Rotation.");
        register(module.endRotations, "Ends rotations after this time.");
        register(module.angle, "If your angle to a Crystal is bigger than" +
            " this, you will be rotated tick by tick until you look at it " +
            "(A value of 180 means this is off).");
        register(module.placeAngle, "If your angle to a Position you want to" +
            " place on is bigger than this, you will be rotated tick by tick" +
            " until you look at it (A value of 180 means this is off).");
        register(module.height, "Where you want to hit the Crystal. A value" +
            " of 0.5 would be its middle, while 0 would hit it at its feet.");
        register(module.placeHeight, "Height to hit the block at.");
        register(module.rotationTicks, "How long you have to look at a " +
            "Crystal before you attack it (in ms).");
        register(module.focusRotations,
                "Focuses the rotations on one crystal that is to break.");
        register(module.focusAngleCalc, "Compares Angles to crystals to check" +
                " if we might want to focus on another crystal.");
        register(module.focusExponent, "How much weight the angle should" +
                " get when comparing 2 crystals (focus)." +
                " A value of 0 means that" +
                " rotations are not taken into account.");
        register(module.focusDiff, "Ignores angle differences if the" +
                " difference between the 2 angles gets smaller than this value."
                + " (focus)");
        register(module.rotationExponent, "How much weight the angle should" +
                " get when comparing 2 crystals." +
                " A value of 0 means that" +
                " rotations are not taken into account.");
        register(module.minRotDiff, "Ignores angle differences if the" +
                " difference between the 2 angles gets smaller than this " +
                "value.");
        register(module.existed,
            "How long a Crystal has existed before we attack it.");
        register(module.pingExisted, "Takes your Ping into account," +
            " when applying Existed.");

        register(module.targetRange,
            "Only Players within this Range will be targeted.");
        register(module.pbTrace, "BreakTrace taken into account when placing.");
        register(module.range, "Range from Target to crystal.");
        register(module.suicide, "Will kill you if it can damage the Target.");
        register(module.multiTask,
            "Allows Placing and Breaking in the same tick.");
        register(module.multiPlaceCalc,
            "Always calculates MultiPlace, even if not breaking.");
        register(module.multiPlaceMinDmg, "Only takes Crystals that deal more" +
            "than MinDamage into account when preventing MultiPlace.");
        register(module.yCalc, "Doesn't calculate positions above players and" +
                " makes the calculation faster. Not useful at the Worldborder" +
                ", since positions above players might actually do damage " +
                "there.");
        register(module.dangerSpeed, "Ignores all SlowDelays when in danger.");
        register(module.cooldown, "For Servers with SwitchCooldown.");
        register(module.placeCoolDown,
            "Similar to Cooldown, just for placing.");
        register(module.antiFriendPop, "Tries not to pop Friends.");
        register(module.antiFeetPlace, "Automatically replaces crystals at the feet of opponents.");
        register(module.feetBuffer, "Delay for anti feet place.");
        register(module.motionCalc, "Only when not MultiThreading: Starts a" +
            " Calculation that evaluates if you should break before or after" +
            " you update your position on the server.");

        register(module.holdFacePlace, "Hold Mouse 1 to FacePlace.");
        register(module.facePlace,
            "Faceplaces if the target has less health than this.");
        register(module.minFaceDmg,
            "Minimum Damage the FacePlace has to deal.");
        register(module.armorPlace, "Faceplaces if the Target wears a " +
            "piece of armor has less durability than this (%).");
        register(module.pickAxeHold,
            "Allows HoldFacePlace while holding a pickaxe.");
        register(module.antiNaked,
            "Won't attack players that don't wear any armor.");
        register(module.fallBack, "Calculates a Fallback Crystal. In case a" +
            " position is blocked by a crystal and there are no other good" +
            " positions and we haven't found any good crystals yet, that" +
            " Fallback crystal will be attacked. The Fallback Crystal doesn't" +
            " necessarily deal Damage to the target, but also won't damage us" +
            " too much. Its explosion will free the position.");
        register(module.fallBackDiff, "Only if the Difference between the" +
            " damage the blocked Position deals and the damage the next best " +
            " position deals is greater or equal to this value a Fallback" +
            " Crystal will be attacked.");
        register(module.fallBackDmg,
            "Maximum damage a Fallback crystal is allowed to deal to us.");

        register(module.autoSwitch, "-None, will never AutoSwitch.\n-Bind," +
            " uses the SwitchBind to AutoSwitch if possible.\n-Always, always" +
            " switches to Crystals if a good position is found.");
        register(module.mainHand, "Will use your MainHand.");
        register(module.switchBind, "The SwitchBind for AutoSwitch - Bind.");
        register(module.switchBack, "Switches back to the last Item" +
            " after it placed (When using MainHand).");
        register(module.useAsOffhand, "Uses the Bind as Offhand Bind.");
        register(module.instantOffhand, "Speeds up the Switch to the Offhand.");
        register(module.pingBypass, "If PingBypass is on, this module won't " +
            "place anymore. The SwitchBind will then be used as your" +
            " Offhand - CrystalBind.");
        register(module.swing, SwingType.DESCRIPTION);
        register(module.placeHand,  SwingType.DESCRIPTION);
        register(module.obbyHand,  SwingType.DESCRIPTION);

        register(module.render, "Turns Rendering on and off.");
        register(module.renderMode, "Mode for the crystal render indicator.");
        register(module.renderTime, "Time to render the ESP for.");
        register(module.box, "Renders a box at the crystal position.");
        register(module.boxColor, "Color of the ESP.");
        register(module.outLine, "Color of the ESP outline.");
        register(module.indicatorColor, "Color of the ESP indicator.");
        register(module.fade, "Fade the box after.");
        register(module.fadeTime, "Time to render the Fade ESP for.");
        register(module.renderDamage, "Renders the Damage a position deals.");

        register(module.setDead, "Removes Crystals after it attacked them." +
            " Only use SetDead settings on servers where you" +
            " can be very sure that your crystals will break.");
        register(module.instantSetDead,
            "Removes Crystals when using the Instant setting." +
            " Only use SetDead settings on servers where you" +
            " can be very sure that your crystals will break.");
        register(module.pseudoSetDead, "Similar to SetDead, but without a " +
            "visual indicator. The Crystals are dead but will be visible." +
            " Only use SetDead settings on servers where you" +
            " can be very sure that your crystals will break.");
        register(module.simulateExplosion, "Simulates the Explosion created" +
            " by breaking a crystal. Sets all crystals in range dead." +
            " Only use SetDead settings on servers where you" +
            " can be very sure that your crystals will break.");
        register(module.soundRemove, "Uses SoundPackets to detect Explosions.");
        register(module.deathTime, "Minecraft doesn't allow you to place" +
            " crystals on dead Entities. Crystals will only be placed on dead" +
            " entities if they've been dead longer than this time in ms.");

        register(module.obsidian, "Places Obsidian for crystals when" +
            " no positions can be found. Placing Obsidian doesn't work" +
            " together with step by step Rotations.");
        register(module.obbySwitch, "Without this Obsidian will only be " +
                "placed if you are holding it. With this it will switch" +
                " to Obsidian silently.");
        register(module.obbyDelay, "Delay between 2 Obsidian Placements.");
        register(module.obbyCalc, "The Calculation for Obsidian can be very " +
            "intensive, delay between 2 Obsidian calculations.");
        register(module.helpingBlocks, "Maximum amount of blocks to place. " +
            "High values can make the calculations more complicated.");
        register(module.obbyMinDmg, "Minimum Damage for Obsidian.");
        register(module.terrainCalc, "Not only for Obsidian: Takes blocks " +
            "blown up by the Explosion into account.");
        register(module.obbySafety, "If a position that needs Obsidian can " +
            " deal high damage to you exists, Safety will be more increased" +
            " (Offhand might switch to Totem etc.).");
        register(module.obbyTrace, "RayTrace for Obsidian. Non Fast Raytrace " +
            "will make the Calculation more complicated.");
        register(module.obbyTerrain, "Terrain Calculation for Obsidian.");
        register(module.obbyPreSelf, "Tries to make the calculation easier. " +
            "Good for high HelpingBlocks values.");
        register(module.fastObby, "Limits the Obsidian Calculation to only" +
            " the blocks around the target. Useful when your PC" +
            " can't deal with it.");
        register(module.maxDiff, "When evaluating the best Obsidian position" +
            " the difference of the amount of HelpingBlocks needed is taken" +
            " into account. This setting determines how much that difference " +
            "factors in.");
        register(module.maxDmgDiff, "When evaluating the best Obsidian " +
            " position the difference in Damage dealt is taken into account." +
            " This settings determines how much that difference factors in.");
        register(module.obbySwing, "-Always, Swings for every block placed.\n" +
            "-Never, never swings for obsidian.\n-Once swings once no matter " +
            "how many blocks placed.");
        register(module.obbyFallback, "Uses the Fallback crystal if an " +
            "obsidian position is blocked.");
        register(module.obbyRotate, "-None, won't rotate for obsidian.\n" +
            "-Normal, normally rotates, only viable with 1 HelpingBlock.\n" +
            "-Packet, will send packets to rotate, can lag you back.");

        register(module.interact, "Allows Obsidian to interact with water.");
        register(module.inside,
                "Liquid Interact is different on different servers." +
                        " Try around with this setting on/off." +
                        " (CrystalAnarchy.org requires it off for example.)");
        register(module.lava, "Places blocks in lava and mines them, then" +
                        " places a crystal inside the air blocks that" +
                        " were created this way. Only possible on no" +
                        " cooldown servers. Blocks that don't drop items are" +
                        " to be favoured. Examples are glass and ice.");
        register(module.water, "Same as lava but much less consistent " +
                "because lava burns the dropped items.");
        register(module.liquidObby, "Combines the Lava/Water settings" +
                " with Obsidian placements.");
        register(module.liquidRayTrace, "RayTrace when using Lava/Water.");
        register(module.liqDelay, "Delay with which to use Lava/Water.");
        register(module.liqRotate, "Note that rotating for Liquids isn't" +
                " really supported yet. You can use Rotate Packet, " +
                "but it might lag you back.");
        register(module.pickaxeOnly, "Only uses Lava/Water while you " +
                "are holding a pickaxe.");
        register(module.interruptSpeedmine,
                "Lava/Water need to, so they might stop Speedmine.");
        register(module.setAir, "Sets the liquid blocks to air clientsided.");
        register(module.absorb, "Places and extra block that is supposed to" +
                " absorp the dropped item. Doesn't really work... :(");
        register(module.requireOnGround, "You should be standing " +
                "on the ground for liquids to work.");
        register(module.ignoreLavaItems,
                "Ignores Items in Lava when placing. (BETA)");
        register(module.sponges, "Not implemented yet :(");

        register(module.antiTotem, "Normally you can only pop a player " +
            "all 10 ticks. AntiTotem uses Minecrafts damage mechanics to pop " +
            "players faster than that. Only works in very specific situations" +
            ". E.g. the players health needs to be very low. This can cause" +
            " their AutoTotem to fail if they have > 100ms ping.");
        register(module.totemHealth, "Targets health needs to be less than" +
            " this value in order to attempt AntiTotem on them.");
        register(module.minTotemOffset, "Damage dealt needs to deal this much" +
            " more than the targets health, so Regen doesn't stop it.");
        register(module.maxTotemOffset, "Damage needs to deal less than this " +
            " + the targets health.");
        register(module.popDamage, "Damage required to pop the target " +
            "after it just popped.");
        register(module.totemSync, "Uses DamageSync mechanics (Recommended).");
        register(module.syncForce, "DamageSyncs Force-AntiTotem.");
        register(module.forcePlaceConfirm, "Time in ms to confirm that the" +
            " Force-AntiTotem Crystal has been spawned.");
        register(module.forceBreakConfirm, "Time in ms to confirm that the " +
            "Force-AntiTotem Crystal has been blown up after it spawned.");
        register(module.forceAntiTotem, "Attempts to force the player to a" +
            " health where he can be AntiTotem'd.");
        register(module.forceSlow, "More of a Debug Setting, changes" +
                " some math in the background.");
        register(module.attempts, "Delay between Force-AntiTotem Attempts.");

        register(module.damageSync, "Minecraft 1.9+ Damage mechanics work " +
            "like this: After you damaged a player a cooldown time of 10 " +
            "ticks (half a second) starts. Within this time you can only " +
            "damage the player if you deal more damage than the last damage " +
            "dealt to him. And even in that case he will only receive the " +
            "difference between last and current damage as damage. This means" +
            " it doesn't matter if you place 10 crystals/s or 2 crystals/" +
            "s, you will deal the same damage. You will however save 8 " +
            "crystals from being wasted. This is good in situations where " +
            "Resources are limited (Duels, Oldfag, etc.). But you will also" +
            " lose out on some protection the 8 wasted crystals grant by" +
            " blowing the enemies crystals up.");
        register(module.preSynCheck, "Will not attempt to place crystals" +
            " while DamageSync is confirming.");
        register(module.discreteSync, "Uses different Math that might make " +
            "delays more accurate.");
        register(module.placeConfirm, "Confirms that your crystal has" +
            " actually been placed within this time. Otherwise a new crystal" +
            " will be placed.");
        register(module.breakConfirm, "Confirms that your crystal has" +
            " been blown within this time. Otherwise a new crystal will be" +
            " placed.");
        register(module.syncDelay, "DamageSync Delay. Recommended at 500.");
        register(module.surroundSync, "You might've noticed that by placing " +
            "slowly you probably won't be able to outplace the targets" +
            " Surround. This setting leaves a crystal there until the next " +
            "DamageSync period.");

        register(module.bExtrapol, "Not yet implemented.");
        register(module.placeExtrapolation, "Not yet implemented.");
        register(module.selfExtrapolation, "Not yet implemented.");
        register(module.fullExtrapol, "Not yet implemented.");

        register(module.idPredict, "Attacks crystals before the server" +
            " even spawned them, by predicting Entity Ids. This can allow " +
            "you to blow up more crystals and faster than normally possible. " +
            "There's also a chance that you will get kicked by this." +
            " This chance is minimized if you only use ID-Predict in " +
            "separate 1v1 worlds. The more players in the world, the higher " +
            "the chance to get kicked.");
        register(module.idOffset, "Offset to the currently highest EntityID.");
        register(module.idDelay, "Delay with which GOD-Packets are send.");
        register(module.idPackets, "Amount of Attack-Packets to send" +
            " (Increases your chances of hitting your crystal).");
        register(module.godAntiTotem, "Combines the ID-Prediction" +
            " with AntiTotem. This can theoretically make any ones AutoTotem" +
            " fail. This settings is not bound to the ID-Predict setting.");
        register(module.godSwing, "-Always, Swings for every Attack-Packet " +
            "sent.\n-Never, never swings (Server might flag).\n-Once only " +
            "swings once, no matter how many attack packets.");
        register(module.preCalc, "Runs a fast calculation only limited to a " +
            "few blocks. This can speed up and lighten the load the " +
            "calculation has on the CPU, but might make you miss out on " +
            "AntiTotem etc.");
        register(module.preCalcDamage, "If a Pre-Calced position deals this" +
            " or more damage it will instantly be accepted.");

        register(module.multiThread, "By running the AutoCrystal calculation " +
            "on a different Thread (parallel to Minecraft) it will not hold " +
            "up rendering, causing FPS to increase.");
        register(module.rotationThread, "Rotations with MultiThreading are " +
            "difficult to implement. But its possible:\n-Predict, will " +
            "predict when to start the calculation, so its done by the time " +
            "we can rotate.\n-Cancel, cancels packets you send to spoof them " +
            "once the calculation is finished.\n-Wait, kinda defeats the" +
            " purpose of MultiThreading. Will make Minecraft wait until the " +
            "calculation is finished.");
        register(module.partial, "Development Setting for Predict.");
        register(module.maxCancel, "Packets cancelled by " +
            "RotationThread - Cancel will be send after this time in ms if " +
            "they haven't been spoofed.");
        register(module.timeOut, "Maximum Time for the MainThread to wait" +
            " when using RotationThread - Wait.");
        register(module.threadDelay, "Delay between 2 calculations (Low " +
            "delays mean more Load for the CPU).");
        register(module.blockDestroyThread,
            "Places right after SpeedMine broke a block.");
        register(module.explosionThread, "Starts a Thread" +
            " immediately after an Explosion.");
        register(module.soundThread, "Starts a Thread immediately after" +
            " an Explosion (Sound comes earlier than the Explosion itself).");
        register(module.entityThread, "Starts a new Calculation in the moment" +
            " a player moves. That means we can attack him as soon as " +
            "possible after he leaves a hole.");
        register(module.gameloop, "Attempts to start a thread every gameloop." +
            " Not recommended when rotating. If this or MotionThread is off " +
            "AutoCrystal might not place.");
        register(module.spawnThread, "Starts a Thread immediately after a " +
            "crystal spawned.");
        register(module.destroyThread, "Starts a Thread immediately after " +
            "crystals have been destroyed.");
        register(module.serverThread, "Starts a Thread before spawn packets " +
                "are sent to the player (Allows for faster ca if other thread settings are off).");
        register(module.asyncServerThread, "Runs another thread that spawns ca " +
                "threads based on server tick time.");
        register(module.earlyFeetThread, "When using serverThread, it is much easier " +
                "for your CA to be outplaced by surrounds. This setting will start threads early into " +
                "server ticks when your CA target can be feet placed, which makes your ca exponentially " +
                "harder to outplace. (Experimental)");
        register(module.lateBreakThread, "Breaks crystals late into a server tick " +
                "when earlyFeetThread is enabled so that your CA is not outplaced.");
        register(module.motionThread, "Starts a Thread immediately after " +
                "a MotionUpdateEvent. If this or GameLoop is off AutoCrystal" +
            " might not place.");
        register(module.blockChangeThread, "Starts a Thread immediately after" +
            " a block has changed close to an enemy player.");

        register(module.priority, "Allows you to set the RotationSpoof-" +
            "Priority. Should only be used if you understand how this " +
            "can affect the client.");

        register(module.shield, "Only use this when you have low ping." +
            " Places crystals which don't deal damage to you to shield" +
            " you from enemies crystals.");
        register(module.shieldCount, "MultiPlace for Shield.");
        register(module.shieldMinDamage, "When to activate the shield.");
        register(module.shieldSelfDamage, "Max damage to deal to yourself.");
    }

    @Override
    public String getDescription()
    {
        return "Places and breaks EndCrystals.";
    }

}
