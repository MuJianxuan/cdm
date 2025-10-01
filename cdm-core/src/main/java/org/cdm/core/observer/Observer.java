package org.cdm.core.observer;

/**
 * 观察者接口
 * @param <T> 事件类型
 */
public interface Observer<T extends Event> {
    void update(T event);
    
    default boolean isAsync() {
        return false;
    }
    
    default int getPriority() {
        return 0;
    }
}
