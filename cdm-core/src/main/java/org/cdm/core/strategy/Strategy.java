package org.cdm.core.strategy;

/**
 * 策略池
 * @author Rao
 * @Date 2021-11-01
 **/
public interface Strategy<T extends StrategyKey> {

    /**
     * 执行
     * @param param
     * @return
     */
    void execute(T param);

    /**
     * 策略 key
     * @return
     */
    StrategyKey strategyKey();

}
