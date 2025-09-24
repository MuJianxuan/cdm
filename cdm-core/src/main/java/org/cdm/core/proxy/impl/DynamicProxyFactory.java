package org.cdm.core.proxy.impl;

import org.cdm.core.proxy.ProxyFactory;
import org.cdm.core.proxy.ProxyInterceptor;
import org.cdm.core.proxy.ProxyInvocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * 动态代理工厂实现
 * @param <T> 目标类型
 */
public class DynamicProxyFactory<T> implements ProxyFactory<T> {
    
    @Override
    public T createProxy(T target, List<ProxyInterceptor<T>> interceptors) {
        if (target == null) {
            throw new IllegalArgumentException("Target cannot be null");
        }
        
        Class<?> targetClass = target.getClass();
        ClassLoader classLoader = targetClass.getClassLoader();
        Class<?>[] interfaces = targetClass.getInterfaces();
        
        if (interfaces.length == 0) {
            // 如果目标类没有实现接口，尝试获取其父类的接口
            Class<?> superClass = targetClass.getSuperclass();
            if (superClass != null && superClass != Object.class) {
                interfaces = superClass.getInterfaces();
            }
        }
        
        if (interfaces.length == 0) {
            throw new IllegalArgumentException("Target class must implement at least one interface");
        }
        
        // 按顺序排序拦截器
        List<ProxyInterceptor<T>> sortedInterceptors = new ArrayList<>(interceptors);
        Collections.sort(sortedInterceptors, Comparator.comparingInt(ProxyInterceptor::getOrder));
        
        InvocationHandler handler = new ProxyInvocationHandler<>(target, sortedInterceptors);
        return (T) Proxy.newProxyInstance(classLoader, interfaces, handler);
    }
    
    /**
     * 代理调用处理器
     */
    private static class ProxyInvocationHandler<T> implements InvocationHandler {
        private final T target;
        private final List<ProxyInterceptor<T>> interceptors;
        private int currentInterceptorIndex = 0;
        
        public ProxyInvocationHandler(T target, List<ProxyInterceptor<T>> interceptors) {
            this.target = target;
            this.interceptors = interceptors;
        }
        
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 创建代理调用对象
            ProxyInvocation invocation = new ProxyInvocationImpl(proxy, method, args, target, this);
            
            // 如果有拦截器，调用第一个拦截器
            if (!interceptors.isEmpty() && currentInterceptorIndex < interceptors.size()) {
                ProxyInterceptor<T> interceptor = interceptors.get(currentInterceptorIndex);
                currentInterceptorIndex++;
                return interceptor.intercept(target, method.getName(), args, invocation);
            }
            
            // 没有更多拦截器，直接调用目标方法
            return method.invoke(target, args);
        }
        
        public Object proceed() throws Throwable {
            Method method = ((ProxyInvocationImpl) getCurrentInvocation()).getMethod();
            Object[] args = ((ProxyInvocationImpl) getCurrentInvocation()).getArguments();
            return method.invoke(target, args);
        }
        
        private ProxyInvocation getCurrentInvocation() {
            // 这里简化处理，实际应用中可能需要更复杂的上下文管理
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            // 实际实现中需要更好的方式来获取当前调用上下文
            return null;
        }
    }
    
    /**
     * 代理调用实现
     */
    private static class ProxyInvocationImpl implements ProxyInvocation {
        private final Object proxy;
        private final Method method;
        private final Object[] args;
        private final Object target;
        private final ProxyInvocationHandler<?> handler;
        
        public ProxyInvocationImpl(Object proxy, Method method, Object[] args, Object target, ProxyInvocationHandler<?> handler) {
            this.proxy = proxy;
            this.method = method;
            this.args = args;
            this.target = target;
            this.handler = handler;
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
        
        public Method getMethod() {
            return method;
        }
    }
}
