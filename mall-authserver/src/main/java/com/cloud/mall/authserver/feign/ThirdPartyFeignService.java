package com.cloud.mall.authserver.feign;

import com.cloud.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author ws
 * @Date 2021/3/27 19:37
 * @Version 1.0
 */
@FeignClient(value = "mall-thirdParty")
public interface ThirdPartyFeignService {
    @GetMapping("/sms/code")
    R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code);
}
