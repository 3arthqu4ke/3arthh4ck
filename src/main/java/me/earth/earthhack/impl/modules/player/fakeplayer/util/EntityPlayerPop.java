package me.earth.earthhack.impl.modules.player.fakeplayer.util;

import com.mojang.authlib.GameProfile;
import me.earth.earthhack.impl.util.minecraft.ICachedDamage;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.thread.EnchantmentUtil;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityPlayerPop extends EntityPlayerAttack implements ICachedDamage
{
    @SuppressWarnings("unused")
    public EntityPlayerPop(World worldIn)
    {
        super(worldIn);
    }

    public EntityPlayerPop(World worldIn, GameProfile gameProfileIn)
    {
        super(worldIn, gameProfileIn);
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn)
    {
        if (slotIn == EntityEquipmentSlot.OFFHAND)
        {
            ItemStack stack = new ItemStack(Items.TOTEM_OF_UNDYING);
            stack.setCount(1);
            return stack;
        }

        return super.getItemStackFromSlot(slotIn);
    }

    @Override
    public void setHealth(float health)
    {
        if (health <= 0.0f)
        {
            pop();
            return;
        }

        super.setHealth(health);
    }

    @Override
    public void setDead()
    {
        // Issue with me popping causing FakePlayer to disappear (???)
    }

    public void pop()
    {
        NetworkUtil.receive(new SPacketEntityStatus(this, (byte) 35));
        super.setHealth(1.0f);
        this.setAbsorptionAmount(8.0f);
        this.clearActivePotions();
        this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 900, 1));
        this.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 100, 1));
    }

    @Override
    public int getArmorValue()
    {
        // shitty fix for now
        return mc.player.getTotalArmorValue();
    }

    @Override
    public float getArmorToughness()
    {
        // shitty fix for now
        return (float) mc.player
            .getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS)
            .getAttributeValue();
    }

    @Override
    public int getExplosionModifier(DamageSource source)
    {
        return EnchantmentUtil.getEnchantmentModifierDamage(
            this.getArmorInventoryList(), source);
    }

}
