package cn.javastack.springboot.starter.sample.sample;

import cn.javastack.springboot.sample.api.model.SampleDefinition;
import cn.javastack.springboot.sample.api.model.SampleResult;
import cn.javastack.springboot.sample.api.registry.SampleRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Starter Sample 示例集成测试：验证自定义 Starter 自动装配示例注册和执行。
 * 微信公众号：Java技术栈
 */
@SpringBootTest
class StarterSampleSamplesTest {

    @Autowired
    private SampleRegistry sampleRegistry;

    @Test
    void testAllStarterSamplesRegistered() {
        List<SampleDefinition> all = sampleRegistry.getAllDefinitions();
        List<String> ids = all.stream().map(SampleDefinition::getId).toList();

        assertTrue(ids.contains("starter-auto-config"), "应包含自动装配示例");
        assertTrue(ids.contains("starter-get-service-name"), "应包含服务调用示例");
    }

    @Test
    void testAutoConfigSample() {
        SampleDefinition def = sampleRegistry.getDefinition("starter-auto-config");
        assertNotNull(def);
        assertEquals("Starter", def.getCategory());
        assertEquals("javastack-spring-boot-starter-sample", def.getModule());
        assertFalse(def.getConfigHints().isEmpty());

        SampleResult result = sampleRegistry.execute("starter-auto-config", Map.of());
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
    }

    @Test
    void testGetServiceNameSample() {
        SampleResult result = sampleRegistry.execute("starter-get-service-name", Map.of());
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
    }
}
