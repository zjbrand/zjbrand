package com.eden.dao;

import com.eden.entity.User;

public interface UserDao {

	  	//根据用户名查询用户
		User findByUserName(String username);

		//保存用户
		void save(User user);
		
}
