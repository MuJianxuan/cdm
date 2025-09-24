# Implementation Plan

## [Overview]
为cdm-core模块实现高优先级设计模式抽象，提供企业级开发中常用的对象创建、事件通知、功能扩展和访问控制等核心设计模式模板。

本项目旨在扩展cdm-core模块的设计模式库，基于现有6种设计模式（动作、单例、策略、事件驱动状态机、过滤器链、责任链）的成功实现经验，新增4种高优先级设计模式：工厂模式、观察者模式、装饰器模式和代理模式。这些设计模式将遵循现有代码规范，采用Java 21最新特性，提供类型安全、线程安全的抽象实现，帮助开发者快速构建可维护、可扩展的企业级应用。

## [Types]
新增4种设计模式的核心类型定义和接口规范。

### 工厂模式类型定义
```java
// 基础工厂接口
public interface Factory<T, R> extends Action<T, R> {
    R create(T param);
    
    @Override
    default R doAction(T param) {
        return create(param);
    }
}

// 抽象工厂接口
public interface AbstractFactory<T extends FactoryKey, R> extends Factory<T, R> {
    Factory<T, R> getFactory(T key);
    
    @Override
    default R create(T param) {
        return getFactory(param).create(param);
    }
}

// 工厂键标识
public interface FactoryKey {
    String key();
}

// 对象池工厂
public interface PooledFactory<T, R> extends Factory<T, R> {
    R borrowObject(T param);
    void returnObject(R object);
    void clearPool();
}
```

### 观察者模式类型定义
```java
// 观察者接口
public interface Observer<T extends Event> {
    void update(T event);
    
    default boolean isAsync() {
        return false;
    }
    
    default int getPriority() {
        return 0;
    }
}

// 被观察者接口
public interface Observable<T extends Event> {
    void addObserver(Observer<T> observer);
    void removeObserver(Observer<T> observer);
    void notifyObservers(T event);
    int getObserverCount();
}

// 事件基础接口
public interface Event {
    String getEventType();
    long getTimestamp();
    Object getSource();
}

// 异步事件总线
public interface EventBus<T extends Event> extends Observable<T> {
    void publish(T event);
    void publishAsync(T event);
    void register(Class<? extends T> eventType, Observer<T> observer);
    void unregister(Observer<T> observer);
}
```

### 装饰器模式类型定义
```java
// 装饰器基础接口
public interface Decorator<T> {
    T getComponent();
    
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

// 可装饰组件接口
public interface Decoratable<T> {
    T decorate(Function<T, T> decorator);
    
    default T chainDecorators(List<Function<T, T>> decorators) {
        T result = (T) this;
        for (Function<T, T> decorator : decorators) {
            result = decorator.apply(result);
        }
        return result;
    }
}

// 装饰器构建器
public interface DecoratorBuilder<T> {
    DecoratorBuilder<T> add(Function<T, T> decorator);
    T build();
}
```

### 代理模式类型定义
```java
// 代理接口
public interface Proxy<T> extends Action<T, T> {
    T getTarget();
    
    default boolean isProxyFor(Class<?> type) {
        return type.isInstance(getTarget());
    }
}

// 动态代理工厂
public interface ProxyFactory<T> {
    T createProxy(T target, List<ProxyInterceptor<T>> interceptors);
    
    static <T> ProxyFactory<T> createDefault() {
        return new DynamicProxyFactory<>();
    }
}

// 代理拦截器
public interface ProxyInterceptor<T> {
    Object intercept(T target, String methodName, Object[] args, ProxyInvocation invocation) throws Throwable;
    
    default int getOrder() {
        return 0;
    }
}

// 代理调用
public interface ProxyInvocation {
    Object proceed() throws Throwable;
    String getMethodName();
    Object[] getArguments();
    Object getTarget();
}
```

## [Files]
新增4种设计模式的完整文件结构和模块组织。

### 新文件创建清单

#### 工厂模式文件结构
```
cdm-core/src/main/java/org/cdm/core/factory/
├── Factory.java
├── AbstractFactory.java
├── FactoryKey.java
├── PooledFactory.java
├── impl/
│   ├── SimpleFactory.java
│   ├── AbstractFactoryImpl.java
│   └── PooledFactoryImpl.java
└── example/
    └── FactoryExample.java
```

#### 观察者模式文件结构
```
cdm-core/src/main/java/org/cdm/core/observer/
├── Observer.java
├── Observable.java
├── Event.java
├── EventBus.java
├── impl/
│   ├── SimpleObservable.java
│   ├── SimpleEventBus.java
│   └── AsyncEventBus.java
└── example/
    └── ObserverExample.java
```

