package com.github.sachin.lootin.utils.config;

import java.util.List;

public class WorldConfig {

    protected String worldName;
    protected boolean shouldAutoReplenish;
    protected boolean shouldRefillCustomChests;
    protected int maxRefills;
    protected long refillTime;
    protected boolean resetSeedOnFill;
    protected List<String> blacklistStructures;

    public WorldConfig(String worldName, boolean shouldAutoReplenish, boolean shouldRefillCustomChests, int maxRefills, long refillTime, boolean resetSeedOnFill,List<String> blacklistStructures) {
        this.worldName = worldName;
        this.shouldAutoReplenish = shouldAutoReplenish;
        this.shouldRefillCustomChests = shouldRefillCustomChests;
        this.maxRefills = maxRefills;
        this.refillTime = refillTime;
        this.resetSeedOnFill = resetSeedOnFill;
        this.blacklistStructures = blacklistStructures;
    }
}
