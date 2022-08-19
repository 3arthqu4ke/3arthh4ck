package me.earth.earthhack.impl.util.ncp;

public abstract class NcpTrace {
    protected double x0;
    protected double y0;
    protected double z0;
    protected double dX;
    protected double dY;
    protected double dZ;
    protected int blockX;
    protected int blockY;
    protected int blockZ;
    protected int endBlockX;
    protected int endBlockY;
    protected int endBlockZ;
    protected double oX;
    protected double oY;
    protected double oZ;
    protected double t = Double.MIN_VALUE;
    protected double tol = 0.0;
    protected boolean forceStepEndPos = true;
    protected int step = 0;
    protected boolean secondaryStep = true;
    private int maxSteps = Integer.MAX_VALUE;
    protected boolean collides = false;

    public NcpTrace() {
        set(0, 0, 0, 0, 0, 0);
    }

    protected abstract boolean step(int blockX, int blockY, int blockZ, double oX, double oY, double oZ, double dT, boolean isPrimary);

    public void set(double x0, double y0, double z0, double x1, double y1, double z1) {
        this.x0 = x0;
        this.y0 = y0;
        this.z0 = z0;
        dX = x1 - x0;
        dY = y1 - y0;
        dZ = z1 - z0;
        blockX = Visible.floor(x0);
        blockY = Visible.floor(y0);
        blockZ = Visible.floor(z0);
        endBlockX = Visible.floor(x1);
        endBlockY = Visible.floor(y1);
        endBlockZ = Visible.floor(z1);
        oX = x0 - (double) blockX;
        oY = y0 - (double) blockY;
        oZ = z0 - (double) blockZ;
        t = 0.0;
        step = 0;
        collides = false;
    }

    private static double tDiff(double dTotal, double offset, boolean isEndBlock) {
        if (dTotal > 0.0) {
            if (offset >= 1.0) {
                return isEndBlock ? Double.MAX_VALUE : 0.0;
            } else {
                return (1.0 - offset) / dTotal;
            }
        } else if (dTotal < 0.0) {
            if (offset <= 0.0) {
                return isEndBlock ? Double.MAX_VALUE : 0.0;
            } else {
                return offset / -dTotal;
            }
        } else {
            return Double.MAX_VALUE;
        }
    }

    public void loop() {
        double tX, tY, tZ, tMin;
        int transitions;
        boolean transX, transY, transZ;
        while (t + tol < 1.0) {
            tX = tDiff(dX, oX, blockX == endBlockX);
            tY = tDiff(dY, oY, blockY == endBlockY);
            tZ = tDiff(dZ, oZ, blockZ == endBlockZ);
            tMin = Math.max(0.0, Math.min(tX,  Math.min(tY, tZ)));
            if (tMin == Double.MAX_VALUE) {
                if (step < 1) {
                    tMin = 0.0;
                } else {
                    break;
                }
            }

            if (t + tMin > 1.0) {
                tMin = 1.0 - t;
            }

            step ++;
            if (!step(blockX, blockY, blockZ, oX, oY, oZ, tMin, true)) {
                break;
            }

            if (t + tMin + tol >= 1.0 && isEndBlock()) {
                break;
            }

            transitions = 0;
            transX = transY = transZ = false;
            if (tX == tMin && blockX != endBlockX && dX != 0.0) {
                transX = true;
                transitions ++;
            }
            if (tY == tMin && blockY != endBlockY && dY != 0.0) {
                transY = true;
                transitions ++;
            }
            if (tZ == tMin && blockZ != endBlockZ && dZ != 0.0) {
                transZ = true;
                transitions ++;
            }

            oX = Math.min(1.0, Math.max(0.0, oX + tMin * dX));
            oY = Math.min(1.0, Math.max(0.0, oY + tMin * dY));
            oZ = Math.min(1.0, Math.max(0.0, oZ + tMin * dZ));
            t = Math.min(1.0, t + tMin);

            if (transitions > 0) {
                if (!handleTransitions(transitions, transX, transY, transZ)) {
                    break;
                }

                if (forceStepEndPos && t + tol >= 1.0) {
                    step(blockX, blockY, blockZ, oX, oY, oZ, 0.0, true);
                    break;
                }
            } else {
                break;
            }

            if (step >= maxSteps) {
                break;
            }
        }
    }

