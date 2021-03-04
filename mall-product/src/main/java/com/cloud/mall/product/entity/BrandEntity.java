package com.cloud.mall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.zip.Adler32;

import com.cloud.common.annotation.ListValue;
import com.cloud.common.validgroup.AddGroup;
import com.cloud.common.validgroup.UpdateGroup;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * 品牌
 * 
 * @author chenshun
 * @email 2276089666@qq.com
 * @date 2021-01-09 13:23:20
 */

/**
 * 分组校验:
 * 1.创建一个空接口,例如:UpdateGroup,AddGroup
 * 2.在controller处使用@Validated(value = {AddGroup.class}),并指定我们要使用的组
 * 3.对我们实体类的校验注解加上groups属性:Validated的value是什么组我们的校验注解才生效
 * 4.对于使用Validated加上了组value的接口校验,我们实体类的校验注解如果没指明group,在分组校验时不生效
 *
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	//当是新增的时候我们AddGroup这个组校验,修改的时候我们UpdateGroup这个组校验
	@NotNull(message = "修改的时候品牌id不能为空",groups = UpdateGroup.class)
	@Null(message = "新增的时候品牌id必须为空",groups = AddGroup.class)
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "名称不能为空",groups = {AddGroup.class,UpdateGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotBlank(message = "logo的URL不能为空",groups = {AddGroup.class})
	//当这个属性有值时URL这个校验要生效
	@URL(message = "Logo必须为一个合法的URL地址",groups = {AddGroup.class,UpdateGroup.class})
	private String logo;
	/**
	 * 介绍
	 */
	@NotEmpty(message = "介绍不能为空",groups = {AddGroup.class})
	private String descript;
	/**
	 * 自定义校验器:
	 * 1.编写一个自定义的校验注解,例:@ListValue
	 * 2.编写一个自定义的校验器,例:ListValueConstraintValidator
	 * 3.关联检验注解和校验器 例:validatedBy = {ListValueConstraintValidator.class}
	 */
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	//自定义的校验注解
	@ListValue(values = {0,1},message = "显示状态必须为0或1的Integer类型",groups = {AddGroup.class,})
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotNull(message = "检索首字母不能为空",groups = {AddGroup.class})
	@Pattern(regexp = "^[a-zA-z]$",message = "首字母必须为a-z或者A-Z之间的一个字母",groups = {AddGroup.class,UpdateGroup.class})//自己定制的校验,写自己的正则表达式
	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull(message = "排序不能为空",groups = {AddGroup.class})
	@Min(value = 0,message = "排序必须是大于等于0的整数",groups = {AddGroup.class,UpdateGroup.class})
	private Integer sort;

}
