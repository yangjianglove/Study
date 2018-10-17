package com.uv.rpc.netty.client;
/*
 * @author uv
 * @date 2018/10/12 20:56
 * client消息处理类
 */

import com.uv.rpc.netty.protocol.RpcFuture;
import com.uv.rpc.netty.protocol.RpcRequest;
import com.uv.rpc.netty.protocol.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHandler extends SimpleChannelInboundHandler<RpcResponse>{

    //存储future（类似java线程执行返回结果的future）
    private Map<String, RpcFuture> futureMap = new ConcurrentHashMap<>();

    //处理服务端返回的数据
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        //request和response的ID相同，根据ID存储future
        RpcFuture rpcFuture = futureMap.get(response.getId());
        //服务端发送回response，放到future中，供java动态代理类获取结果
        rpcFuture.setResponse(response);
        //移除掉future
        futureMap.remove(response.getId());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
    //发送消息，并返回用来存储response的future
    public RpcFuture sendRequest(RpcRequest request, Channel channel) {
        RpcFuture rpcFuture = new RpcFuture();
        futureMap.put(request.getId(), rpcFuture);
        channel.writeAndFlush(request);
        return rpcFuture;
    }

}
