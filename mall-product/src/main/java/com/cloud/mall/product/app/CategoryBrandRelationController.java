package com.cloud.mall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cloud.common.utils.PageUtils;
import com.cloud.common.utils.R;
import com.cloud.mall.product.entity.BrandEntity;
import com.cloud.mall.product.service.BrandService;
import com.cloud.mall.product.vo.BrandVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.cloud.mall.product.entity.CategoryBrandRelationEntity;
import com.cloud.mall.product.service.CategoryBrandRelationService;


/**
 * 品牌分类关联
 *
 * @author ws
 * @email 2276089666@qq.com
 * @date 2021-01-09 14:08:00
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    /**
     * 获取当前品牌的所有分类列表
     */
    @GetMapping( "/catelog/list")
    public R getCateLogList(@RequestParam("brandId") Long brandId){
        QueryWrapper<CategoryBrandRelationEntity> queryWrapper = categoryBrandRelationService.getlist(brandId);
        List<CategoryBrandRelationEntity> list = categoryBrandRelationService.list(queryWrapper);
        return R.ok().put("data", list);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.saveDetail(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @Autowired
    BrandService brandService;

    @GetMapping("/brands/list")
    public R getBrandsList(@RequestParam("catId") Long catId){
        List <BrandEntity> list =brandService.selectBrand(catId);
        //1.controller接收数据和校验数据
        //2.调用service
        //3.以及封装相应的vo
        List<BrandVo> brandVos=null;
        /**
         * 判断集合一定要判断他是否为空，和size
         */
        if (list!=null&&!list.isEmpty()){
             brandVos= list.stream().map(a -> {
                BrandVo brandVo = new BrandVo();
                brandVo.setBrandId(a.getBrandId());
                brandVo.setBrandName(a.getName());
                return brandVo;
            }).collect(Collectors.toList());
        }
        return R.ok().put("data",brandVos);
    }

}
