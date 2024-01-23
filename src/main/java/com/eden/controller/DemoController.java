package com.eden.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("demo1")
public class DemoController {
	
	@RequestMapping("demo2")//restFUL
	public String demo() {
		System.out.println("hello thymeleaf");
		return "demo";
	}

}
