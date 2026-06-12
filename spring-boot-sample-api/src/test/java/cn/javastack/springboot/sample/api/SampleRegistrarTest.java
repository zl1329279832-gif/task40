package cn.javastack.springboot.sample.api;

import cn.javastack.springboot.sample.api.annotation.SampleDemo;
import cn.javastack.springboot.sample.api.model.SampleDefinition;
import cn.javastack.springboot.sample.api.model.SampleResult;
import cn.javastack.springboot.sample.api.registry.SampleRegistrar;
import cn.javastack.springboot.sample.api.registry.SampleRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SampleRegistrar 集成测试：验证代理 Bean 扫描和容错跳过。
 * 微信公众号：Java技术栈
 */
@SpringBootTest(classes = SampleRegistrarTest.TestApplication.class)
class SampleRegistrarTest {

    @Autowired
    private SampleRegistry sampleRegistry;

    @Autowired
    private SampleRegistrar sampleRegistrar;

    /**
     * 回归测试：被 CGLIB 代理的 Sample Bean，
     * 扫描器必须通过 AopUtils.getTargetClass() 穿透代理找到 @SampleDemo 方法。
     */
    @Test
    void testProxiedBeanSampleRegistered() {
        SampleDefinition def = sampleRegistry.getDefinition("proxied-hello");
        assertNotNull(def, "CGLIB 代理 Bean 的 @SampleDemo 方法必须被扫描注册");
        assertEquals("proxied-hello", def.getId());
        assertEquals("Proxied Hello", def.getName());
    }

    /**
     * 回归测试：代理 Bean 的示例可以正常执行。
     */
    @Test
    void testProxiedBeanSampleExecution() {
        SampleResult result = sampleRegistry.execute("proxied-hello", Map.of("name", "proxy"));
        assertTrue(result.isSuccess(), "代理 Bean 示例执行应成功");
        assertEquals("Hello, proxy", result.getData());
    }

    /**
     * 回归测试：存在会抛异常的 prototype Bean 时，扫描不中断，其他示例正常注册。
     */
    @Test
    void testScanContinuesDespiteProblematicBeans() {
        List<SampleDefinition> all = sampleRegistry.getAllDefinitions();
        assertFalse(all.isEmpty(), "即使存在异常 Bean，其他示例也必须被注册");
        assertTrue(all.stream().anyMatch(d -> "proxied-hello".equals(d.getId())),
                "proxied-hello 示例必须存在");
    }

    /**
     * 回归测试：普通（非代理）Bean 的示例也正常注册。
     */
    @Test
    void testNonProxiedBeanSampleRegistered() {
        SampleDefinition def = sampleRegistry.getDefinition("plain-hello");
        assertNotNull(def, "普通 Bean 的 @SampleDemo 方法必须被扫描注册");
    }

    @SpringBootApplication
    static class TestApplication {

        /**
         * 通过 ProxyFactory 创建 CGLIB 代理 Bean，
         * 模拟 @Transactional 等 AOP 场景下的代理对象。
         */
        @Bean
        public ProxiedSampleBean proxiedSampleBean() {
            ProxyFactory factory = new ProxyFactory();
            factory.setTarget(new ProxiedSampleBean());
            factory.setProxyTargetClass(true); // 强制 CGLIB
            return (ProxiedSampleBean) factory.getProxy();
        }

        @Bean
        public PlainSampleBean plainSampleBean() {
            return new PlainSampleBean();
        }

        /**
         * prototype 作用域 + 构造器抛异常 —— getBean() 每次调用都失败。
         * 扫描器必须捕获异常并跳过，不中断其他 Bean 的扫描。
         */
        @Bean
        @Scope(BeanDefinition.SCOPE_PROTOTYPE)
        public ProblematicBean problematicBean() {
            throw new RuntimeException("模拟 Bean 创建失败");
        }
    }

    static class ProxiedSampleBean {

        @SampleDemo(
                id = "proxied-hello",
                name = "Proxied Hello",
                description = "被 CGLIB 代理的示例",
                category = "Test",
                module = "test",
                tags = {"proxy", "test"}
        )
        public Object hello(Map<String, Object> params) {
            String name = params != null && params.containsKey("name")
                    ? params.get("name").toString() : "World";
            return "Hello, " + name;
        }
    }

    static class PlainSampleBean {

        @SampleDemo(
                id = "plain-hello",
                name = "Plain Hello",
                description = "普通非代理示例",
                category = "Test",
                module = "test",
                tags = {"test"}
        )
        public Object hello(Map<String, Object> params) {
            return "Plain Hello";
        }
    }

    static class ProblematicBean {
    }
}
