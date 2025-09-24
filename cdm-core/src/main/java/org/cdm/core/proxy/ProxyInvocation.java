package org.cdm.core.proxy;

/**
 * 代理调用
 */
public interface ProxyInvocation {
    Object proceed() throws Throwable;
    String getMethodName();
    Object[] getArguments();
    Object getTarget();
}
