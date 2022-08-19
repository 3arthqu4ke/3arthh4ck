package me.earth.earthhack.impl.modules.combat.autoarmor.modes;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.combat.autoarmor.AutoArmor;
import me.earth.earthhack.impl.modules.combat.autoarmor.util.LevelStack;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;

import java.util.*;

public enum ArmorMode implements Globals
{
    Blast
    {
        @Override
        public Map<EntityEquipmentSlot, Integer> setup(boolean xCarry,
                                                       boolean curse,
                                                       boolean prio,
                                                       float threshold)
        {
            boolean wearingBlast = false;
            Set<EntityEquipmentSlot> cursed = new HashSet<>(6);
            List<EntityEquipmentSlot> empty = new ArrayList<>(4);
            for (int i = 5; i < 9; i++)
            {
                ItemStack stack = InventoryUtil.get(i);
                if (!stack.isEmpty())
                {
                    if (stack.getItem() instanceof ItemArmor)
                    {
                        int lvl = EnchantmentHelper.getEnchantmentLevel(
                                Enchantments.BLAST_PROTECTION, stack);

                        if (lvl > 0)
                        {
                            wearingBlast = true;
                        }
                    }
                    else
                    {
                        empty.add(AutoArmor.fromSlot(i));
                    }

                    if (EnchantmentHelper.hasBindingCurse(stack))
                    {
                        cursed.add(AutoArmor.fromSlot(i));
                    }
                }
                else
                {
                    empty.add(AutoArmor.fromSlot(i));
                }
            }

            if (wearingBlast && empty.isEmpty())
            {
                return new HashMap<>(1, 1.0f); // 2 for elytra
            }

            Map<EntityEquipmentSlot, LevelStack> map =
                    new HashMap<>(6);
            Map<EntityEquipmentSlot, LevelStack> blast =
                    new HashMap<>(6);

            for (int i = 8; i < 45; i++)
            {
                if (i == 5)
                {
                    i = 9;
                }

                ItemStack stack = getStack(i);
                if (!stack.isEmpty()
                        && stack.getItem() instanceof ItemArmor
                        && AutoArmor.curseCheck(stack, curse))
                {
                    float d = DamageUtil.getDamage(stack);
                    ItemArmor armor = (ItemArmor) stack.getItem();
                    EntityEquipmentSlot type = armor.getEquipmentSlot();
                    int blastLvL = EnchantmentHelper.getEnchantmentLevel(
                            Enchantments.BLAST_PROTECTION, stack);

                    if (blastLvL != 0)
                    {
                        compute(
                           stack, blast, type, i, blastLvL, d, prio, threshold);
                    }

                    int lvl = EnchantmentHelper.getEnchantmentLevel(
                            Enchantments.PROTECTION, stack);

                    if (blastLvL != 0)
                    {
                        if (lvl >= 4)
                        {
                            lvl += blastLvL;
                        }
                        else
                        {
                            continue;
                        }
                    }

                    compute(stack, map, type, i, lvl, d, prio, threshold);
                }

                if (i == 8 && xCarry)
                {
                    i = 0;
                }
            }

            Map<EntityEquipmentSlot, Integer> result = new HashMap<>(6);
            if (wearingBlast)
            {
                for (EntityEquipmentSlot slot : empty)
                {
                    if (map.get(slot) == null)
                    {
                        LevelStack e = blast.get(slot);
                        if (e != null)
                        {
                            map.put(slot, e);
                        }
                    }
                }

                map.keySet().retainAll(empty);
                map.forEach((key, value) -> result.put(key, value.getSlot()));
            }
            else
            {
                // TODO: Option to solve unlucky states where we are wearing
                //  blast but we have prot for that, while theres a blast piece
                //  where we dont have prot that we arent wearing
                boolean foundBlast = false;
                List<EntityEquipmentSlot> both = new ArrayList<>(4);
                for (EntityEquipmentSlot slot : empty)
                {
                    LevelStack b = blast.get(slot);
                    LevelStack p = map.get(slot);

                    if (b == null && p != null)
                    {
                        result.put(slot, p.getSlot());
                    }
                    else if (b != null && p == null)
                    {
                        foundBlast = true;
                        result.put(slot, b.getSlot());
                    }
                    else if (b != null)
                    {
                        both.add(slot);
                    }
                }

                for (EntityEquipmentSlot b : both)
                {
                    if (foundBlast)
                    {
                        result.put(b, map.get(b).getSlot());
                    }
                    else
                    {
                        foundBlast = true;
                        result.put(b, blast.get(b).getSlot());
                    }
                }

                if (!foundBlast && !blast.isEmpty())
                {
                    Optional<Map.Entry<EntityEquipmentSlot, LevelStack>> first =
                        blast.entrySet()
                             .stream()
                             .filter(e -> !cursed.contains(e.getKey()))
                             .findFirst();

                    first.ifPresent(e ->
                        result.put(e.getKey(), e.getValue().getSlot()));
                }
            }

            return result;
        }
    },
    Protection()
    {
        @Override
        public Map<EntityEquipmentSlot, Integer> setup(boolean xCarry,
                                                       boolean curse,
                                                       boolean prio,
                                                       float threshold)
        {
            List<EntityEquipmentSlot> semi  = new ArrayList<>(4);
            List<EntityEquipmentSlot> empty = new ArrayList<>(4);
            for (int i = 4; i < 9; i++)
            {
                ItemStack stack = InventoryUtil.get(i);
                EntityEquipmentSlot slot = AutoArmor.fromSlot(i);
                if (!stack.isEmpty())
                {
                    if (EnchantmentHelper.hasBindingCurse(stack))
                    {
                        continue;
                    }

                    if (stack.getItem() instanceof ItemArmor)
                    {
                        if (EnchantmentHelper.getEnchantmentLevel(
                                Enchantments.PROTECTION, stack) == 0)
                        {
                            semi.add(slot);
                        }
                    }
                    else
                    {
                        empty.add(slot);
                    }
                }
                else
                {
                    empty.add(slot);
                }
            }

            if (empty.isEmpty())
            {
                return new HashMap<>(0); // 1 for Elytra
            }

            Map<EntityEquipmentSlot, LevelStack> map =
                    new HashMap<>(6);

            for (int i = 8; i < 45; i++)
            {
                if (i == 5)
                {
                    i = 9;
                }

                ItemStack stack = getStack(i);
                if (!stack.isEmpty()
                        && stack.getItem() instanceof ItemArmor
                        && AutoArmor.curseCheck(stack, curse))
                {
                    float d = DamageUtil.getDamage(stack);
                    ItemArmor armor = (ItemArmor) stack.getItem();
                    EntityEquipmentSlot type = armor.getEquipmentSlot();
                    int lvl = EnchantmentHelper.getEnchantmentLevel(
                            Enchantments.PROTECTION, stack);

                    if (lvl >= 4)
                    {
                        lvl += EnchantmentHelper.getEnchantmentLevel(
                                Enchantments.BLAST_PROTECTION, stack);
                    }

                    compute(stack, map, type, i, lvl, d, prio, threshold);
                }

                if (i == 8 && xCarry)
                {
                    i = 0;
                }
            }

            for (EntityEquipmentSlot s : semi)
            {
                LevelStack entry = map.get(s);
                if (entry != null && entry.getLevel() > 0)
                {
                    empty.add(s);
                }
            }

            map.keySet().retainAll(empty);
            Map<EntityEquipmentSlot, Integer> result = new HashMap<>(6);
            map.forEach((key,value) -> result.put(key, value.getSlot()));
            return result;
        }
    },
    Elytra()
    {
        @Override
        public Map<EntityEquipmentSlot, Integer> setup(boolean xCarry,
                                                       boolean curse,
                                                       boolean prio,
                                                       float threshold)
        {
            Map<EntityEquipmentSlot, Integer> map =
                    Blast.setup(xCarry, curse, prio, threshold);

            int bestDura   = 0;
            int bestElytra = -1;
            ItemStack elytra = InventoryUtil.get(6);
            if (!elytra.isEmpty()
                && (elytra.getItem() instanceof ItemElytra
                    || EnchantmentHelper.hasBindingCurse(elytra)))
            {
                // handling of taking off the
                // armor should be done somewhere else
                map.remove(EntityEquipmentSlot.CHEST);
                return map;
            }

            for (int i = 8; i < 45; i++)
            {
                if (i == 5)
                {
                    i = 9;
                }

                ItemStack stack = getStack(i);
                if (!stack.isEmpty()
                        && stack.getItem() instanceof ItemElytra
                        && AutoArmor.curseCheck(stack, curse))
                {
                    int lvl = EnchantmentHelper.getEnchantmentLevel(
                            Enchantments.UNBREAKING, stack) + 1;

                    int dura = DamageUtil.getDamage(stack) * lvl;

                    if (bestElytra == -1
                        || !prio && dura > bestDura
                        || prio && dura > threshold && dura < bestDura)
                    {
                        bestElytra = i;
                        bestDura = dura;
                    }
                }

                if (i == 8 && xCarry)
                {
                    i = 0;
                }
            }

            if (bestElytra != -1)
            {
                map.put(EntityEquipmentSlot.CHEST, bestElytra);
            }

            return map;
        }
    };

    public abstract Map<EntityEquipmentSlot, Integer> setup(boolean xCarry,
                                                            boolean curse,
                                                            boolean prio,
                                                            float threshold);
    /**
     * @param slot the slot to get a stack from.
     * @return {@link InventoryPlayer#getItemStack()} if the slot is 8,
     *         {@link InventoryUtil#get(int)} otherwise.
     */
    public static ItemStack getStack(int slot)
    {
        if (slot == 8)
        {
            return mc.player.inventory.getItemStack();
        }

        return InventoryUtil.get(slot);
    }

    private static void compute(
            ItemStack stack,
            Map<EntityEquipmentSlot, LevelStack> map,
            EntityEquipmentSlot type,
            int slot,
            int level,
            float damage,
            boolean prio,
            float threshold)
    {
        map.compute(type, (k, v) ->
        {
            if (v == null || !v.isBetter(damage, threshold, level, prio))
            {
                return new LevelStack(stack, damage, slot, level);
            }

            return v;
        });
    }

}
