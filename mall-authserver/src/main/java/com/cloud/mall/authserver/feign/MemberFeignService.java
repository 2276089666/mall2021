package com.cloud.mall.authserver.feign;

import com.cloud.common.utils.R;
import com.cloud.mall.authserver.vo.UserLoginVo;
import com.cloud.mall.authserver.vo.UserRegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author ws
 * @Date 2021/3/28 13:23
 * @Version 1.0
 */
@FeignClient("mall-member")
public interface MemberFeignService {


    @PostMapping("member/member/register")
    R register(@RequestBody UserRegisterVo registerVo);

    @PostMapping("member/member/login")
    R login(@RequestBody UserLoginVo loginVo);
}
