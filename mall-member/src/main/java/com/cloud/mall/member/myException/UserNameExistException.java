package com.cloud.mall.member.myException;

/**
 * @Author ws
 * @Date 2021/3/28 12:34
 * @Version 1.0
 */
public class UserNameExistException extends RuntimeException{
    public UserNameExistException() {
        super("用户名已存在异常");
    }
}
