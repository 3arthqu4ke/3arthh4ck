package me.earth.earthhack.impl.modules.combat.bowspam;

import me.earth.earthhack.api.module.data.DefaultData;

final class BowSpamData extends DefaultData<BowSpam>
{
    public BowSpamData(BowSpam module)
    {
        super(module);
        register(module.delay,
                "The delay in ticks (~50ms) between each arrow. While low " +
                "delays feel impressive 10 ticks should already max out" +
                " your damage.");
        register(module.tpsSync,
                "If the delay should be synced with the servers TPS.");
        register(module.bowBomb, "Use this while Flying above the enemy." +
                " Will make your arrows deal more damage.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Spam arrows quickly.";
    }

}
