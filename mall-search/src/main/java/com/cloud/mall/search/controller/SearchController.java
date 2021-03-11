package com.cloud.mall.search.controller;

import com.cloud.mall.search.service.MallSearchService;
import com.cloud.mall.search.vo.SearchParamVo;
import com.cloud.mall.search.vo.SearchResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Author ws
 * @Date 2021/3/11 16:54
 * @Version 1.0
 */
@Controller
public class SearchController {

    @Autowired
    MallSearchService mallSearchService;

    @GetMapping("/list.html")
    public String listPage(SearchParamVo searchParamVo, Model model){
        SearchResponseVo searchResponseVo=mallSearchService.search(searchParamVo);
        model.addAttribute("result",searchResponseVo);
        return "list";
    }
}
