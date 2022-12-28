package org.cdm.core.eventdrivenstatemachine;

/**
 * desc: 事件枚举
 *
 * @author Rao
 * @Date 2022/06/27
 **/
public interface DrivenEvent {

    /**
     * 事件值
     * @return
     */
    String event();

    /**
     * 状态
     * @return
     */
    StateEnum initialState();

    /**
     * 终态
     * @return
     */
    StateEnum finalState();

}
