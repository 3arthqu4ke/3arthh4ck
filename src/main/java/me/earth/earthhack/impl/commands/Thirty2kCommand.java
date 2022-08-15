package me.earth.earthhack.impl.commands;

import me.earth.earthhack.impl.commands.abstracts.AbstractStackCommand;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Arrays;

import static me.earth.earthhack.impl.util.thread.EnchantmentUtil.addEnchantment;

public class Thirty2kCommand extends AbstractStackCommand
{
    public Thirty2kCommand()
    {
        super("32k", "32k");
        CommandDescriptions.register(this, "Gives you a 32k sword.");
    }

    @Override
    protected ItemStack getStack(String[] args)
    {
        if (Arrays.stream(args).anyMatch("-bow"::equalsIgnoreCase))
        {
            ItemStack s = new ItemStack(Items.BOW);
            s.setStackDisplayName("3\u00B2arthB0w");
            s.setCount(64);

            addEnchantment(s, 48, Short.MAX_VALUE); // Power  32767
            addEnchantment(s, 49, Short.MAX_VALUE); // Punch  32767
            addEnchantment(s, 50, Short.MAX_VALUE); // Flame  32767
            addEnchantment(s, 51, 1);               // Infinity  1

            addEnchantment(s, 34, Short.MAX_VALUE); // Unbreaking  32767
            addEnchantment(s, 70, 1);               // Mending
            addEnchantment(s, 71, 1);               // Curse of Vanishing
            return s;
        }

        if (Arrays.stream(args).anyMatch("-skeleton"::equalsIgnoreCase))
        {
            return getSkeleton();
        }

        if (Arrays.stream(args).anyMatch("-slime"::equalsIgnoreCase)
            || Arrays.stream(args).anyMatch("-magma"::equalsIgnoreCase))
        {
            return getSlime(args);
        }

        if (Arrays.stream(args).anyMatch("-pick"::equalsIgnoreCase))
        {
            ItemStack s = new ItemStack(Items.DIAMOND_PICKAXE);
            s.setStackDisplayName("3\u00B2arth P1ck");

            addEnchantment(s, 32, Short.MAX_VALUE); // Efficiency 32767
            if (Arrays.stream(args).anyMatch("-fortune"::equalsIgnoreCase))
            {
                addEnchantment(s, 35, Short.MAX_VALUE); // Fortune 32767
            }
            else if (Arrays.stream(args).anyMatch("-silk"::equalsIgnoreCase))
            {
                addEnchantment(s, 33, 1); // Silk touch
            }

            addEnchantment(s, 34, Short.MAX_VALUE); // Unbreaking  32767
            addEnchantment(s, 70, 1);               // Mending
            addEnchantment(s, 71, 1);               // Curse of Vanishing
            return s;
        }

        boolean helmet = Arrays.stream(args).anyMatch("-helmet"::equalsIgnoreCase);
        boolean chest = Arrays.stream(args).anyMatch("-chest"::equalsIgnoreCase);
        boolean legs = Arrays.stream(args).anyMatch("-legs"::equalsIgnoreCase);
        boolean boots = Arrays.stream(args).anyMatch("-boots"::equalsIgnoreCase);

        ItemStack s = null;
        if (helmet) {
            s = new ItemStack(Items.DIAMOND_HELMET);
            s.setStackDisplayName("3\u00B2arth H3lmet");
        }

        if (chest) {
            s = new ItemStack(Items.DIAMOND_CHESTPLATE);
            s.setStackDisplayName("3\u00B2arth Ch3stPl4te");
        }

        if (legs) {
            s = new ItemStack(Items.DIAMOND_LEGGINGS);
            s.setStackDisplayName("3\u00B2arth L3ggings");
            addEnchantment(s, 3, Short.MAX_VALUE); // Blast Prot  32767
        }

        if (boots) {
            s = new ItemStack(Items.DIAMOND_BOOTS);
            s.setStackDisplayName("3\u00B2arth Bo0ts");
        }

        if (helmet || chest || legs || boots) {
            String dura = CommandUtil.getArgument("--dura", args);
            if (dura != null) {
                try {
                    s.setItemDamage(Integer.parseInt(dura));
                } catch (NumberFormatException e) {
                    ChatUtil.sendMessage(TextColor.RED + e.getMessage());
                }
            }

            addEnchantment(s, 0, Short.MAX_VALUE);  // Protection  32767
            addEnchantment(s, 7, Short.MAX_VALUE);  // Thorns  32767
            addEnchantment(s, 34, Short.MAX_VALUE); // Unbreaking  32767
            addEnchantment(s, 70, 1);               // Mending
            addEnchantment(s, 71, 1);               // Curse of Vanishing
            return s;
        }

        return get32kSword();
    }

