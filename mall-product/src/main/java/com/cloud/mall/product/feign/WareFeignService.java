package com.cloud.mall.product.feign;

import com.cloud.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Author ws
 * @Date 2021/3/2 16:53
 * @Version 1.0
 */
@FeignClient("mall-ware")
public interface WareFeignService {
    @PostMapping("ware/waresku/hasStock")
    R getHasStock(@RequestBody List<Long> skuIds);
}
