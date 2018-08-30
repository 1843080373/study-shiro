package com.shiro.shirodemo.dao;

import com.shiro.shirodemo.domain.User;

public interface UserRepository extends BaseRepository<User,Long>{
    User findByName(String name);
}