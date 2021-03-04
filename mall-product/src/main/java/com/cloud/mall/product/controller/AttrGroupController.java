package com.cloud.mall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.cloud.common.utils.PageUtils;
import com.cloud.common.utils.R;
import com.cloud.mall.product.entity.AttrEntity;
import com.cloud.mall.product.service.AttrAttrgroupRelationService;
import com.cloud.mall.product.service.AttrService;
import com.cloud.mall.product.service.CategoryService;
import com.cloud.mall.product.vo.AttrGroupRelationVo;
import com.cloud.mall.product.vo.AttrGroupWithAttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.cloud.mall.product.entity.AttrGroupEntity;
import com.cloud.mall.product.service.AttrGroupService;


/**
 * 属性分组
 *
 * @author ws
 * @email 2276089666@qq.com
 * @date 2021-01-09 14:08:04
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    CategoryService categoryService;
    /**
     * 列表
     */
    @RequestMapping("/list/{cateLogId}")
    public R list(@RequestParam Map<String, Object> params,@PathVariable("cateLogId") Long cateLogId){
//        PageUtils page = attrGroupService.queryPage(params);
        PageUtils queryPage=attrGroupService.queryPages(params,cateLogId);
        return R.ok().put("page", queryPage);
    }

    @Autowired
    AttrService attrService;

    /**
     * 查询属性分组的关联
     * @param attrgroupId
     * @return
     */
    @GetMapping("/{attrgroupId}/attr/relation")
    public R getRelationList(@PathVariable("attrgroupId") Long attrgroupId){
        List<AttrEntity> data=attrService.getAttrs(attrgroupId);
        return R.ok().put("data",data);
    }

    @GetMapping("/{attrgroupId}/noattr/relation")
    public R getNoRelationList(@RequestParam Map<String,Object> params ,@PathVariable("attrgroupId") Long attrgroupId){
        PageUtils queryPage=attrService.getNoRelationAttrs(params,attrgroupId);
        return R.ok().put("page",queryPage);
    }

    /**
     * 获取分类下所有分组&关联属性
     * @param catelogId
     * @return
     */
    @GetMapping("/{catelogId}/withattr")
    public R getAttrAndGroup(@PathVariable("catelogId") Long catelogId){
        List <AttrGroupWithAttrVo> attrGroupWithAttrVos= attrGroupService.getAttrAndGroupByCatelogId(catelogId);
        return R.ok().put("data",attrGroupWithAttrVos);
    }


    /**
     * 新建关联关系
     * @param list
     * @return
     */
    @PostMapping("/attr/relation")
    public R saveRelation(@RequestBody List <AttrGroupRelationVo> list){
        attrAttrgroupRelationService.saveBach(list);
        return R.ok();
    }

    @Autowired
    AttrAttrgroupRelationService attrAttrgroupRelationService;

    @PostMapping("/attr/relation/delete")
    public R deleteAttrGroupRelations(@RequestBody List<AttrGroupRelationVo> list){
        attrAttrgroupRelationService.delete(list);
        return R.ok();
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        Long catelogId = attrGroup.getCatelogId();
        Long[] path=categoryService.findCateLogPath(catelogId);
        attrGroup.setCatelogPath(path);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
