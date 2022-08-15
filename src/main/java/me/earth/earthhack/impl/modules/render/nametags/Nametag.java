package me.earth.earthhack.impl.modules.render.nametags;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.media.Media;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.PhaseUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class Nametag implements Globals
{
    private static final ModuleCache<Media> MEDIA =
            Caches.getModule(Media.class);

    private final Nametags module;
    public final EntityPlayer player;
    public final StackRenderer mainHand;
    public final List<StackRenderer> stacks;
    public final String nameString;
    public final int nameColor;
    public final int nameWidth;
    public int maxEnchHeight;
    public boolean renderDura;
    public static boolean isRendering;

    public Nametag(Nametags module, EntityPlayer player)
    {
        this.module = module;
        this.player = player;
        this.stacks = new ArrayList<>(6);

        ItemStack mainStack = player.getHeldItemMainhand();
        if (mainStack.isEmpty())
        {
            mainHand = null;
        }
        else
        {
            boolean damageable = mainStack.isItemStackDamageable()
                                    && module.durability.getValue();
            if (damageable)
            {
                renderDura = true;
            }

            mainHand = new StackRenderer(mainStack, damageable);
            calcEnchHeight(mainHand);
        }

        for (int i = 3; i > -1; i--) {
            addStack(player.inventory.armorInventory.get(i));
        }

        addStack(player.getHeldItemOffhand());

        this.nameColor = getColor(player);
        this.nameString = getName(player);
        this.nameWidth = Managers.TEXT.getStringWidth(nameString);

        for (StackRenderer sr : stacks)
        {
            calcEnchHeight(sr);
        }
    }

    private void calcEnchHeight(StackRenderer sr)
    {
        int enchHeight = EnchantmentHelper.getEnchantments(sr.getStack())
                .size();
        if (module.armor.getValue() && enchHeight > maxEnchHeight)
        {
            maxEnchHeight = enchHeight;
        }
    }

    private void addStack(ItemStack stack)
    {
        if (!stack.isEmpty())
        {
            boolean damageable = stack.isItemStackDamageable()
                                    && module.durability.getValue();
            if (damageable)
            {
                renderDura = true;
            }

            stacks.add(new StackRenderer(stack, damageable));
        }
    }

    private String getName(EntityPlayer player)
    {
        String name = player.getDisplayName().getFormattedText().trim();

        String s;
        if (module.media.getValue())
        {
            s = MEDIA.returnIfPresent(m -> m.convert(name), name);
        }
        else
        {
            s = name;
        }

        StringBuilder builder = new StringBuilder(s);
        boolean offset = builder.toString().replaceAll("§.", "").length() > 0;
        if (module.id.getValue())
        {
            builder.append(offset ? " " : "")
                   .append("ID: ")
                   .append(player.getEntityId());
            offset = true;
        }

        if (module.gameMode.getValue())
        {
            builder.append((offset ? " " : ""))
                   .append("[")
                   .append(player.isCreative()
                             ? "C"
                             : (player.isSpectator() ? "I" : "S"))
                   .append("]");
            offset = true;
        }

        NetHandlerPlayClient connection = mc.getConnection();
        if (connection != null)
        {
            NetworkPlayerInfo playerInfo =
                    connection.getPlayerInfo(player.getUniqueID());
            //noinspection ConstantConditions
            if (module.ping.getValue() && playerInfo != null)
            {
                builder.append((offset ? " " : ""))
                       .append(playerInfo.getResponseTime())
                       .append("ms");
                offset = true;
            }
        }

        if (module.health.getValue())
        {
            double health = Math.ceil(EntityUtil.getHealth(player));
            String healthColor;

            if (health > 18.0)
            {
                healthColor = TextColor.GREEN;
            }
            else if (health > 16.0)
            {
                healthColor = TextColor.DARK_GREEN;
            }
            else if (health > 12.0)
            {
                healthColor = TextColor.YELLOW;
            }
            else if (health > 8.0)
            {
                healthColor = TextColor.GOLD;
            }
            else if (health > 5.0)
            {
                healthColor = TextColor.RED;
            }
            else
            {
                healthColor = TextColor.DARK_RED;
            }

            builder.append((offset ? " " : ""))
                   .append(healthColor)
                   .append(health > 0.0 ? (int) health : "0");
        }

        if (module.pops.getValue())
        {
            int pops = Managers.COMBAT.getPops(player);
            if (pops != 0)
            {
                builder.append(TextColor.WHITE)
                       .append(" -")
                       .append(pops);
            }
        }

        if (module.motion.getValue())
        {
            builder.append(" ")
                .append(TextColor.GRAY)
                .append("x: ")
                .append(TextColor.WHITE)
                .append(MathUtil.round(player.motionX * 20, 2))
                .append(TextColor.GRAY)
                .append(", y: ")
                .append(TextColor.WHITE)
                .append(MathUtil.round(player.motionY * 20, 2))
                .append(TextColor.GRAY)
                .append(", z: ")
                .append(TextColor.WHITE)
                .append(MathUtil.round(player.motionZ * 20, 2));
        }

        if (module.motionKpH.getValue())
        {
            double kpH = Math.sqrt(
                player.motionX * player.motionX
                    + player.motionZ * player.motionZ) * 20 * 3.6;

            builder.append(" ")
                   .append(TextColor.WHITE)
                   .append(MathUtil.round(kpH, 2))
                   .append(TextColor.GRAY)
                   .append(" km/h");
        }

        return builder.toString();
    }

    private int getColor(EntityPlayer player)
    {
        if (Managers.FRIENDS.contains(player))
        {
            return 0xff66ffff;
        }

        if (module.burrow.getValue())
        {
            BlockPos pos = PositionUtil.getPosition(player);
            IBlockState state = mc.world.getBlockState(pos);
            if (!state.getMaterial().isReplaceable()
                    && state.getBoundingBox(mc.world, pos).offset(pos).maxY
                            > player.posY)
            {
                return 0xff670067;
            }
        }

        if (module.phase.getValue() && PhaseUtil.isPhasing(
            player, module.pushMode.getValue()))
        {
            return 0xff670067;
        }

        if (Managers.ENEMIES.contains(player))
        {
            return 0xffff0000;
        }

        if (player.isInvisible())
        {
            return 0xffff2500;
        }

        //noinspection ConstantConditions
        if (mc.getConnection() != null
              && mc.getConnection().getPlayerInfo(player.getUniqueID()) == null)
        {
            return 0xffef0147;
        }

        if (player.isSneaking() && module.sneak.getValue())
        {
            return 0xffff9900;
        }

        return 0xffffffff;
    }

}
