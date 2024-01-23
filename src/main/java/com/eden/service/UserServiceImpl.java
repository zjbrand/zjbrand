package com.eden.service;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;

import com.eden.dao.UserDao;
import com.eden.entity.User;

@Service
@Transactional
public class UserServiceImpl implements UserService{
	
	private UserDao userDao;
	
	@Autowired
	public UserServiceImpl(UserDao userDao) {
		super();
		this.userDao = userDao;
	}

	@Override
	public void register(User user) {
		// 1.根据用户名查询数据库中是否存在该用户
		User userDB=userDao.findByUserName(user.getUsername());
		//2.判断用户是否存在
		if(!ObjectUtils.isEmpty(userDB)) throw new RuntimeException("当前用户已被注册！");
		//4.密码加密 特点：相同字符串多次使用md5进行加密，加密结果始终相同
		String newPassword=DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8));
		user.setPassword(newPassword);
		//3.注册用户
		userDao.save(user);
	}

	@Override
	public User login(String username, String password) {
		// 1.根据用户名查询用户密码
		User userDB =userDao.findByUserName(username);
		//判断是否查到
		if(ObjectUtils.isEmpty(userDB)) throw new RuntimeException("用户名不正确！");
		//对密码进行md5加密
		String passwordSecret = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
		//比较密码
		if(!userDB.getPassword().equals(passwordSecret)) throw new RuntimeException("密码输入错误！");
		//比较一致，返回user
		return userDB;
	}
}
