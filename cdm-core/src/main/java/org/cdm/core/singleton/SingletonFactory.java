package org.cdm.core.singleton;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * 单例工厂类
 * 提供单例实例的创建和管理功能
 * @author Rao
 * @Date 2025-06-17
 */
public final class SingletonFactory {

    private static final Map<String, Singleton<?>> singletonRegistry = new HashMap<>();

    private SingletonFactory() {
        // 私有构造函数防止实例化
    }

    /**
     * 注册单例实例
     * @param name 单例名称
     * @param singleton 单例实例
     * @param <T> 单例类型
     */
    public static <T> void register(String name, Singleton<T> singleton) {
        Objects.requireNonNull(name, "Singleton name cannot be null");
        Objects.requireNonNull(singleton, "Singleton instance cannot be null");
        singletonRegistry.put(name, singleton);
    }

    /**
     * 获取单例实例
     * @param name 单例名称
     * @param <T> 单例类型
     * @return 单例实例
     */
    @SuppressWarnings("unchecked")
    public static <T> T getInstance(String name) {
        Singleton<?> singleton = singletonRegistry.get(name);
        if (singleton == null) {
            throw new IllegalArgumentException("Singleton not found: " + name);
        }
        return (T) singleton.getInstance();
    }

    /**
     * 检查单例是否存在
     * @param name 单例名称
     * @return 是否存在
     */
    public static boolean contains(String name) {
        return singletonRegistry.containsKey(name);
    }

    /**
     * 移除单例
     * @param name 单例名称
     */
    public static void remove(String name) {
        Singleton<?> singleton = singletonRegistry.remove(name);
        if (singleton != null) {
            singleton.destroy();
        }
    }

    /**
     * 清空所有单例
     */
    public static void clear() {
        singletonRegistry.values().forEach(Singleton::destroy);
        singletonRegistry.clear();
    }

    /**
     * 创建饿汉式单例
     * @param supplier 实例提供者
     * @param <T> 单例类型
     * @return 饿汉式单例
     */
    public static <T> Singleton<T> createEagerSingleton(Supplier<T> supplier) {
        Objects.requireNonNull(supplier, "Supplier cannot be null");
        return new EagerSingleton<T>() {
            @Override
            protected T createInstance() {
                return supplier.get();
            }
        };
    }

    /**
     * 创建双重检查锁单例
     * @param supplier 实例提供者
     * @param <T> 单例类型
     * @return 双重检查锁单例
     */
    public static <T> Singleton<T> createDoubleCheckedSingleton(Supplier<T> supplier) {
        Objects.requireNonNull(supplier, "Supplier cannot be null");
        return new DoubleCheckedSingleton<T>() {
            @Override
            protected T createInstance() {
                return supplier.get();
            }
        };
    }

    /**
     * 创建懒汉式单例
     * @param supplier 实例提供者
     * @param <T> 单例类型
     * @return 懒汉式单例
     */
    public static <T> Singleton<T> createLazySingleton(Supplier<T> supplier) {
        Objects.requireNonNull(supplier, "Supplier cannot be null");
        return new AbstractSingleton<T>() {
            @Override
            protected T createInstance() {
                return supplier.get();
            }
        };
    }

}