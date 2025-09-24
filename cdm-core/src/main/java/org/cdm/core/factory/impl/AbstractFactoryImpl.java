package org.cdm.core.factory.impl;

import org.cdm.core.factory.AbstractFactory;
import org.cdm.core.factory.Factory;
import org.cdm.core.factory.FactoryKey;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Collections;

/**
 * 抽象工厂实现
 * @param <T> 工厂键类型
 * @param <R> 返回类型
 */
public class AbstractFactoryImpl<T extends FactoryKey, R> implements AbstractFactory<T, R> {
    
    private final Map<String, Factory<T, R>> factoryMap = new ConcurrentHashMap<>();
    
    /**
     * 注册工厂
     * @param key 工厂键
     * @param factory 工厂实例
     * @throws IllegalArgumentException 如果key或factory为null
     */
    public void registerFactory(String key, Factory<T, R> factory) {
        if (key == null || factory == null) {
            throw new IllegalArgumentException("Key and factory cannot be null");
        }
        factoryMap.put(key, factory);
    }
    
    /**
     * 注销工厂
     * @param key 工厂键
     * @return 被移除的工厂实例，如果不存在则返回null
     */
    public Factory<T, R> unregisterFactory(String key) {
        return factoryMap.remove(key);
    }
    
    /**
     * 获取工厂
     * @param key 工厂键
     * @return 工厂实例，如果不存在则返回null
     */
    @Override
    public Factory<T, R> getFactory(T key) {
        if (key == null) {
            return null;
        }
        return factoryMap.get(key.key());
    }
    
    /**
     * 通过键创建对象
     * @param key 工厂键
     * @return 创建的对象
     * @throws IllegalArgumentException 如果找不到对应的工厂
     */
    @Override
    public R create(T key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        Factory<T, R> factory = getFactory(key);
        if (factory == null) {
            throw new IllegalArgumentException("No factory found for key: " + key.key());
        }
        return factory.create(key);
    }
    
    /**
     * 通过键和参数创建对象
     * 这个方法用于支持测试中的使用场景
     * @param key 工厂键
     * @param param 创建参数
     * @return 创建的对象
     * @throws IllegalArgumentException 如果找不到对应的工厂
     * @param <P> 参数类型
     */
    @Override
    public <P> R create(T key, P param) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        String factoryKey = key.key();
        Factory<T, R> factory = factoryMap.get(factoryKey);
        if (factory == null) {
            throw new IllegalArgumentException("No factory found for key: " + factoryKey);
        }
        
        // 使用工厂创建对象，忽略参数或将其作为创建逻辑的一部分
        // 这里我们使用key作为参数，因为工厂期望的是T类型而不是P类型
        return factory.create(key);
    }
    
    /**
     * 检查是否包含指定键的工厂
     * @param key 工厂键
     * @return 如果包含返回true，否则返回false
     */
    public boolean containsFactory(T key) {
        if (key == null) {
            return false;
        }
        return factoryMap.containsKey(key.key());
    }
    
    /**
     * 获取已注册工厂的数量
     * @return 工厂数量
     */
    public int size() {
        return factoryMap.size();
    }
    
    /**
     * 获取所有已注册的工厂键
     * @return 工厂键集合的不可变视图
     */
    public Set<String> getRegisteredKeys() {
        return Collections.unmodifiableSet(factoryMap.keySet());
    }
    
    /**
     * 清空所有已注册的工厂
     */
    public void clear() {
        factoryMap.clear();
    }
    
    /**
     * 检查工厂映射是否为空
     * @return 如果为空返回true，否则返回false
     */
    public boolean isEmpty() {
        return factoryMap.isEmpty();
    }
}
