package me.earth.earthhack.impl.util.ncp;

import me.earth.earthhack.impl.modules.combat.autocrystal.helpers.PositionHistoryHelper;

import java.util.Deque;

public abstract class PositionHistoryChecker {
    // protected boolean invalidateFailed = true; TODO: implement
    protected boolean checkOldLook = true;
    protected int ticksToCheck = 10;

    protected abstract boolean check(double x, double y, double z,
                                     float yaw, float pitch,
                                     int blockX, int blockY, int blockZ);

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean checkFlyingQueue(double x, double y, double z,
                                    float oldYaw, float oldPitch,
                                    int blockX, int blockY, int blockZ,
                                    PositionHistoryHelper history) {
        if (checkOldLook) { // isPositionValid in RotationHistory
            if (check(x, y, z, oldYaw, oldPitch, blockX, blockY, blockZ)) {
                return true;
            }/* else {
                // set isPositionValid false in RotationHistory
            }*/
        }

        Deque<PositionHistoryHelper.RotationHistory> queue = history.getPackets();
        if (queue.size() == 0) {
            return false;
        }

        int checked = 0;
        for (PositionHistoryHelper.RotationHistory data : queue) {
            if (data == null) {
                continue;
            }

            if (++checked > 10) {
                break;
            }

            if (!data.hasLook) {
                continue;
            }

            float yaw = data.yaw;
            float pitch = data.pitch;
            if (yaw == oldYaw && pitch == oldPitch) {
                continue;
            }

            if (check(x, y, z, yaw, pitch, blockX, blockY, blockZ)) {
                return true;
            }
        }

        return false;
    }

}
