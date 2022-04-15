package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.api.config.preset.BuildinPreset;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACPages;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.AntiFriendPop;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.AntiWeakness;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.Attack;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.AutoSwitch;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.PreCalc;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.RenderDamagePos;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.RotationThread;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.SwingTime;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.SwingType;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.Target;
import me.earth.earthhack.impl.util.helpers.blocks.modes.PlaceSwing;
import me.earth.earthhack.impl.util.helpers.blocks.modes.RayTraceMode;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;

import java.awt.*;

/*
    StringBuilder builder = new StringBuilder("\n");
    for (Field field : this.getClass().getDeclaredFields())
    {
        if (field.getType().isAssignableFrom(Setting.class))
        {
            field.setAccessible(true);
            builder.append("add(module.")
                    .append(field.getName())
                    .append(", ");

            Object o = null;
            try
            {
                o = ((Setting<?>) field.get(this)).getValue();
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }

            if (o instanceof Enum)
            {
                if (o.getClass().getSuperclass() != Enum.class)
                {
                    builder.append(o.getClass().getSuperclass()
                                               .getSimpleName())
                           .append(".");
                }
                else
                {
                    builder.append(o.getClass().getSimpleName())
                           .append(".");
                }
            }


            if (o instanceof Color)
            {
                Color c = (Color) o;
                builder.append("new Color(")
                       .append(c.getRed())
                       .append(", ")
                       .append(c.getGreen())
                       .append(", ")
                       .append(c.getBlue())
                       .append(", ")
                       .append(c.getAlpha())
                       .append(")");
            }
            else
            {
                builder.append(o);
            }

            if (o instanceof Float)
            {
                builder.append("f");
            }

            builder.append(");\n");
        }
    }

    Earthhack.getLogger().info(builder.toString());
 */
