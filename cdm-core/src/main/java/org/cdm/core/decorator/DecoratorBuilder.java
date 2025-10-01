package org.cdm.core.decorator;

import java.util.function.Function;

/**
 * 装饰器构建器接口
 * <p>
 * 提供链式调用的装饰器构建方式，允许逐步添加多个装饰器并最终构建装饰后的组件。
 * 该接口实现了构建器模式，为装饰器的创建提供了流畅的API。
 * </p>
 *
 * @param <T> 组件类型，表示要被装饰的目标对象类型
 * @author CDM
 * @since 1.0.0
 */
public interface DecoratorBuilder<T> {
    
    /**
     * 添加装饰器到构建器
     * <p>
     * 将指定的装饰函数添加到装饰器链中，支持链式调用。
     * 装饰器将按照添加的顺序在构建时应用到组件上。
     * </p>
     *
     * @param decorator 装饰函数，用于对组件进行装饰操作
     * @return 当前构建器实例，支持链式调用
     * @throws IllegalArgumentException 如果装饰函数为null
     */
    DecoratorBuilder<T> add(Function<T, T> decorator);
    
    /**
     * 构建装饰后的组件
     * <p>
     * 按照添加装饰器的顺序依次应用所有装饰器，返回最终的装饰组件实例。
     * 构建完成后，构建器的状态将被重置，可以重新开始新的构建过程。
     * </p>
     *
     * @return 应用所有装饰器后的组件实例
     */
    T build();
}
