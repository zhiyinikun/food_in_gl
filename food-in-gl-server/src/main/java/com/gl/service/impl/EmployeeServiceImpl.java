package com.gl.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.gl.constant.MessageConstant;
import com.gl.constant.PasswordConstant;
import com.gl.constant.StatusConstant;
import com.gl.context.BaseContext;
import com.gl.dto.EmployeeDTO;
import com.gl.dto.EmployeeLoginDTO;
import com.gl.dto.EmployeePageQueryDTO;
import com.gl.entity.Employee;
import com.gl.exception.AccountLockedException;
import com.gl.exception.AccountNotFoundException;
import com.gl.exception.PasswordErrorException;
import com.gl.mapper.EmployeeMapper;
import com.gl.result.PageResult;
import com.gl.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;



    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        //密码比对
        //对前端密码进行MD5加密，DigestUtils的方法加密的结果与messageDigest的方法加密结果一致，这里使用DigestUtils替换MessageDigest 可省掉部分代码
        password = DigestUtils.md5DigestAsHex(password.getBytes());   //需要通过getBytes()将字符串转化成数组
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 添加员工
     * @param employeeDTO
     * @return
     */ @Override
    public void save(EmployeeDTO employeeDTO) {
         //实体类  因为前端给的参数与实体类有差距，所以用employeeDTO接收，这里再转换
        Employee employee = new Employee();

        //对象copy
        BeanUtils.copyProperties(employeeDTO,employee);

        //对employee的其他属性进行赋值
        //利用封装好的类
        //账号的状态，1即正常，2即锁定
        employee.setStatus(StatusConstant.ENABLE);

        //创建时间以及更新时间
//        employee.setCreateTime(LocalDateTime.now());  //LocalDateTime.now()当前系统时间
//        employee.setUpdateTime(LocalDateTime.now());

        //创建密码，设置默认密码123456，后续可更改.进行md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

        //用户id，和修改id ，通过ThreadLocal来获取
//        employee.setCreateUser(BaseContext.getCurrentId());
//        employee.setUpdateUser(BaseContext.getCurrentId());
        //通过Mapper插入数据
        employeeMapper.insert(employee);


    }

    /**
     * 分页查询
     * @param employeePageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        //利用mybatis的Pagehelper插件，底层还是利用ThreadLocal来进行分享分页信息，动态的利用limit和传入的页码和每页记录数写入到下面mapper的里
        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());

        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);

        //进行转换，因为需要返回的是PageResult类型，而利用Pagehelper返回的是Page类型
        long total = page.getTotal();
        List<Employee> records = page.getResult();
        return new PageResult(total,records);
    }

    /**
     * 启动禁用账号
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, long id) {
//        employeeMapper.startOrStop(status,id); 普通写法，直接传入status和id

//        Employee employee = new Employee();   普通的new对象
//        employee.setStatus(status);
//        employee.setId(id);

        //跟上面方法效果一样，但是是利用了build注解
        Employee employee = Employee.builder()
                .status(status)
                .id(id)
                .build();
        employeeMapper.update(employee);  //为了代码复用性
    }

    /**
     * 根据id获取员工信息
     * @param id
     * @return
     */
    @Override
    public Employee getById(long id) {
         Employee employee = employeeMapper.getById(id);
        //因为前端要的是全部实体属性，本来可以通过VO删减掉password，这里不改前端，所以就先把密码重写赋值给前端
        employee.setPassword("******");
        return employee;
    }

    @Override
    public void update(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO,employee);
        employee.setStatus(StatusConstant.ENABLE);

//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(BaseContext.getCurrentId());
        //复用了上面更改账号启用禁用的mapper
        employeeMapper.update(employee);
    }


}
