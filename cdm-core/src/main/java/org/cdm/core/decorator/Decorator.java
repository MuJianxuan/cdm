package org.cdm.core.decorator;

/**
 * 装饰器基础接口
 * <p>
 * 定义了装饰器模式的核心契约，提供获取被装饰组件和解包装饰器的功能。
 * 该接口支持装饰器链的遍历和类型安全的装饰器解包操作。
 * </p>
 *
 * @param <T> 组件类型，表示被装饰的目标对象类型
 * @author CDM
 * @since 1.0.0
 */
public interface Decorator<T> {
    
    /**
     * 获取被装饰的原始组件
     * <p>
     * 返回装饰器包装的原始组件实例。对于装饰器链，这通常返回链中的下一个组件。
     * </p>
     *
     * @return 被装饰的原始组件实例
     */
    T getComponent();
    
    /**
     * 解包指定类型的装饰器
     * <p>
     * 从装饰器链中查找并返回指定类型的装饰器实例。如果当前装饰器就是目标类型，则直接返回；
     * 否则递归地在被装饰的组件中查找（如果组件也是装饰器的话）。
     * </p>
     *
     * @param decoratorType 要解包的装饰器类型
     * @param <D> 装饰器类型参数
     * @return 指定类型的装饰器实例，如果未找到则返回null
     * @throws IllegalArgumentException 如果decoratorType为null
     */
    default <D extends Decorator<T>> D unwrap(Class<D> decoratorType) {
        if (decoratorType.isInstance(this)) {
            return decoratorType.cast(this);
        }
        if (getComponent() instanceof Decorator) {
            return ((Decorator<T>) getComponent()).unwrap(decoratorType);
        }
        return null;
    }
}
