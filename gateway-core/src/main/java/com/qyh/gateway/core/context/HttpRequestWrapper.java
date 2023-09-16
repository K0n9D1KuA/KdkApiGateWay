package com.qyh.gateway.core.context;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public class HttpRequestWrapper {


    private FullHttpRequest request;
    private ChannelHandlerContext ctx;

    public FullHttpRequest getRequest() {
        return request;
    }

    public void setRequest(FullHttpRequest request) {
        this.request = request;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }
}
