package com.qyh.gateway.core.netty;

import com.qyh.gateway.core.context.HttpRequestWrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import com.qyh.gateway.core.netty.process.NettyProcessor;


/**
 * @author K0n9D1KuA
 * @version 1.0
 * @description: netty组件handler 里面会委托 NettyProcessor 去处理请求
 * @email 3161788646@qq.com
 * @date 2023/9/13 21:19
 */

public class NettyHttpServerHandler extends ChannelInboundHandlerAdapter {
    //核心逻辑委托给该类
    private final NettyProcessor delegate;

    public NettyHttpServerHandler(NettyProcessor delegate) {
        this.delegate = delegate;
    }

    //连接发生读事件的时候 触发
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // new HttpObjectAggregator(com.qyh.gateway.core.config.getMaxContentLength()), //请求报文聚合成FullHttpRequest
        // 上一个handler处理成了 FullHttpRequest
        FullHttpRequest request = (FullHttpRequest) msg;


        //包装 netty上下文和请求体
        HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper();
        httpRequestWrapper.setCtx(ctx);
        httpRequestWrapper.setRequest(request);

        //核心逻辑委托给nettyProcessor实现
        delegate.process(httpRequestWrapper);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        System.out.println("----");
    }
}
