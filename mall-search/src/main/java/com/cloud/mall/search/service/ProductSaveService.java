package com.cloud.mall.search.service;

import com.cloud.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @Author ws
 * @Date 2021/3/2 19:42
 * @Version 1.0
 */
public interface ProductSaveService {
    Boolean productStatusUp(List<SkuEsModel> list) throws IOException;
}
