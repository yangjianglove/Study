package com.uv.rpc.netty.server;
/*
 * @author uv
 * @date 2018/10/12 18:33
 * 处理服务端接收的数据
 */

import com.uv.rpc.discovery.ScanUtil;
import com.uv.rpc.netty.protocol.RpcRequest;
import com.uv.rpc.netty.protocol.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.lang.reflect.Method;

public class ServerHandler extends ChannelInboundHandlerAdapter{


    //接受client发送的消息并处理
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        RpcRequest request = (RpcRequest) msg;
        //返回的数据结构
        RpcResponse response = new RpcResponse();
        response.setId(request.getId()); //返回ID与request对应
        try {
            //执行相应的方法
            Object result = invokeMethod(request);
            response.setData(result);
            response.setStatus(1);
        } catch (Exception e) {
            e.printStackTrace();
            response.setData(null);
            response.setStatus(-1);
        }
        //返回执行结果
        ctx.writeAndFlush(response);
    }

    //通知处理器最后的channelRead()是当前批处理中的最后一条消息时调用
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    //读操作时捕获到异常时调用
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }

    //客户端去和服务端连接成功时触发
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        ctx.writeAndFlush("hello client");
    }

    //根据请求，通过Java反射的方式执行相应的方法
    private Object invokeMethod(RpcRequest request) throws Exception{

        String className = request.getClassName();
        //根据暴露的接口，找到实现类
        Class<?> clazz = ScanUtil.interfaceClassMap.get(className);

        //找到要执行的方法
        Method method = clazz.getDeclaredMethod(request.getMethodName(), request.getParameterTypes());
        //构造参数列表

        Object result = method.invoke(clazz.newInstance(), request.getParameters());
        return result;
    }
}
