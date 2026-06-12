package cn.javastack.springboot.sample.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 示例注册注解，标注在方法上，将方法注册为可执行的示例
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SampleDemo {

    String id();

    String name();

    String description() default "";

    String category() default "";

    String module() default "";

    String[] configHints() default {};

    String[] tags() default {};

    SampleParam[] params() default {};

}
