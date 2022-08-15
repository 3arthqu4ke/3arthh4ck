package me.earth.earthhack.impl.core.ducks.entity;

/**
 * Duck interface for {@link net.minecraft.client.entity.EntityPlayerSP}.
 */
public interface IEntityPlayerSP
{
    double getLastReportedX();

    double getLastReportedY();

    double getLastReportedZ();

    float getLastReportedYaw();

    float getLastReportedPitch();

    boolean getLastOnGround();

    void setLastReportedX(double x);

    void setLastReportedY(double y);

    void setLastReportedZ(double z);

    void setLastReportedYaw(float yaw);

    void setLastReportedPitch(float pitch);

    int getPositionUpdateTicks();

    void superUpdate();

    void invokeUpdateWalkingPlayer();

    void setHorseJumpPower(float jumpPower);

}
