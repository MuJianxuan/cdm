package org.cdm.core.observer.impl;

import org.cdm.core.observer.Event;
import org.cdm.core.observer.EventBus;
import org.cdm.core.observer.Observer;

import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.Map;

/**
 * 简单事件总线实现
 * @param <T> 事件类型
 */
public class SimpleEventBus<T extends Event> implements EventBus<T> {
    
    private final ConcurrentMap<Class<? extends T>, List<Observer<T>>> eventObservers = new ConcurrentHashMap<>();
    private final List<Observer<T>> globalObservers = new CopyOnWriteArrayList<>();
    
    @Override
    public void addObserver(Observer<T> observer) {
        globalObservers.add(observer);
    }
    
    @Override
    public void removeObserver(Observer<T> observer) {
        globalObservers.remove(observer);
        // 从所有事件类型中移除观察者
        eventObservers.values().forEach(observers -> observers.remove(observer));
    }
    
    @Override
    public void notifyObservers(T event) {
        // 通知全局观察者
        notifyObserverList(globalObservers, event);
        
        // 通知特定事件类型的观察者
        List<Observer<T>> specificObservers = eventObservers.get(event.getClass());
        if (specificObservers != null) {
            notifyObserverList(specificObservers, event);
        }
    }
    
    @Override
    public int getObserverCount() {
        int count = globalObservers.size();
        for (List<Observer<T>> observers : eventObservers.values()) {
            count += observers.size();
        }
        return count;
    }
    
    @Override
    public void publish(T event) {
        notifyObservers(event);
    }
    
    @Override
    public void publishAsync(T event) {
        new Thread(() -> publish(event)).start();
    }
    
    @Override
    public void register(Class<? extends T> eventType, Observer<T> observer) {
        eventObservers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(observer);
    }
    
    @Override
    public void unregister(Observer<T> observer) {
        removeObserver(observer);
    }
    
    private void notifyObserverList(List<Observer<T>> observers, T event) {
        // 按优先级排序
        observers.sort((o1, o2) -> Integer.compare(o2.getPriority(), o1.getPriority()));
        
        // 分离同步和异步观察者
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
}
