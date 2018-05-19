package com.minmin.imemo.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * author:minmin
 * email:775846180@qq.com
 * time:2018/01/25
 * desc:自定义线程池
 * version:1.0
 */

public class MyThreadPoolExecutor extends ThreadPoolExecutor {

    //设置核心池大小
    private static int corePoolSize = 5;
    //设置线程池最大能接受多少线程
    private static int maximumPoolSize = 10;
    //当前线程数大于corePoolSize、小于maximumPoolSize时，超出corePoolSize的线程数的生命周期
    private static long keepActiveTime = 200;
    //设置时间单位，秒
    private static TimeUnit timeUnit = TimeUnit.SECONDS;
    //设置线程池缓存队列的排队策略为FIFO，并且指定缓存队列大小为5
    private static BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(5);
    //自定义线程池
    private static MyThreadPoolExecutor executor;

    public MyThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public static MyThreadPoolExecutor getInstance() {

        return new MyThreadPoolExecutor(corePoolSize, maximumPoolSize, keepActiveTime, timeUnit, workQueue);

    }

}
