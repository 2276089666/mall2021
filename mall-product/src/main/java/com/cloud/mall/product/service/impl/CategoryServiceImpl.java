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
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
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
    public Map<String, List<Category2Vo>> getCategoryJson()  {
        // TODO: 2021/3/6 1. 空结果缓存:解决缓存穿透(多个请求对0个缓存 n:0)   2.设置过期时间(加随机值):解决缓存雪崩(多个请求对多个缓存同时失效 n:n)   3.加锁:解决缓存击穿 (多个请求对1个缓存,这个缓存失效 n:1)

        String categoryJson = stringRedisTemplate.opsForValue().get("categoryJson");
        if (StringUtils.isEmpty(categoryJson)) {
            Map<String, List<Category2Vo>> categoryJsonBySql = getCategoryJsonBySqlONRedisson();
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
        return getDataFromDb();
    }


    /**
     * redis分布式锁底层实现
     *
     * @return
     */
    public Map<String, List<Category2Vo>> getCategoryJsonBySqlONRedisLock()  {
        //为了删锁的时候不删别人的锁,我们每个线程加锁的时候锁的value都生成唯一的token,redis官网的set指令下面有说明
        String token = UUID.randomUUID().toString();
        //占分布式锁,而且为了保证加锁后断电等意外,我们的锁没有得到释放,造成死锁,所以我们设置锁的过期时间,并且保持加锁和过期时间这一操作是原子性
        Boolean aBoolean = stringRedisTemplate.opsForValue().setIfAbsent("lock", token,100,TimeUnit.SECONDS);
        if (aBoolean) {
            log.info("获取分布式锁成功,key:\t{},value:\t{}","lock",token);
            Map<String, List<Category2Vo>> dataFromDb=null;
            //竞争到锁就执行我们的业务
            try {
                dataFromDb= getDataFromDb();
            }finally {
                String lockValue = stringRedisTemplate.opsForValue().get("lock");
//            //删除锁之前看看是不是我们上的锁,避免自己的锁自动过期,删掉了别人的锁
//            if (token.equals(lockValue)){
//                //删除我们的分布式锁,让别的线程可以重新竞争锁
//                stringRedisTemplate.delete("lock");
//            }
                //但是删除锁也是要原子性的,redis官网的set指令下面有说明,用rua脚本执行删锁
                String script="if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
                        "then\n" +
                        "    return redis.call(\"del\",KEYS[1])\n" +
                        "else\n" +
                        "    return 0\n" +
                        "end";
                Long execute = stringRedisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList("lock"), token);
                if (execute==1){
                    log.info("释放分布式锁成功");
                }else {
                    log.info("释放分布式锁失败");
                }
            }

            return dataFromDb;
        } else {
            //竞争锁失败,过一会重试
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("获取分布式锁失败,等待100毫秒重试");
            return getCategoryJsonBySqlONRedisLock();
        }
        // TODO: 2021/3/7 还有怎么自动续期等问题,如果想简单点我们只要把锁的过期时间弄长一点就行,redis官网的set指令下面有说明,不同的语言有不同的解决方案,Java是Redisson
    }

    @Autowired
    RedissonClient redisson;

    /**
     * 缓存与数据库的一致性
     * 1.双写模式:先修改数据库再修改缓存(问题:当前后两个线程都去修改数据库,先修改数据库的线程比后修改数据库的线程后修改缓存,会导致后修改数据库的线程的最新数据的缓存被覆盖,出现缓存数据库数据不一致)
     * 解决1:加锁(分布式读写锁),保证某个线程修改数据库和更新缓存这次操作全部完成后,别的线程才能对同一数据进行修改数据库和更新缓存操作
     * 解决2:每个缓存有过期时间,缓存一过期,脏数据就没有了
     * 2.失效模式:先修改数据库再删除缓存(问题:A,B,C三个线程,按照时间先后:(A写a-1) -> (B写a-2) -> (A删缓存) -> (c读a-1) -> (B删缓存) -> (c更新缓存a-1)  数据库:a-2,缓存a-1,两个不一致)
     * 同上
     * 3.使用中间件canal,伪装自己是mysql的slave解析binlog,mysql表数据有变化他会同步到redis里面去
     */

    /**
     * 使用redisson的可重入锁实现分布式锁
     * @return
     */
    public Map<String, List<Category2Vo>> getCategoryJsonBySqlONRedisson()  {
        //只要锁的名字相同就是同一把锁,redisson存我们的锁的类型为hash key:redissonLock {key:81122499-2141-4827-bb50-63c176caa1bc:91 value:1}
        //其中91为我们的线程id
        /**
         * 锁的名字
         * 粒度越细,越好
         * 11号商品  product-11-lock
         * 12号商品  product-12-lock
         */
        RLock redissonLock = redisson.getLock("CategoryJsonLock");
        //没有拿到锁的线程会阻塞式等待
        redissonLock.lock();

        /**
         * redissonLock.lock(20,TimeUnit.SECONDS);
         * 自己设置过期时间,他不会给我们自动续期
         * 1.如果我们传递了过期时间,redisson会发送给redis执行脚本,进行占锁
         * 2.如果没有指定,默认时间 lockWatchdogTimeout = 30 * 1000;
         * 3.占锁成功就会来个定时任务,1/3的看门狗时间后执行,重置过期时间为看门狗时间30s
         */
        log.info("加锁成功");
        Map<String, List<Category2Vo>> dataFromDb=null;
        try {
            //锁会由redisson的看门狗自动续期30s,出现问题,锁没有续期最多30s所就会释放
            dataFromDb= getDataFromDb();
        }finally {
            log.info("释放锁");
            redissonLock.unlock();
        }
        return dataFromDb;
    }


    private Map<String, List<Category2Vo>> getDataFromDb() {
        //看看是否已经有线程查过数据库并更新到缓存
        String categoryJson = stringRedisTemplate.opsForValue().get("categoryJson");
        if (categoryJson != null && !categoryJson.isEmpty()) {
            log.info("缓存命中");
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

        //在本地锁里面查询数据存到redis缓存保证这个操作的原子性
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