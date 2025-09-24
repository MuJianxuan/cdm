package org.cdm.core.decorator.impl;

import org.cdm.core.decorator.Decorator;

import java.util.function.Function;

/**
 * 简单装饰器实现
 * <p>
 * 提供基础的装饰器功能实现，包装单个组件和单个装饰函数。
 * 该实现适用于只需要应用单个装饰逻辑的场景，是装饰器模式的最简实现。
 * </p>
 *
 * @param <T> 组件类型，表示被装饰的目标对象类型
 * @author CDM
 * @since 1.0.0
 */
public class SimpleDecorator<T> implements Decorator<T> {
    
    /** 被装饰的原始组件实例 */
    private final T component;
    
    /** 用于装饰组件的函数 */
    private final Function<T, T> decoratorFunction;
    
    /**
     * 构造简单装饰器实例
     * <p>
     * 使用指定的组件和装饰函数创建装饰器实例。
     * </p>
     *
     * @param component 要被装饰的原始组件，不能为null
     * @param decoratorFunction 装饰函数，用于对组件进行装饰操作，不能为null
     * @throws IllegalArgumentException 如果component或decoratorFunction为null
     */
    public SimpleDecorator(T component, Function<T, T> decoratorFunction) {
        if (component == null) {
            throw new IllegalArgumentException("Component cannot be null");
        }
        if (decoratorFunction == null) {
            throw new IllegalArgumentException("Decorator function cannot be null");
        }
        this.component = component;
        this.decoratorFunction = decoratorFunction;
    }
    
    /**
     * 获取被装饰的原始组件
     * <p>
     * 返回装饰器包装的原始组件实例，不包含任何装饰逻辑。
     * </p>
     *
     * @return 被装饰的原始组件实例
     */
    @Override
    public T getComponent() {
        return component;
    }
    
    /**
     * 获取装饰后的组件
     * <p>
     * 应用装饰函数到原始组件上，返回装饰后的组件实例。
     * 每次调用都会重新应用装饰逻辑，确保返回最新的装饰结果。
     * </p>
     *
     * @return 应用装饰函数后的组件实例
     */
    public T getDecoratedComponent() {
        return decoratorFunction.apply(component);
    }
}
