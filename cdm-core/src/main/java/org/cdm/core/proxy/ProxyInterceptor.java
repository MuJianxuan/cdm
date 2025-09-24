package org.cdm.core.proxy;

/**
 * 代理拦截器
 * @param <T> 目标类型
 */
public interface ProxyInterceptor<T> {
    Object intercept(T target, String methodName, Object[] args, ProxyInvocation invocation) throws Throwable;
    
    default int getOrder() {
        return 0;
    }
}
