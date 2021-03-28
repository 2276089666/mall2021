package com.cloud.common.exception;

/**
 * @Author ws
 * @Date 2021/1/25 9:16
 * @Version 1.0
 */

/**
 * 错误码规则定义为5位数字,前两位表示业务场景,最后三位表示错误码,例如10001 10:通用 001:系统未知异常
 * 错误码列表:
 * 10:通用
 * 11:商品
 * 12:订单
 * 13:购物车
 * 14:物流
 * 15:用户
 */
public enum ExceptionCode {
    UNKNOWN_EXCEPTION(10000,"系统未知异常"),
    SMS_CODE_EXCEPTION(10002,"短信验证码频率过高"),
    VALID_EXCEPTION(10001,"参数格式校验异常"),
    USER_EXIST_EXCEPTION(15001,"用户名已存在异常"),
    PHONE_EXIST_EXCEPTION(15002,"手机号已存在异常"),
    LOGIN_ACCOUNT_OR_PASSWORD_EXCEPTION(15003,"账号或者密码错误"),
    PRODUCT_ON_ES_EXCEPTION(11000,"商品上架异常");


    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    private int code;
    private String message;

    ExceptionCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
