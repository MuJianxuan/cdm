package org.cdm.core.eventdrivenstatemachine;

/**
 * 状态变换
 * desc: 状态助手
 *   读状态与写状态
 *
 *   1、行为管理 状态 触发什么事件  会改变状态 并触发行为 （消费数据）
 *   2、可配置化的 状态变换,  事件是个枚举  状态也是个枚举，
 * @author Rao
 * @Date 2022/06/22
 **/
public abstract class AbstractStateAssistant<T extends StateMachineId> {

    /**
     * 读状态
     * @return
     */
    protected abstract StateEnum read(StateContext<T> stateContext);

    /**
     * 写状态
     * @return
     */
    protected abstract Boolean write(StateContext<T> stateContext);

}