public class EarthPreset extends BuildinPreset<AutoCrystal>
{
    public EarthPreset(AutoCrystal module)
    {
        super("crystalpvp.cc",
                module,
                "3arthqu4ke's config for Crystalpvp.cc (~30ms, eu). " +
                "Abuses the Fast-Mainhand Switch.");
        add(module.pages, ACPages.Place);
        add(module.place, true);
        add(module.targetMode, Target.Damage);
        add(module.placeRange, 5.25f);
        add(module.placeTrace, 5.25f);
        add(module.minDamage, 6.0f);
        add(module.placeDelay, 0);
        add(module.maxSelfPlace, 9.0f);
        add(module.multiPlace, 2);
        add(module.slowPlaceDmg, 5.0f);
        add(module.slowPlaceDelay, 500);
        add(module.override, false);
        add(module.newVer, false);
        add(module.newVerEntities, true);
        add(module.placeSwing, SwingTime.None);
        add(module.simulatePlace, 0);
        add(module.attackMode, Attack.Always);
        add(module.attack, true);
        add(module.breakRange, 6.0f);
        add(module.breakDelay, 0);
        add(module.breakTrace, 3.0f);
        add(module.minBreakDamage, 2.0f);
        add(module.maxSelfBreak, 10.0f);
        add(module.slowBreakDamage, 3.0f);
        add(module.slowBreakDelay, 500);
        add(module.instant, true);
        add(module.asyncCalc, true);
        add(module.alwaysCalc, true);
        add(module.packets, 1);
        add(module.overrideBreak, false);
        add(module.antiWeakness, AntiWeakness.Switch);
        add(module.instantAntiWeak, true);
        add(module.efficient, true);
        add(module.manually, true);
        add(module.manualDelay, 500);
        add(module.breakSwing, SwingTime.Post);
        add(module.rotate, ACRotate.None);
        add(module.endRotations, 250);
        add(module.angle, 86.05f);
        add(module.placeAngle, 82.05f);
        add(module.height, 0.04f);
        add(module.rotationTicks, 20);
        add(module.existed, 0);
        add(module.pingExisted, false);
        add(module.targetRange, 12.0f);
        add(module.pbTrace, 3.1f);
        add(module.range, 12.0f);
        add(module.suicide, false);
        add(module.multiTask, true);
        add(module.multiPlaceCalc, false);
        add(module.multiPlaceMinDmg, true);
        add(module.dangerSpeed, true);
        add(module.cooldown, 0);
        add(module.placeCoolDown, 0);
        add(module.antiFriendPop, AntiFriendPop.None);
        add(module.motionCalc, false);
        add(module.holdFacePlace, true);
        add(module.facePlace, 10.0f);
        add(module.minFaceDmg, 2.0f);
        add(module.armorPlace, 5.0f);
        add(module.pickAxeHold, false);
        add(module.antiNaked, false);
        add(module.fallBack, true);
        add(module.fallBackDiff, 10.0f);
        add(module.fallBackDmg, 2.0f);
        add(module.autoSwitch, AutoSwitch.Always);
        add(module.mainHand, true);
        add(module.switchBind, Bind.none());
        add(module.switchBack, true);
        add(module.useAsOffhand, false);
        add(module.instantOffhand, true);
        add(module.pingBypass, true);
        add(module.swing, SwingType.MainHand);
        add(module.placeHand, SwingType.MainHand);
        add(module.obbyHand, SwingType.MainHand);
        add(module.render, true);
        add(module.boxColor, new Color(255, 255, 255, 120));
        add(module.outLine, new Color(255, 255, 255, 255));
        add(module.renderDamage, RenderDamagePos.None);
        add(module.setDead, false);
        add(module.instantSetDead, true);
        add(module.pseudoSetDead, true);
        add(module.simulateExplosion, false);
        add(module.soundRemove, true);
        add(module.deathTime, 30);
        add(module.obsidian, true);
        add(module.obbySwitch, false);
        add(module.obbyDelay, 500);
        add(module.obbyCalc, 500);
        add(module.helpingBlocks, 1);
        add(module.terrainCalc, false);
        add(module.obbySafety, false);
        add(module.obbyTrace, RayTraceMode.Fast);
        add(module.obbyTerrain, true);
        add(module.obbyPreSelf, true);
        add(module.fastObby, 0);
        add(module.maxDiff, 1);
        add(module.maxDmgDiff, 0.0);
        add(module.obbySwing, PlaceSwing.Once);
        add(module.obbyFallback, false);
        add(module.obbyRotate, Rotate.None);
        add(module.antiTotem, true);
        add(module.totemHealth, 1.5f);
        add(module.minTotemOffset, 0.0f);
        add(module.maxTotemOffset, 2.0f);
        add(module.popDamage, 13.0f);
        add(module.totemSync, true);
        add(module.forceAntiTotem, false);
        add(module.syncForce, true);
        add(module.forcePlaceConfirm, 100);
        add(module.forceBreakConfirm, 100);
        add(module.attempts, 500);
        add(module.damageSync, false);
        add(module.preSynCheck, false);
        add(module.discreteSync, false);
        add(module.placeConfirm, 250);
        add(module.breakConfirm, 250);
        add(module.syncDelay, 500);
        add(module.surroundSync, true);
        add(module.bExtrapol, 0);
        add(module.placeExtrapolation, 0);
        add(module.selfExtrapolation, false);
        add(module.fullExtrapol, false);
        add(module.idPredict, false);
        add(module.idOffset, 1);
        add(module.idPackets, 1);
        add(module.godAntiTotem, false);
        add(module.godSwing, PlaceSwing.Once);
        add(module.preCalc, PreCalc.None);
        add(module.preCalcDamage, 13.6f);
        add(module.multiThread, true);
        add(module.rotationThread, RotationThread.Predict);
        add(module.partial, 0.8f);
        add(module.maxCancel, 10);
        add(module.timeOut, 2);
        add(module.entityThread, true);
        add(module.blockDestroyThread, true);
        add(module.threadDelay, 25);
        add(module.explosionThread, true);
        add(module.soundThread, true);
        add(module.spawnThread, true);
        add(module.destroyThread, true);
        add(module.blockChangeThread, true);
        add(module.priority, 1500);
    }

}
