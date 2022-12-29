package org.cdm.core.strategy;

import org.cdm.core.action.Action;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 定义池的时候需要带上 业务标识（Business identity）
 * @author Rao
 * @Date 2021-11-01
 **/
public class StrategyPool<T extends BaseStrategyKey,R> implements Action<T,R> {
    private final Map<String, Strategy<T,R>> strategyMap;

    public StrategyPool(List<Strategy<T,R>> strategyList ) {
        this.strategyMap = strategyList.stream().filter(strategy -> Objects.nonNull(strategy.strategyKey())).collect(Collectors.toMap(strategy -> strategy.strategyKey().key(), Function.identity()));
    }


    @Override
    public R doAction(T param) {
        Strategy<T,R> strategy = Optional.ofNullable(strategyMap).orElse(new HashMap<>(2)).get(param.key());
        return Optional.ofNullable(strategy).orElseThrow(() -> new RuntimeException("No strategy found!")).doAction(param);
    }

}
