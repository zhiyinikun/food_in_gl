package com.gl.controller.admin;

import com.gl.constant.JwtClaimsConstant;
import com.gl.dto.EmployeeDTO;
import com.gl.dto.EmployeeLoginDTO;
import com.gl.dto.EmployeePageQueryDTO;
import com.gl.entity.Employee;
import com.gl.properties.JwtProperties;
import com.gl.result.PageResult;
import com.gl.result.Result;
import com.gl.service.EmployeeService;
import com.gl.utils.JwtUtil;
import com.gl.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Api(tags = "员工相关接口")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;



    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        //封装给前端
        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation(value = "员工退出")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增
     * @param employeeDTO
     * @return
     */
    @PostMapping
    @ApiOperation(value = "新增员工")
    public Result save(@RequestBody EmployeeDTO employeeDTO){
        log.info("新增员工：{}", employeeDTO);
        employeeService.save(employeeDTO);
        return Result.success();
    }

    /**
     * 分页查询
     * @param employeePageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "分页查询")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("分页查询参数为：{}", employeePageQueryDTO);
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 启动禁用账号
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation(value = "启动禁用账号")
    public Result startOrStop(@PathVariable Integer status, Long id){  //路径参数用@PathVariable注解
        log.info("启动禁用账号：状态{},id{}",status,id);
        employeeService.startOrStop(status,id);
        return Result.success();
    }

    /**
     * 通过id获取用户信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "通过id获取用户信息")
    public Result<Employee> getById(@PathVariable long id){
        log.info("用户id：{}",id);
        Employee employee = employeeService.getById(id);
        return  Result.success(employee);
    }

    /**
     * 编辑员工信息
     */
    @PutMapping("")
    @ApiOperation(value = "编辑员工信息")
    public Result update(@RequestBody EmployeeDTO employeeDTO){
        log.info("需要修改用户的数据是：{}",employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }



}
