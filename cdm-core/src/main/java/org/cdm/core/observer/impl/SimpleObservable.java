package org.cdm.core.observer.impl;

import org.cdm.core.observer.Event;
import org.cdm.core.observer.Observable;
import org.cdm.core.observer.Observer;

import java.util.Comparator;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.Set;

/**
 * 简单被观察者实现
 * @param <T> 事件类型
 */
public class SimpleObservable<T extends Event> implements Observable<T> {
    
    private final Set<Observer<T>> observers = new ConcurrentSkipListSet<>(
        (o1, o2) -> Integer.compare(o2.getPriority(), o1.getPriority())
    );
    
    @Override
    public void addObserver(Observer<T> observer) {
        observers.add(observer);
    }
    
    @Override
    public void removeObserver(Observer<T> observer) {
        observers.remove(observer);
    }
    
    @Override
    public void notifyObservers(T event) {
        // 按优先级排序处理观察者
        List<Observer<T>> syncObservers = new CopyOnWriteArrayList<>();
        List<Observer<T>> asyncObservers = new CopyOnWriteArrayList<>();
        
        for (Observer<T> observer : observers) {
            if (observer.isAsync()) {
                asyncObservers.add(observer);
            } else {
                syncObservers.add(observer);
            }
        }
        
        // 同步通知
        syncObservers.forEach(observer -> {
            try {
                observer.update(event);
            } catch (Exception e) {
                // 记录异常但不中断其他观察者的执行
                System.err.println("Observer update failed: " + e.getMessage());
            }
        });
        
        // 异步通知
        asyncObservers.forEach(observer -> {
            new Thread(() -> {
                try {
                    observer.update(event);
                } catch (Exception e) {
                    System.err.println("Async observer update failed: " + e.getMessage());
                }
            }).start();
        });
    }
    
    @Override
    public int getObserverCount() {
        return observers.size();
    }
}
