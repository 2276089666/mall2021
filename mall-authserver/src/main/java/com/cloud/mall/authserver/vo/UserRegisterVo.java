package com.cloud.mall.authserver.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @Author ws
 * @Date 2021/3/27 20:44
 * @Version 1.0
 */
@Data
public class UserRegisterVo {
    @NotEmpty(message = "用户名不能为空")
    @Length(min = 6,max = 18,message = "用户名必须为6-18位")
    private String userName;

    @NotEmpty(message = "密码不能为空")
    @Length(min = 6,max = 18,message = "密码必须为6-18位")
    private String password;

    @NotEmpty(message = "手机号不能为空")
    // 手机号以1开头 第二位3-9 后面9位任意
    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$",message = "手机号格式不正确")
    private String phone;

    @NotEmpty(message = "验证码不能为空")
    private String code;
}
