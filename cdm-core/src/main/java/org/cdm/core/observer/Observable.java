package org.cdm.core.observer;

/**
 * 被观察者接口
 * @param <T> 事件类型
 */
public interface Observable<T extends Event> {
    void addObserver(Observer<T> observer);
    void removeObserver(Observer<T> observer);
    void notifyObservers(T event);
    int getObserverCount();
}
