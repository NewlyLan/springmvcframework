package com.lanlongbin.springframework.annotation;

import java.lang.annotation.*;

/**
 * @author 兰龙斌
 * @date Created in 2019/5/4 1:59
 * @description
 * @version: v1.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LanRequsetMapping {
    String name() default "";
}
