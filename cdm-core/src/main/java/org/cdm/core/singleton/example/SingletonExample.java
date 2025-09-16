package org.cdm.core.singleton.example;

import org.cdm.core.singleton.*;

/**
 * 单例模式使用示例
 * @author Rao
 * @Date 2025-06-17
 */
public class SingletonExample {

    // 示例服务类
    public static class DatabaseService {
        private final String name;
        
        public DatabaseService(String name) {
            this.name = name;
            System.out.println("DatabaseService '" + name + "' created");
        }
        
        public void connect() {
            System.out.println("Connecting to database: " + name);
        }
        
        public String getName() {
            return name;
        }
    }

    // 饿汉式单例示例
    public static class EagerDatabaseSingleton extends EagerSingleton<DatabaseService> {
        
        public static final EagerDatabaseSingleton INSTANCE = new EagerDatabaseSingleton();
        
        private EagerDatabaseSingleton() {}
        
        @Override
        protected DatabaseService createInstance() {
            return new DatabaseService("EagerDatabase");
        }
    }

    // 双重检查锁单例示例
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

    // 枚举单例示例
    public enum EnumDatabaseSingleton implements Singleton<DatabaseService> {
        INSTANCE;
        
        private final DatabaseService service;
        
        EnumDatabaseSingleton() {
            this.service = new DatabaseService("EnumDatabase");
        }
        
        @Override
        public DatabaseService getInstance() {
            return service;
        }
        
        @Override
        public boolean isInitialized() {
            return true;
        }
        
        @Override
        public void destroy() {
            throw new UnsupportedOperationException("Enum singleton does not support destroy");
        }
    }

    public static void main(String[] args) {
        System.out.println("=== 单例模式使用示例 ===\n");
        
        // 1. 饿汉式单例
        System.out.println("1. 饿汉式单例:");
        DatabaseService eager1 = EagerDatabaseSingleton.INSTANCE.getInstance();
        DatabaseService eager2 = EagerDatabaseSingleton.INSTANCE.getInstance();
        System.out.println("Same instance: " + (eager1 == eager2));
        eager1.connect();
        System.out.println();
        
        // 2. 双重检查锁单例
        System.out.println("2. 双重检查锁单例:");
        DatabaseService lazy1 = LazyDatabaseSingleton.getSingleton().getInstance();
        DatabaseService lazy2 = LazyDatabaseSingleton.getSingleton().getInstance();
        System.out.println("Same instance: " + (lazy1 == lazy2));
        lazy1.connect();
        System.out.println();
        
        // 3. 枚举单例
        System.out.println("3. 枚举单例:");
        DatabaseService enum1 = EnumDatabaseSingleton.INSTANCE.getInstance();
        DatabaseService enum2 = EnumDatabaseSingleton.INSTANCE.getInstance();
        System.out.println("Same instance: " + (enum1 == enum2));
        enum1.connect();
        System.out.println();
        
        // 4. 使用SingletonFactory
        System.out.println("4. 使用SingletonFactory:");
        Singleton<DatabaseService> factorySingleton = SingletonFactory.createEagerSingleton(
            () -> new DatabaseService("FactoryDatabase")
        );
        SingletonFactory.register("factoryDB", factorySingleton);
        
        DatabaseService factory1 = SingletonFactory.getInstance("factoryDB");
        DatabaseService factory2 = SingletonFactory.getInstance("factoryDB");
        System.out.println("Same instance: " + (factory1 == factory2));
        factory1.connect();
        System.out.println("Contains 'factoryDB': " + SingletonFactory.contains("factoryDB"));
        
        // 5. 测试销毁功能
        System.out.println("\n5. 测试销毁功能:");
        System.out.println("Before destroy - Initialized: " + factorySingleton.isInitialized());
        try {
            factorySingleton.destroy();
            System.out.println("After destroy - Initialized: " + factorySingleton.isInitialized());
        } catch (Exception e) {
            System.out.println("Destroy exception: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
        
        // 测试懒加载单例的销毁
        System.out.println("\nTesting lazy singleton destroy:");
        Singleton<DatabaseService> lazySingleton = SingletonFactory.createLazySingleton(
            () -> new DatabaseService("LazyFactoryDatabase")
        );
        SingletonFactory.register("lazyDB", lazySingleton);
        
        DatabaseService lazyFactory1 = SingletonFactory.getInstance("lazyDB");
        System.out.println("Before destroy - Initialized: " + lazySingleton.isInitialized());
        lazySingleton.destroy();
        System.out.println("After destroy - Initialized: " + lazySingleton.isInitialized());
    }
}