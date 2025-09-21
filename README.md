# cdm (Code Design Mode)

## 关于本项目

`cdm` 是一个专注于设计模式的代码库，旨在将常见的设计模式进行抽象和封装，以便在不同的 Java 项目中轻松复用。通过提供标准化的接口和实现，本项目可以帮助开发者快速构建稳定、可扩展且易于维护的软件架构。

## 模块介绍

本项目采用多模块架构，各模块职责清晰：

*   **`cdm-core`**: 核心模块，包含了所有设计模式的抽象接口和具体实现。开发者可以直接依赖此模块来使用相应的设计模式。
*   **`cdm-spring-boot-starter`**: Spring Boot 集成模块。此模块将核心功能封装为 Spring Boot Starter，提供了自动配置和依赖注入的便利，简化了在 Spring 环境下的使用。
*   **`cdm-spring-boot-example`**: 示例模块，展示了如何在实际的 Spring Boot 项目中使用 `cdm` 框架，为开发者提供了最佳实践参考。

## 快速开始

你可以通过以下步骤在你的 Maven 项目中开始使用 `cdm`：

1.  **添加依赖**

    在你的 `pom.xml` 文件中添加 `cdm-core` 或 `cdm-spring-boot-starter` 的依赖（请根据实际的 GroupID, ArtifactID 和 Version 进行替换）：

    ```xml
    <!-- 如果你只希望使用核心设计模式功能 -->
    <dependency>
        <groupId>org.cdm</groupId>
        <artifactId>cdm-core</artifactId>
        <version>LATEST</version>
    </dependency>

    <!-- 如果你在 Spring Boot 项目中使用 -->
    <dependency>
        <groupId>org.cdm</groupId>
        <artifactId>cdm-spring-boot-starter</artifactId>
        <version>LATEST</version>
    </dependency>
    ```

2.  **使用示例**

    具体的使用方法请参考后续的“设计模式”章节以及 `cdm-spring-boot-example` 模块中的代码。

## 设计模式

本框架对多种经典设计模式进行了封装，提供了易于使用的接口和实现。

### 1. 单例模式 (Singleton)

单例模式确保一个类只有一个实例，并提供一个全局访问点。`cdm-core` 提供了多种实现方式，包括饿汉式、懒汉式（双重检查锁）、枚举，并提供了一个 `SingletonFactory` 来统一管理。

#### 使用示例

**a) 继承 `EagerSingleton` (饿汉式)**

饿汉式在类加载时就完成了实例化，是线程安全的。

```java
// 1. 定义你的服务类
public static class DatabaseService {
    // ... 服务类具体实现
}

// 2. 创建一个继承自 EagerSingleton 的单例类
public static class EagerDatabaseSingleton extends EagerSingleton<DatabaseService> {
    
    public static final EagerDatabaseSingleton INSTANCE = new EagerDatabaseSingleton();
    
    private EagerDatabaseSingleton() {}
    
    @Override
    protected DatabaseService createInstance() {
        return new DatabaseService("EagerDatabase");
    }
}

// 3. 获取实例
DatabaseService service = EagerDatabaseSingleton.INSTANCE.getInstance();
```

**b) 继承 `DoubleCheckedSingleton` (懒汉式)**

懒汉式在第一次使用时才进行实例化，通过双重检查锁保证线程安全。

```java
public static class LazyDatabaseSingleton extends DoubleCheckedSingleton<DatabaseService> {
    
    private static volatile LazyDatabaseSingleton instance;
    
    public static LazyDatabaseSingleton getSingleton() {
        if (instance == null) {
            synchronized (LazyDatabaseSingleton.class) {
                if (instance == null) {
                    instance = new LazyDatabaseSingleton();
                }
            }
        }
        return instance;
    }
    
    private LazyDatabaseSingleton() {}
    
    @Override
    protected DatabaseService createInstance() {
        return new DatabaseService("LazyDatabase");
    }
}

// 获取实例
DatabaseService service = LazyDatabaseSingleton.getSingleton().getInstance();
```

**c) 使用 `SingletonFactory`**

`SingletonFactory` 是一个推荐的、用于集中管理各类单例对象的工厂类。

```java
// 注册一个饿汉式单例
Singleton<DatabaseService> factorySingleton = SingletonFactory.createEagerSingleton(
    () -> new DatabaseService("FactoryDatabase")
);
SingletonFactory.register("factoryDB", factorySingleton);

// 从工厂获取实例
DatabaseService service = SingletonFactory.getInstance("factoryDB");
```

### 2. 策略模式 (Strategy)

策略模式定义了一系列算法，并将每个算法封装起来，使它们可以互相替换。`cdm-core` 提供了一个 `StrategyPool` 来管理和执行不同的策略。

#### 使用示例

**a) 定义策略标识**

首先，你需要定义一个用于区分不同策略的 Key。它可以是一个简单的枚举或类，需要实现 `BaseStrategyKey` 接口。

