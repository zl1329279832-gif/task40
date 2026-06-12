package cn.javastack.springboot.sample.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 示例演示注解，标注在方法上声明一个可执行的示例。
 * 微信公众号：Java技术栈
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SampleDemo {

    /** 示例唯一标识 */
    String id();

    /** 示例名称 */
    String name();

    /** 示例描述 */
    String description();

    /** 示例分类（如 WebMVC、MyBatis-Plus、Starter） */
    String category();

    /** 所属模块 */
    String module();

    /** 相关配置提示 */
    String[] configHints() default {};

    /** 标签 */
    String[] tags() default {};

    /** 参数说明 */
    SampleParam[] params() default {};
}
