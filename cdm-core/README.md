# CDM-Core - 核心设计模式实现

[![Java 21](https://img.shields.io/badge/Java-21-blue.svg)](https://openjdk.java.net/projects/jdk/21/)
[![Maven](https://img.shields.io/badge/Maven-3.x-orange.svg)](https://maven.apache.org/)
[![Javadoc](https://img.shields.io/badge/Javadoc-Online-blue.svg)](https://mujianxuan.github.io/cdm/apidocs/)

## 📋 模块简介

cdm-core是CDM项目的核心模块，包含了8种经典设计模式的现代化Java实现。模块采用Java 21最新特性，提供类型安全、线程安全、高性能的设计模式抽象，帮助开发者在企业级应用中优雅地解决常见的设计问题。

### ✨ 设计模式概览

| 设计模式 | 接口/类 | 主要功能 | 适用场景 |
|---------|---------|----------|----------|
| **动作模式** | `Action<T,R>` | 函数式参数处理 | 数据转换、业务处理 |
| **单例模式** | `Singleton<T>` | 实例生命周期管理 | 配置管理、连接池 |
| **策略模式** | `Strategy<T,R>` | 动态算法选择 | 支付系统、排序算法 |
| **事件驱动状态机** | `EventDrivenStateMachine<T>` | 复杂状态管理 | 订单流程、工作流 |
| **过滤器链** | `FilterChain<T>` | 请求链式处理 | 权限验证、日志记录 |
| **责任链** | `ResponsibilityChain<T>` | 请求分发处理 | 审批流程、异常处理 |
| **装饰器模式** | `Decorator<T>` | 对象动态装饰 | 功能扩展、AOP |
| **工厂模式** | `Factory<T,R>` | 对象创建管理 | 复杂对象创建、依赖注入 |

## 🎯 设计模式详解

### 1. 动作模式 (Action Pattern)

动作模式是函数式编程的基础，提供了一种将操作作为参数传递的机制。

#### 核心接口
```java
@FunctionalInterface
public interface Action<T, R> {
    /**
     * 处理执行
     * @param param 输入参数
     * @return 处理结果
     */
    R doAction(T param);
    
    /**
     * 空执行（无返回值）
     * @param param 输入参数
     */
    default void execute(T param) {
        this.doAction(param);
    }
}
```

#### 使用示例
```java
import org.cdm.core.action.Action;

// 字符串转换
Action<String, String> toUpperCase = str -> str.toUpperCase();
String result = toUpperCase.doAction("hello"); // "HELLO"

// 数值计算
Action<Integer, Integer> doubleValue = num -> num * 2;
Integer doubled = doubleValue.doAction(5); // 10

// 对象转换
Action<UserDTO, UserEntity> convertToEntity = dto -> {
    UserEntity entity = new UserEntity();
    entity.setName(dto.getName());
    entity.setEmail(dto.getEmail());
    return entity;
};

UserEntity entity = convertToEntity.doAction(userDto);
```

#### 高级用法
```java
// 动作组合
Action<String, String> trimAndUpper = str -> 
    toUpperCase.doAction(str.trim());

// 条件动作
Action<String, String> conditionalAction = str -> 
    str.length() > 5 ? str.toUpperCase() : str.toLowerCase();

// 异常处理
Action<String, Integer> safeParse = str -> {
    try {
        return Integer.parseInt(str);
    } catch (NumberFormatException e) {
        return 0;
    }
};
```

### 2. 单例模式 (Singleton Pattern)

单例模式确保一个类只有一个实例，并提供全局访问点。cdm-core提供了多种线程安全的实现方式。

#### 核心接口
```java
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
```

#### 实现方式

##### 双重检查锁定 (Double-Checked Locking)
```java
import org.cdm.core.singleton.DoubleCheckedSingleton;

// 创建线程安全的单例
Singleton<DatabaseConnection> dbSingleton = new DoubleCheckedSingleton<>(() -> {
    return new DatabaseConnection("jdbc:mysql://localhost:3306/mydb");
});

// 获取实例
DatabaseConnection connection = dbSingleton.getInstance();

// 检查初始化状态
boolean isInitialized = dbSingleton.isInitialized(); // true

// 使用实例
connection.executeQuery("SELECT * FROM users");
```

##### 饿汉式 (Eager Initialization)
```java
import org.cdm.core.singleton.EagerSingleton;

// 立即初始化
Singleton<CacheManager> cacheSingleton = new EagerSingleton<>(() -> {
    return new CacheManager(1000); // 容量1000
});

// 获取实例（立即可用）
CacheManager cache = cacheSingleton.getInstance();
```

##### 枚举单例 (Enum Singleton)
```java
import org.cdm.core.singleton.EnumSingleton;

// 类型安全的枚举单例
public enum ConfigManager {
    INSTANCE;
    
    private final Properties config;
    
    ConfigManager() {
        this.config = loadConfiguration();
    }
    
    public String getProperty(String key) {
        return config.getProperty(key);
    }
}

// 使用
String dbUrl = ConfigManager.INSTANCE.getProperty("db.url");
```

#### 最佳实践
```java
// 单例工厂
public class SingletonFactory {
    private static final Map<Class<?>, Singleton<?>> singletons = new ConcurrentHashMap<>();
    
    public static <T> Singleton<T> getSingleton(Class<T> clazz, Supplier<T> supplier) {
        return (Singleton<T>) singletons.computeIfAbsent(clazz, 
            k -> new DoubleCheckedSingleton<>(supplier));
    }
}

// 使用工厂
Singleton<Logger> loggerSingleton = SingletonFactory.getSingleton(
    Logger.class, 
    () -> new Logger("application.log")
);
```

### 3. 策略模式 (Strategy Pattern)

策略模式定义了一系列算法，并将每个算法封装起来，使它们可以互相替换。

#### 核心接口
```java
public interface Strategy<T extends StrategyKey, R> extends Action<T, R> {
    /**
     * 策略键
     * @return 策略标识
     */
    StrategyKey strategyKey();
}
```

#### 使用示例

##### 基础策略实现
```java
import org.cdm.core.strategy.Strategy;
import org.cdm.core.strategy.StrategyKey;

// 定义策略键
public enum PaymentMethod implements StrategyKey {
    CREDIT_CARD, DEBIT_CARD, PAYPAL, BANK_TRANSFER
}

// 支付策略接口
public interface PaymentStrategy extends Strategy<PaymentMethod, PaymentResult> {
    PaymentResult processPayment(BigDecimal amount, String account);
}

// 信用卡支付策略
public class CreditCardPayment implements PaymentStrategy {
    @Override
    public StrategyKey strategyKey() {
        return PaymentMethod.CREDIT_CARD;
    }
    
    @Override
    public PaymentResult doAction(PaymentMethod method) {
        return processPayment(BigDecimal.valueOf(100), "1234-5678-9012-3456");
    }
    
    @Override
    public PaymentResult processPayment(BigDecimal amount, String cardNumber) {
        // 验证卡号
        if (isValidCard(cardNumber)) {
            return PaymentResult.success("Credit card payment processed");
        }
        return PaymentResult.failure("Invalid card number");
    }
}
```

##### 策略池管理
```java
import org.cdm.core.strategy.StrategyPool;

// 创建策略池
StrategyPool<PaymentMethod, PaymentResult> paymentPool = new StrategyPool<>();

// 注册策略
paymentPool.register(new CreditCardPayment());
paymentPool.register(new PayPalPayment());
paymentPool.register(new BankTransferPayment());

// 使用策略
PaymentMethod method = PaymentMethod.CREDIT_CARD;
PaymentStrategy strategy = paymentPool.getStrategy(method);
PaymentResult result = strategy.doAction(method);
```

##### 动态策略选择
```java
public class PaymentService {
    private final StrategyPool<PaymentMethod, PaymentResult> strategyPool;
    
    public PaymentService(StrategyPool<PaymentMethod, PaymentResult> strategyPool) {
        this.strategyPool = strategyPool;
    }
    
    public PaymentResult processPayment(PaymentRequest request) {
        PaymentMethod method = determinePaymentMethod(request);
        PaymentStrategy strategy = strategyPool.getStrategy(method);
        
        if (strategy == null) {
            throw new UnsupportedPaymentMethodException(method);
        }
        
        return strategy.doAction(method);
    }
    
    private PaymentMethod determinePaymentMethod(PaymentRequest request) {
        // 根据请求内容选择支付方式
        if (request.getCardNumber() != null) {
            return request.getCardType() == CardType.CREDIT 
                ? PaymentMethod.CREDIT_CARD
                : PaymentMethod.DEBIT_CARD;
    }
}
```

#### 策略模式最佳实践
```java
// 策略注解
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface StrategyType {
    PaymentMethod value();
}

// 注解驱动的策略
@StrategyType(PaymentMethod.CREDIT_CARD)
public class AnnotatedCreditCardPayment implements PaymentStrategy {
    // 实现细节
}

// Spring集成策略发现
@Component
public class StrategyDiscovery {
    @Autowired
    private ApplicationContext context;
    
    public <T extends StrategyKey, R> StrategyPool<T, R> discoverStrategies(Class<Strategy<T, R>> strategyType) {
        StrategyPool<T, R> pool = new StrategyPool<>();
        
        Map<String, Strategy<T, R>> strategies = context.getBeansOfType(strategyType);
        strategies.values().forEach(pool::register);
        
        return pool;
    }
}
```

### 4. 事件驱动状态机 (Event-Driven State Machine)

事件驱动状态机用于管理复杂的业务流程状态转换，特别适合订单处理、工作流等场景。

#### 核心组件

##### 状态机核心类
```java
public class EventDrivenStateMachine<T extends StateMachineId> extends StateContext<T> {
    
    public void execute(DrivenEvent drivenEvent, T param) {
        // 状态验证
        // 状态转换
        // 事件处理
    }
}
```

##### 状态上下文
```java
public class StateContext<T extends StateMachineId> implements Serializable {
    private StateEnum currentState;
    private StateEnum finalState;
    private DrivenEvent event;
    private T source;
    private StateMachineId stateMachineId;
}
```

##### 事件定义
```java
public interface DrivenEvent {
    StateEnum initialState();
    StateEnum finalState();
    String eventName();
}
```

#### 使用示例

##### 订单状态管理
```java
// 定义订单状态
public enum OrderState implements StateEnum {
    PENDING_PAYMENT("待支付"),
    PAID("已支付"),
    SHIPPED("已发货"),
    DELIVERED("已送达"),
    COMPLETED("已完成"),
    CANCELLED("已取消");
    
    private final String description;
    
    OrderState(String description) {
        this.description = description;
    }
    
    @Override
    public String state() {
        return this.name();
    }
}

// 定义订单事件
public enum OrderEvent implements DrivenEvent {
    PAYMENT_COMPLETED(OrderState.PENDING_PAYMENT, OrderState.PAID, "支付完成"),
    SHIP_ORDER(OrderState.PAID, OrderState.SHIPPED, "订单发货"),
    DELIVER_ORDER(OrderState.SHIPPED, OrderState.DELIVERED, "订单送达"),
    CONFIRM_RECEIPT(OrderState.DELIVERED, OrderState.COMPLETED, "确认收货"),
    CANCEL_ORDER(OrderState.PENDING_PAYMENT, OrderState.CANCELLED, "取消订单");
    
    private final StateEnum initialState;
    private final StateEnum finalState;
    private final String eventName;
    
    OrderEvent(StateEnum initialState, StateEnum finalState, String eventName) {
        this.initialState = initialState;
        this.finalState = finalState;
        this.eventName = eventName;
    }
    
    @Override
    public StateEnum initialState() {
        return initialState;
    }
    
    @Override
    public StateEnum finalState() {
        return finalState;
    }
    
    @Override
    public String eventName() {
        return eventName;
    }
}
```

##### 状态机助手
```java
public class OrderStateAssistant extends AbstractStateAssistant<Order> {
    
    @Override
    public StateEnum read(StateContext<Order> context) {
        // 从数据库读取当前状态
        Order order = orderRepository.findById(context.getStateMachineId().getId());
        return OrderState.valueOf(order.getStatus());
    }
    
    @Override
    public void write(StateContext<Order> context) {
        // 持久化新状态
        Order order = context.getSource();
        order.setStatus(context.getFinalState().state());
        orderRepository.save(order);
    }
}
```

##### 事件行为处理器
```java
public class PaymentCompletedHandler extends AbstractEventBehaviorHandler<Order> {
    
    @Override
    public DrivenEvent drivenEvent() {
        return OrderEvent.PAYMENT_COMPLETED;
    }
    
    @Override
    public void triggerBehaviorHandle(StateContext<Order> context) {
        Order order = context.getSource();
        
        // 发送支付确认邮件
        emailService.sendPaymentConfirmation(order.getCustomerEmail());
        
        // 更新库存
        inventoryService.reserveItems(order.getItems());
        
        // 记录支付日志
        paymentLogService.logPayment(order.getId(), order.getPaymentAmount());
        
        log.info("Payment completed for order: {}", order.getId());
    }
}
```

##### 完整的状态机使用
```java
// 配置状态机
@Configuration
public class OrderStateMachineConfig {
    
    @Bean
    public EventDrivenStateMachine<Order> orderStateMachine(
            OrderStateAssistant stateAssistant,
            List<AbstractEventBehaviorHandler<Order>> handlers) {
        
        Map<DrivenEvent, AbstractEventBehaviorHandler<Order>> handlerMap = handlers.stream()
            .collect(Collectors.toMap(
                AbstractEventBehaviorHandler::drivenEvent, 
                Function.identity()
            ));
        
        return new EventDrivenStateMachine<>(stateAssistant, handlerMap);
    }
}

// 业务服务中使用
@Service
public class OrderService {
    
    @Autowired
    private EventDrivenStateMachine<Order> stateMachine;
    
    public void processPayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        // 执行状态转换
        stateMachine.execute(OrderEvent.PAYMENT_COMPLETED, order);
    }
}
```

### 5. 过滤器链 (Filter Chain)

过滤器链模式用于对请求进行链式处理，每个过滤器都可以对请求进行处理或传递给下一个过滤器。

#### 核心接口
```java
public interface Filter<T> {
    /**
     * 执行过滤
     * @param param 参数
     * @param filterChain 过滤器链
     */
    void doFilter(T param, FilterChain<T> filterChain);
}
```

#### 实现方式

##### 索引过滤器链
```java
import org.cdm.core.filterchain.IndexFilterChain;

// 创建过滤器链
IndexFilterChain<HttpRequest> filterChain = new IndexFilterChain<>();

// 添加过滤器
filterChain.addFilter(new AuthenticationFilter());
filterChain.addFilter(new AuthorizationFilter());
filterChain.addFilter(new LoggingFilter());
filterChain.addFilter(new RateLimitFilter());

// 执行过滤
HttpRequest request = new HttpRequest("/api/users", "GET");
filterChain.doFilter(request, filterChain);
```

##### 迭代器过滤器链
```java
import org.cdm.core.filterchain.IteratorFilterChain;

// 创建迭代器过滤器链
IteratorFilterChain<HttpRequest> iteratorChain = new IteratorFilterChain<>();

// 添加过滤器
iteratorChain.addFilter(new ValidationFilter());
iteratorChain.addFilter(new TransformationFilter());

// 执行过滤
iteratorChain.doFilter(request, iteratorChain);
```

#### 过滤器实现示例
```java
// 认证过滤器
public class AuthenticationFilter implements Filter<HttpRequest> {
    
    @Override
    public void doFilter(HttpRequest request, FilterChain<HttpRequest> chain) {
        String token = request.getHeader("Authorization");
        
        if (token == null || !isValidToken(token)) {
            throw new AuthenticationException("Invalid or missing token");
        }
        
        // 设置用户信息
        request.setAttribute("userId", extractUserId(token));
        
        // 传递给下一个过滤器
        chain.doFilter(request, chain);
    }
}

// 授权过滤器
public class AuthorizationFilter implements Filter<HttpRequest> {
    
    @Override
    public void doFilter(HttpRequest request, FilterChain<HttpRequest> chain) {
        String userId = (String) request.getAttribute("userId");
        String path = request.getPath();
        
        if (!hasPermission(userId, path)) {
            throw new AuthorizationException("Insufficient permissions");
        }
        
        // 记录授权信息
        request.setAttribute("authorized", true);
        
        // 继续过滤链
        chain.doFilter(request, chain);
    }
}
```

### 6. 责任链 (Responsibility Chain)

责任链模式将请求沿着处理者链进行传递，直到有一个处理者能够处理该请求。

#### 核心组件

##### 责任链接口
```java
public interface Chain<T> {
    /**
     * 执行链
     * @param param 参数
     * @return 是否继续执行
     */
    boolean execute(T param);
}
```

##### 链节点
```java
public interface ChainNode<T> {
    /**
     * 是否支持处理
     * @param param 参数
     * @return 是否支持
     */
    boolean support(T param);
    
    /**
     * 执行处理
     * @param param 参数
     * @return 处理结果
     */
    boolean doExecute(T param);
}
```

#### 使用示例

##### 审批流程
```java
// 审批请求
public class ApprovalRequest {
    private String requestId;
    private BigDecimal amount;
    private String requester;
    private ApprovalLevel requiredLevel;
    
    // getters and setters
}

// 审批级别
public enum ApprovalLevel {
    MANAGER(1000),      // 1000元以下
    DIRECTOR(10000),    // 10000元以下
    CEO(100000),        // 100000元以下
    BOARD(Long.MAX_VALUE); // 无限制
    
    private final long maxAmount;
    
    ApprovalLevel(long maxAmount) {
        this.maxAmount = maxAmount;
    }
    
    public boolean canApprove(BigDecimal amount) {
        return amount.compareTo(BigDecimal.valueOf(maxAmount)) <= 0;
    }
}
```

##### 审批节点实现
```java
// 经理审批节点
public class ManagerApprovalNode extends AbstractChainNode<ApprovalRequest> {
    
    @Override
    public boolean support(ApprovalRequest request) {
        return ApprovalLevel.MANAGER.canApprove(request.getAmount());
    }
    
    @Override
    public boolean doExecute(ApprovalRequest request) {
        System.out.println("Manager approving request: " + request.getRequestId());
        
        // 模拟审批逻辑
        boolean approved = request.getAmount().compareTo(BigDecimal.valueOf(500)) <= 0;
        
        if (approved) {
            System.out.println("Request approved by Manager");
            notificationService.notifyApproval(request);
            return true; // 审批通过，结束责任链
        }
        
        return false; // 继续下一个审批者
    }
}

// 总监审批节点
public class DirectorApprovalNode extends AbstractChainNode<ApprovalRequest> {
    
    @Override
    public boolean support(ApprovalRequest request) {
        return ApprovalLevel.DIRECTOR.canApprove(request.getAmount());
    }
    
    @Override
    public boolean doExecute(ApprovalRequest request) {
        System.out.println("Director reviewing request: " + request.getRequestId());
        
        // 检查业务规则
        if (request.getAmount().compareTo(BigDecimal.valueOf(5000)) > 0) {
            // 需要额外审核
            return false;
        }
        
        System.out.println("Request approved by Director");
        return true;
    }
}
```

##### 责任链配置和使用
```java
// 配置责任链
public class ApprovalChainConfig {
    
    public static ResponsibilityChain<ApprovalRequest> createApprovalChain() {
        ResponsibilityChain<ApprovalRequest> chain = new ResponsibilityChain<>();
        
        // 按优先级添加审批节点
        chain.addChainNode(new ManagerApprovalNode());
        chain.addChainNode(new DirectorApprovalNode());
        chain.addChainNode(new CEOApprovalNode());
        chain.addChainNode(new BoardApprovalNode());
        
        return chain;
    }
}

// 使用责任链
@Service
public class ApprovalService {
    
    private final ResponsibilityChain<ApprovalRequest> approvalChain;
    
    public ApprovalService() {
        this.approvalChain = ApprovalChainConfig.createApprovalChain();
    }
    
    public ApprovalResult processApproval(ApprovalRequest request) {
        boolean approved = approvalChain.execute(request);
        
        if (approved) {
            return ApprovalResult.success("Request approved");
        } else {
            return ApprovalResult.failure("Request requires manual review");
        }
    }
}
```

### 7. 装饰器模式 (Decorator Pattern)

装饰器模式允许向一个对象动态地添加新的行为，而不需要修改该对象的基类或使用继承。cdm-core提供了灵活的装饰器实现，支持链式装饰和构建器模式。

#### 核心接口

##### 装饰器基础接口
```java
public interface Decorator<T> {
    /**
     * 获取被装饰的原始组件
     * @return 被装饰的原始组件实例
     */
    T getComponent();
}
```

##### 可装饰组件接口
```java
public interface Decoratable<T> {
    /**
     * 装饰当前组件
     * @param decorator 装饰器函数
     * @return 装饰后的组件
     */
    T decorate(Function<T, T> decorator);
    
    /**
     * 解包装饰器
     * @return 原始组件
     */
    T unwrap();
}
```

##### 装饰器构建器接口
```java
public interface DecoratorBuilder<T> {
    /**
     * 添加装饰器
     * @param decorator 装饰器函数
     * @return 构建器实例，支持链式调用
     */
    DecoratorBuilder<T> add(Function<T, T> decorator);
    
    /**
     * 构建装饰后的组件
     * @return 装饰后的组件实例
     */
    T build();
}
```

#### 实现方式

##### 简单装饰器 (SimpleDecorator)
```java
import org.cdm.core.decorator.impl.SimpleDecorator;

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
    
    @Override
    public String toString() {
        return "TextComponent{text='" + text + "'}";
    }
}

// 创建简单装饰器
TextComponent original = new TextComponent("Hello World");
Decorator<TextComponent> simpleDecorator = new SimpleDecorator<>(original, component -> {
    TextComponent decorated = new TextComponent(component.getText().toUpperCase());
    return decorated;
});

// 获取装饰后的组件
TextComponent decorated = simpleDecorator.getComponent();
System.out.println("装饰后文本: " + decorated.getText()); // 输出: HELLO WORLD
```

##### 链式装饰器 (ChainDecorator)
```java
import org.cdm.core.decorator.impl.ChainDecorator;

// 创建链式装饰器
ChainDecorator<TextComponent> chainDecorator = new ChainDecorator<>(original)
    .addDecorator(component -> new TextComponent(component.getText().toUpperCase()))
    .addDecorator(component -> new TextComponent("*** " + component.getText() + " ***"))
    .addDecorator(component -> new TextComponent("[INFO] " + component.getText()));

// 获取装饰后的组件
TextComponent chainDecorated = chainDecorator.getDecoratedComponent();
System.out.println("链式装饰后文本: " + chainDecorated.getText()); 
// 输出: [INFO] *** HELLO WORLD ***

// 获取装饰器数量
System.out.println("装饰器数量: " + chainDecorator.getDecoratorCount()); // 输出: 3

// 获取装饰器列表
List<Function<TextComponent, TextComponent>> decorators = chainDecorator.getDecorators();
```

##### 装饰器构建器 (DecoratorBuilder)
```java
import org.cdm.core.decorator.impl.DecoratorBuilderImpl;

// 使用装饰器构建器
DecoratorBuilder<TextComponent> builder = new DecoratorBuilderImpl<>(original)
    .add(component -> new TextComponent(component.getText().toUpperCase()))
    .add(component -> new TextComponent(">>> " + component.getText() + " <<<"))
    .add(component -> new TextComponent("[BUILDER] " + component.getText()));

// 构建装饰后的组件
TextComponent builtDecorated = builder.build();
System.out.println("构建器装饰后文本: " + builtDecorated.getText()); 
// 输出: [BUILDER] >>> HELLO WORLD <<<
```

#### 高级用法

##### 自定义装饰器
```java
// 日志装饰器
public class LoggingDecorator<T> implements Decorator<T> {
    private final T component;
    private final Logger logger = LoggerFactory.getLogger(LoggingDecorator.class);
    
    public LoggingDecorator(T component) {
        this.component = component;
    }
    
    @Override
    public T getComponent() {
        logger.info("Accessing component: {}", component.getClass().getSimpleName());
        return component;
    }
}

// 缓存装饰器
public class CachingDecorator<T> implements Decorator<T> {
    private final T component;
    private final Map<String, Object> cache = new ConcurrentHashMap<>();
    
    public CachingDecorator(T component) {
        this.component = component;
    }
    
    @Override
    public T getComponent() {
        return component;
    }
    
    public Object getCachedResult(String key, Supplier<Object> supplier) {
        return cache.computeIfAbsent(key, k -> {
            logger.debug("Cache miss for key: {}", k);
            return supplier.get();
        });
    }
    
    public void clearCache() {
        cache.clear();
    }
}
```

##### 组合装饰器
```java
// 组合多个装饰器
public class CompositeDecorator<T> implements Decorator<T> {
    private final T component;
    private final List<Decorator<T>> decorators;
    
    public CompositeDecorator(T component, List<Decorator<T>> decorators) {
        this.component = component;
        this.decorators = new ArrayList<>(decorators);
    }
    
    @Override
    public T getComponent() {
        T result = component;
        for (Decorator<T> decorator : decorators) {
            result = decorator.getComponent();
        }
        return result;
    }
    
    public void addDecorator(Decorator<T> decorator) {
        decorators.add(decorator);
    }
    
    public void removeDecorator(Decorator<T> decorator) {
        decorators.remove(decorator);
    }
}
```

##### 条件装饰器
```java
// 条件装饰器
public class ConditionalDecorator<T> implements Decorator<T> {
    private final T component;
    private final Predicate<T> condition;
    private final Function<T, T> decoratorFunction;
    
    public ConditionalDecorator(T component, Predicate<T> condition, Function<T, T> decoratorFunction) {
        this.component = component;
        this.condition = condition;
        this.decoratorFunction = decoratorFunction;
    }
    
    @Override
    public T getComponent() {
        if (condition.test(component)) {
            return decoratorFunction.apply(component);
        }
        return component;
    }
}

// 使用条件装饰器
TextComponent conditionalComponent = new TextComponent("Hello World");
Decorator<TextComponent> conditionalDecorator = new ConditionalDecorator<>(
    conditionalComponent,
    comp -> comp.getText().length() > 5,
    comp -> new TextComponent(comp.getText().toUpperCase())
);

TextComponent conditionalResult = conditionalDecorator.getComponent();
System.out.println("条件装饰结果: " + conditionalResult.getText()); // 输出: HELLO WORLD
```

#### 实际应用场景

##### 数据处理管道
```java
// 数据处理装饰器
public class DataProcessingDecorator implements Decorator<String> {
    private final String data;
    
    public DataProcessingDecorator(String data) {
        this.data = data;
    }
    
    @Override
    public String getComponent() {
        return data;
    }
}

// 构建数据处理管道
String rawData = "  hello,world,java,decorator  ";
DecoratorBuilder<String> dataPipeline = new DecoratorBuilderImpl<>(rawData)
    .add(String::trim)                    // 去除首尾空格
    .add(s -> s.toUpperCase())           // 转大写
    .add(s -> s.replace(",", " "))        // 替换逗号为空格
    .add(s -> "PROCESSED: " + s);        // 添加前缀

String processedData = dataPipeline.build();
System.out.println("处理后的数据: " + processedData);
// 输出: PROCESSED: HELLO WORLD JAVA DECORATOR
```

##### HTTP请求装饰器
```java
// HTTP请求装饰器
public class HttpRequestDecorator implements Decorator<HttpRequest> {
    private final HttpRequest request;
    
    public HttpRequestDecorator(HttpRequest request) {
        this.request = request;
    }
    
    @Override
    public HttpRequest getComponent() {
        return request;
    }
}

// 构建HTTP请求装饰链
HttpRequest httpRequest = new HttpRequest("/api/users", "GET");
ChainDecorator<HttpRequest> requestDecorator = new ChainDecorator<>(httpRequest)
    .addDecorator(req -> {
        // 添加认证头
        req.addHeader("Authorization", "Bearer token123");
        return req;
    })
    .addDecorator(req -> {
        // 添加请求ID
        req.addHeader("X-Request-ID", UUID.randomUUID().toString());
        return req;
    })
    .addDecorator(req -> {
        // 添加时间戳
        req.addHeader("X-Timestamp", String.valueOf(System.currentTimeMillis()));
        return req;
    });

HttpRequest decoratedRequest = requestDecorator.getDecoratedComponent();
```

### 8. 工厂模式 (Factory Pattern)

工厂模式提供了一种创建对象的接口，让子类决定实例化哪一个类。cdm-core提供了多层次的工厂实现，包括简单工厂、抽象工厂和对象池工厂。

#### 核心接口

##### 基础工厂接口
```java
public interface Factory<T, R> extends Action<T, R> {
    /**
     * 创建对象
     * @param param 创建参数
     * @return 创建的对象
     */
    R create(T param);
}
```

##### 抽象工厂接口
```java
public interface AbstractFactory<T extends FactoryKey, R> {
    /**
     * 注册工厂
     * @param key 工厂键
     * @param factory 工厂实例
     */
    void registerFactory(String key, Factory<T, R> factory);
    
    /**
     * 注销工厂
     * @param key 工厂键
     * @return 被移除的工厂实例
     */
    Factory<T, R> unregisterFactory(String key);
    
    /**
     * 获取工厂
     * @param key 工厂键
     * @return 工厂实例
     */
    Factory<T, R> getFactory(T key);
    
    /**
     * 通过键创建对象
     * @param key 工厂键
     * @return 创建的对象
     */
    R create(T key);
    
    /**
     * 通过键和参数创建对象
     * @param key 工厂键
     * @param param 创建参数
     * @return 创建的对象
     */
    <P> R create(T key, P param);
}
```

##### 工厂键接口
```java
public interface FactoryKey {
    /**
     * 获取工厂键
     * @return 工厂键字符串
     */
    String key();
}
```

##### 对象池工厂接口
```java
public interface PooledFactory<T, R> extends Factory<T, R> {
    /**
     * 借用对象
     * @param param 借用参数
     * @return 借用的对象
     */
    R borrowObject(T param);
    
    /**
     * 归还对象
     * @param object 要归还的对象
     */
    void returnObject(R object);
    
    /**
     * 清空对象池
     */
    void clearPool();
}
```

#### 实现方式

##### 简单工厂 (SimpleFactory)
```java
import org.cdm.core.factory.impl.SimpleFactory;

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

// 使用函数式接口创建简单工厂
Factory<String, Product> simpleFactory = new SimpleFactory<>(name -> new Product(name, "simple"));
Product product1 = simpleFactory.create("SimpleProduct");
System.out.println("创建产品: " + product1);

// 使用实例创建简单工厂
Product fixedProduct = new Product("FixedProduct", "fixed");
Factory<String, Product> fixedFactory = new SimpleFactory<>(fixedProduct);
Product product2 = fixedFactory.create("any param"); // 忽略参数，返回固定实例
System.out.println("固定产品: " + product2);
```

##### 抽象工厂 (AbstractFactory)
```java
import org.cdm.core.factory.impl.AbstractFactoryImpl;

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
    
    @Override
    public String toString() {
        return "ProductFactoryKey{type='" + type + "'}";
    }
}

// 创建抽象工厂
AbstractFactory<ProductFactoryKey, Product> abstractFactory = new AbstractFactoryImpl<>();

// 注册具体工厂
abstractFactory.registerFactory("electronic", key -> new Product(key.key(), "electronic"));
abstractFactory.registerFactory("clothing", key -> new Product(key.key(), "clothing"));
abstractFactory.registerFactory("food", key -> new Product(key.key(), "food"));

// 通过键创建对象
Product electronicProduct = abstractFactory.create(new ProductFactoryKey("electronic"));
Product clothingProduct = abstractFactory.create(new ProductFactoryKey("clothing"));
Product foodProduct = abstractFactory.create(new ProductFactoryKey("food"));

System.out.println("电子产品: " + electronicProduct);
System.out.println("服装产品: " + clothingProduct);
System.out.println("食品产品: " + foodProduct);

// 通过键和参数创建对象
Product customElectronic = abstractFactory.create(new ProductFactoryKey("electronic"), "iPhone");
System.out.println("自定义电子产品: " + customElectronic);

// 工厂管理功能
System.out.println("已注册工厂数量: " + abstractFactory.size());
System.out.println("已注册的工厂键: " + abstractFactory.getRegisteredKeys());
System.out.println("是否包含'electronic'工厂: " + abstractFactory.containsFactory(new ProductFactoryKey("electronic")));

// 获取具体工厂
Factory<ProductFactoryKey, Product> factory = abstractFactory.getFactory(new ProductFactoryKey("electronic"));
System.out.println("获取的工厂实例: " + factory);

// 注销工厂
abstractFactory.unregisterFactory("food");
System.out.println("注销'food'工厂后数量: " + abstractFactory.size());
```

##### 对象池工厂 (PooledFactory)
```java
import org.cdm.core.factory.impl.PooledFactoryImpl;

// 创建对象池工厂
PooledFactory<String, Product> pooledFactory = new PooledFactoryImpl<>(
    name -> new Product(name, "pooled"),
    5 // 最大池大小
);

// 创建并借用对象
Product pooledProduct1 = pooledFactory.borrowObject("PooledProduct1");
Product pooledProduct2 = pooledFactory.borrowObject("PooledProduct2");
System.out.println("创建池化产品1: " + pooledProduct1);
System.out.println("创建池化产品2: " + pooledProduct2);

// 归还对象到池中
pooledFactory.returnObject(pooledProduct1);
pooledFactory.returnObject(pooledProduct2);
System.out.println("归还产品到池中，当前池大小: " + ((PooledFactoryImpl<String, Product>) pooledFactory).getPoolSize());

// 从池中借用对象（应该重用之前的对象）
Product pooledProduct3 = pooledFactory.borrowObject("PooledProduct3");
System.out.println("从池中借用产品: " + pooledProduct3);
System.out.println("当前池大小: " + ((PooledFactoryImpl<String, Product>) pooledFactory).getPoolSize());

// 清空池
pooledFactory.clearPool();
System.out.println("清空池后大小: " + ((PooledFactoryImpl<String, Product>) pooledFactory).getPoolSize());
```

#### 高级用法

##### 自定义工厂实现
```java
// 数据库连接工厂
public class DatabaseConnectionFactory implements Factory<String, Connection> {
    private final String url;
    private final String username;
    private final String password;
    
    public DatabaseConnectionFactory(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }
    
    @Override
    public Connection create(String databaseName) {
        try {
            String fullUrl = url + "/" + databaseName;
            return DriverManager.getConnection(fullUrl, username, password);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create database connection", e);
        }
    }
}

// 使用自定义工厂
Factory<String, Connection> dbFactory = new DatabaseConnectionFactory(
    "jdbc:mysql://localhost:3306", "root", "password");
Connection connection = dbFactory.create("mydb");
```

##### 工厂注册中心
```java
// 工厂注册中心
public class FactoryRegistry {
    private static final Map<String, Factory<?, ?>> registry = new ConcurrentHashMap<>();
    
    public static <T, R> void register(String name, Factory<T, R> factory) {
        registry.put(name, factory);
    }
    
    @SuppressWarnings("unchecked")
    public static <T, R> Factory<T, R> getFactory(String name) {
        return (Factory<T, R>) registry.get(name);
    }
    
    public static <T, R> R create(String factoryName, T param) {
        Factory<T, R> factory = getFactory(factoryName);
        if (factory == null) {
            throw new IllegalArgumentException("Factory not found: " + factoryName);
        }
        return factory.create(param);
    }
}

// 注册工厂
FactoryRegistry.register("product", new SimpleFactory<>(name -> new Product(name, "registry")));
FactoryRegistry.register("connection", new DatabaseConnectionFactory("jdbc:mysql://localhost:3306", "root", "password"));

// 使用注册中心
Product registryProduct = FactoryRegistry.create("product", "RegistryProduct");
Connection registryConnection = FactoryRegistry.create("connection", "testdb");
```

##### 工厂装饰器
```java
// 工厂装饰器
public class LoggingFactoryDecorator<T, R> implements Factory<T, R> {
    private final Factory<T, R> factory;
    private final Logger logger = LoggerFactory.getLogger(LoggingFactoryDecorator.class);
    
    public LoggingFactoryDecorator(Factory<T, R> factory) {
        this.factory = factory;
    }
    
    @Override
    public R create(T param) {
        logger.info("Creating object using factory: {} with param: {}", factory.getClass().getSimpleName(), param);
        long startTime = System.currentTimeMillis();
        
        try {
            R result = factory.create(param);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Object created successfully in {} ms", duration);
            return result;
        } catch (Exception e) {
            logger.error("Failed to create object", e);
            throw e;
        }
    }
}

// 使用工厂装饰器
Factory<String, Product> originalFactory = new SimpleFactory<>(name -> new Product(name, "decorated"));
Factory<String, Product> loggingFactory = new LoggingFactoryDecorator<>(originalFactory);
Product decoratedProduct = loggingFactory.create("LoggedProduct");
```

#### 实际应用场景

##### 服务层工厂
```java
// 服务接口
public interface UserService {
    User getUserById(Long id);
    List<User> getAllUsers();
    void saveUser(User user);
}

// 服务实现
public class UserServiceImpl implements UserService {
    // 实现细节
}

// 服务工厂
public class ServiceFactory implements Factory<String, UserService> {
    @Override
    public UserService create(String serviceType) {
        switch (serviceType) {
            case "default":
                return new UserServiceImpl();
            case "cached":
                return new CachedUserService(new UserServiceImpl());
            case "transactional":
                return new TransactionalUserService(new UserServiceImpl());
            default:
                throw new IllegalArgumentException("Unknown service type: " + serviceType);
        }
    }
}

// 使用服务工厂
Factory<String, UserService> serviceFactory = new ServiceFactory();
UserService defaultService = serviceFactory.create("default");
UserService cachedService = serviceFactory.create("cached");
```

##### 配置对象工厂
```java
// 配置对象
public class AppConfig {
    private String appName;
    private int maxConnections;
    private boolean debugMode;
    
    // getters and setters
}

// 配置工厂
public class ConfigFactory implements Factory<Map<String, Object>, AppConfig> {
    @Override
    public AppConfig create(Map<String, Object> configMap) {
        AppConfig config = new AppConfig();
        config.setAppName((String) configMap.getOrDefault("appName", "DefaultApp"));
        config.setMaxConnections((Integer) configMap.getOrDefault("maxConnections", 10));
        config.setDebugMode((Boolean) configMap.getOrDefault("debugMode", false));
        return config;
    }
}

// 使用配置工厂
Factory<Map<String, Object>, AppConfig> configFactory = new ConfigFactory();
Map<String, Object> configData = new HashMap<>();
configData.put("appName", "MyApp");
configData.put("maxConnections", 20);
configData.put("debugMode", true);

AppConfig appConfig = configFactory.create(configData);
```

## 🚀 性能优化和最佳实践

### 性能考虑

1. **单例模式性能**
   - 双重检查锁定：适合延迟初始化场景，第一次获取时有轻微性能开销
   - 饿汉式：启动时初始化，运行时性能最佳
   - 枚举单例：JVM保证线程安全，性能最优

2. **策略模式性能**
   - 策略池使用ConcurrentHashMap，查找复杂度O(1)
   - 建议策略数量控制在合理范围内（<100个）
   - 频繁创建的策略考虑使用对象池

3. **状态机性能**
   - 状态转换是轻量级操作，主要开销在业务逻辑
   - 建议缓存频繁使用的事件处理器
   - 避免在状态转换中执行耗时操作

4. **过滤器链性能**
   - 索引过滤器链：适合过滤器数量较少场景
   - 迭代器过滤器链：适合需要动态增删过滤器的场景
   - 建议过滤器保持轻量级，避免阻塞操作

5. **责任链性能**
   - 平均查找复杂度O(n)，建议链长度控制在10个节点以内
   - 支持提前终止，优化常见情况的性能

6. **装饰器模式性能**
   - 简单装饰器：适合单个装饰逻辑，性能开销最小
   - 链式装饰器：装饰器数量影响性能，建议控制在5个以内
   - 装饰器构建器：构建时一次性应用所有装饰器，适合批量处理
   - 避免在装饰器中执行耗时操作，保持装饰逻辑轻量级

7. **工厂模式性能**
   - 简单工厂：创建对象开销最小，适合简单对象创建
   - 抽象工厂：工厂查找复杂度O(1)，但注册/注销有性能开销
   - 对象池工厂：适合频繁创建销毁的对象，池大小需要合理配置
   - 建议工厂数量控制在合理范围内（<50个），避免内存占用过大

### 线程安全

所有设计模式实现都考虑了线程安全性：

- **单例模式**：使用volatile和synchronized确保线程安全
- **策略模式**：ConcurrentHashMap保证并发访问安全
- **状态机**：状态转换过程是原子操作
- **过滤器链**：支持并发执行，无共享状态
- **责任链**：线程安全的设计，支持多线程并发执行

### 内存管理

1. **避免内存泄漏**
   - 单例模式提供destroy()方法用于清理
   - 策略池支持动态注销策略
   - 状态机助手支持状态清理

2. **对象复用**
   - 推荐使用对象池管理频繁创建的对象
   - 状态上下文对象可复用，减少GC压力

## 🧪 测试指南

### 单元测试示例

#### 动作模式测试
```java
@Test
public void testAction() {
    Action<String, String> action = str -> str.toUpperCase();
    
    String result = action.doAction("hello");
    
    assertEquals("HELLO", result);
}

@Test
public void testActionComposition() {
    Action<String, String> trim = str -> str.trim();
    Action<String, String> upper = str -> str.toUpperCase();
    
    Action<String, String> composed = str -> upper.doAction(trim.doAction(str));
    
    assertEquals("HELLO", composed.doAction("  hello  "));
}
```

#### 单例模式测试
```java
@Test
public void testSingleton() {
    Singleton<DatabaseConnection> singleton = new DoubleCheckedSingleton<>(() -> 
        new DatabaseConnection("test"));
    
    DatabaseConnection conn1 = singleton.getInstance();
    DatabaseConnection conn2 = singleton.getInstance();
    
    assertSame(conn1, conn2);
    assertTrue(singleton.isInitialized());
}

@Test
public void testSingletonThreadSafety() throws InterruptedException {
    Singleton<AtomicInteger> singleton = new DoubleCheckedSingleton<>(() -> 
        new AtomicInteger(0));
    
    int threadCount = 100;
    CountDownLatch latch = new CountDownLatch(threadCount);
    Set<AtomicInteger> instances = ConcurrentHashMap.newKeySet();
    
    for (int i = 0; i < threadCount; i++) {
        new Thread(() -> {
            instances.add(singleton.getInstance());
            latch.countDown();
        }).start();
    }
    
    latch.await();
    assertEquals(1, instances.size());
}
```

#### 策略模式测试
```java
@Test
public void testStrategyPool() {
    StrategyPool<PaymentMethod, String> pool = new StrategyPool<>();
    
    Strategy<PaymentMethod, String> creditCardStrategy = method -> "Credit Card Payment";
    creditCardStrategy.strategyKey = PaymentMethod.CREDIT_CARD;
    
    pool.register(creditCardStrategy);
    
    Strategy<PaymentMethod, String> retrieved = pool.getStrategy(PaymentMethod.CREDIT_CARD);
    assertNotNull(retrieved);
    assertEquals("Credit Card Payment", retrieved.doAction(PaymentMethod.CREDIT_CARD));
}
```

#### 装饰器模式测试
```java
@Test
public void testSimpleDecorator() {
    TextComponent original = new TextComponent("Hello World");
    Decorator<TextComponent> decorator = new SimpleDecorator<>(original, 
        component -> new TextComponent(component.getText().toUpperCase()));
    
    TextComponent decorated = decorator.getComponent();
    assertEquals("HELLO WORLD", decorated.getText());
    assertNotSame(original, decorated);
}

@Test
public void testChainDecorator() {
    TextComponent original = new TextComponent("Hello World");
    ChainDecorator<TextComponent> chainDecorator = new ChainDecorator<>(original)
        .addDecorator(component -> new TextComponent(component.getText().toUpperCase()))
        .addDecorator(component -> new TextComponent("*** " + component.getText() + " ***"));
    
    TextComponent decorated = chainDecorator.getDecoratedComponent();
    assertEquals("*** HELLO WORLD ***", decorated.getText());
    assertEquals(2, chainDecorator.getDecoratorCount());
}

@Test
public void testDecoratorBuilder() {
    TextComponent original = new TextComponent("Hello World");
    DecoratorBuilder<TextComponent> builder = new DecoratorBuilderImpl<>(original)
        .add(component -> new TextComponent(component.getText().toUpperCase()))
        .add(component -> new TextComponent(">>> " + component.getText() + " <<<"));
    
    TextComponent decorated = builder.build();
    assertEquals(">>> HELLO WORLD <<<", decorated.getText());
}
```

#### 工厂模式测试
```java
@Test
public void testSimpleFactory() {
    Factory<String, Product> factory = new SimpleFactory<>(name -> new Product(name, "test"));
    
    Product product = factory.create("TestProduct");
    assertEquals("TestProduct", product.getName());
    assertEquals("test", product.getType());
}

@Test
public void testAbstractFactory() {
    AbstractFactory<ProductFactoryKey, Product> factory = new AbstractFactoryImpl<>();
    
    factory.registerFactory("electronic", key -> new Product(key.key(), "electronic"));
    factory.registerFactory("clothing", key -> new Product(key.key(), "clothing"));
    
    Product electronic = factory.create(new ProductFactoryKey("electronic"));
    Product clothing = factory.create(new ProductFactoryKey("clothing"));
    
    assertEquals("electronic", electronic.getType());
    assertEquals("clothing", clothing.getType());
    assertEquals(2, factory.size());
}

@Test
public void testPooledFactory() {
    PooledFactory<String, Product> factory = new PooledFactoryImpl<>(
        name -> new Product(name, "pooled"), 3);
    
    Product product1 = factory.borrowObject("Product1");
    Product product2 = factory.borrowObject("Product2");
    
    factory.returnObject(product1);
    factory.returnObject(product2);
    
    assertEquals(2, ((PooledFactoryImpl<String, Product>) factory).getPoolSize());
    
    Product product3 = factory.borrowObject("Product3");
    assertEquals(1, ((PooledFactoryImpl<String, Product>) factory).getPoolSize());
}

### 集成测试

#### Spring Boot集成测试
```java
@SpringBootTest
@AutoConfigureMockMvc
public class StateMachineIntegrationTest {
    
    @Autowired
    private EventDrivenStateMachine<Order> stateMachine;
    
    @Test
    public void testOrderStateTransition() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderState.PENDING_PAYMENT.state());
        
        // 执行状态转换
        stateMachine.execute(OrderEvent.PAYMENT_COMPLETED, order);
        
        // 验证状态
        assertEquals(OrderState.PAID.state(), order.getStatus());
    }
}
```

## 📚 相关资源

### 官方文档
- [Java 21 官方文档](https://docs.oracle.com/en/java/javase/21/)
- [Spring Boot 3.2 文档](https://docs.spring.io/spring-boot/docs/3.2.x/reference/)
- [Maven 官方文档](https://maven.apache.org/guides/)

### 设计模式参考
- [设计模式：可复用面向对象软件的基础](https://book.douban.com/subject/1052241/)
- [Head First 设计模式](https://book.douban.com/subject/1400656/)
- [Java 设计模式](https://refactoringguru.cn/design-patterns/java)

### 开源项目
- [Spring Framework](https://github.com/spring-projects/spring-framework)
- [Hutool](https://github.com/dromara/hutool)
- [Lombok](https://github.com/projectlombok/lombok)

## 🤝 贡献指南

我们欢迎社区贡献！如果您想为cdm-core模块贡献代码，请遵循以下步骤：

### 开发环境设置
1. Fork 项目仓库
2. 克隆到本地：`git clone https://github.com/your-username/cdm.git`
3. 导入到IDE（推荐IntelliJ IDEA）
4. 运行测试确保环境正常：`mvn test`

### 代码规范
- 遵循Google Java Style Guide
- 所有公共API必须有完整的JavaDoc注释
- 单元测试覆盖率不低于80%
- 提交前运行代码格式化工具

### 提交规范
- 提交信息使用英文，格式：`[模块] 简短描述`
- 例如：`[cdm-core] Add new singleton implementation`
- 详细描述变更内容和原因

### Pull Request 流程
1. 创建特性分支：`git checkout -b feature/new-feature`
2. 提交更改：`git commit -m '[cdm-core] Add new feature'`
3. 推送到远程：`git push origin feature/new-feature`
4. 创建Pull Request，描述变更内容
5. 等待代码审查和合并

## 📄 许可证

cdm-core模块基于 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) 许可证开源。

## 👥 维护者

- **主要维护者**: Rao
- **创建时间**: 2021年
- **最后更新**: 2025年

## 📞 联系方式

- **项目主页**: [https://github.com/MuJianxuan/cdm](https://github.com/MuJianxuan/cdm)
- **问题反馈**: [GitHub Issues](https://github.com/MuJianxuan/cdm/issues)
- **讨论区**: [GitHub Discussions](https://github.com/MuJianxuan/cdm/discussions)

---

**⭐ 如果cdm-core对您有帮助，请给主项目一个Star！**

<div align="center">
  
[![Star History Chart](https://api.star-history.com/svg?repos=MuJianxuan/cdm&type=Date)](https://star-history.com/#MuJianxuan/cdm&Date)

</div>
