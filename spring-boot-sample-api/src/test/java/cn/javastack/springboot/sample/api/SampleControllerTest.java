package cn.javastack.springboot.sample.api;

import cn.javastack.springboot.sample.api.annotation.SampleDemo;
import cn.javastack.springboot.sample.api.annotation.SampleParam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * SampleController 集成测试
 * 微信公众号：Java技术栈
 */
@SpringBootTest(classes = SampleControllerTest.TestApplication.class)
class SampleControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(WebApplicationContext context) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void testListSamples() throws Exception {
        mockMvc.perform(get("/api/samples").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[?(@.id == 'test-hello')]").exists())
                .andExpect(jsonPath("$[?(@.id == 'test-error')]").exists());
    }

    @Test
    void testGetSampleById() throws Exception {
        mockMvc.perform(get("/api/samples/test-hello").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("test-hello"))
                .andExpect(jsonPath("$.name").value("Hello 示例"))
                .andExpect(jsonPath("$.category").value("Test"));
    }

    @Test
    void testGetSampleNotFound() throws Exception {
        mockMvc.perform(get("/api/samples/non-existent").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testExecuteSampleSuccess() throws Exception {
        mockMvc.perform(post("/api/samples/test-hello/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"World\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.sampleId").value("test-hello"))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    void testExecuteSampleWithError() throws Exception {
        mockMvc.perform(post("/api/samples/test-error/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorMessage").isNotEmpty());
    }

    @Test
    void testExecuteSampleWithEmptyBody() throws Exception {
        mockMvc.perform(post("/api/samples/test-hello/execute")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testExecuteNonExistentSample() throws Exception {
        mockMvc.perform(post("/api/samples/unknown/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorMessage").value("示例不存在: unknown"));
    }

    /**
     * 测试用 Spring Boot 应用，启用自动配置
     */
    @SpringBootApplication
    @Import(TestSamples.class)
    static class TestApplication {
    }

    /**
     * 测试用示例 Bean
     */
    @Component
    static class TestSamples {

        @SampleDemo(
                id = "test-hello",
                name = "Hello 示例",
                description = "返回 Hello 消息",
                category = "Test",
                module = "test",
                tags = {"test"},
                params = {
                        @SampleParam(name = "name", type = "String", required = false,
                                description = "名字", defaultValue = "World", example = "Java")
                }
        )
        public Object hello(Map<String, Object> params) {
            String name = params != null && params.containsKey("name")
                    ? params.get("name").toString() : "World";
            return "Hello, " + name;
        }

        @SampleDemo(
                id = "test-error",
                name = "异常示例",
                description = "模拟异常",
                category = "Test",
                module = "test",
                tags = {"test", "error"}
        )
        public Object error(Map<String, Object> params) {
            throw new RuntimeException("模拟执行异常");
        }
    }
}
