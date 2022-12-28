package org.cdm.core.eventdrivenstatemachine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * desc: 状态上下文
 *
 * @author create 2022/7/16 by rao
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StateContext<T extends StateMachineId> implements Serializable {

    private static final long serialVersionUID = -1546567128073146592L;
    /**
     * 机子 ID
     */
    private StateMachineId stateMachineId;

    /**
     * 当前状态
     */
    private StateEnum currentState;

    /**
     * 触发事件
     */
    private DrivenEvent event;

    /**
     * 终态
     */
    private StateEnum finalState;

    /**
     * 业务源
     */
    private T source;

    public StateContext(T source){
        this.source = source;
    }

}
