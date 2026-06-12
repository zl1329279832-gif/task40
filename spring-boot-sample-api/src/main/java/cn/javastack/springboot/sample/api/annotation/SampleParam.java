package cn.javastack.springboot.sample.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 示例参数描述注解。
 * 微信公众号：Java技术栈
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SampleParam {

    /** 参数名 */
    String name();

    /** 参数类型（如 String、int、long） */
    String type();

    /** 是否必填 */
    boolean required() default false;

    /** 参数描述 */
    String description() default "";

    /** 默认值 */
    String defaultValue() default "";

    /** 示例值 */
    String example() default "";
}
