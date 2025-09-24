package org.cdm.core.factory.impl;

import org.cdm.core.factory.PooledFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;

/**
 * 对象池工厂实现
 * @param <T> 参数类型
 * @param <R> 返回类型
 */
public class PooledFactoryImpl<T, R> implements PooledFactory<T, R> {
    
    private final Function<T, R> creator;
    private final Queue<R> objectPool = new ConcurrentLinkedQueue<>();
    private final int maxPoolSize;
    
    public PooledFactoryImpl(Function<T, R> creator) {
        this(creator, Integer.MAX_VALUE);
    }
    
    public PooledFactoryImpl(Function<T, R> creator, int maxPoolSize) {
        this.creator = creator;
        this.maxPoolSize = maxPoolSize;
    }
    
    @Override
    public R create(T param) {
        return borrowObject(param);
    }
    
    @Override
    public R borrowObject(T param) {
        R object = objectPool.poll();
        if (object == null) {
            return creator.apply(param);
        }
        return object;
    }
    
    @Override
    public void returnObject(R object) {
        if (objectPool.size() < maxPoolSize) {
            objectPool.offer(object);
        }
    }
    
    @Override
    public void clearPool() {
        objectPool.clear();
    }
    
    public int getPoolSize() {
        return objectPool.size();
    }
}
