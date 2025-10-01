package org.cdm.core.proxy.impl;

import org.cdm.core.proxy.Proxy;
import org.cdm.core.proxy.ProxyInterceptor;
import org.cdm.core.proxy.ProxyInvocation;

import java.lang.reflect.Method;
import java.util.List;
import java.util.ArrayList;

/**
 * 简单代理实现
 * @param <T> 目标类型
 */
public class SimpleProxy<T> implements Proxy<T> {
    
    private final T target;
    private final List<ProxyInterceptor<T>> interceptors;
    
    public SimpleProxy(T target) {
        this(target, new ArrayList<>());
    }
    
    public SimpleProxy(T target, List<ProxyInterceptor<T>> interceptors) {
        this.target = target;
        this.interceptors = new ArrayList<>(interceptors);
    }
    
    @Override
    public T getTarget() {
        return target;
    }
    
    @Override
    public T doAction(T input) {
        // 简单代理直接返回目标对象
        return target;
    }
    
    public T invoke(String methodName, Object[] args) throws Throwable {
        Method method = findMethod(methodName, args);
        if (method == null) {
            throw new NoSuchMethodException("Method not found: " + methodName);
        }
        
        // 创建代理调用对象
        ProxyInvocation invocation = new SimpleProxyInvocation(method, args, target);
        
        // 依次调用拦截器
        Object result = target;
        for (ProxyInterceptor<T> interceptor : interceptors) {
            result = interceptor.intercept(target, methodName, args, invocation);
        }
        
        // 调用目标方法
        return (T) method.invoke(target, args);
    }
    
    private Method findMethod(String methodName, Object[] args) {
        Class<?> targetClass = target.getClass();
        Class<?>[] paramTypes = args != null ? new Class[args.length] : new Class[0];
        
        for (int i = 0; i < paramTypes.length; i++) {
            if (args[i] != null) {
                paramTypes[i] = args[i].getClass();
            }
        }
        
        try {
            return targetClass.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
            // 尝试查找匹配的方法
            for (Method method : targetClass.getMethods()) {
                if (method.getName().equals(methodName) && isCompatible(method.getParameterTypes(), paramTypes)) {
                    return method;
                }
            }
            return null;
        }
    }
    
    private boolean isCompatible(Class<?>[] methodParams, Class<?>[] callParams) {
        if (methodParams.length != callParams.length) {
            return false;
        }
        
        for (int i = 0; i < methodParams.length; i++) {
            if (callParams[i] != null && !methodParams[i].isAssignableFrom(callParams[i])) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 简单代理调用实现
     */
    private static class SimpleProxyInvocation implements ProxyInvocation {
        private final Method method;
        private final Object[] args;
        private final Object target;
        
        public SimpleProxyInvocation(Method method, Object[] args, Object target) {
            this.method = method;
            this.args = args != null ? args.clone() : new Object[0];
            this.target = target;
        }
        
        @Override
        public Object proceed() throws Throwable {
            return method.invoke(target, args);
        }
        
        @Override
        public String getMethodName() {
            return method.getName();
        }
        
        @Override
        public Object[] getArguments() {
            return args != null ? args.clone() : new Object[0];
        }
        
        @Override
        public Object getTarget() {
            return target;
        }
    }
}
