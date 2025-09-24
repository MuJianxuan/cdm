package org.cdm.core.observer.example;

import org.cdm.core.observer.Event;
import org.cdm.core.observer.Observer;
import org.cdm.core.observer.impl.AsyncEventBus;
import org.cdm.core.observer.impl.SimpleEventBus;

/**
 * 观察者模式使用示例
 */
public class ObserverExample {
    
    // 示例事件类
    public static class UserEvent implements Event {
        private final String eventType;
        private final String username;
        private final long timestamp;
        private final Object source;
        
        public UserEvent(String eventType, String username, Object source) {
            this.eventType = eventType;
            this.username = username;
            this.timestamp = System.currentTimeMillis();
            this.source = source;
        }
        
        public String getUsername() {
            return username;
        }
        
        @Override
        public String getEventType() {
            return eventType;
        }
        
        @Override
        public long getTimestamp() {
            return timestamp;
        }
        
        @Override
        public Object getSource() {
            return source;
        }
        
        @Override
        public String toString() {
            return "UserEvent{type='" + eventType + "', username='" + username + "', timestamp=" + timestamp + "}";
        }
    }
    
    // 同步观察者
    public static class SyncUserObserver implements Observer<UserEvent> {
        private final String name;
        
        public SyncUserObserver(String name) {
            this.name = name;
        }
        
        @Override
        public void update(UserEvent event) {
            System.out.println("[" + name + " - 同步] 收到事件: " + event);
            // 模拟处理时间
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        @Override
        public int getPriority() {
            return 1;
        }
    }
    
    // 异步观察者
    public static class AsyncUserObserver implements Observer<UserEvent> {
        private final String name;
        
        public AsyncUserObserver(String name) {
            this.name = name;
        }
        
        @Override
        public void update(UserEvent event) {
            System.out.println("[" + name + " - 异步] 收到事件: " + event);
        }
        
        @Override
        public boolean isAsync() {
            return true;
        }
        
        @Override
        public int getPriority() {
            return 2; // 更高优先级
        }
    }
    
    // 低优先级观察者
    public static class LowPriorityObserver implements Observer<UserEvent> {
        private final String name;
        
        public LowPriorityObserver(String name) {
            this.name = name;
        }
        
        @Override
        public void update(UserEvent event) {
            System.out.println("[" + name + " - 低优先级] 收到事件: " + event);
        }
        
        @Override
        public int getPriority() {
            return 0;
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        // 1. 简单事件总线示例
        System.out.println("=== 简单事件总线示例 ===");
        SimpleEventBus<UserEvent> simpleEventBus = new SimpleEventBus<>();
        
        // 注册观察者
        simpleEventBus.addObserver(new SyncUserObserver("观察者1"));
        simpleEventBus.addObserver(new AsyncUserObserver("观察者2"));
        simpleEventBus.addObserver(new LowPriorityObserver("观察者3"));
        
        // 发布事件
        UserEvent loginEvent = new UserEvent("USER_LOGIN", "张三", "UserService");
        System.out.println("发布登录事件: " + loginEvent);
        simpleEventBus.publish(loginEvent);
        
        Thread.sleep(500); // 等待异步处理完成
        
        // 2. 异步事件总线示例
        System.out.println("\n=== 异步事件总线示例 ===");
        AsyncEventBus<UserEvent> asyncEventBus = new AsyncEventBus<>();
        
        // 注册观察者
        asyncEventBus.register(UserEvent.class, new SyncUserObserver("异步观察者1"));
        asyncEventBus.register(UserEvent.class, new AsyncUserObserver("异步观察者2"));
        
        // 异步发布事件
        UserEvent registerEvent = new UserEvent("USER_REGISTER", "李四", "UserService");
        System.out.println("异步发布注册事件: " + registerEvent);
        asyncEventBus.publishAsync(registerEvent);
        
        Thread.sleep(500); // 等待异步处理完成
        
        // 3. 特定事件类型注册示例
        System.out.println("\n=== 特定事件类型注册示例 ===");
        SimpleEventBus<UserEvent> typedEventBus = new SimpleEventBus<>();
        
        // 注册全局观察者
        typedEventBus.addObserver(new SyncUserObserver("全局观察者"));
        
        // 注册特定事件类型观察者
        typedEventBus.register(UserEvent.class, new AsyncUserObserver("用户事件观察者"));
        
        UserEvent logoutEvent = new UserEvent("USER_LOGOUT", "王五", "UserService");
        System.out.println("发布登出事件: " + logoutEvent);
        typedEventBus.publish(logoutEvent);
        
        Thread.sleep(500); // 等待异步处理完成
        
        // 清理资源
        asyncEventBus.shutdown();
        
        System.out.println("\n=== 观察者模式示例完成 ===");
    }
}
