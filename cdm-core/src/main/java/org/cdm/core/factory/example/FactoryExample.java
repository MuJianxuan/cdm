package org.cdm.core.factory.example;

import org.cdm.core.factory.Factory;
import org.cdm.core.factory.FactoryKey;
import org.cdm.core.factory.PooledFactory;
import org.cdm.core.factory.impl.AbstractFactoryImpl;
import org.cdm.core.factory.impl.PooledFactoryImpl;
import org.cdm.core.factory.impl.SimpleFactory;

/**
 * 工厂模式使用示例
 */
public class FactoryExample {
    
    // 示例产品类
    public static class Product {
        private final String name;
        private final String type;
        
        public Product(String name, String type) {
            this.name = name;
            this.type = type;
        }
        
        public String getName() {
            return name;
        }
        
        public String getType() {
            return type;
        }
        
        @Override
        public String toString() {
            return "Product{name='" + name + "', type='" + type + "'}";
        }
    }
    
    // 工厂键实现
    public static class ProductFactoryKey implements FactoryKey {
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
    
    public static void main(String[] args) {
        // 1. 简单工厂示例
        System.out.println("=== 简单工厂示例 ===");
        Factory<String, Product> simpleFactory = new SimpleFactory<>(name -> new Product(name, "simple"));
        Product product1 = simpleFactory.create("SimpleProduct");
        System.out.println("创建产品: " + product1);
        
        // 2. 抽象工厂示例
        System.out.println("\n=== 抽象工厂示例 ===");
        AbstractFactoryImpl<ProductFactoryKey, Product> abstractFactory = new AbstractFactoryImpl<>();
        
        // 注册具体工厂 - 使用适配器模式解决类型不匹配问题
        abstractFactory.registerFactory("electronic", new Factory<ProductFactoryKey, Product>() {
            @Override
            public Product create(ProductFactoryKey key) {
                return new Product(key.key(), "electronic");
            }
        });
        
        abstractFactory.registerFactory("clothing", new Factory<ProductFactoryKey, Product>() {
            @Override
            public Product create(ProductFactoryKey key) {
                return new Product(key.key(), "clothing");
            }
        });
        
        // 通过键和参数创建对象
        Product electronicProduct = abstractFactory.create(new ProductFactoryKey("electronic"), "iPhone");
        Product clothingProduct = abstractFactory.create(new ProductFactoryKey("clothing"), "T-Shirt");
        
        System.out.println("电子产品: " + electronicProduct);
        System.out.println("服装产品: " + clothingProduct);
        
        // 通过键创建对象
        Product electronicProduct2 = abstractFactory.create(new ProductFactoryKey("electronic"));
        System.out.println("通过键创建的电子产品: " + electronicProduct2);
        
        // 工厂管理功能示例
        System.out.println("\n=== 工厂管理功能示例 ===");
        System.out.println("已注册工厂数量: " + abstractFactory.size());
        System.out.println("已注册的工厂键: " + abstractFactory.getRegisteredKeys());
        System.out.println("是否包含'electronic'工厂: " + abstractFactory.containsFactory(new ProductFactoryKey("electronic")));
        
        // 获取具体工厂
        Factory<ProductFactoryKey, Product> factory = abstractFactory.getFactory(new ProductFactoryKey("electronic"));
        System.out.println("获取的工厂实例: " + factory);
        
        // 注销工厂
        abstractFactory.unregisterFactory("electronic");
        System.out.println("注销'electronic'工厂后数量: " + abstractFactory.size());
        System.out.println("是否还包含'electronic'工厂: " + abstractFactory.containsFactory(new ProductFactoryKey("electronic")));
        
        // 重新注册
        abstractFactory.registerFactory("electronic", new Factory<ProductFactoryKey, Product>() {
            @Override
            public Product create(ProductFactoryKey key) {
                return new Product(key.key(), "electronic");
            }
        });
        System.out.println("重新注册后数量: " + abstractFactory.size());
        
        // 3. 对象池工厂示例
        System.out.println("\n=== 对象池工厂示例 ===");
        PooledFactory<String, Product> pooledFactory = new PooledFactoryImpl<>(
            name -> new Product(name, "pooled"),
            5 // 最大池大小为5
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
    }
}