```java
// 使用枚举作为策略 Key
public enum PaymentStrategyKey implements BaseStrategyKey {
    ALIPAY,
    WECHAT_PAY;

    @Override
    public String key() {
        return this.name();
    }
}
```

**b) 实现具体策略**

为每种支付方式创建一个具体的策略类，实现 `Strategy` 接口。

```java
// 支付宝支付策略
public class AlipayStrategy implements Strategy<PaymentStrategyKey, String> {
    @Override
    public String doAction(PaymentStrategyKey param) {
        // 实际的支付逻辑
        return "Using Alipay to pay...";
    }

    @Override
    public StrategyKey strategyKey() {
        return PaymentStrategyKey.ALIPAY;
    }
}

// 微信支付策略
public class WechatPayStrategy implements Strategy<PaymentStrategyKey, String> {
    @Override
    public String doAction(PaymentStrategyKey param) {
        // 实际的支付逻辑
        return "Using WeChat Pay to pay...";
    }

    @Override
    public StrategyKey strategyKey() {
        return PaymentStrategyKey.WECHAT_PAY;
    }
}
```

**c) 创建并使用策略池**

将所有策略添加到一个 List 中，并用它来初始化 `StrategyPool`。

```java
// 1. 初始化所有策略
List<Strategy<PaymentStrategyKey, String>> strategies = new ArrayList<>();
strategies.add(new AlipayStrategy());
strategies.add(new WechatPayStrategy());

// 2. 创建策略池
StrategyPool<PaymentStrategyKey, String> paymentStrategyPool = new StrategyPool<>(strategies);

// 3. 根据 Key 执行相应的策略
String result = paymentStrategyPool.doAction(PaymentStrategyKey.ALIPAY);
System.out.println(result); // 输出: Using Alipay to pay...

String result2 = paymentStrategyPool.doAction(PaymentStrategyKey.WECHAT_PAY);
System.out.println(result2); // 输出: Using WeChat Pay to pay...
```

### 3. 责任链模式 (Chain of Responsibility)

责任链模式将请求的发送者和接收者解耦，允许多个对象都有机会处理请求。请求沿着链传递，直到有一个对象处理它为止。`cdm-core` 通过 `ResponsibilityChain` 对此模式进行了实现。

#### 使用示例

**a) 定义处理上下文**

首先，定义一个将在链中传递的对象，用于携带数据。

```java
// 例如，一个简单的请求对象
public class Request {
    private String content;
    private boolean handled = false;

    // getters and setters
}
```

**b) 创建责任链节点**

创建多个具体的处理节点，每个节点都继承自 `AbstractChainNode`。你需要实现 `support` 方法来决定是否处理该请求，以及 `execute` 方法来执行具体的处理逻辑。`order` 方法用于指定节点在链中的顺序。

```java
// 节点1：日志记录处理器
public class LoggerNode extends AbstractChainNode<Request> {
    @Override
    public boolean support(Request request) {
        // 所有请求都支持日志记录
        return true;
    }

    @Override
    public void execute(Request request) {
        System.out.println("Request received: " + request.getContent());
        // 注意：这里没有设置 handled = true，所以会继续传递
    }

    @Override
    public int order() {
        return 1; // 顺序为1
    }
}

// 节点2：具体业务处理器
public class BusinessNode extends AbstractChainNode<Request> {
    @Override
    public boolean support(Request request) {
        // 只处理包含 "business" 的请求
        return request.getContent().contains("business");
    }

    @Override
    public void execute(Request request) {
        System.out.println("Handling business request...");
        request.setHandled(true); // 标记为已处理，链在此中断
    }

    @Override
    public int order() {
        return 2; // 顺序为2
    }
}
```

**c) 构建并执行责任链**

将创建的节点实例添加到一个 List 中，然后用它来初始化 `ResponsibilityChain`。

```java
// 1. 初始化所有节点
List<AbstractChainNode<Request>> nodes = new ArrayList<>();
nodes.add(new LoggerNode());
nodes.add(new BusinessNode());

// 2. 创建责任链
ResponsibilityChain<Request> chain = new ResponsibilityChain<>(nodes);

// 3. 创建请求并执行
Request request = new Request();
request.setContent("This is a business request.");
chain.execute(request);

// 输出:
// Request received: This is a business request.
// Handling business request...
```

## 贡献

欢迎对本项目做出贡献！我们鼓励您通过以下方式参与：

*   **报告 Bug**：如果您发现了 Bug，请创建一个 Issue 来详细描述它。
*   **提出功能建议**：如果您有新的想法或功能建议，也请通过 Issue 来告诉我们。
*   **提交 Pull Request**：我们欢迎各种形式的贡献，包括代码、文档和测试。请确保您的代码遵循项目现有的编码风格。

## 许可证

本项目采用 [Apache License 2.0](LICENSE) 开源许可证。
