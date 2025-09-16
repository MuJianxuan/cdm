package org.cdm.core.singleton;

/**
 * 饿汉式单例实现
 * 在类加载时就创建实例，线程安全但可能浪费资源
 * @author Rao
 * @Date 2025-06-17
 * @param <T> 单例实例类型
 */
public abstract class EagerSingleton<T> implements Singleton<T> {

    protected final T instance;
    protected volatile boolean initialized = true;

    public EagerSingleton() {
        this.instance = createInstance();
    }

    @Override
    public T getInstance() {
        return instance;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void destroy() {
        // 饿汉式单例通常不支持销毁
        throw new UnsupportedOperationException("Eager singleton does not support destroy operation");
    }

    /**
     * 创建单例实例的抽象方法
     * 子类必须实现此方法来提供具体的实例创建逻辑
     * @return 单例实例
     */
    protected abstract T createInstance();

}