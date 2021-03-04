package com.cloud.mall.product.feign;

import com.cloud.common.to.es.SkuEsModel;
import com.cloud.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Author ws
 * @Date 2021/3/2 20:21
 * @Version 1.0
 */
@FeignClient("mall-search")
public interface SearchFeignService {
    @PostMapping("/search/save/product")
    R productStatusUp(@RequestBody List<SkuEsModel> list);
}
