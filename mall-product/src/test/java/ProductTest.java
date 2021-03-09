
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cloud.mall.product.MallProductApplication;
import com.cloud.mall.product.entity.BrandEntity;
import com.cloud.mall.product.service.BrandService;
import com.cloud.mall.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;


/**
 * @Author ws
 * @Date 2021/1/9 15:10
 * @Version 1.0
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MallProductApplication.class)
public class ProductTest {
    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Test
    public void test() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setDescript("aaaa");
        brandEntity.setName("华为");
        boolean save = brandService.save(brandEntity);
        System.out.println(save);
    }

    @Test
    public void test2() {
        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1));
        for (BrandEntity entity : list) {
            System.out.println(entity);
        }
    }

    @Test
    public void test3(){
        Long[] path = categoryService.findCateLogPath((long) 229);
        log.info("完整路径{}", Arrays.asList(path));
    }

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Test
    public void testRedis(){
        String v1 = stringRedisTemplate.opsForValue().get("cache_category");
        System.out.println(v1);
    }

    @Test
    public void testRandom(){
        double random = Math.random()*100;
        System.out.println(random);
        long l = new Double(random).longValue();
        System.out.println(l);
    }

    @Autowired
    RedissonClient redissonClient;

    @Test
    public void RedissonTest(){
        System.out.println(redissonClient);
    }
}
