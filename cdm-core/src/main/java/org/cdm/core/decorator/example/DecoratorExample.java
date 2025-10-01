package org.cdm.core.decorator.example;

import org.cdm.core.decorator.Decorator;
import org.cdm.core.decorator.DecoratorBuilder;
import org.cdm.core.decorator.impl.ChainDecorator;
import org.cdm.core.decorator.impl.DecoratorBuilderImpl;
import org.cdm.core.decorator.impl.SimpleDecorator;

import java.util.function.Function;

/**
 * 装饰器模式使用示例
 * <p>
 * 演示了装饰器模式的各种使用方式，包括简单装饰、链式装饰、构建器模式装饰等。
 * 通过消息服务的示例展示了装饰器模式在实际应用中的灵活性和强大功能。
 * </p>
 *
 * @author CDM
 * @since 1.0.0
 */
public class DecoratorExample {
    
    /**
     * 消息服务接口
     * <p>
     * 定义了消息服务的基本功能，包括发送消息和获取服务名称。
     * 作为装饰器模式中的组件接口使用。
     * </p>
     */
    public interface MessageService {
        
        /**
         * 发送消息
         * <p>
         * 将指定的消息发送到目标，并返回处理后的结果。
         * </p>
         *
         * @param message 要发送的消息内容
         * @return 消息发送的结果
         */
        String sendMessage(String message);
        
        /**
         * 获取服务名称
         * <p>
         * 返回当前服务的名称标识。
         * </p>
         *
         * @return 服务名称
         */
        String getServiceName();
    }
    
    /**
     * 基础消息服务实现
     * <p>
     * 消息服务的最基础实现，提供基本的消息发送功能。
     * 作为装饰器模式中的具体组件使用。
     * </p>
     */
    public static class BasicMessageService implements MessageService {
        
        /** 服务名称 */
        private final String serviceName;
        
        /**
         * 构造基础消息服务实例
         *
         * @param serviceName 服务名称
         */
        public BasicMessageService(String serviceName) {
            this.serviceName = serviceName;
        }
        
        /**
         * 发送消息
         * <p>
         * 返回包含服务名称和消息内容的基础格式字符串。
         * </p>
         *
         * @param message 要发送的消息内容
         * @return 格式化的消息字符串
         */
        @Override
        public String sendMessage(String message) {
            return "Basic [" + serviceName + "]: " + message;
        }
        
        /**
         * 获取服务名称
         *
         * @return 基础服务名称
         */
        @Override
        public String getServiceName() {
            return serviceName;
        }
    }
    
    /**
     * 日志装饰器
     * <p>
     * 为消息服务添加日志记录功能，在消息发送前后输出日志信息。
     * 演示了装饰器模式中具体装饰器的实现方式。
     * </p>
     */
    public static class LoggingMessageService implements MessageService, Decorator<MessageService> {
        
        /** 被装饰的组件 */
        private final MessageService component;
        
        /**
         * 构造日志装饰器实例
         *
         * @param component 要被装饰的消息服务组件
         */
        public LoggingMessageService(MessageService component) {
            this.component = component;
        }
        
        /**
         * 发送消息（带日志记录）
         * <p>
         * 在发送消息前后输出日志信息，然后调用被装饰组件的发送方法。
         * </p>
         *
         * @param message 要发送的消息内容
         * @return 消息发送的结果
         */
        @Override
        public String sendMessage(String message) {
            System.out.println("LOG: Sending message - " + message);
            String result = component.sendMessage(message);
            System.out.println("LOG: Message sent - " + result);
            return result;
        }
        
        /**
         * 获取服务名称
         * <p>
         * 返回被装饰组件的服务名称，保持原有的服务标识。
         * </p>
         *
         * @return 被装饰组件的服务名称
         */
        @Override
        public String getServiceName() {
            return component.getServiceName();
        }
        
        /**
         * 获取被装饰的原始组件
         *
         * @return 被装饰的消息服务组件
         */
        @Override
        public MessageService getComponent() {
            return component;
        }
    }
    
    /**
     * 加密装饰器
     * <p>
     * 为消息服务添加加密功能，在发送消息前对消息内容进行加密处理。
     * 演示了装饰器模式中具体装饰器的实现方式。
     * </p>
     */
    public static class EncryptionMessageService implements MessageService, Decorator<MessageService> {
        
        /** 被装饰的组件 */
        private final MessageService component;
        
        /**
         * 构造加密装饰器实例
         *
         * @param component 要被装饰的消息服务组件
         */
        public EncryptionMessageService(MessageService component) {
            this.component = component;
        }
        
        /**
         * 发送消息（带加密处理）
         * <p>
         * 在发送消息前对消息内容进行加密处理，然后调用被装饰组件的发送方法。
         * </p>
         *
         * @param message 要发送的消息内容
         * @return 加密后消息发送的结果
         */
        @Override
        public String sendMessage(String message) {
            String encryptedMessage = "ENCRYPTED(" + message + ")";
            return component.sendMessage(encryptedMessage);
        }
        
