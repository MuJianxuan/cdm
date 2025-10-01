package org.cdm.core.factory.impl;

import org.cdm.core.factory.Factory;

import java.util.function.Function;

/**
 * 简单工厂实现
 * @param <T> 参数类型
 * @param <R> 返回类型
 */
public class SimpleFactory<T, R> implements Factory<T, R> {
    
    private final Function<T, R> creator;
    
    public SimpleFactory(Function<T, R> creator) {
        this.creator = creator;
    }
    
    public SimpleFactory(R instance) {
        this.creator = param -> instance;
    }
    
    @Override
    public R create(T param) {
        return creator.apply(param);
    }
}
