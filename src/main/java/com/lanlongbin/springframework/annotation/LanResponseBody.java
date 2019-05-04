package com.lanlongbin.springframework.annotation;

import java.lang.annotation.*;

/**
 * @author 兰龙斌
 * @date Created in 2019/5/4 1:53
 * @description
 * @version: v1.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LanResponseBody {
}
