package cn.javastack.springboot.sample.api.registry;

import cn.javastack.springboot.sample.api.annotation.SampleDemo;
import cn.javastack.springboot.sample.api.annotation.SampleParam;
import cn.javastack.springboot.sample.api.model.SampleDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * 示例注册扫描器，在所有单例 Bean 初始化完成后扫描 @SampleDemo 注解并注册。
 * 微信公众号：Java技术栈
 */
@Slf4j
public class SampleRegistrar implements SmartInitializingSingleton {

    private final SampleRegistry registry;
    private final ApplicationContext applicationContext;

    public SampleRegistrar(SampleRegistry registry, ApplicationContext applicationContext) {
        this.registry = registry;
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterSingletonsInstantiated() {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);
            scanBean(bean);
        }
        log.info("示例目录注册完成，共 {} 个示例", registry.getAllDefinitions().size());
    }

    private void scanBean(Object bean) {
        Method[] methods = bean.getClass().getDeclaredMethods();
        for (Method method : methods) {
            SampleDemo annotation = method.getAnnotation(SampleDemo.class);
            if (annotation != null) {
                SampleDefinition definition = buildDefinition(annotation);
                registry.register(definition, bean, method);
                log.debug("注册示例: {} -> {}.{}", definition.getId(),
                        bean.getClass().getSimpleName(), method.getName());
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
        definition.setConfigHints(List.of(annotation.configHints()));
        definition.setTags(List.of(annotation.tags()));
        definition.setParams(buildParams(annotation.params()));
        return definition;
    }

    private List<SampleDefinition.ParamInfo> buildParams(SampleParam[] sampleParams) {
        return Arrays.stream(sampleParams).map(p -> {
            SampleDefinition.ParamInfo info = new SampleDefinition.ParamInfo();
            info.setName(p.name());
            info.setType(p.type());
            info.setRequired(p.required());
            info.setDescription(p.description());
            info.setDefaultValue(p.defaultValue());
            info.setExample(p.example());
            return info;
        }).toList();
    }
}