#### 装饰器模式文件结构
```
cdm-core/src/main/java/org/cdm/core/decorator/
├── Decorator.java
├── Decoratable.java
├── DecoratorBuilder.java
├── impl/
│   ├── SimpleDecorator.java
│   ├── DecoratorBuilderImpl.java
│   └── ChainDecorator.java
└── example/
    └── DecoratorExample.java
```

#### 代理模式文件结构
```
cdm-core/src/main/java/org/cdm/core/proxy/
├── Proxy.java
├── ProxyFactory.java
├── ProxyInterceptor.java
├── ProxyInvocation.java
├── impl/
│   ├── DynamicProxyFactory.java
│   ├── SimpleProxy.java
│   └── LoggingInterceptor.java
└── example/
    └── ProxyExample.java
```

## [Functions]
新增设计模式的核心功能实现。

### 工厂模式函数
- `Factory.create(T param)` - 创建对象实例
- `AbstractFactory.getFactory(T key)` - 获取具体工厂
- `PooledFactory.borrowObject(T param)` - 从对象池借用对象
- `PooledFactory.returnObject(R object)` - 归还对象到池

### 观察者模式函数
- `Observer.update(T event)` - 接收事件通知
- `Observable.addObserver(Observer<T> observer)` - 添加观察者
- `Observable.notifyObservers(T event)` - 通知所有观察者
- `EventBus.publish(T event)` - 发布事件
- `EventBus.publishAsync(T event)` - 异步发布事件

### 装饰器模式函数
- `Decorator.getComponent()` - 获取被装饰组件
- `Decoratable.decorate(Function<T, T> decorator)` - 应用装饰器
- `DecoratorBuilder.add(Function<T, T> decorator)` - 添加装饰器
- `DecoratorBuilder.build()` - 构建装饰后的对象

### 代理模式函数
- `Proxy.getTarget()` - 获取目标对象
- `ProxyFactory.createProxy(T target, List<ProxyInterceptor<T>> interceptors)` - 创建代理对象
- `ProxyInterceptor.intercept(T target, String methodName, Object[] args, ProxyInvocation invocation)` - 拦截方法调用
- `ProxyInvocation.proceed()` - 继续执行原方法

## [Classes]
新增设计模式的具体类实现。

### 工厂模式类
- `SimpleFactory<T, R>` - 简单工厂实现
- `AbstractFactoryImpl<T extends FactoryKey, R>` - 抽象工厂实现
- `PooledFactoryImpl<T, R>` - 对象池工厂实现
- `FactoryExample` - 工厂模式使用示例

### 观察者模式类
- `SimpleObservable<T extends Event>` - 简单被观察者实现
- `SimpleEventBus<T extends Event>` - 简单事件总线实现
- `AsyncEventBus<T extends Event>` - 异步事件总线实现
- `ObserverExample` - 观察者模式使用示例

### 装饰器模式类
- `SimpleDecorator<T>` - 简单装饰器实现
- `DecoratorBuilderImpl<T>` - 装饰器构建器实现
- `ChainDecorator<T>` - 链式装饰器实现
- `DecoratorExample` - 装饰器模式使用示例

### 代理模式类
- `DynamicProxyFactory<T>` - 动态代理工厂实现
- `SimpleProxy<T>` - 简单代理实现
- `LoggingInterceptor<T>` - 日志拦截器实现
- `ProxyExample` - 代理模式使用示例

## [Dependencies]
无新增外部依赖，所有实现基于Java 21标准库和现有cdm-core模块。

## [Testing]
每个设计模式都将包含完整的单元测试和集成示例。

### 测试文件结构
```
cdm-core/src/test/java/org/cdm/core/
├── factory/
│   ├── FactoryTest.java
│   ├── AbstractFactoryTest.java
│   └── PooledFactoryTest.java
├── observer/
│   ├── ObserverTest.java
│   ├── EventBusTest.java
│   └── AsyncEventBusTest.java
├── decorator/
│   ├── DecoratorTest.java
│   ├── DecoratorBuilderTest.java
│   └── ChainDecoratorTest.java
└── proxy/
    ├── ProxyTest.java
    ├── DynamicProxyTest.java
    └── ProxyInterceptorTest.java
```

### 测试策略
- 单元测试覆盖核心功能
- 集成测试验证模式组合使用
- 性能测试确保实现效率
- 线程安全测试验证并发场景

## [Implementation Order]
按照设计模式的独立性和复杂度，建议按以下顺序实现：

1. **工厂模式** - 最基础的对象创建模式，为其他模式提供创建支持
2. **观察者模式** - 独立的事件通知机制，不依赖其他新模式
3. **装饰器模式** - 功能扩展模式，可独立实现
4. **代理模式** - 最复杂的访问控制模式，可利用前三种模式

每个模式的实现步骤：
1. 创建基础接口定义
2. 实现核心类
3. 添加示例代码
4. 编写单元测试
5. 集成测试验证
6. 文档完善
