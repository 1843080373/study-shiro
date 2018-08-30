package com.shiro.shirodemo.dao;

import java.util.List;
import java.util.Map;

import com.shiro.shirodemo.bean.User;

public interface UserDao {
	List<User> getByMap(Map<String, Object> map);
	User getById(Integer id);
	Integer create(User user);
	int update(User user);
	int delete(Integer id);
	User getByUserName(String userName);
}
