package com.cloud.mall.product.thread;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author ws
 * @Date 2021/3/30 14:29
 * @Version 1.0
 */
public class JUC {
    public static ReentrantLock lock = new ReentrantLock();
    public static int i = 0;

    public static class ReenterLockDemo implements Runnable {
        @Override
        public void run() {
            for (int j = 0; j < 10; j++) {
                lock.lock();
                lock.lock(); //同一个线程可以多次获得锁
                lock.lock();
                try {
                    i++;
                } finally {
                    lock.unlock();
                    lock.unlock(); // 释放锁得次数大于加锁得次数会抛出异常
                    lock.unlock(); // 释放锁得次数小于加锁得次数,这个线程会一直拥有这个锁,别得线程就一直拿不到这个锁,直到加锁得线程死亡,锁资源才能释放
                }
            }
        }
    }

    // 可重入锁测试
    public static void main(String[] args) throws InterruptedException {
        ReenterLockDemo reenterLockDemo = new ReenterLockDemo();
        Thread thread = new Thread(reenterLockDemo);
        thread.start();
        thread.join();
        System.out.println(i);
    }
}
