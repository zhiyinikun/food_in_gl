package com.gl.service;

import com.gl.dto.EmployeeDTO;
import com.gl.dto.EmployeeLoginDTO;
import com.gl.dto.EmployeePageQueryDTO;
import com.gl.entity.Employee;
import com.gl.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);



    /**
     * 添加员工
     * @param employeeDTO
     * @return
     */
    void save(EmployeeDTO employeeDTO);

    /**
     * 分页查询
     * @param employeePageQueryDTO
     * @return
     */
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 启动禁用账号
     * @param status
     * @param id
     */
    void startOrStop(Integer status, long id);

    /**
     * 根据id获取员工信息
     * @param id
     * @return
     */
    Employee getById(long id);

    /**
     * 编辑员工信息
     * @param employeeDTO
     */
    void update(EmployeeDTO employeeDTO);
}
