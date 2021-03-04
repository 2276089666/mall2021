package com.cloud.mall.coupon.feign;
import org.springframework.cloud.openfeign.FeignClient;


/**
 * @Author ws
 * @Date 2021/2/19 17:19
 * @Version 1.0
 */
@FeignClient("mall-product")
public interface ProductFeignService {

}
