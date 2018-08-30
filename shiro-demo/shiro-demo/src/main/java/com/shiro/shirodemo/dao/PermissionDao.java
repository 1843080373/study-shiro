package com.shiro.shirodemo.dao;

import java.util.List;
import java.util.Map;

import com.shiro.shirodemo.bean.Permission;

public interface PermissionDao {
	List<Permission> getByMap(Map<String, Object> map);

	Permission getById(Integer id);

	Integer create(Permission permission);

	int update(Permission permission);

	int delete(Integer id);

	List<Permission> getList();

	List<Permission> getByUserId(Integer userId);
}
