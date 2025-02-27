package org.cdm.spring.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Rao
 * @Date 2025/2/27
 **/
@SpringBootApplication
public class ExampleApplication {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(ExampleApplication.class);
        springApplication.run(args);
    }
}
