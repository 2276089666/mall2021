package com.cloud.mall.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 商品三级分类
 *
 * @author chenshun
 * @email 2276089666@qq.com
 * @date 2021-01-09 13:23:19
 */
@Data
@TableName("pms_category")
public class CategoryEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 分类id
     */
    @TableId
    private Long catId;
    /**
     * 分类名称
     */
    private String name;
    /**
     * 父分类id
     */
    private Long parentCid;
    /**
     * 层级
     */
    private Integer catLevel;
    /**
     * 是否显示[0-不显示，1显示]
     */
    @TableLogic(value = "1",delval = "0")//全局的逻辑删除和我们表中的值的显示与否与我们的逻辑相反,我们修改一下为1显示,0不显示
    private Integer showStatus;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 图标地址
     */
    private String icon;
    /**
     * 计量单位
     */
    private String productUnit;
    /**
     * 商品数量
     */
    private Integer productCount;

    /**
     * 为了树形展示,我们加一个属性,就是当前菜单的孩子
     */
    @TableField(exist = false)//数据库表中没这个属性,避免映射出错,我们排除这个属性
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)//如果这个对象是空的我们删除这个字段
    private List<CategoryEntity> children;

}
