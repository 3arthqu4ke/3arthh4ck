package me.earth.earthhack.impl.util.animation;

import net.minecraft.util.math.MathHelper;

/**
 * Class representing an animation
 * @author megyn
 */
public class Animation {

    private double start;
    private double end;
    private double speed;
    private double current;
    private double last;

    private double progress = 0;

    private boolean backwards;
    private boolean reverseOnEnd;
    private boolean playing;

    private AnimationMode mode;

    public Animation(double start, double end, double speed, boolean backwards, AnimationMode mode) {
        this.start = start;
        this.end = end;
        this.speed = speed;
        this.backwards = backwards;
        current = start;
        last = start;
        this.mode = mode;
        playing = true;
    }

    public Animation(double start, double end, double speed, boolean backwards, boolean reverseOnEnd, AnimationMode mode) {
        this.start = start;
        this.end = end;
        this.speed = speed;
        this.backwards = backwards;
        this.reverseOnEnd = reverseOnEnd;
        current = start;
        last = start;
        this.mode = mode;
        playing = true;
    }

    public void add(float partialTicks) {
        if (playing) {
            last = current;
            if (mode == AnimationMode.LINEAR) {
                current += (backwards ? -1 : 1) * (speed);
            } else if (mode == AnimationMode.EXPONENTIAL) {
                for (int i = 0; i < (1 / partialTicks); i++) {
                    current += speed;
                    speed *= speed;
                    if (speed > 0 && backwards) {
                        speed *= -1;
                    }
                    /*if (current >= 0) {
                        current *= (backwards ? -1 : 1) * (speed);
                    } else if (current < 0) {
                        current = -current * (backwards ? -1 : 1) * (speed);
                    }*/
                }
            }
            current = MathHelper.clamp(current, start, end);
            if (current >= end) {
                if (reverseOnEnd) {
                    backwards = !backwards;
                } else {
                    playing = false;
                }
            }
        }
    }

    /*public void add() {
        if (playing) {
            last = current;
            if (mode == AnimationMode.LINEAR) {
                current += (backwards ? -1 : 1) * (speed);
            } else if (mode == AnimationMode.EXPONENTIAL) {
                current *= (backwards ? -1 : 1) * (speed);
            }
            current = MathHelper.clamp(current, start, end);
            if (current >= end) {
                if (reverseOnEnd) {
                    backwards = !backwards;
                } else {
                    playing = false;
                }
            }
        }
    }*/

    public double getStart() {
        return start;
    }

    public void setStart(double start) {
        this.start = start;
    }

    public double getEnd() {
        return end;
    }

    public void setEnd(double end) {
        this.end = end;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getCurrent() {
        return current;
    }

    public double getCurrent(float partialTicks) {
        return playing ? last + (current - last) * partialTicks : current;
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

    public void setBackwards(boolean backwards) {
        this.backwards = backwards;
    }

    public boolean isReverseOnEnd() {
        return reverseOnEnd;
    }

    public void setReverseOnEnd(boolean reverseOnEnd) {
        this.reverseOnEnd = reverseOnEnd;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }
}
