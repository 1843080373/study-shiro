package com.shiro.shirodemo.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.shiro.shirodemo.bean.User;
import com.shiro.shirodemo.service.UserService;

@RequestMapping(value = "/users")
@RestController
public class UserController {
	
	@Autowired
	private UserService userService;
	
	
	@RequestMapping(method = RequestMethod.GET)
    public List<User> list(HttpServletRequest request) {
		return userService.getByMap(null);
    }
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public User detail(@PathVariable Integer id) {
		return userService.getById(id);
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public User create(@RequestBody User user) {
		return userService.create(user);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public User update(@RequestBody User user) {
		return userService.update(user);
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public int delete(@PathVariable Integer id) {
		return userService.delete(id);
    }
}