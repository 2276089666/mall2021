package com.cloud.mall.product.web;

import com.cloud.mall.product.entity.CategoryEntity;
import com.cloud.mall.product.service.CategoryService;
import com.cloud.mall.product.vo.Category2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @Author ws
 * @Date 2021/3/5 14:59
 * @Version 1.0
 */
@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;

    @GetMapping(value = {"/index","/index.html","/"})
    public String indexPage(Model model){
        List<CategoryEntity> categoryEntityList =categoryService.getLevelOne();

        model.addAttribute("categorys",categoryEntityList);
        //因为有mvc的前缀和后缀拼串,这个请求就会返回到src\main\resources\templates\index.html
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String,List<Category2Vo>> getCategoryJson(){
        return categoryService.getCategoryJson();
    }
}
