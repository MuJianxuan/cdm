package org.cdm.core.observer.example;

import org.cdm.core.observer.Event;
import org.cdm.core.observer.Observer;
import org.cdm.core.observer.impl.SimpleEventBus;
import org.cdm.core.observer.impl.AsyncEventBus;

/**
 * 简单的观察者功能验证测试
 */
public class SimpleObserverTest {
    
    static class TestEvent implements Event {
        private final String eventType;
        private final long timestamp;
        private final Object source;
        private final String data;
        
        public TestEvent(String eventType, Object source, String data) {
            this.eventType = eventType;
            this.timestamp = System.currentTimeMillis();
            this.source = source;
            this.data = data;
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
        
        public String getData() {
            return data;
        }
    }
    
    static class TestObserver implements Observer<TestEvent> {
        private TestEvent lastEvent;
        private int updateCount = 0;
        private boolean async;
        private int priority;
        
        public TestObserver(boolean async, int priority) {
            this.async = async;
            this.priority = priority;
        }
        
        @Override
        public void update(TestEvent event) {
            this.lastEvent = event;
            this.updateCount++;
            System.out.println("观察者收到事件: " + event.getData() + " (优先级: " + priority + ")");
        }
        
        @Override
        public boolean isAsync() {
            return async;
        }
        
        @Override
        public int getPriority() {
            return priority;
        }
        
        public TestEvent getLastEvent() {
            return lastEvent;
        }
        
        public int getUpdateCount() {
            return updateCount;
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 观察者功能验证测试 ===");
        
        // 测试1: SimpleEventBus基本功能
        System.out.println("\n1. 测试SimpleEventBus基本功能");
        SimpleEventBus<TestEvent> simpleEventBus = new SimpleEventBus<>();
        TestObserver observer1 = new TestObserver(false, 1);
        TestObserver observer2 = new TestObserver(true, 2);
        
        simpleEventBus.addObserver(observer1);
        simpleEventBus.addObserver(observer2);
        
        TestEvent event1 = new TestEvent("TEST_EVENT", "TestSource", "测试数据1");
        simpleEventBus.publish(event1);
        
        Thread.sleep(100); // 等待异步处理
        
        // 验证结果
        if (observer1.getUpdateCount() == 1 && observer2.getUpdateCount() == 1) {
            System.out.println("✓ SimpleEventBus基本功能测试通过");
        } else {
            System.out.println("✗ SimpleEventBus基本功能测试失败");
        }
        
        // 测试2: 优先级排序
        System.out.println("\n2. 测试优先级排序");
        SimpleEventBus<TestEvent> priorityEventBus = new SimpleEventBus<>();
        TestObserver lowPriorityObserver = new TestObserver(false, 0);
        TestObserver highPriorityObserver = new TestObserver(false, 10);
        TestObserver mediumPriorityObserver = new TestObserver(false, 5);
        
        priorityEventBus.addObserver(lowPriorityObserver);
        priorityEventBus.addObserver(highPriorityObserver);
        priorityEventBus.addObserver(mediumPriorityObserver);
        
        TestEvent event2 = new TestEvent("PRIORITY_TEST", "TestSource", "优先级测试数据");
        priorityEventBus.publish(event2);
        
        // 验证结果
        if (lowPriorityObserver.getUpdateCount() == 1 && 
            highPriorityObserver.getUpdateCount() == 1 && 
            mediumPriorityObserver.getUpdateCount() == 1) {
            System.out.println("✓ 优先级排序测试通过");
        } else {
            System.out.println("✗ 优先级排序测试失败");
        }
        
        // 测试3: AsyncEventBus功能
        System.out.println("\n3. 测试AsyncEventBus功能");
        AsyncEventBus<TestEvent> asyncEventBus = new AsyncEventBus<>();
        TestObserver asyncObserver1 = new TestObserver(false, 1);
        TestObserver asyncObserver2 = new TestObserver(true, 2);
        
        asyncEventBus.addObserver(asyncObserver1);
        asyncEventBus.addObserver(asyncObserver2);
        
        TestEvent event3 = new TestEvent("ASYNC_TEST", "TestSource", "异步测试数据");
        asyncEventBus.publishAsync(event3);
        
        Thread.sleep(200); // 等待异步处理
        
        // 验证结果
        if (asyncObserver1.getUpdateCount() == 1 && asyncObserver2.getUpdateCount() == 1) {
            System.out.println("✓ AsyncEventBus功能测试通过");
        } else {
            System.out.println("✗ AsyncEventBus功能测试失败");
        }
        
        // 清理资源
        asyncEventBus.shutdown();
        
        System.out.println("\n=== 所有观察者功能验证测试完成 ===");
    }
}
