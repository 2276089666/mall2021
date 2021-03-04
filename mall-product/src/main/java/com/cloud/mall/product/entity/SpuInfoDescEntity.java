package com.cloud.mall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * spu信息介绍
 * 
 * @author chenshun
 * @email 2276089666@qq.com
 * @date 2021-01-09 13:23:19
 */
@Data
@TableName("pms_spu_info_desc")
public class SpuInfoDescEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 商品id
	 */
	@TableId(type = IdType.INPUT)//当我们自己输入主键的时候要设置IdType，不然它默认是自增的
	private Long spuId;
	/**
	 * 商品介绍
	 */
	private String decript;

}
