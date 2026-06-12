package cn.javastack.springboot.starter.sample.sample;

import cn.javastack.springboot.sample.api.annotation.SampleDemo;
import cn.javastack.springboot.sample.api.annotation.SampleParam;
import cn.javastack.springboot.starter.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义 Starter 模块示例注册
 */
@Component
@RequiredArgsConstructor
public class StarterSampleDemos {

    private final TestService testService;

    @SampleDemo(
            id = "starter-auto-config",
            name = "自动装配机制",
            description = "演示 Spring Boot 自定义 Starter 的自动装配原理：@AutoConfiguration + @ConditionalOnProperty 实现条件化 Bean 注册",
            category = "Auto-Configuration",
            module = "javastack-spring-boot-starter-sample",
            configHints = {
                    "javastack.starter.enabled=true 激活 TestServiceAutoConfiguration",
                    "@ConditionalOnProperty 控制 Bean 是否创建",
                    "自动装配注册文件: META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports"
            },
            tags = {"starter", "auto-configuration", "conditional"}
    )
    public Object autoConfigDemo(Map<String, Object> params) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("autoConfigClass", "TestServiceAutoConfiguration");
        result.put("conditionalProperty", "javastack.starter.enabled=true");
        result.put("registeredBean", "TestService");
        result.put("beanClass", testService.getClass().getName());
        result.put("registrationFile", "META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports");
        result.put("springBootVersion", "4.0.3 (使用 @AutoConfiguration 替代 spring.factories)");
        return result;
    }

    @SampleDemo(
            id = "starter-service-invocation",
            name = "调用自动装配的服务",
            description = "演示调用通过自定义 Starter 自动装配的 TestService，验证自动装配是否生效",
            category = "Auto-Configuration",
            module = "javastack-spring-boot-starter-sample",
            configHints = {"javastack.starter.enabled=true 需要在 application.yml 中配置"},
            tags = {"starter", "service"}
    )
    public Object serviceInvocation(Map<String, Object> params) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("serviceName", testService.getServiceName());
        result.put("serviceClass", testService.getClass().getName());
        result.put("description", "TestService 通过 Starter 自动装配注入，调用 getServiceName() 返回服务名称");
        return result;
    }

    @SampleDemo(
            id = "starter-usage-pattern",
            name = "Starter 使用模式",
            description = "展示自定义 Spring Boot Starter 的完整使用流程：依赖引入、配置启用、自动装配原理",
            category = "Auto-Configuration",
            module = "javastack-spring-boot-starter-sample",
            configHints = {
                    "1. 添加依赖: cn.javastack:javastack-spring-boot-starter:1.0",
                    "2. 配置启用: javastack.starter.enabled=true",
                    "3. 直接注入使用: @Autowired TestService testService"
            },
            tags = {"starter", "best-practice"}
    )
    public Object starterUsagePattern(Map<String, Object> params) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("step1_dependency", Map.of(
                "groupId", "cn.javastack",
                "artifactId", "javastack-spring-boot-starter",
                "version", "1.0"
        ));
        result.put("step2_configuration", Map.of(
                "property", "javastack.starter.enabled",
                "value", "true",
                "file", "application.yml"
        ));
        result.put("step3_autoConfiguration", Map.of(
                "class", "TestServiceAutoConfiguration",
                "annotation", "@AutoConfiguration",
                "condition", "@ConditionalOnProperty(prefix=\"javastack.starter\", name=\"enabled\", havingValue=\"true\")"
        ));
        result.put("step4_usage", Map.of(
                "inject", "@Autowired TestService testService",
                "call", "testService.getServiceName()",
                "result", testService.getServiceName()
        ));
        result.put("keyFiles", List.of(
                "javastack-spring-boot-starter/src/main/java/.../config/TestServiceAutoConfiguration.java",
                "javastack-spring-boot-starter/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports",
                "javastack-spring-boot-starter-sample/src/main/resources/application.yml"
        ));
        return result;
    }

}
