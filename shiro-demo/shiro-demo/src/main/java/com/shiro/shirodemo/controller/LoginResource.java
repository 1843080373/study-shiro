package com.shiro.shirodemo.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shiro.shirodemo.domain.Role;
import com.shiro.shirodemo.domain.User;
import com.shiro.shirodemo.service.ILoginService;

@RestController
public class LoginResource {

    @Autowired
    private ILoginService loginService;
    private static final transient Logger log = LoggerFactory.getLogger(LoginResource.class);
    //退出的时候是get请求，主要是用于退出
    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public String login(){
        return "login";
    }

    //post登录
    //http://127.0.0.1:8082/login?username=ROOT&password=111111
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String loginUrl(HttpServletRequest req){
        //添加用户认证信息
        Subject subject = SecurityUtils.getSubject();
        String username=req.getParameter("username");
	    String password=req.getParameter("password");
        log.info("username="+username+",password="+password);
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(
        		username,
                password);
        try {
			//进行验证，这里可以捕获异常，然后返回对应信息
			subject.login(usernamePasswordToken);
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
		return "index";
    }

    @RequestMapping(value = "/index")
    public String index(){
        return "index";
    }

    //登出
    @RequestMapping(value = "/logout")
    public String logout(){
        return "logout";
    }

    //错误页面展示
    @RequestMapping(value = "/error",method = RequestMethod.POST)
    public String error(){
        return "error ok!";
    }

    //数据初始化
    //http://127.0.0.1:8082/addRole?userId=2&roleName=dev
    @RequestMapping(value = "/addUser")
    public String addUser(HttpServletRequest req){
    	String username=req.getParameter("username");
	    String password=req.getParameter("password");
        log.info("username="+username+",password="+password);
    	Map<String,Object> map=new HashMap<>();
    	map.put("username", username);
    	map.put("password", password);
        User user = loginService.addUser(map);
        return "addUser is ok! \n" + JSONObject.toJSONString(user);
    }

    //角色初始化
    @RequestMapping(value = "/addRole")
    //http://127.0.0.1:8082/addRole?userId=2&roleName=dev
    public String addRole(HttpServletRequest req){
    	String userId=req.getParameter("userId");
	    String roleName=req.getParameter("roleName");
        log.info("userId="+userId+",roleName="+roleName);
    	Map<String,Object> map=new HashMap<>();
    	map.put("userId", userId);
    	map.put("roleName", roleName);
        Role role = loginService.addRole(map);
        return "addRole is ok! \n" + JSON.toJSONString(role);
    }

    //注解的使用
    @RequiresRoles({
    	"dev"
    })
    @RequiresPermissions("create")
    @RequestMapping(value = "/create")
    public String create(){
        return "Create success!";
    }
}