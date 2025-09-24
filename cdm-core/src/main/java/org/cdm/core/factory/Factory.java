package org.cdm.core.factory;

import org.cdm.core.action.Action;

/**
 * 基础工厂接口
 * @param <T> 参数类型
 * @param <R> 返回类型
 */
public interface Factory<T, R> extends Action<T, R> {
    R create(T param);
    
    @Override
    default R doAction(T param) {
        return create(param);
    }
}
