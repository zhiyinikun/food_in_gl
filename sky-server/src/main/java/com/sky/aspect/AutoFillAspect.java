package com.sky.aspect;
//1.自定义注解AutoFile,用于表示需要进行公共字段自动填充的方法
//2.自定义切面类AutoFillAspect,统一拦截加入AutoFill注解的方法
//3.再Mapper的方法上加入AutoFill注解

import com.sky.annotation.AutoFill;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static com.sky.constant.AutoFillConstant.*;


@Aspect   //加入切面注解
@Component
@Slf4j
public class AutoFillAspect {
    /**
     * 切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")  //指定目录对其进行拦截,对mapper的所有类所有方法。(..)匹配其所有的形参类型。以及加了这个自定义AutoFile注解的进行拦截
    public void autoFillPointCut(){}

    /**
     * 前置通知，对公共字段赋值
     */
    @Before("autoFillPointCut()") //指定切点表达式
    public void autoFill(JoinPoint joinPoint){
        log.info("进行公共字段自动填充");

        //获取当前被拦截的方法上的数据库操作类型，因为拦截的是方法，所以转型为其子接口MethodSignature
        MethodSignature signature = (MethodSignature) joinPoint.getSignature(); //方法签名对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//获取方法上的注解对象
        OperationType operationType = autoFill.value();//获取数据库操作类型

        //获取当前被拦截的方法的参数--实体对象,也就是例如void insert(Employee employee);默认实体也就是里面的employee 是放在第一位也就是args[0]
        Object[] args = joinPoint.getArgs();
        if(args == null || args.length == 0){
            return; //不存在参数则直接return
        }
        Object entity = args[0];
        //准备赋值的数据
        LocalDateTime now = LocalDateTime.now(); //获取当前时间
        Long currentId = BaseContext.getCurrentId(); //从线程那获取id

        //根据不同的操作类型，为对应的属性通过反射进行赋值
        if(operationType == OperationType.INSERT){
            try {
                //反射set方法  为了避免方法名写错，定义了常量。用其替代
                Method setCreateTimes = entity.getClass().getDeclaredMethod(SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(SET_UPDATE_USER, Long.class);

                //通过对set方法的反射来赋值
                setCreateTimes.invoke(entity,now);
                setCreateUser.invoke(entity,currentId);
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(operationType == OperationType.UPDATE){
//反射set方法

            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(SET_UPDATE_USER, Long.class);

                //通过对set方法的反射来赋值
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }
}