    @SuppressWarnings("DuplicatedCode")
    protected boolean handleTransitions(int transitions, boolean transX, boolean transY, boolean transZ) {
        if (transitions > 1 && secondaryStep) {
            if (!handleSecondaryTransitions(transitions, transX, transY, transZ)) {
                return false;
            }
        }

        double tcMin = 1.0;

        if (transX) {
            if (dX > 0.0) {
                blockX ++;
                tcMin = Math.min(tcMin, ((double) blockX - x0) / dX);
            } else {
                blockX --;
                tcMin = Math.min(tcMin, (1.0 + (double) blockX - x0) / dX);
            }
        }

        if (transY) {
            if (dY > 0.0) {
                blockY ++;
                tcMin = Math.min(tcMin, ((double) blockY - y0) / dY);
            } else {
                blockY --;
                tcMin = Math.min(tcMin, (1.0 + (double) blockY - y0) / dY);
            }
        }

        if (transZ) {
            if (dZ > 0.0) {
                blockZ ++;
                tcMin = Math.min(tcMin, ((double) blockZ - z0) / dZ);
            } else {
                blockZ --;
                tcMin = Math.min(tcMin, (1.0 + (double) blockZ - z0) / dZ);
            }
        }

        oX = x0 + tcMin * dX - (double) blockX;
        oY = y0 + tcMin * dY - (double) blockY;
        oZ = z0 + tcMin * dZ - (double) blockZ;
        t = tcMin;
        return true;
    }

    protected boolean handleSecondaryTransitions(int transitions, boolean transX, boolean transY, boolean transZ) {
        if (transX) {
            if (!step(blockX + (dX > 0 ? 1 : -1), blockY, blockZ,
                      dX > 0 ? 0.0 : 1.0, oY, oZ, 0.0, false)) {
                return false;
            }
        }

        if (transY) {
            if (!step(blockX, blockY + (dY > 0 ? 1 : -1), blockZ, oX,
                      dY > 0 ? 0.0 : 1.0, oZ, 0.0, false)) {
                return false;
            }
        }

        if (transZ) {
            if (!step(blockX, blockY, blockZ + (dZ > 0 ? 1 : -1), oX, oY,
                      dZ > 0 ? 0.0 : 1.0, 0.0, false)) {
                return false;
            }
        }

        if (transitions == 3) {
            return handleSecondaryDoubleTransitions();
        }

        return true;
    }

    protected boolean handleSecondaryDoubleTransitions() {
        if (!step(blockX + (dX > 0 ? 1 : -1), blockY + (dY > 0 ? 1 : -1), blockZ, dX > 0 ? 0.0 : 1.0, dY > 0 ? 0.0 : 1.0, oZ, 0.0, false)) {
            return false;
        }

        if (!step(blockX + (dX > 0 ? 1 : -1), blockY, blockZ + (dZ > 0 ? 1 : -1), dX > 0 ? 0.0 : 1.0, oY, dZ > 0 ? 0.0 : 1.0, 0.0, false)) {
            return false;
        }

        return step(blockX, blockY + (dY > 0 ? 1 : -1), blockZ + (dZ > 0 ? 1 : -1), oX, dY > 0 ? 0.0 : 1.0, dZ > 0 ? 0.0 : 1.0, 0.0, false);
    }

    public boolean isEndBlock() {
        return blockX == endBlockX && blockY == endBlockY && blockZ == endBlockZ;
    }

    public int getStepsDone() {
        return step;
    }

    public int getMaxSteps() {
        return maxSteps;
    }

    public void setMaxSteps(int maxSteps) {
        this.maxSteps = maxSteps;
    }

}
