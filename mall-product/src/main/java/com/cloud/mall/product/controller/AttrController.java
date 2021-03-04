package com.cloud.mall.product.controller;

import java.util.Arrays;
import java.util.Map;

import com.cloud.common.utils.PageUtils;
import com.cloud.common.utils.R;
import com.cloud.mall.product.vo.AttrResponseVo;
import com.cloud.mall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloud.mall.product.service.AttrService;


/**
 * 商品属性
 *
 * @author ws
 * @email 2276089666@qq.com
 * @date 2021-01-09 14:08:04
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    /**
     * 基本属性列表或者销售属性列表,通过路径变量type来判断
     */
    @RequestMapping("/{type}/list/{catelogId}")
    public R baseList(@RequestParam Map<String, Object> params,@PathVariable("catelogId") Long catelogId,@PathVariable("type") String type){
        PageUtils page = attrService.queryPages(params,catelogId,type);

        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId){
		AttrResponseVo attrResponse = attrService.getAttrInfo(attrId);
        return R.ok().put("attr", attrResponse);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrVo attrVo){

        attrService.saveAttr(attrVo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrVo attrVo){
		attrService.updateAttr(attrVo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeAttrAndRelationByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
