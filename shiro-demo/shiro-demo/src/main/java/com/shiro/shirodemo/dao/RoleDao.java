package com.shiro.shirodemo.dao;

import java.util.List;
import java.util.Map;

import com.shiro.shirodemo.bean.Role;

public interface RoleDao {
	List<Role> getByMap(Map<String, Object> map);
	Role getById(Integer id);
	Integer create(Role role);
	int update(Role role);
	int delete(Integer id);
}
