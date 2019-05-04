package com.lanlongbin.springframework.demo.controller;

import com.lanlongbin.springframework.annotation.LanAutowired;
import com.lanlongbin.springframework.annotation.LanController;
import com.lanlongbin.springframework.demo.service.UserService;

/**
 * @author 兰龙斌
 * @date Created in 2019/5/4 13:51
 * @description
 * @version: v1.0
 */
@LanController
public class UserController {
    @LanAutowired
    UserService userService;
}
