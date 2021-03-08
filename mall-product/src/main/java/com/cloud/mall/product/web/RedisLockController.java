package com.cloud.mall.product.web;

import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author ws
 * @Date 2021/3/7 21:23
 * @Version 1.0
 */
@RestController
public class RedisLockController {


    /**
     * 操作见官方github
     * https://github.com/redisson/redisson/wiki/8.-%E5%88%86%E5%B8%83%E5%BC%8F%E9%94%81%E5%92%8C%E5%90%8C%E6%AD%A5%E5%99%A8#86-%E4%BF%A1%E5%8F%B7%E9%87%8Fsemaphore
     */

    @Autowired
    RedissonClient redisson;

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 写锁是排他锁,读锁是共享锁
     * 读+读 后面的阻塞
     * 读+写 后面的阻塞
     * 写+读 后面的阻塞
     * 读+读 不阻塞
     */


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

    /**
     * 分布式信号量
     * 可以用作限流操作
     *
     */
    @GetMapping("/decreaseSemaphore")
    public String decreaseSemaphore() throws InterruptedException {
        RSemaphore semaphore = redisson.getSemaphore("Semaphore");
        //获取信号量,redis存储key:Semaphore value:1,当value必须为大于或等于1的Integer,否则我们就获取不到,10秒获取不到就不获取了,返回false
        boolean b = semaphore.tryAcquire(10, TimeUnit.SECONDS);
        if (b){
            // TODO: 2021/3/8 执行业务
        }else {
            return "当前流量过大,请稍等";
        }
        return "aa"+b;
    }

    @GetMapping("/addSemaphore")
    public String addSemaphore(){
        RSemaphore semaphore = redisson.getSemaphore("Semaphore");
        //增加信号量
        semaphore.release();
        return "ok";
    }


    /**
     * 闭锁
     * 类似线程栅栏
     * @return
     * @throws InterruptedException
     */

    @GetMapping("/countDownLatch")
    public String countDownLatch() throws InterruptedException {
        RCountDownLatch countDownLatch = redisson.getCountDownLatch("countDownLatch");
        // 设置闭锁的数量为5
        countDownLatch.trySetCount(5L);
        //到这里这个请求阻塞等待,等下面的方法每运行一次减少上面的数量一个,一共运行5次,线程就可以运行下去,返回ok
        countDownLatch.await();

        return "ok";
    }

    @GetMapping("/decreaseCountDownLatch")
    public String decreaseCountDownLatch(){
        RCountDownLatch countDownLatch = redisson.getCountDownLatch("countDownLatch");
        // 消耗数量
        countDownLatch.countDown();
        return "减少一个";
    }
}
