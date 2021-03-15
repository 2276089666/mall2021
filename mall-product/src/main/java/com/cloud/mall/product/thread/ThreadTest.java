package com.cloud.mall.product.thread;

import java.util.concurrent.*;

/**
 * @Author ws
 * @Date 2021/3/14 14:49
 * @Version 1.0
 */
public class ThreadTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        1、 初始化线程的 4 种方式
//        1） 、 继承 Thread
//        2） 、 实现 Runnable 接口
//        3） 、 实现 Callable 接口 + FutureTask （可以拿到返回结果， 可以处理异常）
//        4） 、 线程池

        System.out.println("main-----------------------start");
        //1） 、 继承 Thread
        Thread01 thread01 = new Thread01();
        thread01.start();

        //2） 、 实现 Runnable 接口
        Runnable01 runnable01 = new Runnable01();
        new Thread(runnable01).start();

        //3） 、 实现 Callable 接口 + FutureTask （可以拿到返回结果， 可以处理异常）
        FutureTask<Integer> integerFutureTask = new FutureTask<Integer>(new Callable01());
        new Thread(integerFutureTask).start();
        Integer integer=null;
        try {
            //阻塞等待拿到结果数据
             integer= integerFutureTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println("Callable的异步数据"+integer);
        System.out.println("main------------------------end");

        /**
         * 1.2没返回值
         * 3.有返回值
         * 4.有返回值,可以充分利用资源
         * 以上三种启动线程的方式在我们的业务代码的异步任务时都不行,我们用线程池
         */

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        Future<Integer> submit = executorService.submit(new Callable01());
        try {
            Integer integer1 = submit.get();
            System.out.println("线程池线程返回的结果:\t"+integer1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        /**
         * 线程池的7大参数:
         *
         * @param corePoolSize the number of threads to keep in the pool, even
         *        if they are idle, unless {@code allowCoreThreadTimeOut} is set
         * @param maximumPoolSize the maximum number of threads to allow in the
         *        pool
         * @param keepAliveTime when the number of threads is greater than
         *        the core, this is the maximum time that excess idle threads
         *        will wait for new tasks before terminating.
         * @param unit the time unit for the {@code keepAliveTime} argument
         * @param workQueue the queue to use for holding tasks before they are
         *        executed.  This queue will hold only the {@code Runnable}
         *        tasks submitted by the {@code execute} method.
         * @param threadFactory the factory to use when the executor
         *        creates a new thread
         * @param handler the handler to use when execution is blocked
         *        because the thread bounds and queue capacities are reached
         *
         * corePoolSize:核心线程数,一直存在
         * maximumPoolSize:最大线程数
         * keepAliveTime:多余核心线程数的线程的最大空闲时间,时间一过,回收
         * unit:上面的最大空闲时间的单位
         * workQueue:任务阻塞队列,当任务比最大存货线程多时,将多余的任务放进队列,等有空闲的线程就出队列执行
         * threadFactory:线程的创建工厂
         * handler:如果任务阻塞队列workQueue满了,按照我们指定的拒绝策略拒绝执行任务
         *
         *
         * 运行流程：
         * 1、 线程池创建， 准备好 core 数量的核心线程， 准备接受任务
         * 2、 新的任务进来， 用 core 准备好的空闲线程执行。
         * (1) 、 core 满了， 就将再进来的任务放入阻塞队列中。 空闲的 core 就会自己去阻塞队
         * 列获取任务执行
         * (2) 、 阻塞队列满了， 就直接开新线程执行， 最大只能开到 max 指定的数量
         * (3) 、 max 都执行好了。 Max-core 数量空闲的线程会在 keepAliveTime 指定的时间后自
         * 动销毁。 最终保持到 core 大小
         * (4) 、 如果线程数开到了 max 的数量， 还有新任务进来， 就会使用 reject 指定的拒绝策
         * 略进行处理
         * 3、 所有的线程创建都是由指定的 factory 创建的。
         */

        //LinkedBlockingQueue队列深度为Integer最大取值范围我们调小一点
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                5,
                200,
                300,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100000),
                Executors.defaultThreadFactory(),
                new  ThreadPoolExecutor.AbortPolicy()
        );


        //异步编排使用runAsync无返回值
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
            System.out.println("CompletableFuture=======runAsync");
        }, threadPoolExecutor);

        //异步编排使用supplyAsync有返回值
        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("CompletableFuture======supplyAsync");
            System.out.println("supplyAsync线程id:\t"+Thread.currentThread().getId());
            return 100/0;
        }, threadPoolExecutor).whenCompleteAsync((ret,exception)->{
            System.out.println("whenComplete线程id:\t"+Thread.currentThread().getId());
            System.out.println("返回值:\t"+ret+"\t"+"异常是:"+exception);
        }).exceptionally(throwable -> {
            System.out.println("有异常,我们给定默认返回");
            return 5464;
        });


        //handle的用法
        CompletableFuture<Integer> completableFuture2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("CompletableFuture======supplyAsync");
            System.out.println("supplyAsync线程id:\t"+Thread.currentThread().getId());
            return 100;
        }, threadPoolExecutor).handle((ret,exception)->{
            if (ret!=null){
                return ret*2;
            }
            if (exception!=null){
                return 123;
            }
            return 1;
        });

        CompletableFuture<Integer> completableFuture3 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务3CompletableFuture======supplyAsync");
            System.out.println("supplyAsync线程id:\t"+Thread.currentThread().getId());
            return 100;
        }, threadPoolExecutor);

        CompletableFuture<Integer> completableFuture4 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务4CompletableFuture======supplyAsync");
            System.out.println("supplyAsync线程id:\t"+Thread.currentThread().getId());
            return 100;
        }, threadPoolExecutor);

//        两个都需要完成,不能接收参数
//        completableFuture3.runAfterBothAsync(completableFuture4,()->{
//            System.out.println("任务3和4都执行完后,再执行的任务5");
//        },threadPoolExecutor);

//        //两个都需要完成可以接收参数
//        CompletableFuture<Void> voidCompletableFuture1 = completableFuture3.thenAcceptBothAsync(completableFuture4, (f1, f2) -> {
//            System.out.println("都执行完" + f1 + "asd" + f2);
//        }, threadPoolExecutor);

        CompletableFuture<String> stringCompletableFuture = completableFuture3.thenCombineAsync(completableFuture4, (f1, f2) -> {
            return "asdas" + f1 + f2;
        }, threadPoolExecutor);
        System.out.println(stringCompletableFuture.get());


        Integer integer1 = completableFuture2.get();
        System.out.println("结果"+integer1);

    }

    public static class Thread01 extends Thread{
        @Override
        public void run() {
            System.out.println("Thread01");
            System.out.println("当前线程id:\t"+Thread.currentThread().getId());
        }
    }

    public static class Runnable01 implements Runnable{
        @Override
        public void run() {
            System.out.println("Runnable01");
            System.out.println("当前线程id:\t"+Thread.currentThread().getId());
        }
    }

    public static class Callable01 implements Callable<Integer>{

        @Override
        public Integer call() throws Exception {
            System.out.println("Callable01");
            System.out.println("当前线程id:\t"+Thread.currentThread().getId());
            return 5;
        }
    }
}
