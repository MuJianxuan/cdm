package org.cdm.core.proxy;

import org.cdm.core.proxy.impl.DynamicProxyFactory;

import java.util.List;

/**
 * 动态代理工厂
 * @param <T> 目标类型
 */
public interface ProxyFactory<T> {
    T createProxy(T target, List<ProxyInterceptor<T>> interceptors);
    
    static <T> ProxyFactory<T> createDefault() {
        return new DynamicProxyFactory<>();
    }
}
