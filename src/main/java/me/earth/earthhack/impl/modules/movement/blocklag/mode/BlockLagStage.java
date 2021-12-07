package me.earth.earthhack.impl.modules.movement.blocklag.mode;

import me.earth.earthhack.api.event.events.Stage;

public enum BlockLagStage
{
    Pre
    {
        @Override
        public boolean shouldBlockLag(Stage stage)
        {
            return stage == Stage.PRE;
        }
    },
    Post
    {
        @Override
        public boolean shouldBlockLag(Stage stage)
        {
            return stage == Stage.POST;
        }
    },
    All
    {
        @Override
        public boolean shouldBlockLag(Stage stage)
        {
            return true;
        }
    };

    public abstract boolean shouldBlockLag(Stage stage);
}
