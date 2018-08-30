package com.shiro.shirodemo.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shiro.shirodemo.bean.Permission;
import com.shiro.shirodemo.dao.PermissionDao;

@Service
public class PermissionService {
    @Autowired
	private PermissionDao permissionDao;
	
	public List<Permission> getByMap(Map<String,Object> map) {
	    return permissionDao.getByMap(map);
	}
	
	public Permission getById(Integer id) {
		return permissionDao.getById(id);
	}
	
	public Permission create(Permission permission) {
		permissionDao.create(permission);
		return permission;
	}
	
	public Permission update(Permission permission) {
		permissionDao.update(permission);
		return permission;
	}
	
	public int delete(Integer id) {
		return permissionDao.delete(id);
	}
}
