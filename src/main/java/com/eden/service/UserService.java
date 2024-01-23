package com.eden.service;

import com.eden.entity.User;

public interface UserService {
	//注册用户
	void register(User user);
	
	//用户登录
	User login(String username, String password);


}
