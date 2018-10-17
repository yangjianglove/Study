package com.uv;

import com.uv.rpc.discovery.ScanUtil;
import com.uv.rpc.discovery.ZookeeperUtil;
import com.uv.rpc.netty.server.NettyServer;

/**
 * <uv> [2018/10/14 16:55]
 */
public class Main {

    //server启动监听的端口
    public static final int serverPort = 8018;

    public static void main(String[] args) {

        //扫描暴露的接口
        ScanUtil.scanPath("com.uv.api");
        //连接zookeeper
        ZookeeperUtil.connect("127.0.0.1", 2181);
        //向zookeeper注册暴露的接口
        ZookeeperUtil.register("127.0.0.1", serverPort);
        //启动server(此过程放在最后，启动服务后，此方法下的不再执行)
        new NettyServer().bind(serverPort);
    }
}
