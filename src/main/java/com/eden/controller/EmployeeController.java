package com.eden.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import com.eden.entity.Employee;
import com.eden.service.EmployeeService;

@Controller
@RequestMapping("employee")
public class EmployeeController {

	private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);
	
	private EmployeeService employeeService;
	
	@Autowired
	public EmployeeController(EmployeeService employeeService) {
		super();
		this.employeeService = employeeService;
	}
	
	@Value("${file.upload.dir}")
	private String realpath;
	
	//删除员工信息
	@RequestMapping("delete")
	public String delete(Integer id) {
		log.debug("删除的员工id: {}",id);
		//1.删除数据
		String photo=employeeService.findById(id).getPhoto();
		
		employeeService.delete(id);
		//2.删除头像
		File file=new File(realpath,photo);
		if(file.exists()) file.delete();
		return "redirect:/employee/lists";
	}
	
	//更新员工信息
	@RequestMapping("update")
	public String update(Employee employee,MultipartFile img) throws IOException {
		log.debug("更新之后的员工信息：id:{},姓名:{},工资:{},生日:{}",employee.getId(),employee.getName(),employee.getSalary(),employee.getBirthday());
		//判断是否更新头像
		boolean notEmpty = !img.isEmpty();
		log.debug("更新了头像：{}",notEmpty);
		if(notEmpty) {//1.删除老的头像,根据id查询原始头像；
			//1.获取旧头像文件名
			String oldPhoto=employeeService.findById(employee.getId()).getPhoto();
			File file=new File(realpath,oldPhoto);
			if (file.exists()) 	file.delete();
			//2.处理新的头像上传
			String OriginalFilename=img.getOriginalFilename();
			/*
			 * String fileNamePrefix=new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new
			 * Date()); String
			 * fileNameSuffix=OriginalFilename.substring(OriginalFilename.lastIndexOf("."));
			 * String newFileName=fileNamePrefix+fileNameSuffix; img.transferTo(new
			 * File(realpath,newFileName));
			 */
			String newFileName=uploadPhoto(img, OriginalFilename);
			//3.修改员工新的头像名称
			employee.setPhoto(newFileName);
			//4.更新员工信息
		}
		//如果头像没有更新，直接更新员工信息
		employeeService.update(employee);
		
		
		return "redirect:/employee/lists";//更新成功调转到员工列表
	}

	private String uploadPhoto(MultipartFile img, String OriginalFilename) throws IOException {
		String fileNamePrefix=new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		String fileNameSuffix=OriginalFilename.substring(OriginalFilename.lastIndexOf("."));
		String newFileName=fileNamePrefix+fileNameSuffix;
		img.transferTo(new File(realpath,newFileName));
		return newFileName;
	}
	
	//根据id查询员工信息
	@RequestMapping("detail")
	public String detail(Integer id,Model model) {
		Employee employee=employeeService.findById(id);
		model.addAttribute("employee", employee);
		return "updateEmp";
	}

	//保存员工信息
	//文件上传提交表单的方式必须是post  2.表单enctype属性必须为 multipart/form-date
	@RequestMapping("save")
	public String save(Employee employee,MultipartFile img) throws  IOException {
		log.debug("姓名：{},薪资：{},生日：{}",employee.getName(),employee.getSalary(),employee.getBirthday());
		String originalFilename=img.getOriginalFilename();
		log.debug("头像名称：{}",originalFilename);
		log.debug("头像大小：{}",img.getSize());
		log.debug("上传的路径：{}",realpath);
		
		//1.处理头像的上传&&修改文件名		
		/*
		 * String fileNameProfix=new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new
		 * Date()); String
		 * fileNameSuffix=originalFilename.substring(originalFilename.lastIndexOf("."));
		 * String newFileName=fileNameProfix+fileNameSuffix; //img.transferTo(new
		 * File(realpath,img.getOriginalFilename())); img.transferTo(new
		 * File(realpath,newFileName));
		 */
		String newFileName=uploadPhoto(img, originalFilename);
		 
		 //2.保存员工信息
		 //employee.setPhoto(realpath+"/"+newFileName);
		 employee.setPhoto(newFileName);//保存头像名
		 employeeService.save(employee);
		 return "redirect:/employee/lists";//保存成功跳转到列表
	}

	//员工列表
	@RequestMapping("lists")
	public String lists(Model model) {
		log.debug("查询所有员工信息");
		List<Employee> employeeList= employeeService.lists();
		model.addAttribute("employeeList", employeeList);
		return "emplist";
	}
}
