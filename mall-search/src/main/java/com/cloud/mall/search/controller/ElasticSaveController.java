package com.cloud.mall.search.controller;

import com.cloud.common.exception.ExceptionCode;
import com.cloud.common.to.es.SkuEsModel;
import com.cloud.common.utils.R;
import com.cloud.mall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * @Author ws
 * @Date 2021/3/2 19:26
 * @Version 1.0
 */
@RestController
@RequestMapping("/search/save")
@Slf4j
public class ElasticSaveController {

    @Autowired
    ProductSaveService productSaveService;

    @PostMapping("/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> list)  {
        Boolean aBoolean=false;
        try {
             aBoolean= productSaveService.productStatusUp(list);
        }catch (Exception e){
            log.error("ElasticSaveController商品上架异常:{}",e);
            return R.error(ExceptionCode.PRODUCT_ON_ES_EXCEPTION.getCode(),ExceptionCode.PRODUCT_ON_ES_EXCEPTION.getMessage());
        }
        return aBoolean ? R.error(ExceptionCode.PRODUCT_ON_ES_EXCEPTION.getCode(),ExceptionCode.PRODUCT_ON_ES_EXCEPTION.getMessage()):R.ok();
    }
}
