package org.cdm.core.decorator.impl;

import org.cdm.core.decorator.Decorator;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Function;

/**
 * 链式装饰器实现
 * <p>
 * 提供装饰器链的管理功能，支持动态添加多个装饰器并按顺序应用。
 * 该实现维护装饰器列表，允许在运行时构建复杂的装饰器链。
 * </p>
 *
 * @param <T> 组件类型，表示被装饰的目标对象类型
 * @author CDM
 * @since 1.0.0
 */
public class ChainDecorator<T> implements Decorator<T> {
    
    /** 被装饰的原始组件实例 */
    private final T component;
    
    /** 装饰器函数列表，按添加顺序存储 */
    private final List<Function<T, T>> decorators = new ArrayList<>();
    
    /**
     * 构造链式装饰器实例
     * <p>
     * 使用指定的组件创建链式装饰器，初始装饰器列表为空。
     * </p>
     *
     * @param component 要被装饰的原始组件，不能为null
     * @throws IllegalArgumentException 如果component为null
     */
    public ChainDecorator(T component) {
        if (component == null) {
            throw new IllegalArgumentException("Component cannot be null");
        }
        this.component = component;
    }
    
    /**
     * 添加装饰器到装饰器链
     * <p>
     * 将指定的装饰函数添加到装饰器链的末尾，支持链式调用。
     * 装饰器将按照添加的顺序在获取装饰组件时应用。
     * </p>
     *
     * @param decorator 装饰函数，用于对组件进行装饰操作
     * @return 当前链式装饰器实例，支持链式调用
     * @throws IllegalArgumentException 如果decorator为null
     */
    public ChainDecorator<T> addDecorator(Function<T, T> decorator) {
        if (decorator == null) {
            throw new IllegalArgumentException("Decorator cannot be null");
        }
        decorators.add(decorator);
        return this;
    }
    
    /**
     * 获取装饰后的组件
     * <p>
     * 按照装饰器列表的顺序依次应用所有装饰器到原始组件上，
     * 每个装饰器的输出作为下一个装饰器的输入，返回最终的装饰结果。
     * </p>
     *
     * @return 应用所有装饰器后的组件实例
     */
    public T getDecoratedComponent() {
        T result = component;
        for (Function<T, T> decorator : decorators) {
            result = decorator.apply(result);
        }
        return result;
    }
    
    /**
     * 获取被装饰的原始组件
     * <p>
     * 返回装饰器链包装的原始组件实例，不包含任何装饰逻辑。
     * </p>
     *
     * @return 被装饰的原始组件实例
     */
    @Override
    public T getComponent() {
        return component;
    }
    
    /**
     * 获取装饰器数量
     * <p>
     * 返回当前装饰器链中包含的装饰器函数数量。
     * </p>
     *
     * @return 装饰器函数的数量
     */
    public int getDecoratorCount() {
        return decorators.size();
    }
    
    /**
     * 获取装饰器列表的只读副本
     * <p>
     * 返回当前装饰器列表的不可变副本，防止外部代码修改内部状态。
     * </p>
     *
     * @return 装饰器函数列表的只读副本
     */
    public List<Function<T, T>> getDecorators() {
        return Collections.unmodifiableList(new ArrayList<>(decorators));
    }
}
