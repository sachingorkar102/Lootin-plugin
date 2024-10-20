package com.github.sachin.lootin.utils.config;

public class WorldConfig {

    protected String worldName;
    protected boolean shouldAutoReplenish;
    protected boolean shouldRefillCustomChests;
    protected int maxRefills;
    protected long refillTime;
    protected boolean resetSeedOnFill;

    public WorldConfig(String worldName, boolean shouldAutoReplenish, boolean shouldRefillCustomChests, int maxRefills, long refillTime, boolean resetSeedOnFill) {
        this.worldName = worldName;
        this.shouldAutoReplenish = shouldAutoReplenish;
        this.shouldRefillCustomChests = shouldRefillCustomChests;
        this.maxRefills = maxRefills;
        this.refillTime = refillTime;
        this.resetSeedOnFill = resetSeedOnFill;
    }
}
