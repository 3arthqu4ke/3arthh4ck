package me.earth.earthhack.impl.modules.combat.autocrystal.modes;

public enum ACRotate
{
    None
    {
        @Override
        public boolean noRotate(ACRotate rotate)
        {
            return true;
        }
    },
    Break
    {
        @Override
        public boolean noRotate(ACRotate rotate)
        {
            return rotate == Place || rotate == None;
        }
    },
    Place
    {
        @Override
        public boolean noRotate(ACRotate rotate)
        {
            return rotate == Break || rotate == None;
        }
    },
    All
    {
        @Override
        public boolean noRotate(ACRotate rotate)
        {
            return false;
        }
    };

    public abstract boolean noRotate(ACRotate rotate);

}
