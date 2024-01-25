package com.gl.annotation;

import com.gl.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于表示需要进行功能字段自动填充处理的某个方法
 */
@Target(ElementType.METHOD)//指定注解只能用在方法上
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
        //通过枚举来指定数据库操作类型，update和insert
        OperationType value();
}
