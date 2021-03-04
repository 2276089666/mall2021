package com.cloud.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.common.utils.PageUtils;
import com.cloud.mall.order.entity.OrderEntity;

import java.util.Map;

/**
 * 订单
 *
 * @author ws
 * @email 2276089666@qq.com
 * @date 2021-01-09 16:31:37
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

