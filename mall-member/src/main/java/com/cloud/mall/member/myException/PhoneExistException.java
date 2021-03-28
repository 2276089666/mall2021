package com.cloud.mall.member.myException;

/**
 * @Author ws
 * @Date 2021/3/28 12:32
 * @Version 1.0
 */
public class PhoneExistException extends RuntimeException{
    public PhoneExistException() {
        super("手机号已存在异常");
    }
}