    private ItemStack get32kSword() {
        ItemStack s = new ItemStack(Items.DIAMOND_SWORD);
        s.setStackDisplayName("3\u00B2arthbl4de");
        s.setCount(64);

        addEnchantment(s, 16, Short.MAX_VALUE); // Sharpness   32767
        addEnchantment(s, 19, 10);              // Knockback   10
        addEnchantment(s, 20, Short.MAX_VALUE); // Fire Aspect 32767
        addEnchantment(s, 21, 10);              // Looting     10
        addEnchantment(s, 22, 3);               // Sweeping    3
        addEnchantment(s, 34, Short.MAX_VALUE); // Unbreaking  32767
        addEnchantment(s, 70, 1);               // Mending
        addEnchantment(s, 71, 1);               // Curse of Vanishing
        return s;
    }

    private ItemStack getSkeleton() {
        EntitySkeleton skeleton = new EntitySkeleton(mc.world);
        ItemStack s;
        s = new ItemStack(Items.DIAMOND_HELMET);
        s.setStackDisplayName("3\u00B2arth H3lmet");
        basicArmorEnchants(s);
        skeleton.setItemStackToSlot(EntityEquipmentSlot.HEAD, s);

        s = new ItemStack(Items.DIAMOND_CHESTPLATE);
        s.setStackDisplayName("3\u00B2arth Ch3stPl4te");
        basicArmorEnchants(s);
        skeleton.setItemStackToSlot(EntityEquipmentSlot.CHEST, s);

        s = new ItemStack(Items.DIAMOND_LEGGINGS);
        s.setStackDisplayName("3\u00B2arth L3ggings");
        addEnchantment(s, 3, Short.MAX_VALUE); // Blast Prot  32767
        basicArmorEnchants(s);
        skeleton.setItemStackToSlot(EntityEquipmentSlot.LEGS, s);


        s = new ItemStack(Items.DIAMOND_BOOTS);
        s.setStackDisplayName("3\u00B2arth Bo0ts");
        basicArmorEnchants(s);
        skeleton.setItemStackToSlot(EntityEquipmentSlot.FEET, s);

        s = get32kSword();
        skeleton.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, s);

        s = new ItemStack(Items.TOTEM_OF_UNDYING, 1);
        skeleton.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, s);

        s = new ItemStack(Items.SPAWN_EGG);
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        NBTTagCompound entityTag = new NBTTagCompound();
        skeleton.writeEntityToNBT(entityTag);
        entityTag.setString("id", "minecraft:skeleton");
        nbtTagCompound.setTag("EntityTag", entityTag);
        nbtTagCompound.setString("id", "minecraft:spawn_egg");
        nbtTagCompound.setByte("Count", (byte) 64);
        s.setTagCompound(nbtTagCompound);
        s.setCount(64);

        return s;
    }

    private ItemStack getSlime(String[] args) {
        ItemStack s = new ItemStack(Items.SPAWN_EGG);
        NBTTagCompound nbtTagCompound = new NBTTagCompound();

        NBTTagCompound entityTag = new NBTTagCompound();
        if (Arrays.stream(args).anyMatch("-magma"::equalsIgnoreCase)) {
            entityTag.setString("id", "minecraft:magma_cube");
        } else {
            entityTag.setString("id", "minecraft:slime");
        }

        entityTag.setInteger("Size", CommandUtil.getInt("--size", 10, args));
        nbtTagCompound.setTag("EntityTag", entityTag);
        nbtTagCompound.setString("id", "minecraft:spawn_egg");
        nbtTagCompound.setByte("Count", (byte) 64);

        s.setTagCompound(nbtTagCompound);
        s.setCount(64);
        return s;
    }

    private void basicArmorEnchants(ItemStack s) {
        addEnchantment(s, 0, Short.MAX_VALUE);  // Protection  32767
        addEnchantment(s, 7, Short.MAX_VALUE);  // Thorns  32767
        addEnchantment(s, 34, Short.MAX_VALUE); // Unbreaking  32767
        addEnchantment(s, 70, 1);               // Mending
        addEnchantment(s, 71, 1);               // Curse of Vanishing
    }

}
