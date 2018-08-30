package com.shiro.shirodemo.service;

import java.util.Map;

import com.shiro.shirodemo.domain.Role;
import com.shiro.shirodemo.domain.User;

public interface ILoginService {

	User addUser(Map<String, Object> map);

	Role addRole(Map<String, Object> map);

	User findByName(String name);

}
