package org.cdm.core.proxy.example;

import org.cdm.core.proxy.ProxyFactory;
import org.cdm.core.proxy.ProxyInterceptor;
import org.cdm.core.proxy.ProxyInvocation;
import org.cdm.core.proxy.impl.DynamicProxyFactory;
import org.cdm.core.proxy.impl.LoggingInterceptor;

import java.util.ArrayList;
import java.util.List;

/**
 * 代理模式使用示例
 */
public class ProxyExample {
    
    // 示例服务接口
    public interface UserService {
        String getUserById(Long id);
        boolean saveUser(String username, String email);
        void deleteUser(Long id);
        List<String> getAllUsers();
    }
    
    // 用户服务实现
    public static class UserServiceImpl implements UserService {
        @Override
        public String getUserById(Long id) {
            System.out.println("Executing getUserById with id: " + id);
            return "User-" + id;
        }
        
        @Override
        public boolean saveUser(String username, String email) {
            System.out.println("Executing saveUser with username: " + username + ", email: " + email);
            return true;
        }
        
        @Override
        public void deleteUser(Long id) {
            System.out.println("Executing deleteUser with id: " + id);
        }
        
        @Override
        public List<String> getAllUsers() {
            System.out.println("Executing getAllUsers");
            List<String> users = new ArrayList<>();
            users.add("User-1");
            users.add("User-2");
            users.add("User-3");
            return users;
        }
    }
    
    // 性能监控拦截器
    public static class PerformanceInterceptor<T> implements ProxyInterceptor<T> {
        @Override
        public Object intercept(T target, String methodName, Object[] args, ProxyInvocation invocation) throws Throwable {
            long startTime = System.currentTimeMillis();
            System.out.println("[Performance] Method " + methodName + " started at " + startTime);
            
            try {
                Object result = invocation.proceed();
                long endTime = System.currentTimeMillis();
                System.out.println("[Performance] Method " + methodName + " completed in " + (endTime - startTime) + "ms");
                return result;
            } catch (Exception e) {
                long endTime = System.currentTimeMillis();
                System.out.println("[Performance] Method " + methodName + " failed after " + (endTime - startTime) + "ms");
                throw e;
            }
        }
        
        @Override
        public int getOrder() {
            return 0; // 最高优先级
        }
    }
    
    // 安全检查拦截器
    public static class SecurityInterceptor<T> implements ProxyInterceptor<T> {
        @Override
        public Object intercept(T target, String methodName, Object[] args, ProxyInvocation invocation) throws Throwable {
            System.out.println("[Security] Checking permissions for method: " + methodName);
            
            // 模拟安全检查
            if (methodName.equals("deleteUser")) {
                System.out.println("[Security] Delete operation requires admin privileges");
            }
            
            // 继续执行
            return invocation.proceed();
        }
        
        @Override
        public int getOrder() {
            return 2; // 中等优先级
        }
    }
    
    // 缓存拦截器
    public static class CacheInterceptor<T> implements ProxyInterceptor<T> {
        private final java.util.Map<String, Object> cache = new java.util.HashMap<>();
        
        @Override
        public Object intercept(T target, String methodName, Object[] args, ProxyInvocation invocation) throws Throwable {
            // 为简单起见，只缓存 getUserById 方法
            if (methodName.equals("getUserById") && args.length == 1) {
                String cacheKey = methodName + "-" + args[0];
                if (cache.containsKey(cacheKey)) {
                    System.out.println("[Cache] Returning cached result for: " + cacheKey);
                    return cache.get(cacheKey);
                }
                
                Object result = invocation.proceed();
                cache.put(cacheKey, result);
                System.out.println("[Cache] Cached result for: " + cacheKey);
                return result;
            }
            
            return invocation.proceed();
        }
        
        @Override
        public int getOrder() {
            return 3; // 较低优先级
        }
    }
    
    public static void main(String[] args) {
        // 1. 动态代理示例
        System.out.println("=== 动态代理示例 ===");
        UserService userService = new UserServiceImpl();
        
        // 创建拦截器列表
        List<ProxyInterceptor<UserService>> interceptors = new ArrayList<>();
        interceptors.add(new PerformanceInterceptor<>());
        interceptors.add(new LoggingInterceptor<>("UserService"));
        interceptors.add(new SecurityInterceptor<>());
        interceptors.add(new CacheInterceptor<>());
        
        // 创建代理工厂并生成代理对象
        ProxyFactory<UserService> proxyFactory = new DynamicProxyFactory<>();
        UserService proxyUserService = proxyFactory.createProxy(userService, interceptors);
        
        // 测试代理方法调用
        System.out.println("\n--- 调用 getUserById ---");
        String user1 = proxyUserService.getUserById(1L);
        System.out.println("Result: " + user1);
        
        System.out.println("\n--- 再次调用 getUserById (应该从缓存获取) ---");
        String user1Cached = proxyUserService.getUserById(1L);
        System.out.println("Result: " + user1Cached);
        
        System.out.println("\n--- 调用 saveUser ---");
        boolean saved = proxyUserService.saveUser("John", "john@example.com");
        System.out.println("Save result: " + saved);
        
        System.out.println("\n--- 调用 deleteUser ---");
        proxyUserService.deleteUser(1L);
        
        System.out.println("\n--- 调用 getAllUsers ---");
        List<String> allUsers = proxyUserService.getAllUsers();
        System.out.println("All users: " + allUsers);
        
        // 2. 多层代理示例
        System.out.println("\n\n=== 多层代理示例 ===");
        UserService anotherUserService = new UserServiceImpl();
        
        // 创建第一层代理（只有日志）
        List<ProxyInterceptor<UserService>> loggingInterceptors = new ArrayList<>();
        loggingInterceptors.add(new LoggingInterceptor<>("Layer1"));
        UserService firstProxy = proxyFactory.createProxy(anotherUserService, loggingInterceptors);
        
        // 创建第二层代理（添加性能监控）
        List<ProxyInterceptor<UserService>> perfInterceptors = new ArrayList<>();
        perfInterceptors.add(new PerformanceInterceptor<>());
        UserService secondProxy = proxyFactory.createProxy(firstProxy, perfInterceptors);
        
        System.out.println("\n--- 多层代理调用 ---");
        String multiLayerResult = secondProxy.getUserById(2L);
        System.out.println("Multi-layer proxy result: " + multiLayerResult);
        
        System.out.println("\n=== 代理模式示例完成 ===");
    }
}
