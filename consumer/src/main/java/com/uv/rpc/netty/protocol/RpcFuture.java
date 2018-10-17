package com.uv.rpc.netty.protocol;
/*
 * @author uv
 * @date 2018/10/14 13:30
 * 获取server返回消息
 */

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RpcFuture implements Future<Object> {

    private RpcResponse response = null;
    //等待响应超时时间10s
    private long timeout = 10000;
    
    private final Lock lock = new ReentrantLock();

    private final Condition done = lock.newCondition();

    //未实现
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    //未实现
    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }

    //有响应结果则已经完成响应
    @Override
    public boolean isDone() {
        return response != null;
    }

    //获取响应结果，等待响应的超时时间为5s
    @Override
    public Object get() throws InterruptedException, ExecutionException {
        return get(timeout, TimeUnit.MILLISECONDS);
    }

    //获取响应结果
    @Override
    public Object get(long timeout, TimeUnit unit) {
        //记录响应时间
        long start = System.currentTimeMillis();
        lock.lock();
        try {
            //循环判断服务端是否返回响应结果
            while (!isDone()) {
                //阻塞当前线程，当future接收到response时被唤醒；或达到超时时间唤醒
                done.await(timeout, TimeUnit.MILLISECONDS);
                //服务端返回结果 或者 等待响应超时
                if (isDone() || System.currentTimeMillis() - start > timeout) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
        //返回响应结果
        if (response != null && response.getStatus() == 1) {
            return response.getData();
        }
        return null;
    }

    //handler获取到响应结果，添加到future
    public void setResponse(RpcResponse response) {
        this.response = response;
        //获取到响应结果，唤醒被阻塞的线程
        lock.lock();
        try {
            if (done != null) {
                done.signal();
            }
        } finally {
            lock.unlock();
        }
    }
}
