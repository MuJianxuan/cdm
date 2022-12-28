package org.cdm.core.strategy;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 定义池的时候需要带上 业务标识（Business identity）
 * @author Rao
 * @Date 2021-11-01
 **/
public class StrategyPool<T extends BaseStrategyKey> {
    private final Map<String, Strategy<T>> strategyMap;

    public StrategyPool(List<Strategy<T>> strategyList ) {
        this.strategyMap = strategyList.stream().filter(strategy -> Objects.nonNull(strategy.strategyKey())).collect(Collectors.toMap(strategy -> strategy.strategyKey().key(), Function.identity()));
    }

    /**
     * 执行业务
     * @param param
     */
    public void execute(T param){
        Strategy<T> strategy = Optional.ofNullable(strategyMap).orElse(new HashMap<>()).get(param.key());
        Optional.ofNullable(strategy).orElseThrow(() -> new RuntimeException("No strategy found!")).execute(param);
    }
}
