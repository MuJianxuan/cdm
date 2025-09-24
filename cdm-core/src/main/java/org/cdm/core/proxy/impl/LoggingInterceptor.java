package org.cdm.core.proxy.impl;

import org.cdm.core.proxy.ProxyInterceptor;
import org.cdm.core.proxy.ProxyInvocation;

/**
 * 日志拦截器实现
 * @param <T> 目标类型
 */
public class LoggingInterceptor<T> implements ProxyInterceptor<T> {
    
    private final String loggerName;
    
    public LoggingInterceptor() {
        this("Proxy");
    }
    
    public LoggingInterceptor(String loggerName) {
        this.loggerName = loggerName;
    }
    
    @Override
    public Object intercept(T target, String methodName, Object[] args, ProxyInvocation invocation) throws Throwable {
        // 记录方法调用开始
        logInfo("Method invocation started: " + methodName + "(" + formatArgs(args) + ")");
        
        long startTime = System.currentTimeMillis();
        try {
            // 继续执行调用链
            Object result = invocation.proceed();
            
            // 记录方法调用成功
            long endTime = System.currentTimeMillis();
            logInfo("Method invocation completed: " + methodName + " (took " + (endTime - startTime) + "ms)");
            
            return result;
        } catch (Exception e) {
            // 记录方法调用异常
            long endTime = System.currentTimeMillis();
            logError("Method invocation failed: " + methodName + " (took " + (endTime - startTime) + "ms) - " + e.getMessage());
            throw e;
        }
    }
    
    @Override
    public int getOrder() {
        return 1; // 较低优先级
    }
    
    private String formatArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(args[i]);
        }
        return sb.toString();
    }
    
    private void logInfo(String message) {
        System.out.println("[" + loggerName + " INFO] " + message);
    }
    
    private void logError(String message) {
        System.err.println("[" + loggerName + " ERROR] " + message);
    }
}
