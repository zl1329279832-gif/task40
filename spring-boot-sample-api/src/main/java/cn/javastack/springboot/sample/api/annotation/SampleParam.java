package cn.javastack.springboot.sample.api.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 示例参数元数据注解，用于描述示例方法的参数信息
 */
@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface SampleParam {

    String name();

    String type() default "String";

    boolean required() default false;

    String description() default "";

    String defaultValue() default "";

    String example() default "";

}
