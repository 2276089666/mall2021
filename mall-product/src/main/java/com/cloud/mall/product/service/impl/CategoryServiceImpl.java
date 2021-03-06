package com.cloud.mall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("categoryService")
@Slf4j
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

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     * 引入redis缓存
     *
     * @return
     */
    @Override
    public Map<String, List<Category2Vo>> getCategoryJson() {
        // TODO: 2021/3/6 1. 空结果缓存:解决缓存穿透(多个请求对0个缓存 n:0)   2.设置过期时间(加随机值):解决缓存雪崩(多个请求对多个缓存同时失效 n:n)   3.加锁:解决缓存击穿 (多个请求对1个缓存,这个缓存失效 n:1)

        String categoryJson = stringRedisTemplate.opsForValue().get("categoryJson");
        if (StringUtils.isEmpty(categoryJson)) {
            Map<String, List<Category2Vo>> categoryJsonBySql = getCategoryJsonBySql();
            if (categoryJsonBySql == null) {
                //设置空串,解决缓存穿透
                stringRedisTemplate.opsForValue().set("categoryJson", "", 1, TimeUnit.DAYS);
            }
            return categoryJsonBySql;
        }
        Map<String, List<Category2Vo>> map = JSON.parseObject(categoryJson, new TypeReference<Map<String, List<Category2Vo>>>() {
        });
        return map;
    }

    /**
     * 需要查询数据库等业务方法
     *
     * @return
     */
    //本地锁锁不住别的服务,有多少相同服务名的当前模块的微服务我们就查询多少次数据库
    public synchronized Map<String, List<Category2Vo>> getCategoryJsonBySql() {
        //看看是否已经有线程查过数据库并更新到缓存
        String categoryJson = stringRedisTemplate.opsForValue().get("categoryJson");
        if (categoryJson!=null&&!categoryJson.isEmpty()){
            Map<String, List<Category2Vo>> map = JSON.parseObject(categoryJson, new TypeReference<Map<String, List<Category2Vo>>>() {
            });
            return map;
        }
        System.out.println("缓存没命中");
        /**
         * 2.0为了减少程序和数据库的频繁交互,我们决定将数据先统一查出来,再过滤
         */
        //2.0查出所有的CategoryEntity
        List<CategoryEntity> categoryEntityListAll = baseMapper.selectList(null);

        //查出所有的一级分类
        List<CategoryEntity> levelOne = getParentByCid(categoryEntityListAll, 0L);
        Map<String, List<Category2Vo>> map = levelOne.stream().collect(Collectors.toMap(a -> a.getCatId().toString(), b -> {
            //查出所有的二级分类
            List<CategoryEntity> categoryEntityList = getParentByCid(categoryEntityListAll, b.getCatId());
            List<Category2Vo> category2VoList = null;
            if (categoryEntityList != null && !categoryEntityList.isEmpty()) {
                //构造二级分类vo
                category2VoList = categoryEntityList.stream().map(c -> {
                    Category2Vo category2Vo = new Category2Vo();
                    category2Vo.setCatalog1Id(b.getCatId().toString());
                    category2Vo.setId(c.getCatId().toString());
                    category2Vo.setName(c.getName());
                    //找出二级分类的属性List<catalog3> catalog3List并赋值
                    List<CategoryEntity> catalog3List = getParentByCid(categoryEntityListAll, c.getCatId());
                    if (catalog3List != null && !catalog3List.isEmpty()) {
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

        //在本地锁里面查询数据存到缓存保证这个操作的原子性
        String mapJson = JSON.toJSONString(map);
        //设置缓存过期时间为随机时间,解决缓存雪崩
        double random = Math.random();
        stringRedisTemplate.opsForValue().set("categoryJson", mapJson, new Double(Math.random() * 100).longValue(), TimeUnit.MINUTES);

        return map;
    }

    /**
     * 利用所有数据,通过catId来过滤我们的分类
     *
     * @param categoryEntityListAll
     * @param catId
     * @return
     */
    private List<CategoryEntity> getParentByCid(List<CategoryEntity> categoryEntityListAll, Long catId) {
        return categoryEntityListAll.stream().filter(a -> a.getParentCid() == catId).collect(Collectors.toList());
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