package org.cdm.core.observer.impl;

import org.cdm.core.observer.Event;
import org.cdm.core.observer.EventBus;
import org.cdm.core.observer.Observer;

import java.util.Comparator;
import java.util.concurrent.*;
import java.util.List;

/**
 * 异步事件总线实现
 * @param <T> 事件类型
 */
public class AsyncEventBus<T extends Event> implements EventBus<T> {
    
    private final ExecutorService executorService;
    private final SimpleEventBus<T> eventBus;
    
    public AsyncEventBus() {
        this(Executors.newCachedThreadPool());
    }
    
    public AsyncEventBus(ExecutorService executorService) {
        this.executorService = executorService;
        this.eventBus = new SimpleEventBus<>();
    }
    
    @Override
    public void addObserver(Observer<T> observer) {
        eventBus.addObserver(observer);
    }
    
    @Override
    public void removeObserver(Observer<T> observer) {
        eventBus.removeObserver(observer);
    }
    
    @Override
    public void notifyObservers(T event) {
        eventBus.notifyObservers(event);
    }
    
    @Override
    public int getObserverCount() {
        return eventBus.getObserverCount();
    }
    
    @Override
    public void publish(T event) {
        eventBus.publish(event);
    }
    
    @Override
    public void publishAsync(T event) {
        executorService.submit(() -> eventBus.publish(event));
    }
    
    @Override
    public void register(Class<? extends T> eventType, Observer<T> observer) {
        eventBus.register(eventType, observer);
    }
    
    @Override
    public void unregister(Observer<T> observer) {
        eventBus.unregister(observer);
    }
    
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