        /**
         * 获取服务名称
         * <p>
         * 返回被装饰组件的服务名称，并添加加密标识。
         * </p>
         *
         * @return 带加密标识的服务名称
         */
        @Override
        public String getServiceName() {
            return component.getServiceName() + " + Encryption";
        }
        
        /**
         * 获取被装饰的原始组件
         *
         * @return 被装饰的消息服务组件
         */
        @Override
        public MessageService getComponent() {
            return component;
        }
    }
    
    /**
     * 压缩装饰器
     * <p>
     * 为消息服务添加压缩功能，在发送消息前对消息内容进行压缩处理。
     * 演示了装饰器模式中具体装饰器的实现方式。
     * </p>
     */
    public static class CompressionMessageService implements MessageService, Decorator<MessageService> {
        
        /** 被装饰的组件 */
        private final MessageService component;
        
        /**
         * 构造压缩装饰器实例
         *
         * @param component 要被装饰的消息服务组件
         */
        public CompressionMessageService(MessageService component) {
            this.component = component;
        }
        
        /**
         * 发送消息（带压缩处理）
         * <p>
         * 在发送消息前对消息内容进行压缩处理，然后调用被装饰组件的发送方法。
         * </p>
         *
         * @param message 要发送的消息内容
         * @return 压缩后消息发送的结果
         */
        @Override
        public String sendMessage(String message) {
            String compressedMessage = "COMPRESSED[" + message + "]";
            return component.sendMessage(compressedMessage);
        }
        
        /**
         * 获取服务名称
         * <p>
         * 返回被装饰组件的服务名称，并添加压缩标识。
         * </p>
         *
         * @return 带压缩标识的服务名称
         */
        @Override
        public String getServiceName() {
            return component.getServiceName() + " + Compression";
        }
        
        /**
         * 获取被装饰的原始组件
         *
         * @return 被装饰的消息服务组件
         */
        @Override
        public MessageService getComponent() {
            return component;
        }
    }
    
    /**
     * 主方法 - 运行装饰器模式示例
     * <p>
     * 演示装饰器模式的各种使用方式，包括：
     * 1. 简单装饰器示例
     * 2. 链式装饰器示例
     * 3. 装饰器构建器示例
     * 4. 装饰器解包示例
     * 5. 函数式装饰器示例
     * </p>
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 1. 简单装饰器示例
        System.out.println("=== 简单装饰器示例 ===");
        MessageService basicService = new BasicMessageService("EmailService");
        System.out.println("基础服务: " + basicService.sendMessage("Hello World"));
        
        // 添加日志装饰器
        MessageService loggingService = new LoggingMessageService(basicService);
        System.out.println("日志装饰后: " + loggingService.sendMessage("Hello World"));
        
        // 2. 链式装饰器示例
        System.out.println("\n=== 链式装饰器示例 ===");
        ChainDecorator<MessageService> chainDecorator = new ChainDecorator<>(basicService);
        chainDecorator.addDecorator(EncryptionMessageService::new)
                    .addDecorator(CompressionMessageService::new)
                    .addDecorator(LoggingMessageService::new);
        
        MessageService chainedService = chainDecorator.getDecoratedComponent();
        System.out.println("链式装饰后: " + chainedService.sendMessage("Hello World"));
        System.out.println("链式服务名称: " + chainedService.getServiceName());
        
        // 3. 装饰器构建器示例
        System.out.println("\n=== 装饰器构建器示例 ===");
        DecoratorBuilder<MessageService> builder = new DecoratorBuilderImpl<>(basicService);
        builder.add(LoggingMessageService::new)
               .add(EncryptionMessageService::new)
               .add(CompressionMessageService::new);

        MessageService builtService = builder.build();
        System.out.println("构建器装饰后: " + builtService.sendMessage("Hello World"));
        System.out.println("构建器服务名称: " + builtService.getServiceName());
        
        // 4. 装饰器解包示例
        System.out.println("\n=== 装饰器解包示例 ===");
        LoggingMessageService unwrappedLogging = ((Decorator<MessageService>) chainedService).unwrap(LoggingMessageService.class);
        if (unwrappedLogging != null) {
            System.out.println("成功解包日志装饰器");
            unwrappedLogging.sendMessage("111");
        }
        
        // 5. 函数式装饰器示例
        System.out.println("\n=== 函数式装饰器示例 ===");
        Function<MessageService, MessageService> loggingDecorator = LoggingMessageService::new;
        Function<MessageService, MessageService> encryptionDecorator = EncryptionMessageService::new;
        
        MessageService functionalService = encryptionDecorator.apply(loggingDecorator.apply(basicService));
        System.out.println("函数式装饰后: " + functionalService.sendMessage("Hello World"));
        
        System.out.println("\n=== 装饰器模式示例完成 ===");
    }
}
