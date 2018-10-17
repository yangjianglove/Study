package com.uv.rpc.proxy;
/*
 * @author uv
 * @date 2018/10/14 12:10
 * 动态代理类
 */

import com.uv.rpc.config.GlobalConfig;
import com.uv.rpc.discovery.ZookeeperUtil;
import com.uv.rpc.netty.client.ClientHandler;
import com.uv.rpc.netty.client.NettyClient;
import com.uv.rpc.netty.protocol.RpcFuture;
import com.uv.rpc.netty.protocol.RpcRequest;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

public class InterfaceProxy implements InvocationHandler {

    //接口类型
    private static Class<?> clazz;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        //根据接口，从zookeeper获取到服务端的ip+port
        String ipAndPort = ZookeeperUtil.getDataFromServer(clazz.getName());
        if (ipAndPort == null || ipAndPort.length() == 0) {
            return null;
        }
        String[] content = ipAndPort.split(":");
        //client启动，连接到server
        ClientHandler handler = NettyClient.startNettyClient(content[0], Integer.valueOf(content[1]));
        //组装request
        RpcRequest request = new RpcRequest()
            .setId(UUID.randomUUID().toString())
            .setClassName(clazz.getName())
            .setMethodName(method.getName())
            .setParameterTypes(method.getParameterTypes())
            .setParameters(args);
        //获取server端的响应结果
        RpcFuture rpcFuture = handler.sendRequest(request, GlobalConfig.getChannelMap().get(content[0] + ":" + content[1]));
        return rpcFuture.get();
    }
    //动态代理绑定
    public static <T> T newInterfaceProxy(Class<T> intf) {
        ClassLoader classLoader = intf.getClassLoader();
        Class<?>[] interfaces = new Class[]{intf};
        InterfaceProxy proxy = new InterfaceProxy();
        //记录接口的类型，用于request请求的ClassName
        clazz = intf;
        return (T) Proxy.newProxyInstance(classLoader, interfaces, proxy);
    }
}

