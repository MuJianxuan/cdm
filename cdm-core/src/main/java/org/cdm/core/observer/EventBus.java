package org.cdm.core.observer;

/**
 * 异步事件总线
 * @param <T> 事件类型
 */
public interface EventBus<T extends Event> extends Observable<T> {
    void publish(T event);
    void publishAsync(T event);
    void register(Class<? extends T> eventType, Observer<T> observer);
    void unregister(Observer<T> observer);
}
