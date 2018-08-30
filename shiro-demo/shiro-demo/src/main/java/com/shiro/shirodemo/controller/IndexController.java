package com.shiro.shirodemo.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shiro.shirodemo.bean.User;
import com.shiro.shirodemo.service.UserService;

/**
 * 权限测试Controller
 * @author Administrator
 *
 */
public class IndexController {
	@Autowired
    private UserService userService;

    @RequestMapping("/index")
    public User index() {
        return userService.getById(1);
    }

    @RequiresPermissions("test")
    @RequestMapping("/test")
    public String test() {
        return "ok";
    }
}
