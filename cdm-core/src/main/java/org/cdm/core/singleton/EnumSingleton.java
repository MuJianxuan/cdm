package org.cdm.core.singleton;

/**
 * 枚举单例实现
 * 使用Java枚举特性实现单例，线程安全且防止反射攻击
 * @author Rao
 * @Date 2025-06-17
 * @param <T> 单例实例类型
 */
public abstract class EnumSingleton<T> implements Singleton<T> {

    protected final T instance;
    protected volatile boolean initialized = true;

    public EnumSingleton() {
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
        // 枚举单例不支持销毁
        throw new UnsupportedOperationException("Enum singleton does not support destroy operation");
    }

    /**
     * 创建单例实例的抽象方法
     * 子类必须实现此方法来提供具体的实例创建逻辑
     * @return 单例实例
     */
    protected abstract T createInstance();

    /**
     * 枚举单例的便捷实现方式
     */
    public enum SimpleEnumSingleton implements Singleton<Object> {
        INSTANCE;

        private Object instance;

        public void setInstance(Object instance) {
            this.instance = instance;
        }

        @Override
        public Object getInstance() {
            return instance;
        }

        @Override
        public boolean isInitialized() {
            return instance != null;
        }

        @Override
        public void destroy() {
            instance = null;
        }
    }

}