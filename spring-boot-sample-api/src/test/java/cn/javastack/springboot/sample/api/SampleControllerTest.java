package cn.javastack.springboot.sample.api;

import cn.javastack.springboot.sample.api.annotation.SampleDemo;
import cn.javastack.springboot.sample.api.annotation.SampleParam;
import cn.javastack.springboot.sample.api.config.SampleApiAutoConfiguration;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * SampleController 集成测试
 */
@SpringBootTest
class SampleControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(WebApplicationContext context) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void testListSamples() throws Exception {
        mockMvc.perform(get("/api/samples"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[0].id", notNullValue()))
                .andExpect(jsonPath("$[0].name", notNullValue()));
    }

    @Test
    void testGetSampleById() throws Exception {
        mockMvc.perform(get("/api/samples/test-hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("test-hello")))
                .andExpect(jsonPath("$.name", is("Hello Sample")))
                .andExpect(jsonPath("$.module", is("test")))
                .andExpect(jsonPath("$.params", hasSize(1)))
                .andExpect(jsonPath("$.params[0].name", is("name")));
    }

    @Test
    void testGetSampleNotFound() throws Exception {
        mockMvc.perform(get("/api/samples/non-existent-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testExecuteSampleSuccess() throws Exception {
        mockMvc.perform(post("/api/samples/test-hello/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"World\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sampleId", is("test-hello")))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", is("Hello, World")))
                .andExpect(jsonPath("$.executionTimeMs", greaterThanOrEqualTo(0)));
    }

    @Test
    void testExecuteSampleWithError() throws Exception {
        mockMvc.perform(post("/api/samples/test-error/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sampleId", is("test-error")))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.errorMessage", containsString("Test error")));
    }

    @Test
    void testExecuteSampleWithEmptyBody() throws Exception {
        mockMvc.perform(post("/api/samples/test-hello/execute")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", is("Hello, default")));
    }

    @Test
    void testExecuteNonExistentSample() throws Exception {
        mockMvc.perform(post("/api/samples/non-existent/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.errorMessage", containsString("Sample not found")));
    }

    /**
     * 测试用 Spring Boot 应用
     */
    @SpringBootApplication
    @Import({SampleApiAutoConfiguration.class, SampleControllerTest.TestSamples.class})
    static class TestApplication {
    }

    /**
     * 测试用示例 Bean
     */
    @Component
    static class TestSamples {

        @SampleDemo(
                id = "test-hello",
                name = "Hello Sample",
                description = "A test sample",
                category = "Test",
                module = "test",
                params = {@SampleParam(name = "name", type = "String", description = "Name to greet", example = "World")},
                configHints = {"No config needed"},
                tags = {"test"}
        )
        public Object hello(Map<String, Object> params) {
            String name = String.valueOf(params.getOrDefault("name", "default"));
            return "Hello, " + name;
        }

        @SampleDemo(
                id = "test-error",
                name = "Error Sample",
                description = "A sample that throws error",
                category = "Test",
                module = "test"
        )
        public Object error(Map<String, Object> params) {
            throw new RuntimeException("Test error occurred");
        }
    }

}
