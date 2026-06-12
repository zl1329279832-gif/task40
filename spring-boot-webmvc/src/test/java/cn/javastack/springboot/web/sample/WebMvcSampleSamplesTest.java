package cn.javastack.springboot.web.sample;

import cn.javastack.springboot.sample.api.model.SampleDefinition;
import cn.javastack.springboot.sample.api.model.SampleResult;
import cn.javastack.springboot.sample.api.registry.SampleRegistrar;
import cn.javastack.springboot.sample.api.registry.SampleRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * WebMVC 示例集成测试：验证示例注册和执行。
 * 微信公众号：Java技术栈
 */
@SpringBootTest
class WebMvcSampleSamplesTest {

    @Autowired
    private SampleRegistry sampleRegistry;

    @Test
    void testAllWebMvcSamplesRegistered() {
        List<SampleDefinition> all = sampleRegistry.getAllDefinitions();
        List<String> ids = all.stream().map(SampleDefinition::getId).toList();

        assertTrue(ids.contains("webmvc-json-response"), "应包含 JSON 响应示例");
        assertTrue(ids.contains("webmvc-xml-response"), "应包含 XML 响应示例");
        assertTrue(ids.contains("webmvc-login"), "应包含登录示例");
        assertTrue(ids.contains("webmvc-validation"), "应包含参数校验示例");
        assertTrue(ids.contains("webmvc-global-exception"), "应包含全局异常处理示例");
    }

    @Test
    void testJsonResponseSample() {
        SampleDefinition def = sampleRegistry.getDefinition("webmvc-json-response");
        assertNotNull(def);
        assertEquals("WebMVC", def.getCategory());
        assertEquals("spring-boot-webmvc", def.getModule());
        assertFalse(def.getParams().isEmpty());

        SampleResult result = sampleRegistry.execute("webmvc-json-response", Map.of("userId", "1001"));
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
    }

    @Test
    void testXmlResponseSample() {
        SampleResult result = sampleRegistry.execute("webmvc-xml-response", Map.of("userId", "U001"));
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
    }

    @Test
    void testLoginSample() {
        SampleResult result = sampleRegistry.execute("webmvc-login", Map.of());
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
    }

    @Test
    void testValidationSample() {
        // 校验通过
        SampleResult result = sampleRegistry.execute("webmvc-validation",
                Map.of("username", "javastack", "age", "25"));
        assertTrue(result.isSuccess());

        // 校验失败
        SampleResult failResult = sampleRegistry.execute("webmvc-validation",
                Map.of("username", "ab", "age", "25"));
        assertTrue(failResult.isSuccess());
    }

    @Test
    void testGlobalExceptionSample() {
        SampleResult result = sampleRegistry.execute("webmvc-global-exception", Map.of());
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
    }
}
