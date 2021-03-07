package com.cloud.mall.product.web;

import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @Author ws
 * @Date 2021/3/7 21:23
 * @Version 1.0
 */
@RestController
public class RedisLockController {

    @Autowired
    RedissonClient redisson;

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 加写锁
     * @return
     */
    @GetMapping("/lock/write")
    public String writeValueWithLock() {
        RReadWriteLock readWriteLock = redisson.getReadWriteLock("rw-lock");
        //写锁,别人不能写,并且加了rw-lock的读锁就不能读,只有写锁释放了才能读
        RLock rLock = readWriteLock.writeLock();
        String uuid = null;
        try {
            //上锁
            rLock.lock();
            uuid = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set("writeValue", uuid);
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }
        return uuid;
    }

    /**
     * 加读锁,有写锁的时候不能读,没有写锁的时候可以并发读
     * @return
     */
    @GetMapping("/lock/read")
    public String readValueWithLock() {
        RReadWriteLock readWriteLock = redisson.getReadWriteLock("rw-lock");
        RLock rLock = readWriteLock.readLock();
        String uuid = "";
        try {
            //加读锁
            rLock.lock();
            uuid = redisTemplate.opsForValue().get("writeValue");
        } finally {
            rLock.unlock();
        }
        return uuid;
    }

    /**
     * 上面的锁只对加了锁的有效,我们没加锁的照样可以读redis的最新值
     * @return
     */
    @GetMapping("/read")
    public String readValueWithNOLock(){
       return redisTemplate.opsForValue().get("writeValue");
    }

    /**
     * 上面加了锁的只对加了锁的有效,没加锁我们照样可以写redis的最新值
     * @return
     */
    @GetMapping("/write")
    public String writeValueWithNOLock(){
        String uuid = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("writeValue", uuid);
        return uuid;
    }
}
