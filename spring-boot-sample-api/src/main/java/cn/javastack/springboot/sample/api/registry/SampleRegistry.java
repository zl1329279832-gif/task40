package cn.javastack.springboot.sample.api.registry;

import cn.javastack.springboot.sample.api.model.SampleDefinition;
import cn.javastack.springboot.sample.api.model.SampleResult;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 示例注册中心，管理所有已注册的示例定义和执行器。
 * 微信公众号：Java技术栈
 */
public class SampleRegistry {

    private final Map<String, SampleDefinition> definitions = new LinkedHashMap<>();
    private final Map<String, SampleExecutor> executors = new LinkedHashMap<>();

    /**
     * 注册一个示例
     */
    public void register(SampleDefinition definition, Object bean, Method method) {
        String id = definition.getId();
        if (definitions.containsKey(id)) {
            throw new IllegalStateException("示例 ID 重复: " + id);
        }
        method.setAccessible(true);
        definitions.put(id, definition);
        executors.put(id, new SampleExecutor(bean, method));
    }

    /**
     * 获取所有示例定义
     */
    public List<SampleDefinition> getAllDefinitions() {
        return new ArrayList<>(definitions.values());
    }

    /**
     * 根据 ID 获取示例定义
     */
    public SampleDefinition getDefinition(String id) {
        return definitions.get(id);
    }

    /**
     * 执行示例
     */
    public SampleResult execute(String id, Map<String, Object> params) {
        SampleDefinition definition = definitions.get(id);
        if (definition == null) {
            return SampleResult.builder()
                    .sampleId(id)
                    .success(false)
                    .errorMessage("示例不存在: " + id)
                    .build();
        }

        SampleExecutor executor = executors.get(id);
        long start = System.currentTimeMillis();
        try {
            Object result = executor.method().invoke(executor.bean(), params);
            long elapsed = System.currentTimeMillis() - start;
            return SampleResult.builder()
                    .sampleId(id)
                    .success(true)
                    .data(result)
                    .executionTimeMs(elapsed)
                    .requestParams(params)
                    .configHints(definition.getConfigHints())
                    .build();
        } catch (Exception ex) {
            long elapsed = System.currentTimeMillis() - start;
            Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
            return SampleResult.builder()
                    .sampleId(id)
                    .success(false)
                    .errorMessage(cause.getMessage())
                    .executionTimeMs(elapsed)
                    .requestParams(params)
                    .configHints(definition.getConfigHints())
                    .build();
        }
    }

    /**
     * 示例执行器，持有目标 Bean 和方法引用
     */
    record SampleExecutor(Object bean, Method method) {
    }
}
