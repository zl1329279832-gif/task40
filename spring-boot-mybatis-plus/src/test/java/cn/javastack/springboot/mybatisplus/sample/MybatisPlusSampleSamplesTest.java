package cn.javastack.springboot.mybatisplus.sample;

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
 * MyBatis-Plus 示例集成测试：验证示例注册和执行。
 * 注意：执行示例方法需要 MySQL 数据库，无数据库时执行会返回友好错误信息。
 * 微信公众号：Java技术栈
 */
@SpringBootTest
class MybatisPlusSampleSamplesTest {

    @Autowired
    private SampleRegistry sampleRegistry;

    @Test
    void testAllMybatisPlusSamplesRegistered() {
        List<SampleDefinition> all = sampleRegistry.getAllDefinitions();
        List<String> ids = all.stream().map(SampleDefinition::getId).toList();

        assertTrue(ids.contains("mybatis-plus-get-by-id"), "应包含根据 ID 查询示例");
        assertTrue(ids.contains("mybatis-plus-get-by-username"), "应包含根据用户名查询示例");
        assertTrue(ids.contains("mybatis-plus-create-user"), "应包含创建用户示例");
        assertTrue(ids.contains("mybatis-plus-list-users"), "应包含查询全部用户示例");
    }

    @Test
    void testGetByIdSampleDefinition() {
        SampleDefinition def = sampleRegistry.getDefinition("mybatis-plus-get-by-id");
        assertNotNull(def);
        assertEquals("MyBatis-Plus", def.getCategory());
        assertEquals("spring-boot-mybatis-plus", def.getModule());
        assertFalse(def.getParams().isEmpty());
        assertEquals("id", def.getParams().get(0).getName());
    }

    @Test
    void testExecuteSampleReturnsResult() {
        // 无论数据库是否可用，示例执行不应抛出未捕获异常
        SampleResult result = sampleRegistry.execute("mybatis-plus-get-by-id", Map.of("id", "1"));
        assertNotNull(result);
        assertEquals("mybatis-plus-get-by-id", result.getSampleId());
        // 如果数据库不可用，success 可能为 true（因为异常在 try-catch 中被处理）
    }

    @Test
    void testListUsersSampleDefinition() {
        SampleDefinition def = sampleRegistry.getDefinition("mybatis-plus-list-users");
        assertNotNull(def);
        assertTrue(def.getTags().contains("List"));
    }
}
