package com.cloud.mall.product.exception;

import com.cloud.common.exception.ExceptionCode;
import com.cloud.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

/**
 * @Author ws
 * @Date 2021/1/25 8:52
 * @Version 1.0
 */

/**
 * 统一处理异常
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.cloud.mall.product.controller")
public class MallExceptionControllerAdvice {


    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R validExceptionHandler(MethodArgumentNotValidException e){
        BindingResult bindingResult = e.getBindingResult();
        HashMap<String, String> errorMap = new HashMap<>();
        bindingResult.getFieldErrors().forEach((error)->{
            // 实体类的一个属性最好标一个校验注解,不然当一个属性出现多个异常时,我们的map的key就会重复,导致出错
            errorMap.put(error.getField(),error.getDefaultMessage());
        });
        return R.error(ExceptionCode.VALID_EXCEPTION.getCode(),ExceptionCode.VALID_EXCEPTION.getMessage()).put("data",errorMap);
    }

//    @ExceptionHandler(value = Throwable.class)
//    public R unknownExceptionHandler(Throwable throwable){
//        return R.error(ExceptionCode.UNKNOWN_EXCEPTION.getCode(),ExceptionCode.UNKNOWN_EXCEPTION.getMessage());
//    }

}
