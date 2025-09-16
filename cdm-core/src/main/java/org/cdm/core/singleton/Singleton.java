package org.cdm.core.singleton;

/**
 * 单例模式接口
 * 定义单例的基本契约
 * @author Rao
 * @Date 2025-06-17
 * @param <T> 单例实例类型
 */
public interface Singleton<T> {

    /**
     * 获取单例实例
     * @return 单例实例
     */
    T getInstance();

    /**
     * 检查单例实例是否已初始化
     * @return 是否已初始化
     */
    boolean isInitialized();

    /**
     * 销毁单例实例（如果支持）
     */
    default void destroy() {
        // 默认实现为空，子类可选择覆盖
    }

}