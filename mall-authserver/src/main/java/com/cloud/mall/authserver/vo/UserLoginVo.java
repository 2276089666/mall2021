package com.cloud.mall.authserver.vo;

import lombok.Data;

/**
 * @Author ws
 * @Date 2021/3/28 14:26
 * @Version 1.0
 */
@Data
public class UserLoginVo {
    private String loginAccount;
    private String password;
}
