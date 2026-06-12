package cn.javastack.springboot.sample.api.config;

import cn.javastack.springboot.sample.api.controller.SampleController;
import cn.javastack.springboot.sample.api.registry.SampleRegistrar;
import cn.javastack.springboot.sample.api.registry.SampleRegistry;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * 示例 API 自动配置类。
 * 微信公众号：Java技术栈
 */
@AutoConfiguration
public class SampleApiAutoConfiguration {

    @Bean
    public SampleRegistry sampleRegistry() {
        return new SampleRegistry();
    }

    @Bean
    public SampleRegistrar sampleRegistrar(SampleRegistry sampleRegistry,
                                           ApplicationContext applicationContext) {
        return new SampleRegistrar(sampleRegistry, applicationContext);
    }

    @Bean
    public SampleController sampleController(SampleRegistry sampleRegistry) {
        return new SampleController(sampleRegistry);
    }
}
