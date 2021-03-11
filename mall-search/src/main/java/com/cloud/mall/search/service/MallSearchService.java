package com.cloud.mall.search.service;

import com.cloud.mall.search.vo.SearchParamVo;
import com.cloud.mall.search.vo.SearchResponseVo;

/**
 * @Author ws
 * @Date 2021/3/11 19:43
 * @Version 1.0
 */
public interface MallSearchService {
    SearchResponseVo search(SearchParamVo searchParamVo);
}
