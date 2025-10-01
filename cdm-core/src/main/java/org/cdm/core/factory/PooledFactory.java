package org.cdm.core.factory;

/**
 * 对象池工厂
 * @param <T> 参数类型
 * @param <R> 返回类型
 */
public interface PooledFactory<T, R> extends Factory<T, R> {
    R borrowObject(T param);
    void returnObject(R object);
    void clearPool();
}
