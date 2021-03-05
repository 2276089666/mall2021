package com.cloud.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.common.utils.PageUtils;
import com.cloud.common.utils.Query;
import com.cloud.mall.product.dao.CategoryDao;
import com.cloud.mall.product.entity.CategoryEntity;
import com.cloud.mall.product.service.CategoryBrandRelationService;
import com.cloud.mall.product.service.CategoryService;
import com.cloud.mall.product.vo.Category2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //查出所以分类
        List<CategoryEntity> all = baseMapper.selectList(null);

        //组装成父子的树形结构

        //找到所有的1级分类
        List<CategoryEntity> collect = all.stream().filter((categoryEntity) ->
                categoryEntity.getParentCid() == 0
        ).map((menu) -> {
            //找到当前菜单的子菜单
            menu.setChildren(getChildren(menu, all));
            return menu;
        }).sorted((menu1, menu2) -> {
            //通过实体的sort属性对我们子菜单list里面的categoryEntity排序
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        })
                .collect(Collectors.toList());
        return collect;
    }

    /**
     * 递归查找所有菜单的子菜单
     *
     * @param currentCategoryEntity 当前菜单
     * @param all                   所有菜单
     * @return 当前菜单的子菜单list
     */
    public List<CategoryEntity> getChildren(CategoryEntity currentCategoryEntity, List<CategoryEntity> all) {
        List<CategoryEntity> collect = all.stream().filter((menu) -> {
            //找出所有菜单中实体的父菜单id与当前菜单的菜单id相同的CategoryEntity
            return menu.getParentCid() == currentCategoryEntity.getCatId();
        }).map((menu) -> {
            //但是上面过滤的菜单中还会有一些菜单的子菜单是我们要找的菜单,所以我们要递归映射一下
            menu.setChildren(getChildren(menu, all));
            return menu;
        }).sorted((menu1, menu2) -> {
            //通过实体的sort属性对我们子菜单list里面的categoryEntity排序
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        })
                .collect(Collectors.toList());
        return collect;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 逻辑删除
        /**
         * 1.配置全局逻辑删除[logic-delete-value: 1 # 逻辑已删除值(默认为 1)
         *                 logic-not-delete-value: 0 # 逻辑未删除值(默认为 0)]
         * 2.在我的实体类给我们的字段加上逻辑删除使用的列
         * @TableLogic(value = "1",delval = "0")
         * 3.测试,我们被删除的show_status列的值变为0
         * 4.详细见官网https://mp.baomidou.com/guide/logic-delete.html#%E4%BD%BF%E7%94%A8%E6%96%B9%E6%B3%95
         */
        baseMapper.deleteBatchIds(asList);
    }

    /**
     * 祖先/父/自己  路径[2,35,229]
     *
     * @param catelogId
     * @return
     */
    @Override
    public Long[] findCateLogPath(Long catelogId) {
        List<Long> list = new ArrayList<>();
        list = getPath(list, catelogId);
        return list.toArray(new Long[list.size()]);
    }

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Override
    @Transactional(rollbackFor = Exception.class)//级联更新要加上事务
    public void cascadeUpdate(CategoryEntity category) {
        this.updateById(category);
        if (!StringUtils.isEmpty(category.getName())) {
            categoryBrandRelationService.updateCategoryDetail(category.getCatId(), category.getName());
        }
        // TODO: 2021/2/16  一旦修改表的数据记得修改关联表的冗余数据
    }

    @Override
    public List<CategoryEntity> getLevelOne() {
        List<CategoryEntity> categoryEntityList = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return categoryEntityList;
    }

    @Override
    public Map<String, List<Category2Vo>> getCategoryJson() {
        //查出所有的一级分类
        List<CategoryEntity> levelOne = getLevelOne();
        Map<String, List<Category2Vo>> map = levelOne.stream().collect(Collectors.toMap(a -> a.getCatId().toString(), b -> {
            //查出所有的二级分类
            List<CategoryEntity> categoryEntityList = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", b.getCatId()));
            List<Category2Vo> category2VoList = null;
            if (categoryEntityList != null && !categoryEntityList.isEmpty()) {
                //构造二级分类vo
                category2VoList = categoryEntityList.stream().map(c -> {
                    Category2Vo category2Vo = new Category2Vo();
                    category2Vo.setCatalog1Id(b.getCatId().toString());
                    category2Vo.setId(c.getCatId().toString());
                    category2Vo.setName(c.getName());
                    //找出二级分类的属性List<catalog3> catalog3List并赋值
                    List<CategoryEntity> catalog3List = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", c.getCatId()));
                    if (catalog3List!=null&&!catalog3List.isEmpty()){
                        //封装catalog3集合
                        List<Category2Vo.catalog3> catalog3s = catalog3List.stream().map(d -> {
                            Category2Vo.catalog3 catalog3 = new Category2Vo.catalog3();
                            catalog3.setCatalog2Id(c.getCatId().toString());
                            catalog3.setId(d.getCatId().toString());
                            catalog3.setName(d.getName());
                            return catalog3;
                        }).collect(Collectors.toList());
                        category2Vo.setCatalog3List(catalog3s);
                    }
                    return category2Vo;
                }).collect(Collectors.toList());
            }
            return category2VoList;
        }));
        return map;
    }

    private List<Long> getPath(List<Long> list, Long catelogId) {
        CategoryEntity categoryEntity = this.getById(catelogId);
        if (categoryEntity.getParentCid() != 0) {
            getPath(list, categoryEntity.getParentCid());
        }
        list.add(categoryEntity.getCatId());
        return list;
    }


}