package me.earth.hudplugin;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ReceiveListener;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PotionManager extends SubscriberImpl implements Globals
{
    public static final PotionManager INSTANCE = new PotionManager();

    private final Map<EntityPlayer, PotionList> potions = new ConcurrentHashMap<>();

    private PotionManager()
    {
        this.listeners.add(new EventListener<UpdateEvent>(UpdateEvent.class)
        {
            @Override
            public void invoke(UpdateEvent updateEvent)
            {
                update();
            }
        });

        this.listeners.add(new ReceiveListener<>(SPacketEntityStatus.class, event ->
        {
            if (event.getPacket().getOpCode() == 35)
            {
                mc.addScheduledTask(() ->
                {
                    Entity entity = event.getPacket().getEntity(mc.world);
                    if (entity instanceof EntityPlayer)
                    {
                        onTotemPop((EntityPlayer) entity);
                    }
                });
            }
        }));
    }

    public void onLogout() {
        this.potions.clear();
    }

    public void updatePlayer() {
        PotionList list = new PotionList();
        for(PotionEffect effect : mc.player.getActivePotionEffects()) {
            list.addEffect(effect);
        }
        potions.put(mc.player, list);
    }

    public void update() {
        updatePlayer();

        ArrayList<EntityPlayer> removeList = new ArrayList<>();
        for (Map.Entry<EntityPlayer, PotionList> potionEntry : potions.entrySet()) {
            boolean notFound = true;
            for (EntityPlayer player : mc.world.playerEntities) {
                if (potions.get(player) == null) {
                    PotionList list = new PotionList();
                    for (PotionEffect effect : player.getActivePotionEffects()) {
                        list.addEffect(effect);
                    }
                    potions.put(player, list);
                    notFound = false;
                }

                if (potionEntry.getKey().equals(player)) {
                    notFound = false;
                }
            }

            if (notFound) {
                removeList.add(potionEntry.getKey());
            }
        }

        for (EntityPlayer player : removeList)
        {
            potions.remove(player);
        }
    }

    public List<PotionEffect> getOwnPotions() {
        return getPlayerPotions(mc.player);
    }

    public List<PotionEffect> getPlayerPotions(EntityPlayer player) {
        PotionList list = this.potions.get(player);
        List<PotionEffect> potions = new ArrayList<>();
        if (list != null) {
            potions = list.getEffects();
        }
        return potions;
    }

    public void onTotemPop(EntityPlayer player) {
        PotionList list = new PotionList();
        potions.put(player, list);
    }

    public PotionEffect[] getImportantPotions(EntityPlayer player) {
        PotionEffect[] array = new PotionEffect[3];
        for(PotionEffect effect : getPlayerPotions(player)) {
            Potion potion = effect.getPotion();
            switch((I18n.format(potion.getName())).toLowerCase()) {
                case "strength" :
                    array[0] = effect;
                    break;
                case "weakness" :
                    array[1] = effect;
                    break;
                case "speed" :
                    array[2] = effect;
                    break;
                default:
            }
        }
        return array;
    }

    public String getPotionString(PotionEffect effect) {
        Potion potion = effect.getPotion();
        return I18n.format(potion.getName()) + " " + (!HUDModule.getInstance().potions1.getValue() && effect.getAmplifier() == 0 ? "" : effect.getAmplifier() + 1 + " ") + TextColor.WHITE + Potion.getPotionDurationString(effect, 1.0f);
    }

    //bit chinese? There must be a way to make this better...
    public String getColoredPotionString(PotionEffect effect) {
        Potion potion = effect.getPotion();
        switch(I18n.format(potion.getName())) {
            case "Jump Boost" :
            case "Speed" :
                return TextColor.AQUA + getPotionString(effect);
            case "Resistance" :
            case "Strength" :
                return TextColor.RED + getPotionString(effect);
            case "Wither" :
            case "Slowness" :
            case "Weakness" :
                return TextColor.BLACK + getPotionString(effect);
            case "Absorption" :
                return TextColor.BLUE + getPotionString(effect);
            case "Haste" :
            case "Fire Resistance" :
                return TextColor.GOLD + getPotionString(effect);
            case "Regeneration" :
                return TextColor.LIGHT_PURPLE + getPotionString(effect);
            case "Night Vision" :
            case "Poison" :
                return TextColor.GREEN + getPotionString(effect);
            default :
                return TextColor.WHITE + getPotionString(effect);
        }
    }

    public String getTextRadarPotionWithDuration(EntityPlayer player) {
        PotionEffect[] array = getImportantPotions(player);
        PotionEffect strength = array[0];
        PotionEffect weakness = array[1];
        PotionEffect speed = array[2];
        return "" + (strength != null ? TextColor.RED + " S" + (int)(strength.getAmplifier() + 1) + " " + Potion.getPotionDurationString(strength, 1.0f) : "")
                + (weakness != null ? TextColor.DARK_GRAY + " W "  + Potion.getPotionDurationString(weakness, 1.0f) : "")
                + (speed != null ? TextColor.AQUA + " S" + (int)(speed.getAmplifier() + 1) + " " + Potion.getPotionDurationString(weakness, 1.0f) : "");
    }

    public String getTextRadarPotion(EntityPlayer player) {
        PotionEffect[] array = getImportantPotions(player);
        PotionEffect strength = array[0];
        PotionEffect weakness = array[1];
        PotionEffect speed = array[2];
        return "" + (strength != null ? TextColor.RED + " S" + (int)(strength.getAmplifier() + 1) + " " : "")
                + (weakness != null ? TextColor.DARK_GRAY + " W " : "")
                + (speed != null ? TextColor.AQUA + " S" + (int)(speed.getAmplifier() + 1) + " " : "");
    }

    public static class PotionList {
        private List<PotionEffect> effects = new ArrayList<>();

        public void addEffect(PotionEffect effect) {
            if(effect != null) {
                this.effects.add(effect);
            }
        }

        public List<PotionEffect> getEffects() {
            return this.effects;
        }
    }
}
