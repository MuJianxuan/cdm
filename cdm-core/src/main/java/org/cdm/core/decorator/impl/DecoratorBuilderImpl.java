package org.cdm.core.decorator.impl;

import org.cdm.core.decorator.DecoratorBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * 装饰器构建器实现
 * <p>
 * 提供装饰器的构建功能，支持链式添加装饰器并在最后一次性构建装饰后的组件。
 * 该实现维护组件实例和装饰器列表，在构建时按顺序应用所有装饰器。
 * </p>
 *
 * @param <T> 组件类型，表示要被装饰的目标对象类型
 * @author CDM
 * @since 1.0.0
 */
public class DecoratorBuilderImpl<T> implements DecoratorBuilder<T> {
    
    /** 要被装饰的原始组件实例 */
    private T component;
    
    /** 装饰器函数列表，按添加顺序存储 */
    private final List<Function<T, T>> decorators = new ArrayList<>();
    
    /**
     * 构造装饰器构建器实例
     * <p>
     * 使用指定的组件初始化构建器，初始装饰器列表为空。
     * </p>
     *
     * @param component 要被装饰的原始组件，不能为null
     * @throws IllegalArgumentException 如果component为null
     */
    public DecoratorBuilderImpl(T component) {
        if (component == null) {
            throw new IllegalArgumentException("Component cannot be null");
        }
        this.component = component;
    }
    
    /**
     * 添加装饰器到构建器
     * <p>
     * 将指定的装饰函数添加到装饰器列表的末尾，支持链式调用。
     * 装饰器将在构建时按照添加的顺序应用到组件上。
     * </p>
     *
     * @param decorator 装饰函数，用于对组件进行装饰操作
     * @return 当前构建器实例，支持链式调用
     * @throws IllegalArgumentException 如果decorator为null
     */
    @Override
    public DecoratorBuilder<T> add(Function<T, T> decorator) {
        if (decorator == null) {
            throw new IllegalArgumentException("Decorator cannot be null");
        }
        decorators.add(decorator);
        return this;
    }
    
    /**
     * 构建装饰后的组件
     * <p>
     * 按照装饰器列表的顺序依次应用所有装饰器到组件上，
     * 每个装饰器的输出作为下一个装饰器的输入，返回最终的装饰结果。
     * 构建完成后，组件实例将被更新为装饰后的版本。
     * </p>
     *
     * @return 应用所有装饰器后的组件实例
     */
    @Override
    public T build() {
        for (Function<T, T> decorator : decorators) {
            component = decorator.apply(component);
        }
        return component;
    }
    
    /**
     * 获取装饰器数量
     * <p>
     * 返回当前构建器中包含的装饰器函数数量。
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
