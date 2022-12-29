package org.cdm.core.state;

/**
 * 状态执行者
 * @author Rao
 * @Date 2022/10/24
 **/
public class StatePerformer<T,R> implements State<T,R> {

    private final State<T,R> state;

    public StatePerformer(State<T, R> state) {
        this.state = state;
    }

    @Override
    public R doAction(T param) {
        return state.doAction(param);
    }

    @Override
    public void execute(T param) {
        state.execute(param);
    }
}
