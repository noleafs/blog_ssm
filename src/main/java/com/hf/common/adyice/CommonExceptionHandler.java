package com.hf.common.adyice;

import com.hf.common.exception.HfException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// 通用异常处理 此注解会拦截所有带有Controller注解的中抛出的异常
@ControllerAdvice
public class CommonExceptionHandler {


    @ExceptionHandler(HfException.class) //指定如果抛出的的异常是LyException就会执行此方法
    public  ResponseEntity<String> handleException(HfException e, HttpServletResponse response){

        return ResponseEntity.status(e.getCode()).body(e.getBody());
    }
}
