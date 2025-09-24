# CDM (Code Design Mode) - Java设计模式抽象库

[![Java 21](https://img.shields.io/badge/Java-21-blue.svg)](https://openjdk.java.net/projects/jdk/21/)
[![Spring Boot 3.2.12](https://img.shields.io/badge/Spring%20Boot-3.2.12-green.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.x-orange.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-red.svg)](LICENSE)

## 📋 项目简介

CDM (Code Design Mode) 是一个专业的Java设计模式抽象库，致力于将经典的设计模式进行现代化封装，提供简洁易用的API接口。项目采用Java 21最新特性，支持Spring Boot无缝集成，帮助开发者快速构建高质量、可维护的企业级应用。
这个项目设立是思想是把设计模式的实现方式都抽象起来成模版，比如策略模式，把一些公共的接口和通用的方法都抽象成模版（基类），开发者可以基于依赖这个包来快速接入业务，并且可以通过模版类快速的知道这个业务所采用的是什么设计模式和快速调整。

### ✨ 核心特性

- **🎯 八大设计模式**：动作模式、单例模式、策略模式、事件驱动状态机、过滤器链、责任链、装饰器模式、工厂模式
- **🔧 Spring Boot集成**：开箱即用的自动配置和依赖注入
- **⚡ Java 21支持**：充分利用现代Java特性，包括预览功能
- **🏗️ 多模块架构**：清晰的项目结构，便于扩展和维护
- **📦 Maven中央仓库**：便捷的依赖管理
- **🔒 线程安全**：所有实现都经过严格的并发测试
- **📚 完整文档**：详细的使用指南和API文档

## 🏗️ 项目架构

CDM采用多模块Maven项目结构，包含三个核心模块：

```
cdm/
├── cdm-core/                    # 核心设计模式实现
├── cdm-spring-boot-starter/     # Spring Boot自动配置
└── cdm-spring-boot-example/     # 使用示例和演示
```

### 模块说明

#### cdm-core
核心设计模式实现模块，包含50+个Java文件，实现了8种经典设计模式：

- **动作模式 (Action)**：函数式接口，提供灵活的参数处理和结果返回
- **单例模式 (Singleton)**：多种线程安全实现（双重检查、饿汉式、枚举）
- **策略模式 (Strategy)**：支持策略池管理和动态策略切换
- **事件驱动状态机 (EventDrivenStateMachine)**：复杂业务流程的状态管理
- **过滤器链 (FilterChain)**：请求处理的链式过滤机制
- **责任链 (ResponsibilityChain)**：请求分发的链式处理模式
- **装饰器模式 (Decorator)**：灵活的对象装饰机制，支持链式装饰和构建器模式
- **工厂模式 (Factory)**：多层次工厂实现，包括简单工厂、抽象工厂和对象池工厂

#### cdm-spring-boot-starter
Spring Boot自动配置模块，提供：

- 自动配置类 `CdmConfiguration`
- 辅助工具类 `CdmHelper`
- 事件驱动状态机的Spring集成
- 依赖注入和Bean管理

#### cdm-spring-boot-example
示例应用模块，展示：

- 各设计模式的实际使用场景
- Spring Boot集成配置
- 最佳实践和代码示例

## 🚀 快速开始

### 环境要求

- Java 21 或更高版本
- Maven 3.6 或更高版本
- Spring Boot 3.2.12（可选）

### Maven依赖

#### 核心模块依赖
```xml
<dependency>
    <groupId>org.cdm</groupId>
    <artifactId>cdm-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

#### Spring Boot集成依赖
```xml
<dependency>
    <groupId>org.cdm</groupId>
    <artifactId>cdm-spring-boot-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

#### 完整项目依赖管理
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>3.2.12</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>org.cdm</groupId>
        <artifactId>cdm-spring-boot-starter</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

### 基本使用示例

#### 1. 动作模式使用
```java
import org.cdm.core.action.Action;

// 定义动作
Action<String, String> upperCaseAction = param -> param.toUpperCase();

// 执行动作
String result = upperCaseAction.doAction("hello world");
System.out.println(result); // 输出: HELLO WORLD

// 或者使用execute方法（无返回值）
upperCaseAction.execute("test");
```

#### 2. 单例模式使用
```java
import org.cdm.core.singleton.DoubleCheckedSingleton;
import org.cdm.core.singleton.Singleton;

// 创建单例
Singleton<DatabaseConnection> singleton = new DoubleCheckedSingleton<>(() -> {
    return new DatabaseConnection("jdbc:mysql://localhost:3306/test");
});

// 获取实例
DatabaseConnection connection = singleton.getInstance();
System.out.println("单例已初始化: " + singleton.isInitialized());

// 销毁单例（可选）
singleton.destroy();
```

#### 3. 策略模式使用
```java
import org.cdm.core.strategy.Strategy;
import org.cdm.core.strategy.StrategyKey;

// 定义策略键
enum PaymentType implements StrategyKey {
    ALIPAY, WECHAT, CREDIT_CARD
}

// 实现策略
class PaymentStrategy implements Strategy<PaymentType, String> {
    private PaymentType paymentType;
    
    public PaymentStrategy(PaymentType paymentType) {
        this.paymentType = paymentType;
    }
    
    @Override
    public StrategyKey strategyKey() {
        return paymentType;
    }
    
    @Override
    public String doAction(PaymentType param) {
        return "Processing payment via: " + param.name();
    }
}

// 使用策略
Strategy<PaymentType, String> strategy = new PaymentStrategy(PaymentType.ALIPAY);
String result = strategy.doAction(PaymentType.ALIPAY);
```

#### 4. 装饰器模式使用
```java
import org.cdm.core.decorator.Decorator;
import org.cdm.core.decorator.impl.SimpleDecorator;
import org.cdm.core.decorator.impl.ChainDecorator;
import org.cdm.core.decorator.DecoratorBuilder;
import org.cdm.core.decorator.impl.DecoratorBuilderImpl;

// 定义被装饰的组件
class TextComponent {
    private String text;
    
    public TextComponent(String text) {
        this.text = text;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
}

// 使用简单装饰器
TextComponent original = new TextComponent("Hello World");
Decorator<TextComponent> simpleDecorator = new SimpleDecorator<>(original, component -> {
    TextComponent decorated = new TextComponent(component.getText().toUpperCase());
    return decorated;
});
TextComponent decorated = simpleDecorator.getComponent();
System.out.println("装饰后文本: " + decorated.getText()); // 输出: HELLO WORLD

// 使用链式装饰器
ChainDecorator<TextComponent> chainDecorator = new ChainDecorator<>(original)
    .addDecorator(component -> new TextComponent(component.getText().toUpperCase()))
    .addDecorator(component -> new TextComponent("*** " + component.getText() + " ***"));
TextComponent chainDecorated = chainDecorator.getDecoratedComponent();
System.out.println("链式装饰后文本: " + chainDecorated.getText()); // 输出: *** HELLO WORLD ***

// 使用装饰器构建器
DecoratorBuilder<TextComponent> builder = new DecoratorBuilderImpl<>(original)
    .add(component -> new TextComponent(component.getText().toUpperCase()))
    .add(component -> new TextComponent(">>> " + component.getText() + " <<<"));
TextComponent builtDecorated = builder.build();
System.out.println("构建器装饰后文本: " + builtDecorated.getText()); // 输出: >>> HELLO WORLD <<<
```

#### 5. 工厂模式使用
```java
import org.cdm.core.factory.Factory;
import org.cdm.core.factory.AbstractFactory;
import org.cdm.core.factory.FactoryKey;
import org.cdm.core.factory.PooledFactory;
import org.cdm.core.factory.impl.SimpleFactory;
import org.cdm.core.factory.impl.AbstractFactoryImpl;
import org.cdm.core.factory.impl.PooledFactoryImpl;

// 定义产品类
class Product {
    private String name;
    private String type;
    
    public Product(String name, String type) {
        this.name = name;
        this.type = type;
    }
    
    public String getName() { return name; }
    public String getType() { return type; }
    
    @Override
    public String toString() {
        return "Product{name='" + name + "', type='" + type + "'}";
    }
}

// 定义工厂键
class ProductFactoryKey implements FactoryKey {
    private final String type;
    
    public ProductFactoryKey(String type) {
        this.type = type;
    }
    
    @Override
    public String key() {
        return type;
    }
}

// 使用简单工厂
Factory<String, Product> simpleFactory = new SimpleFactory<>(name -> new Product(name, "simple"));
Product product1 = simpleFactory.create("SimpleProduct");
System.out.println("创建产品: " + product1);

// 使用抽象工厂
AbstractFactory<ProductFactoryKey, Product> abstractFactory = new AbstractFactoryImpl<>();

// 注册具体工厂
abstractFactory.registerFactory("electronic", key -> new Product(key.key(), "electronic"));
abstractFactory.registerFactory("clothing", key -> new Product(key.key(), "clothing"));

// 通过键创建对象
Product electronicProduct = abstractFactory.create(new ProductFactoryKey("electronic"));
Product clothingProduct = abstractFactory.create(new ProductFactoryKey("clothing"));

System.out.println("电子产品: " + electronicProduct);
System.out.println("服装产品: " + clothingProduct);

// 使用对象池工厂
PooledFactory<String, Product> pooledFactory = new PooledFactoryImpl<>(
    name -> new Product(name, "pooled"),
    5 // 最大池大小
);

// 创建并借用对象
Product pooledProduct1 = pooledFactory.borrowObject("PooledProduct1");
System.out.println("创建池化产品: " + pooledProduct1);

// 归还对象到池中
pooledFactory.returnObject(pooledProduct1);
System.out.println("归还产品到池中，当前池大小: " + ((PooledFactoryImpl<String, Product>) pooledFactory).getPoolSize());
```

#### 6. Spring Boot集成使用
```java
import org.cdm.spring.boot.CdmHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BusinessService {
    
    @Autowired
    private CdmHelper cdmHelper;
    
    public void processBusinessLogic() {
        // 使用事件驱动状态机
        EventDrivenStateMachine<YourStateMachineId> stateMachine = 
            cdmHelper.eventDrivenStateMachine();
        
        // 执行状态转换
        YourStateMachineId param = new YourStateMachineId();
        stateMachine.execute(DrivenEvent.START_PROCESS, param);
    }
}
```

## 📖 详细文档

如需了解各设计模式的详细实现和使用方式，请查看：

- [cdm-core 模块文档](cdm-core/README.md) - 核心设计模式详细说明
- [API文档](https://github.com/MuJianxuan/cdm/wiki) - 完整的API参考
- [使用示例](cdm-spring-boot-example/) - 实际应用示例

## 🔧 开发环境配置

### 构建项目
```bash
# 克隆项目
git clone https://github.com/MuJianxuan/cdm.git
cd cdm

# 编译项目
mvn clean compile

# 运行测试
mvn test

# 打包项目
mvn clean package
```

### 开发要求
- **Java版本**: 21（需要启用预览特性）
- **Maven版本**: 3.6+
- **IDE**: IntelliJ IDEA 或 Eclipse
- **编码规范**: 遵循Google Java Style Guide

## 🧪 测试

CDM项目包含完整的测试套件，覆盖所有核心功能：

```bash
# 运行所有测试
mvn test

# 运行特定模块测试
mvn test -pl cdm-core

# 生成测试报告
mvn test jacoco:report
```

## 🤝 贡献指南

我们欢迎社区贡献！请查看 [CONTRIBUTING.md](CONTRIBUTING.md) 了解如何参与项目开发。

### 快速贡献步骤
1. Fork 项目仓库
2. 创建特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 创建 Pull Request

## 📄 许可证

本项目基于 [Apache License 2.0](LICENSE) 许可证开源。

## 👥 作者和贡献者

- **主要维护者**: Rao
- **项目创建**: 2021年
- **贡献者**: 欢迎提交PR成为贡献者

## 📞 联系方式

- **项目主页**: [https://github.com/MuJianxuan/cdm](https://github.com/MuJianxuan/cdm)
- **问题反馈**: [GitHub Issues](https://github.com/MuJianxuan/cdm/issues)
- **讨论区**: [GitHub Discussions](https://github.com/MuJianxuan/cdm/discussions)

## 🙏 致谢

感谢以下开源项目的贡献：

- [Spring Boot](https://spring.io/projects/spring-boot) - 优秀的Java企业级框架
- [Hutool](https://hutool.cn/) - 小而全的Java工具类库
- [Lombok](https://projectlombok.org/) - 简化Java代码的利器

---

**⭐ 如果这个项目对您有帮助，请给我们一个Star！**

<div align="center">
  
[![Star History Chart](https://api.star-history.com/svg?repos=MuJianxuan/cdm&type=Date)](https://star-history.com/#MuJianxuan/cdm&Date)

</div>
