package cn.javastack.springboot.sample.api.registry;

import cn.javastack.springboot.sample.api.model.SampleDefinition;
import cn.javastack.springboot.sample.api.model.SampleResult;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 示例注册中心，存储所有示例定义和执行器
 */
public class SampleRegistry {

    private final Map<String, SampleDefinition> definitions = new ConcurrentHashMap<>();
    private final Map<String, SampleExecutor> executors = new ConcurrentHashMap<>();

    public void register(SampleDefinition definition, Object bean, Method method) {
        String id = definition.getId();
        if (definitions.containsKey(id)) {
            throw new IllegalStateException("Duplicate sample ID: " + id);
        }
        method.setAccessible(true);
        definitions.put(id, definition);
        executors.put(id, new SampleExecutor(bean, method));
    }

    public List<SampleDefinition> getAllDefinitions() {
        return definitions.values().stream()
                .sorted(Comparator.comparing(SampleDefinition::getModule)
                        .thenComparing(SampleDefinition::getId))
                .collect(Collectors.toList());
    }

    public SampleDefinition getDefinition(String id) {
        return definitions.get(id);
    }

    public SampleResult execute(String id, Map<String, Object> params) {
        SampleDefinition definition = definitions.get(id);
        if (definition == null) {
            return SampleResult.builder()
                    .sampleId(id)
                    .success(false)
                    .errorMessage("Sample not found: " + id)
                    .requestParams(params)
                    .build();
        }

        SampleExecutor executor = executors.get(id);
        long startTime = System.currentTimeMillis();

        try {
            Object result = executor.method.invoke(executor.bean, params != null ? params : Map.of());
            long elapsed = System.currentTimeMillis() - startTime;
            return SampleResult.builder()
                    .sampleId(id)
                    .success(true)
                    .data(result)
                    .executionTimeMs(elapsed)
                    .requestParams(params)
                    .configHints(definition.getConfigHints())
                    .build();
        } catch (InvocationTargetException e) {
            long elapsed = System.currentTimeMillis() - startTime;
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            return SampleResult.builder()
                    .sampleId(id)
                    .success(false)
                    .errorMessage(cause.getClass().getSimpleName() + ": " + cause.getMessage())
                    .executionTimeMs(elapsed)
                    .requestParams(params)
                    .configHints(definition.getConfigHints())
                    .build();
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - startTime;
            return SampleResult.builder()
                    .sampleId(id)
                    .success(false)
                    .errorMessage(e.getClass().getSimpleName() + ": " + e.getMessage())
                    .executionTimeMs(elapsed)
                    .requestParams(params)
                    .configHints(definition.getConfigHints())
                    .build();
        }
    }

    private record SampleExecutor(Object bean, Method method) {}

}
