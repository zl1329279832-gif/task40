package cn.javastack.springboot.sample.api.registry;

import cn.javastack.springboot.sample.api.annotation.SampleDemo;
import cn.javastack.springboot.sample.api.annotation.SampleParam;
import cn.javastack.springboot.sample.api.model.SampleDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 在所有单例 Bean 初始化完成后，扫描带有 @SampleDemo 注解的方法并注册到 SampleRegistry
 */
@Slf4j
@RequiredArgsConstructor
public class SampleRegistrar implements SmartInitializingSingleton {

    private final SampleRegistry registry;
    private final ApplicationContext applicationContext;

    @Override
    public void afterSingletonsInstantiated() {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);
            scanBean(bean);
        }
        log.info("Sample registry initialized, total samples: {}", registry.getAllDefinitions().size());
    }

    private void scanBean(Object bean) {
        Class<?> targetClass = bean.getClass();
        for (Method method : targetClass.getMethods()) {
            SampleDemo annotation = method.getAnnotation(SampleDemo.class);
            if (annotation != null) {
                SampleDefinition definition = buildDefinition(annotation);
                registry.register(definition, bean, method);
                log.info("Registered sample: [{}] {}", annotation.id(), annotation.name());
            }
        }
    }

    private SampleDefinition buildDefinition(SampleDemo annotation) {
        SampleDefinition definition = new SampleDefinition();
        definition.setId(annotation.id());
        definition.setName(annotation.name());
        definition.setDescription(annotation.description());
        definition.setCategory(annotation.category());
        definition.setModule(annotation.module());
        definition.setConfigHints(Arrays.asList(annotation.configHints()));
        definition.setTags(Arrays.asList(annotation.tags()));
        definition.setParams(buildParams(annotation.params()));
        return definition;
    }

    private List<SampleDefinition.ParamInfo> buildParams(SampleParam[] params) {
        return Arrays.stream(params).map(p -> {
            SampleDefinition.ParamInfo info = new SampleDefinition.ParamInfo();
            info.setName(p.name());
            info.setType(p.type());
            info.setRequired(p.required());
            info.setDescription(p.description());
            info.setDefaultValue(p.defaultValue());
            info.setExample(p.example());
            return info;
        }).collect(Collectors.toList());
    }

}
