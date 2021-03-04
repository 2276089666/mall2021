package com.cloud.mall.order.dao;

import com.cloud.mall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author ws
 * @email 2276089666@qq.com
 * @date 2021-01-09 16:31:37
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
