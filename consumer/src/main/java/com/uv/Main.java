package com.uv;

import com.uv.api.UserService;
import com.uv.rpc.discovery.ZookeeperUtil;
import com.uv.rpc.proxy.InterfaceProxy;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <uv> [2018/10/15 9:25]
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        //连接到zookeeper
        ZookeeperUtil.connect("127.0.0.1", 2181);
        ExecutorService executorService = Executors.newCachedThreadPool();
        //设置线程发令枪，计数器为51，启动50个线程惊醒等待
        CountDownLatch latch = new CountDownLatch(51);
        for (int i = 0; i < 50; i++) {
            executorService.execute(new Task(latch));
        }
        //主线程睡眠10s，等待50个线程就绪
        Thread.sleep(10000);
        System.out.println("----并发开始-----");
        //计数器减一到达零，线程开始执行
        latch.countDown();
    }
}
//并发测试
class Task implements Runnable {

    private final CountDownLatch latch;

    public Task(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void run() {
        //计数器减一
        latch.countDown();
        try {
            //线程阻塞，当计数器等于零时，唤醒该线程
            latch.await();
            //动态代理绑定
            UserService userService = InterfaceProxy.newInterfaceProxy(UserService.class);
            //执行结果
            System.out.println(userService.sayHello("Tom"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

