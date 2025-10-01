package org.cdm.core.factory;

/**
 * 抽象工厂接口
 * @param <T> 工厂键类型
 * @param <R> 返回类型
 */
public interface AbstractFactory<T extends FactoryKey, R> {
    Factory<T, R> getFactory(T key);
    
    R create(T key);
    
    <P> R create(T key, P param);
}
