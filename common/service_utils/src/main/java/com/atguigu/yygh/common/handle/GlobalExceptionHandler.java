package com.atguigu.yygh.common.handle;


import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.result.R;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    //Exception异常处理
    @ExceptionHandler(value = Exception.class)
    public R handleException(Exception exception){
        exception.printStackTrace(); //可以写入日志，不用打印控制台，方便排错
        return R.error().message(exception.getMessage());
    }

    //SQLException异常处理
    @ExceptionHandler(value = SQLException.class)
    public R handleSQLException(SQLException sqlException){
        sqlException.printStackTrace();
        return R.error().message(sqlException.getMessage());
    }

    //SQLException异常处理
    @ExceptionHandler(value = ArithmeticException.class)
    public R handleArithmeticException(ArithmeticException arithmeticException){
        arithmeticException.printStackTrace();
        return R.error().message(arithmeticException.getMessage());
    }

    //自定义Exception异常处理
    @ExceptionHandler(value = YyghException.class)
    public R handleYyghException(YyghException yyghException){
        yyghException.printStackTrace();
        R r = R.error();
        r.code(yyghException.getCode());
        r.message(yyghException.getMassage());
        return r;
    }
}
