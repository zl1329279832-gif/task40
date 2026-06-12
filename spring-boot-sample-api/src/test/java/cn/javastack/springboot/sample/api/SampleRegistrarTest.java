package cn.javastack.springboot.sample.api;

import cn.javastack.springboot.sample.api.annotation.SampleDemo;
import cn.javastack.springboot.sample.api.model.SampleDefinition;
import cn.javastack.springboot.sample.api.registry.SampleRegistrar;
import cn.javastack.springboot.sample.api.registry.SampleRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * SampleRegistrar 回归测试：覆盖代理 Bean 扫描和异常容错。
 * 微信公众号：Java技术栈
 */
class SampleRegistrarTest {

    @Test
    void testScanNormalBean() {
        ApplicationContext context = mock(ApplicationContext.class);
        when(context.getBeanDefinitionNames()).thenReturn(new String[]{"annotatedBean"});
        when(context.getBean("annotatedBean")).thenReturn(new AnnotatedTestBean());

        SampleRegistry registry = new SampleRegistry();
        SampleRegistrar registrar = new SampleRegistrar(registry, context);
        registrar.afterSingletonsInstantiated();

        assertEquals(1, registry.getAllDefinitions().size());
        SampleDefinition def = registry.getDefinition("registrar-test-sample");
        assertNotNull(def);
        assertEquals("Registrar Test", def.getName());
        assertEquals("Test", def.getCategory());
    }

    @Test
    void testScanProxiedBean() {
        // 创建 CGLIB 代理，模拟 Spring Security / AOP 场景
        AnnotatedTestBean original = new AnnotatedTestBean();
        ProxyFactory proxyFactory = new ProxyFactory(original);
        proxyFactory.setProxyTargetClass(true);
        Object proxy = proxyFactory.getProxy();

        // 确认确实是代理对象
        assertNotEquals(AnnotatedTestBean.class, proxy.getClass());
        assertTrue(proxy.getClass().getName().contains("$$"), "应为 CGLIB 代理类");

        ApplicationContext context = mock(ApplicationContext.class);
        when(context.getBeanDefinitionNames()).thenReturn(new String[]{"proxiedBean"});
        when(context.getBean("proxiedBean")).thenReturn(proxy);

        SampleRegistry registry = new SampleRegistry();
        SampleRegistrar registrar = new SampleRegistrar(registry, context);
        registrar.afterSingletonsInstantiated();

        // 代理 Bean 上的 @SampleDemo 注解应正常被发现
        assertNotNull(registry.getDefinition("registrar-test-sample"),
                "CGLIB 代理 Bean 的 @SampleDemo 注解未被扫描到");
        assertEquals(1, registry.getAllDefinitions().size());

        // 通过代理 Bean 执行应正常返回
        var result = registry.execute("registrar-test-sample", Map.of("key", "value"));
        assertTrue(result.isSuccess());
    }

    @Test
    void testScanContinuesAfterBeanError() {
        ApplicationContext context = mock(ApplicationContext.class);
        // 第一个 Bean 会抛异常，第二个正常
        when(context.getBeanDefinitionNames()).thenReturn(
                new String[]{"failingBean", "goodBean"});
        when(context.getBean("failingBean")).thenThrow(
                new RuntimeException("模拟 Bean 获取失败"));
        when(context.getBean("goodBean")).thenReturn(new AnnotatedTestBean());

        SampleRegistry registry = new SampleRegistry();
        SampleRegistrar registrar = new SampleRegistrar(registry, context);

        // 不应抛出异常
        assertDoesNotThrow(() -> registrar.afterSingletonsInstantiated());

        // 好的 Bean 应正常注册，不被坏 Bean 影响
        assertNotNull(registry.getDefinition("registrar-test-sample"),
                "异常 Bean 不应阻断后续 Bean 的扫描注册");
        assertEquals(1, registry.getAllDefinitions().size());
    }

    @Test
    void testScanMultipleAnnotatedMethods() {
        ApplicationContext context = mock(ApplicationContext.class);
        when(context.getBeanDefinitionNames()).thenReturn(new String[]{"multiBean"});
        when(context.getBean("multiBean")).thenReturn(new MultiMethodTestBean());

        SampleRegistry registry = new SampleRegistry();
        SampleRegistrar registrar = new SampleRegistrar(registry, context);
        registrar.afterSingletonsInstantiated();

        assertEquals(2, registry.getAllDefinitions().size());
        assertNotNull(registry.getDefinition("multi-test-1"));
        assertNotNull(registry.getDefinition("multi-test-2"));
    }

    /**
     * 带 @SampleDemo 注解的测试 Bean
     */
    public static class AnnotatedTestBean {
        @SampleDemo(
                id = "registrar-test-sample",
                name = "Registrar Test",
                description = "Test sample for registrar scanning",
                category = "Test",
                module = "test"
        )
        public Object testMethod(Map<String, Object> params) {
            return "test result: " + params;
        }
    }

    /**
     * 多个 @SampleDemo 方法的测试 Bean
     */
    public static class MultiMethodTestBean {
        @SampleDemo(
                id = "multi-test-1",
                name = "Multi Test 1",
                description = "第一个示例",
                category = "Test",
                module = "test"
        )
        public Object method1(Map<String, Object> params) {
            return "result-1";
        }

        @SampleDemo(
                id = "multi-test-2",
                name = "Multi Test 2",
                description = "第二个示例",
                category = "Test",
                module = "test"
        )
        public Object method2(Map<String, Object> params) {
            return "result-2";
        }
    }
}
