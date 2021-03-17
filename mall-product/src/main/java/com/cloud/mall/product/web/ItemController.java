package com.cloud.mall.product.web;

import com.cloud.mall.product.service.SkuInfoService;
import com.cloud.mall.product.vo.ItemVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;

/**
 * @Author ws
 * @Date 2021/3/16 14:40
 * @Version 1.0
 */
//商品详情
@Controller
@Slf4j
public class ItemController {

    @Autowired
    SkuInfoService skuInfoService;

    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {

        log.info("商品详情的id:{}", skuId);
        ItemVo item = skuInfoService.item(skuId);
        model.addAttribute("item",item);
        return "item";
    }
}
