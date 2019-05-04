package com.lanlongbin.springframework.annotation;

import java.lang.annotation.*;

/**
 * @author 兰龙斌
 * @date Created in 2019/5/4 1:52
 * @description
 * @version: v1.0
 */
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LanAutowired {
    boolean required() default true;
}
