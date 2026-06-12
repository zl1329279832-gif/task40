package cn.javastack.springboot.sample.api;

import cn.javastack.springboot.sample.api.model.SampleDefinition;
import cn.javastack.springboot.sample.api.model.SampleResult;
import cn.javastack.springboot.sample.api.registry.SampleRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SampleRegistry 单元测试
 */
class SampleRegistryTest {

    private SampleRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new SampleRegistry();
    }

    @Test
    void testRegisterAndGetAllDefinitions() throws Exception {
        SampleDefinition def = createDefinition("test-1", "Test Sample", "TestModule");
        Method method = TestSampleBean.class.getMethod("hello", Map.class);
        TestSampleBean bean = new TestSampleBean();

        registry.register(def, bean, method);

        List<SampleDefinition> all = registry.getAllDefinitions();
        assertEquals(1, all.size());
        assertEquals("test-1", all.get(0).getId());
    }

    @Test
    void testGetDefinitionById() throws Exception {
        SampleDefinition def = createDefinition("test-2", "Test Sample 2", "TestModule");
        Method method = TestSampleBean.class.getMethod("hello", Map.class);
        registry.register(def, new TestSampleBean(), method);

        SampleDefinition found = registry.getDefinition("test-2");
        assertNotNull(found);
        assertEquals("Test Sample 2", found.getName());

        assertNull(registry.getDefinition("non-existent"));
    }

    @Test
    void testExecuteSuccess() throws Exception {
        SampleDefinition def = createDefinition("test-exec", "Exec Test", "TestModule");
        Method method = TestSampleBean.class.getMethod("hello", Map.class);
        registry.register(def, new TestSampleBean(), method);

        SampleResult result = registry.execute("test-exec", Map.of("name", "World"));
        assertTrue(result.isSuccess());
        assertEquals("Hello, World", result.getData());
        assertTrue(result.getExecutionTimeMs() >= 0);
        assertEquals("test-exec", result.getSampleId());
    }

    @Test
    void testExecuteUnknownId() {
        SampleResult result = registry.execute("unknown-id", Map.of());
        assertFalse(result.isSuccess());
        assertEquals("Sample not found: unknown-id", result.getErrorMessage());
    }

    @Test
    void testExecuteWithException() throws Exception {
        SampleDefinition def = createDefinition("test-error", "Error Test", "TestModule");
        Method method = TestSampleBean.class.getMethod("error", Map.class);
        registry.register(def, new TestSampleBean(), method);

        SampleResult result = registry.execute("test-error", Map.of());
        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessage().contains("RuntimeException"));
        assertTrue(result.getErrorMessage().contains("Something went wrong"));
    }

    @Test
    void testDuplicateIdThrowsException() throws Exception {
        SampleDefinition def1 = createDefinition("dup-id", "First", "TestModule");
        SampleDefinition def2 = createDefinition("dup-id", "Second", "TestModule");
        Method method = TestSampleBean.class.getMethod("hello", Map.class);
        TestSampleBean bean = new TestSampleBean();

        registry.register(def1, bean, method);
        assertThrows(IllegalStateException.class, () -> registry.register(def2, bean, method));
    }

    @Test
    void testExecuteWithNullParams() throws Exception {
        SampleDefinition def = createDefinition("test-null", "Null Params", "TestModule");
        Method method = TestSampleBean.class.getMethod("hello", Map.class);
        registry.register(def, new TestSampleBean(), method);

        SampleResult result = registry.execute("test-null", null);
        assertTrue(result.isSuccess());
        assertEquals("Hello, default", result.getData());
    }

    private SampleDefinition createDefinition(String id, String name, String module) {
        SampleDefinition def = new SampleDefinition();
        def.setId(id);
        def.setName(name);
        def.setModule(module);
        def.setDescription("Test description");
        def.setCategory("Test");
        def.setConfigHints(List.of("hint1"));
        def.setTags(List.of("test"));
        def.setParams(List.of());
        return def;
    }

    /**
     * 测试用 Bean
     */
    public static class TestSampleBean {

        public Object hello(Map<String, Object> params) {
            String name = String.valueOf(params.getOrDefault("name", "default"));
            return "Hello, " + name;
        }

        public Object error(Map<String, Object> params) {
            throw new RuntimeException("Something went wrong");
        }
    }

}
