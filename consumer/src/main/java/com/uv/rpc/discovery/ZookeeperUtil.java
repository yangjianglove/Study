package com.uv.rpc.discovery;
/*
 * @author uv
 * @date 2018/10/11 15:56
 * zookeeper 工具类（服务发现）
 * zookeeper存储结构为目录层级结构
 */

import java.io.IOException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class ZookeeperUtil {

    //zookeeper连接超时时间
    private static final int SESSION_TIMEOUT = 5000;
    //在zookeeper注册的根节点
    private static final String ROOT = "/rpc";

    private static ZooKeeper zooKeeper = null;

    /**
     *
     * @param ip zookeeper服务的IP地址
     * @param port 端口号
     * 连接zookeeper服务
     */
    public static void connect(String ip, int port) {
        try {
            zooKeeper = new ZooKeeper(ip + ":" + port, SESSION_TIMEOUT, null);
        } catch (IOException e) {
            System.out.println("zookeeper连接失败！");
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * @param api 调用的接口
     * 从zookeeper获取接口所在服务的地址和端口号
     */
    public static String getDataFromServer(String api) {
        try {
            String path = ROOT + "/" + api;
            Stat exists = zooKeeper.exists(path, true);
            //节点存在
            if(exists != null) {
                byte[] data = zooKeeper.getData(ROOT + "/" + api, true, new Stat());
                String ipAndPost = new String(data);
                return ipAndPost;
            }
        } catch (Exception e) {
            System.out.println("zookeeper获取节点失败");
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }
}
