package com.qyh.gateway.core.netty.process;

import com.qyh.gateway.core.container.LifeCycle;
import com.qyh.gateway.core.context.GatewayContext;

import com.qyh.gateway.core.context.HttpRequestWrapper;
import com.qyh.gateway.core.filter.FilterFactory;
import com.qyh.gateway.core.filter.GatewayFilterChain;
import com.qyh.gateway.core.filter.GatewayFilterChainFactory;
import enums.ResponseCode;
import exception.BaseException;
import com.qyh.gateway.core.helper.RequestHelper;
import com.qyh.gateway.core.helper.ResponseHelper;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;


/**
 * @author K0n9D1KuA
 * @version 1.0
 * @description: 核心逻辑处理类
 * @email 3161788646@qq.com
 * @date 2023/9/3 0:21
 */
@Slf4j
public class NettyProcessor implements LifeCycle {

    //拦截器工厂
    private FilterFactory filterFactory = GatewayFilterChainFactory.getInstance();

    public void process(HttpRequestWrapper httpRequestWrapper) {
        FullHttpRequest request = httpRequestWrapper.getRequest();
        ChannelHandlerContext ctx = httpRequestWrapper.getCtx();

        //转化成gateWayContext
        try {
            //构建核心上下文
            GatewayContext gatewayContext = RequestHelper.doContext(request, ctx);
            //执行过滤器逻辑
            GatewayFilterChain gatewayFilterChain = filterFactory.buildFilterChain(gatewayContext);
            gatewayFilterChain.doFilter(gatewayContext);
        } catch (BaseException e) {
            //捕捉拦截器中出现的异常
            log.error("process error {} {}", e.getCode().getCode(), e.getCode().getMessage());
            FullHttpResponse httpResponse = ResponseHelper.getHttpResponse(e.getCode());
            doWriteAndRelease(ctx, request, httpResponse);
        } catch (Throwable t) {
            log.error("process unKnown error", t);
            FullHttpResponse httpResponse = ResponseHelper.getHttpResponse(ResponseCode.INTERNAL_ERROR);
            doWriteAndRelease(ctx, request, httpResponse);
        }
    }


    private void doWriteAndRelease(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse httpResponse) {
        ctx.writeAndFlush(httpResponse)
                .addListener(ChannelFutureListener.CLOSE); //释放资源后关闭channel
        ReferenceCountUtil.release(request);
    }


    @Override
    public void init() {

    }

    @Override
    public void start() {

    }

    @Override
    public void shutdown() {

    }
}
