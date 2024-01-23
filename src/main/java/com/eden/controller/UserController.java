package com.eden.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.eden.entity.User;
import com.eden.service.UserService;
import com.eden.utils.VerifyCodeUtils;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("user")
public class UserController {
		
	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	
	private UserService userService;
	
	@Autowired	
	public UserController(UserService userService) {
		super();
		this.userService = userService;
	}
	
	//安全退出
	@RequestMapping("logout")
	public String logout(HttpSession session) {
		session.invalidate();//session失效
		return "redirect:/login";
	}
	
	//用户登录
	@RequestMapping("login")
	public String login(String username,String password,HttpSession session) throws UnsupportedEncodingException {
		log.debug("本次登录用户名：{}",username);
		log.debug("本次登录密码：{}",password);
		try {
			//调用业务层进行登录
			User user = userService.login(username,password);
			//登录成功保存用户信息
			session.setAttribute("user", user);
		} catch (Exception e) {
			
			e.printStackTrace();
			return "redirect:/login?msg="+URLEncoder.encode(e.getMessage(),"UTF-8");//登录失败跳转到登录页面（到配置类中找login）
		}
		return "redirect:/employee/lists";//登录成功后跳转到显示所有员工页面(配置类中没有到controller中找)
	}

	//用户注册
	@RequestMapping("register")
	public String register(User user,String code,HttpSession session) throws UnsupportedEncodingException {
		log.debug("用户名：{},真实姓名：{}，密码：{} ，性别：{}",user.getUsername(),user.getRealname(),user.getPassword(),user.getGender());
		log.debug("用户输入的验证码：{}",code);
				
		try {
			//1.判断用户输入的验证码和session中的验证码是否一致
			//如果不一致抛出错误，如果一致进行注册
			String sessionCode=session.getAttribute("code").toString();
			if(!sessionCode.equalsIgnoreCase(code))throw new RuntimeException("验证码输入错误，请重新注册！");
			//2.注册用户
			userService.register(user);
		} catch (Exception e) {
			e.printStackTrace();			
			return "redirect:/register?msg="+URLEncoder.encode(e.getMessage(),"UTF-8");//注册失败，回到注册页面
		}
		
		return "redirect:/login";//注册成功，跳转到登录页面
	}
	
	//生成验证码
	@RequestMapping("generateImageCode")
	public void generateImageCode(HttpSession session,HttpServletResponse response) throws IOException {
		
		//1.生成4位随机数
		String code=VerifyCodeUtils.generateVerifyCode(4);
		//2.由于在点下一个按钮时要比较code，因此要保存到session作用域
		session.setAttribute("code", code);
		//3.根据随机数生成图片
		//4.通过response响应图片
		response.setContentType("image/png");
		ServletOutputStream os=response.getOutputStream();
		VerifyCodeUtils.outputImage(220,60, os, code);
	}
}
