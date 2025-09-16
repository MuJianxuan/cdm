package org.cdm.core.singleton;

/**
 * 双重检查锁单例实现
 * 提供线程安全的懒加载单例模式，性能较好
 * @author Rao
 * @Date 2025-06-17
 * @param <T> 单例实例类型
 */
public abstract class DoubleCheckedSingleton<T> implements Singleton<T> {

    private volatile T instance;
    private volatile boolean initialized = false;

    @Override
    public T getInstance() {
        T result = instance;
        if (result == null) {
            synchronized (this) {
                result = instance;
                if (result == null) {
                    instance = result = createInstance();
                    initialized = true;
                }
            }
        }
        return result;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void destroy() {
        synchronized (this) {
            if (initialized) {
                instance = null;
                initialized = false;
            }
        }
    }

    /**
     * 创建单例实例的抽象方法
     * 子类必须实现此方法来提供具体的实例创建逻辑
     * @return 单例实例
     */
    protected abstract T createInstance();

}