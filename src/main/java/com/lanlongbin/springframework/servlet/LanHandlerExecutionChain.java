package com.lanlongbin.springframework.servlet;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @author 兰龙斌
 * @date Created in 2019/5/3 22:19
 * @description 拦截器
 * @version: v1.0
 */
public class LanHandlerExecutionChain {
    protected Object controller;
    protected Method method;
    protected LanHandlerExecutionChain(Object controller,Method method){
        this.controller = controller;
        this.method = method;
    }

}
