package org.cdm.core.singleton;

/**
 * 抽象单例基类
 * 提供单例模式的通用实现逻辑
 * @author Rao
 * @Date 2025-06-17
 * @param <T> 单例实例类型
 */
public abstract class AbstractSingleton<T> implements Singleton<T> {

    protected volatile T instance;
    protected volatile boolean initialized = false;

    @Override
    public T getInstance() {
        if (!initialized) {
            synchronized (this) {
                if (!initialized) {
                    instance = createInstance();
                    initialized = true;
                }
            }
        }
        return instance;
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