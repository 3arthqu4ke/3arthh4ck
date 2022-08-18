package me.earth.earthhack.impl.modules.combat.autocrystal.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class PlaceData
{
    private final Map<EntityPlayer, ForceData> force         = new HashMap<>();
    private final Map<EntityPlayer, List<PositionData>> corr = new HashMap<>();
    private final Map<BlockPos, PositionData> obby           = new HashMap<>();
    private final Map<BlockPos, PositionData> liquidObby     = new HashMap<>();

    private final List<PositionData> liquid    = new ArrayList<>();
    private final Set<PositionData> data       = new TreeSet<>();
    private final Set<AntiTotemData> antiTotem = new TreeSet<>();
    private final Set<PositionData> shieldData = new TreeSet<>();
    private final Set<PositionData> raytraceData = new TreeSet<>();

    private EntityPlayer shieldPlayer;
    private float highestSelfDamage;
    private final float minDamage;
    private EntityPlayer target;

    public PlaceData(float minDamage)
    {
        this.minDamage = minDamage;
    }

    public void setTarget(EntityPlayer target)
    {
        this.target = target;
    }

    public EntityPlayer getShieldPlayer()
    {
        if (shieldPlayer == null)
        {
            shieldPlayer = new ShieldPlayer(Minecraft.getMinecraft().world);
        }

        return shieldPlayer;
    }

    public void addAntiTotem(AntiTotemData data)
    {
        this.antiTotem.add(data);
    }

    public void addCorrespondingData(EntityPlayer player, PositionData data)
    {
        List<PositionData> list =
                corr.computeIfAbsent(player, v -> new ArrayList<>());

        list.add(data);
    }

    public void confirmHighDamageForce(EntityPlayer player)
    {
        ForceData data = force.computeIfAbsent(player, v -> new ForceData());
        data.setPossibleHighDamage(true);
    }

    public void confirmPossibleAntiTotem(EntityPlayer player)
    {
        ForceData data = force.computeIfAbsent(player, v -> new ForceData());
        data.setPossibleAntiTotem(true);
    }

    public void addForceData(EntityPlayer player, ForcePosition forceIn)
    {
        ForceData data = force.computeIfAbsent(player, v -> new ForceData());
        data.getForceData().add(forceIn);
    }

    public void addAllCorrespondingData()
    {
        for (AntiTotemData antiTotemData : antiTotem)
        {
            for (EntityPlayer player : antiTotemData.getAntiTotems())
            {
                List<PositionData> corresponding = corr.get(player);
                if (corresponding != null)
                {
                    corresponding.forEach(antiTotemData::addCorrespondingData);
                }
            }
        }
    }

    public float getMinDamage()
    {
        return minDamage;
    }

    public EntityPlayer getTarget()
    {
        return target;
    }

    public Set<AntiTotemData> getAntiTotem()
    {
        return antiTotem;
    }

    public Set<PositionData> getData()
    {
        return data;
    }

    public Map<BlockPos, PositionData> getAllObbyData()
    {
        return obby;
    }

    public Map<EntityPlayer, ForceData> getForceData()
    {
        return force;
    }

    public List<PositionData> getLiquid()
    {
        return liquid;
    }

    public Map<BlockPos, PositionData> getLiquidObby()
    {
        return liquidObby;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("PlaceData:\n");
        for (PositionData data : data)
        {
            builder.append("Position: ").append(data.getPos()).append("\n");
        }

        return builder.toString();
    }

    public float getHighestSelfDamage() {
        return highestSelfDamage;
    }

    public void setHighestSelfDamage(float highestSelfDamage) {
        this.highestSelfDamage = highestSelfDamage;
    }

    public Set<PositionData> getShieldData()
    {
        return shieldData;
    }

    public Set<PositionData> getRaytraceData() {
        return raytraceData;
    }

}
