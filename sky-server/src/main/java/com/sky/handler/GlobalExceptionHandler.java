package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        //提取错误日志 Duplicate entry 'cxk' for key 'employee.idx_username'
        String message = ex.getMessage();
        //判断输出的是否是上面日志信息
        if(message.contains("Duplicate entry")){
            //按空格进行截取'cxk'即用户名
            String[] split = message.split(" ");
            String username = split[2];
            String msg = username + MessageConstant.ACCOUNT_EXISTS;
            return Result.error(msg);
        }else {
            return Result.error("未知错误");
        }
    }

}
