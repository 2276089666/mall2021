package com.cloud.mall.thirdParty.controller;

import com.cloud.common.utils.R;
import com.cloud.mall.thirdParty.component.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author ws
 * @Date 2021/3/27 19:28
 * @Version 1.0
 */
@RestController
@RequestMapping("/sms")
public class SmsController {

    @Autowired
    SmsComponent smsComponent;

    /**
     * 提供给mall-authserver服务调用
     * @param phone
     * @param code
     * @return
     */
    @GetMapping("/code")
    public R sendCode(@RequestParam("phone") String phone,@RequestParam("code") String code){
        smsComponent.sendSmsCode(phone,code);
        return R.ok();
    }
}
