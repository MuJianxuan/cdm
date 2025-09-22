# CDM-Core - 核心设计模式实现

[![Java 21](https://img.shields.io/badge/Java-21-blue.svg)](https://openjdk.java.net/projects/jdk/21/)
[![Maven](https://img.shields.io/badge/Maven-3.x-orange.svg)](https://maven.apache.org/)
[![Javadoc](https://img.shields.io/badge/Javadoc-Online-blue.svg)](https://mujianxuan.github.io/cdm/apidocs/)

## 📋 模块简介

cdm-core是CDM项目的核心模块，包含了6种经典设计模式的现代化Java实现。模块采用Java 21最新特性，提供类型安全、线程安全、高性能的设计模式抽象，帮助开发者在企业级应用中优雅地解决常见的设计问题。

### ✨ 设计模式概览

| 设计模式 | 接口/类 | 主要功能 | 适用场景 |
|---------|---------|----------|----------|
| **动作模式** | `Action<T,R>` | 函数式参数处理 | 数据转换、业务处理 |
| **单例模式** | `Singleton<T>` | 实例生命周期管理 | 配置管理、连接池 |
| **策略模式** | `Strategy<T,R>` | 动态算法选择 | 支付系统、排序算法 |
| **事件驱动状态机** | `EventDrivenStateMachine<T>` | 复杂状态管理 | 订单流程、工作流 |
| **过滤器链** | `FilterChain<T>` | 请求链式处理 | 权限验证、日志记录 |
| **责任链** | `ResponsibilityChain<T>` | 请求分发处理 | 审批流程、异常处理 |

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
