package org.cdm.core.proxy;

import org.cdm.core.action.Action;

/**
 * 代理接口
 * @param <T> 目标类型
 */
public interface Proxy<T> extends Action<T, T> {
    T getTarget();
    
    default boolean isProxyFor(Class<?> type) {
        return type.isInstance(getTarget());
    }
}
