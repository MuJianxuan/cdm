package org.cdm.core.strategy;

/**
 * @author create 2022/7/28 by rao
 */
public abstract class BaseStrategyKey implements StrategyKey {

    private final String strategyKey;

    public BaseStrategyKey(String strategyKey) {
        this.strategyKey = strategyKey;
    }

    @Override
    public String key() {
        return this.strategyKey;
    }
}
