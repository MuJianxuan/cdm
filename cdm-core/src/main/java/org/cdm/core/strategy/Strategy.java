package org.cdm.core.strategy;

import org.cdm.core.action.Action;

/**
 * 策略池
 * @author Rao
 * @Date 2021-11-01
 **/
public interface Strategy<T extends StrategyKey,R> extends Action<T,R> {

    /**
     * 策略 key
     * @return
     */
    StrategyKey strategyKey();

}
