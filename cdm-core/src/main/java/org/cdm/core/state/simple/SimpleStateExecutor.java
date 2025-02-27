package org.cdm.core.state.simple;

/**
 * 简单状态执行
 * @author Rao
 * @Date 2022/10/24
 **/
public class SimpleStateExecutor<T,R> implements SimpleState<T,R> {

    private final SimpleState<T,R> simpleState;

    public SimpleStateExecutor(SimpleState<T, R> simpleState) {
        this.simpleState = simpleState;
    }

    @Override
    public R doAction(T param) {
        return simpleState.doAction(param);
    }

    @Override
    public void execute(T param) {
        simpleState.execute(param);
    }
}
