package com.lanlongbin.springframework.demo.controller;

import com.lanlongbin.springframework.annotation.LanAutowired;
import com.lanlongbin.springframework.annotation.LanController;
import com.lanlongbin.springframework.annotation.LanRequsetMapping;
import com.lanlongbin.springframework.annotation.LanRequsetParam;
import com.lanlongbin.springframework.demo.service.UserService;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author 兰龙斌
 * @date Created in 2019/5/4 13:51
 * @description
 * @version: v1.0
 */
@LanController
public class UserController {
    private static final Logger logger = Logger.getLogger(UserController.class);
    @LanAutowired
    UserService userService;

    @LanRequsetMapping("/getName")
    public String getName(HttpServletRequest request, HttpServletResponse response,@LanRequsetParam("name") String name){
        out(response,name);
        return null;
    }
    private void out(HttpServletResponse response,String name){
        try {
            response.setContentType("text/javascript; charset=utf-8");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("我的名字是 ： "+name);
        } catch (IOException e) {
            logger.error("500 响应失败");
        }
    }
}
