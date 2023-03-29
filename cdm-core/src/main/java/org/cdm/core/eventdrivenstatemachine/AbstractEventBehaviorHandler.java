package org.cdm.core.eventdrivenstatemachine;

import java.util.function.Consumer;

/**
 * desc: 事件和行为绑定
 *
 * @author create 2022/7/16 by rao
 */
public abstract class AbstractEventBehaviorHandler<T extends StateMachineId> implements Consumer<StateContext<T>> {

    @Override
    public void accept(StateContext<T> stateContext) {
        this.triggerBehaviorHandle(stateContext);
    }

    /**
     * 触发行为处理
     * @param stateContext
     */
    protected abstract void triggerBehaviorHandle(StateContext<T> stateContext);

    /**
     * 触发事件
     * @return
     */
    public abstract DrivenEvent drivenEvent();

}
