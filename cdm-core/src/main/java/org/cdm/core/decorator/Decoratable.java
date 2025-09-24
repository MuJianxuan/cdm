package org.cdm.core.decorator;

import java.util.List;
import java.util.function.Function;

/**
 * 可装饰组件接口
 * <p>
 * 定义了组件装饰的基本契约，允许对组件进行单个或链式装饰操作。
 * 该接口是装饰器模式的核心接口，为组件提供灵活的装饰能力。
 * </p>
 *
 * @param <T> 组件类型，表示可以被装饰的目标对象类型
 * @author CDM
 * @since 1.0.0
 */
public interface Decoratable<T> {
    
    /**
     * 对组件应用单个装饰器
     * <p>
     * 使用指定的装饰函数对当前组件进行装饰，返回装饰后的组件实例。
     * 装饰函数接收原始组件作为参数，返回装饰后的组件。
     * </p>
     *
     * @param decorator 装饰函数，用于对组件进行装饰操作
     * @return 装饰后的组件实例
     * @throws IllegalArgumentException 如果装饰函数为null
     */
    T decorate(Function<T, T> decorator);
    
    /**
     * 对组件应用装饰器链
     * <p>
     * 按照装饰器列表的顺序依次对组件进行装饰，每个装饰器的输出作为下一个装饰器的输入。
     * 装饰顺序遵循先进先出原则，即列表中第一个装饰器最先应用。
     * </p>
     *
     * @param decorators 装饰器函数列表，按应用顺序排列
     * @return 链式装饰后的组件实例
     * @throws IllegalArgumentException 如果装饰器列表为null
     */
    default T chainDecorators(List<Function<T, T>> decorators) {
        T result = (T) this;
        for (Function<T, T> decorator : decorators) {
            result = decorator.apply(result);
        }
        return result;
    }
}
