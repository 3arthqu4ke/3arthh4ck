/*
 * Decompiled with CFR 0.151.
 */
package me.earth.earthack.impl.modules.misc;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;

public class ModuleAutoGhastFarmer
extends Module {
protected final Setting<Boolean> sound =
        register(new BooleanSetting("Sound", false));
    public int currentX;
    public int currentY;
    public int currentZ;
    public int itemX;
    public int itemY;
    public int itemZ;
    public int ghastX;
    public int ghastY;
    public int ghastZ;
    public boolean ding = false;

        public ModuleAutoGhastFarmer()
    {
        super("AutoGhastFarmer", Category.Player);
    }

    @Override
    public void onEnable() {
        block3: {
            block2: {
                if (ModuleAutoGhastFarmer.mc.player == null) break block2;
                if (ModuleAutoGhastFarmer.mc.world != null) break block3;
            }
            return;
        }
        this.currentX = (int)ModuleAutoGhastFarmer.mc.player.posX;
        this.currentY = (int)ModuleAutoGhastFarmer.mc.player.posY;
        this.currentZ = (int)ModuleAutoGhastFarmer.mc.player.posZ;
    }

    @Override
    public void onDisable() {
        block3: {
            block2: {
                if (ModuleAutoGhastFarmer.mc.player == null) break block2;
                if (ModuleAutoGhastFarmer.mc.world != null) break block3;
            }
            return;
        }
        ModuleAutoGhastFarmer.mc.player.sendChatMessage("#stop");
    }

    @Override
    public void onUpdate() {
        if (ModuleAutoGhastFarmer.mc.player == null || ModuleAutoGhastFarmer.mc.world == null) {
            return;
        }
        Entity ghastEnt = null;
        double dist = Double.longBitsToDouble(Double.doubleToLongBits(0.017520017079696953) ^ 0x7FC8F0C47187D7FBL);
        for (Entity entity : ModuleAutoGhastFarmer.mc.world.loadedEntityList) {
            double ghastDist;
            if (!(entity instanceof EntityGhast) || !((ghastDist = (double)ModuleAutoGhastFarmer.mc.player.getDistance(entity)) < dist)) continue;
            dist = ghastDist;
            ghastEnt = entity;
            this.ghastX = (int)entity.posX;
            this.ghastY = (int)entity.posY;
            this.ghastZ = (int)entity.posZ;
            this.ding = true;
        }
        if (this.ding) {
            if (notifySound.getValue()) {
                ModuleAutoGhastFarmer.mc.player.playSound(SoundEvents.BLOCK_ANVIL_DESTROY, Float.intBitsToFloat(Float.floatToIntBits(5.2897425f) ^ 0x7F294592), Float.intBitsToFloat(Float.floatToIntBits(5.5405655f) ^ 0x7F314C50));
            }
            this.ding = false;
        }
        ArrayList entityItems = new ArrayList();
        entityItems.addAll(ModuleAutoGhastFarmer.mc.world.loadedEntityList.stream().filter(ModuleAutoGhastFarmer::lambda$onUpdate$0).map(ModuleAutoGhastFarmer::lambda$onUpdate$1).filter(ModuleAutoGhastFarmer::lambda$onUpdate$2).collect(Collectors.toList()));
        Entity itemEnt = null;
        Iterator iterator = entityItems.iterator();
        while (iterator.hasNext()) {
            Entity item;
            itemEnt = item = (Entity)iterator.next();
            this.itemX = (int)item.posX;
            this.itemY = (int)item.posY;
            this.itemZ = (int)item.posZ;
        }
        if (ghastEnt != null) {
            ModuleAutoGhastFarmer.mc.player.sendChatMessage("#goto " + this.ghastX + " " + this.ghastY + " " + this.ghastZ);
        } else if (itemEnt != null) {
            ModuleAutoGhastFarmer.mc.player.sendChatMessage("#goto " + this.itemX + " " + this.itemY + " " + this.itemZ);
        } else {
            ModuleAutoGhastFarmer.mc.player.sendChatMessage("#goto " + this.currentX + " " + this.currentY + " " + this.currentZ);
        }
    }

    public static boolean lambda$onUpdate$2(EntityItem entityItem) {
        return entityItem.getItem().getItem() == Items.GHAST_TEAR;
    }

    public static EntityItem lambda$onUpdate$1(Entity entity) {
        return (EntityItem)entity;
    }

    public static boolean lambda$onUpdate$0(Entity entity) {
        return (Entity)entity instanceof EntityItem;
    }
}
