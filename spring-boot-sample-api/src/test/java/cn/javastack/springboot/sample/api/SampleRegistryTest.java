package cn.javastack.springboot.sample.api;

import cn.javastack.springboot.sample.api.model.SampleDefinition;
import cn.javastack.springboot.sample.api.registry.SampleRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SampleRegistry 单元测试
 * 微信公众号：Java技术栈
 */
class SampleRegistryTest {

    private SampleRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new SampleRegistry();
    }

    @Test
    void testRegisterAndGetAllDefinitions() throws Exception {
        SampleDefinition def = createDefinition("test-1", "测试示例1", "描述1");
        TestSampleBean bean = new TestSampleBean();
        Method method = TestSampleBean.class.getMethod("hello", Map.class);

        registry.register(def, bean, method);

        List<SampleDefinition> all = registry.getAllDefinitions();
        assertEquals(1, all.size());
        assertEquals("test-1", all.get(0).getId());
        assertEquals("测试示例1", all.get(0).getName());
    }

    @Test
    void testGetDefinitionById() throws Exception {
        SampleDefinition def = createDefinition("test-2", "测试示例2", "描述2");
        TestSampleBean bean = new TestSampleBean();
        Method method = TestSampleBean.class.getMethod("hello", Map.class);

        registry.register(def, bean, method);

        SampleDefinition found = registry.getDefinition("test-2");
        assertNotNull(found);
        assertEquals("test-2", found.getId());

        assertNull(registry.getDefinition("non-existent"));
    }

    @Test
    void testExecuteSuccess() throws Exception {
        SampleDefinition def = createDefinition("test-3", "成功示例", "会成功执行");
        def.setConfigHints(List.of("配置提示1"));
        TestSampleBean bean = new TestSampleBean();
        Method method = TestSampleBean.class.getMethod("hello", Map.class);

        registry.register(def, bean, method);

        var result = registry.execute("test-3", Map.of("name", "test"));
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertTrue(result.getExecutionTimeMs() >= 0);
        assertEquals("test-3", result.getSampleId());
        assertEquals(List.of("配置提示1"), result.getConfigHints());
    }

    @Test
    void testExecuteUnknownId() {
        var result = registry.execute("unknown-id", Map.of());
        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessage().contains("unknown-id"));
    }

    @Test
    void testExecuteWithException() throws Exception {
        SampleDefinition def = createDefinition("test-error", "异常示例", "会抛出异常");
        TestSampleBean bean = new TestSampleBean();
        Method method = TestSampleBean.class.getMethod("error", Map.class);

        registry.register(def, bean, method);

        var result = registry.execute("test-error", Map.of());
        assertFalse(result.isSuccess());
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().contains("模拟异常"));
    }

    @Test
    void testDuplicateIdThrowsException() throws Exception {
        SampleDefinition def1 = createDefinition("dup-id", "示例1", "描述1");
        SampleDefinition def2 = createDefinition("dup-id", "示例2", "描述2");
        TestSampleBean bean = new TestSampleBean();
        Method method = TestSampleBean.class.getMethod("hello", Map.class);

        registry.register(def1, bean, method);

        assertThrows(IllegalStateException.class, () ->
                registry.register(def2, bean, method));
    }

    @Test
    void testExecuteWithNullParams() throws Exception {
        SampleDefinition def = createDefinition("test-null", "空参数示例", "接受 null 参数");
        TestSampleBean bean = new TestSampleBean();
        Method method = TestSampleBean.class.getMethod("hello", Map.class);

        registry.register(def, bean, method);

        var result = registry.execute("test-null", null);
        assertTrue(result.isSuccess());
    }

    /**
     * 回归测试：验证在子类（模拟 CGLIB 代理）实例上执行父类方法。
     * 扫描器通过 AopUtils.getTargetClass() 获取目标类方法，
     * 注册时 bean 为代理实例，method 来自目标类 —— 执行必须正常。
     */
    @Test
    void testExecuteOnSubclassBean() throws Exception {
        SampleDefinition def = createDefinition("test-subclass", "子类代理示例", "模拟 CGLIB 代理场景");
        SubclassSampleBean proxyBean = new SubclassSampleBean();
        // 方法来自父类 TestSampleBean，但 bean 实例是子类（模拟 CGLIB 代理 extends 目标类）
        Method method = TestSampleBean.class.getMethod("hello", Map.class);

        registry.register(def, proxyBean, method);

        var result = registry.execute("test-subclass", Map.of("name", "proxy"));
        assertTrue(result.isSuccess());
        assertEquals("Hello, proxy", result.getData());
    }

    private SampleDefinition createDefinition(String id, String name, String description) {
        SampleDefinition def = new SampleDefinition();
        def.setId(id);
        def.setName(name);
        def.setDescription(description);
        def.setCategory("Test");
        def.setModule("test-module");
        def.setParams(List.of());
        def.setConfigHints(List.of());
        def.setTags(List.of());
        return def;
    }

    /**
     * 测试用 Bean
     */
    public static class TestSampleBean {
        public Object hello(Map<String, Object> params) {
            return "Hello, " + (params != null ? params.getOrDefault("name", "World") : "World");
        }

        public Object error(Map<String, Object> params) {
            throw new RuntimeException("模拟异常");
        }
    }

    /**
     * 子类 Bean，模拟 CGLIB 代理（代理类 extends 目标类）
     */
    public static class SubclassSampleBean extends TestSampleBean {
    }
}
