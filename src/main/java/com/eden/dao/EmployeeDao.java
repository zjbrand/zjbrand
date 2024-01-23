package com.eden.dao;
import java.util.List;

import com.eden.entity.Employee;

public interface EmployeeDao {
	//员工列表
	List<Employee> lists();
	//保存员工信息
	void save(Employee employee);
	//根据id查询员工信息
	Employee findById(Integer id);
		//更新员工信息
	void update(Employee employee);

	//删除员工信息
	void delete(Integer id);
}
