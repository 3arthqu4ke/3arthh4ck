package me.earth.earthhack.impl.util.animation;

import net.minecraft.util.math.MathHelper;

/**
 * A class representing an animation that takes x ms
 * all values must be positive or else it won't work (will fix later)
 * @author megyn
 */
public class TimeAnimation {

    // length in ms
    private final long length;
    private double start;
    private double end;
    private double current;
    private double progress;
    private boolean playing;
    private boolean backwards;
    private boolean reverseOnEnd;
    private long startTime;
    private long lastTime;
    private double per;
    private long dif;
    private boolean flag;

    private AnimationMode mode;

    public TimeAnimation(long length, double start, double end, boolean backwards, AnimationMode mode) {
        this.length = length;
        this.start = start;
        current = start;
        this.end = end;
        this.mode = mode;
        this.backwards = backwards;
        startTime = System.currentTimeMillis();
        playing = true;
        dif = (System.currentTimeMillis() - startTime);
        switch (mode) {
            case LINEAR:
                per = (end - start) / length;
                break;
            case EXPONENTIAL:
                double dif = end - start;
                flag = dif < 0;
                if (flag) dif *= -1;
                for (int i = 0; i < length; i++) {
                    dif = Math.sqrt(dif);
                }
                per = dif;
                break;
        }
        lastTime = System.currentTimeMillis();
    }

    public TimeAnimation(long length, double start, double end, boolean backwards, boolean reverseOnEnd, AnimationMode mode) {
        this(length, start, end, backwards, mode);
        this.reverseOnEnd = reverseOnEnd;
    }

    public void add(float partialTicks) {
        if (playing) {
            if (mode == AnimationMode.LINEAR) {
                current = start + progress;
                // current = start + (per * ((System.currentTimeMillis() - startTime)));
                progress += per * (System.currentTimeMillis() - lastTime);
                // lastTime = System.currentTimeMillis();
                // progress = (backwards ? -1 : 1) * (per * ((System.currentTimeMillis() - startTime)));
            } else if (mode == AnimationMode.EXPONENTIAL) {
                /*current = start + per;
                if (lastDif != dif) {
                    Earthhack.getLogger().info("per " + per);
                    per *= 1.0d + per;
                    if (flag && per > 0) per *= -1;
                }*/
            }
            current = MathHelper.clamp(current, start, end);
            if (current >= end || (backwards && current <= start)) {
                if (reverseOnEnd) {
                    reverse();
                    reverseOnEnd = false;
                } else {
                    playing = false;
                }
            }
        }
        lastTime = System.currentTimeMillis();
    }

    public long getLength() {
        return length;
    }

    public double getStart() {
        return start;
    }

    public double getEnd() {
        return end;
    }

    public double getCurrent() {
        return current;
    }

    public void setCurrent(double current) {
        this.current = current;
    }

    public AnimationMode getMode() {
        return mode;
    }

    public void setMode(AnimationMode mode) {
        this.mode = mode;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void play() {
        playing = true;
    }

    public void stop() {
        playing = false;
    }

    public boolean isBackwards() {
        return backwards;
    }

    /*public void setBackwards(boolean backwards) {
        this.backwards = backwards;
    }*/

    public void reverse() {
        backwards = !backwards;
        per *= -1;
    }

}
