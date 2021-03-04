package com.cloud.mall.product.feign;

import com.cloud.common.to.SkuReductionTo;
import com.cloud.common.to.SpuBoundTo;
import com.cloud.common.utils.R;
import org.checkerframework.checker.fenum.qual.SwingTitlePosition;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author ws
 * @Date 2021/2/19 17:19
 * @Version 1.0
 */
@FeignClient("mall-coupon")
public interface CouponFeignService {
    /**
     *      远程的mall-coupon服务的请求体的实体类不一致
     *      @PostMapping("/save")
     *      public R save(@RequestBody SpuBoundsEntity spuBounds)
     *
     *      服务调用A调B（POST方式）,如果B服务的接口需要参数，A服务会先把传入的参数SpuBoundTo转为json放在请求体里面，服
     *      务B接收请求时会把传来的json转换成对应的实体类SpuBoundsEntity，只要json兼容就行。
     * @param spuBoundTo
     * @return
     */

    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);

    @PostMapping("/coupon/skufullreduction/saveInfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
