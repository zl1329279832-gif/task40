package cn.javastack.springboot.starter.sample.sample;

import cn.javastack.springboot.sample.api.annotation.SampleDemo;
import cn.javastack.springboot.starter.service.TestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义 Starter 示例能力目录：展示自动装配能力。
 * 微信公众号：Java技术栈
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StarterSampleSamples {

    private final TestService testService;

    @Value("${debug:false}")
    private boolean debug;

    @SampleDemo(
            id = "starter-auto-config",
            name = "自定义 Starter 自动装配",
            description = "演示自定义 Spring Boot Starter 的自动装配能力。javastack-spring-boot-starter 通过 @AutoConfiguration + @ConditionalOnProperty 实现按需装配 TestService Bean。本模块作为接入方，引入 starter 依赖并配置 javastack.starter.enabled=true 即可使用。",
            category = "Starter",
            module = "javastack-spring-boot-starter-sample",
            tags = {"AutoConfiguration", "Starter", "ConditionalOnProperty"},
            configHints = {
                    "javastack-spring-boot-starter 使用 @AutoConfiguration 声明自动配置类",
                    "@ConditionalOnProperty(prefix=\"javastack.starter\", name=\"enabled\", havingValue=\"true\") 控制是否装配",
                    "META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports 注册自动配置",
                    "同时保留 spring.factories 兼容 Spring Boot 2.x",
                    "当前模块 application.yml 配置: javastack.starter.enabled=true"
            }
    )
    public Object autoConfigDemo(Map<String, Object> params) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("提示", "此示例展示了自定义 Starter 的自动装配流程");
        result.put("Starter 模块", "javastack-spring-boot-starter");
        result.put("接入模块", "javastack-spring-boot-starter-sample");
        result.put("装配条件", "javastack.starter.enabled=true");
        result.put("当前 debug 配置", debug);

        // 调用自动装配的 TestService
        String serviceName = testService.getServiceName();
        result.put("TestService 已装配", true);
        result.put("TestService.getServiceName()", serviceName);

        result.put("装配流程", List.of(
                "1. 引入 javastack-spring-boot-starter 依赖",
                "2. application.yml 中设置 javastack.starter.enabled=true",
                "3. Spring Boot 自动扫描 AutoConfiguration.imports",
                "4. @ConditionalOnProperty 条件满足时装配 TestService Bean",
                "5. 业务代码通过 @Autowired / 构造器注入使用 TestService"
        ));
        return result;
    }

    @SampleDemo(
            id = "starter-get-service-name",
            name = "调用 Starter 服务",
            description = "直接调用自动装配的 TestService.getServiceName() 方法，验证 Starter 的 Bean 已成功注入并可用。",
            category = "Starter",
            module = "javastack-spring-boot-starter-sample",
            tags = {"TestService", "Bean", "Invoke"}
    )
    public Object getServiceName(Map<String, Object> params) {
        String serviceName = testService.getServiceName();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("提示", "调用了自动装配的 TestService.getServiceName()");
        result.put("返回值", serviceName);
        result.put("Bean 来源", "TestServiceAutoConfiguration.testService() @Bean");
        return result;
    }
}
