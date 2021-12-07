package me.earth.earthhack.impl.modules.movement.packetfly.util;

import net.minecraft.util.math.Vec3d;

import java.util.Random;

public enum Type
{
    Down()
        {
            @Override
            public Vec3d createOutOfBounds(Vec3d vec3d, int invalid)
            {
                return vec3d.add(0, -invalid, 0);
            }
        },
    Up()
        {
            @Override
            public Vec3d createOutOfBounds(Vec3d vec3d, int invalid)
            {
                return vec3d.add(0, invalid, 0);
            }
        },
    Preserve()
        {
            private final Random random = new Random();

            private int randomInt()
            {
                int result = random.nextInt(29000000);
                if (random.nextBoolean())
                {
                    return result;
                }

                return -result;
            }

            @Override
            public Vec3d createOutOfBounds(Vec3d vec3d, int invalid)
            {
                return vec3d.add(randomInt(), 0, randomInt());
            }
        },
    Switch()
        {
            private final Random random = new Random();

            @Override
            public Vec3d createOutOfBounds(Vec3d vec3d, int invalid)
            {
                boolean down = random.nextBoolean();
                return down ? vec3d.add(0, -invalid, 0)
                            : vec3d.add(0, invalid, 0);
            }
        },
    X()
        {
            @Override
            public Vec3d createOutOfBounds(Vec3d vec3d, int invalid)
            {
                return vec3d.add(invalid, 0, 0);
            }
        },
    Z()
    {
        @Override
        public Vec3d createOutOfBounds(Vec3d vec3d, int invalid)
        {
            return vec3d.add(0, 0, invalid);
        }
    },
    XZ()
    {
        @Override
        public Vec3d createOutOfBounds(Vec3d vec3d, int invalid)
        {
            return vec3d.add(invalid, 0, invalid);
        }
    };

    public abstract Vec3d createOutOfBounds(Vec3d vec3d, int invalid);

}
