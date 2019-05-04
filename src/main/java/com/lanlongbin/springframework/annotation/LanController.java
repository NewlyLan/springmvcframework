package com.lanlongbin.springframework.annotation;

import java.lang.annotation.*;

/**
 * @author 兰龙斌
 * @date Created in 2019/5/4 1:46
 * @description
 * @version: v1.0
 */
@Target(ElementType.TYPE)//表示给类使用
@Retention(RetentionPolicy.RUNTIME)//表示运行时生效
@Documented
public @interface LanController {
    String value() default "";
}
