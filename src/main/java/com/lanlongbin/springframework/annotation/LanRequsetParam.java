package com.lanlongbin.springframework.annotation;

import java.lang.annotation.*;

/**
 * @author 兰龙斌
 * @date Created in 2019/5/4 2:00
 * @description
 * @version: v1.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LanRequsetParam {
    String value() default "";
}
