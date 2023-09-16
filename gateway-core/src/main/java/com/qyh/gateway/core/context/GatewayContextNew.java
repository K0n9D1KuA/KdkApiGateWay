//package com.qyh.gateway.core.context;
//
//import com.qyh.gateway.core.request.GatewayRequest;
//import com.qyh.gateway.core.response.GatewayResponse;
//import config.Rule;
//import config.RuleNew;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.util.ReferenceCountUtil;
//
//import java.util.concurrent.atomic.AtomicInteger;
//
//
///**
// * @PROJECT_NAME: api-gateway
// * @DESCRIPTION: 网关核心上下文类
// * @USER: WuYang
// * @DATE: 2022/12/29 20:59
// */
//public class GatewayContextNew extends BasicContext {
//
//
//    //请求
//    private GatewayRequest request;
//
//    //相应
//    private GatewayResponse response;
//
//    //规则对象
//    private RuleNew rule;
//
//
//    public RuleNew getRule() {
//        return rule;
//    }
//
//    //当前请求重试次数
//    private AtomicInteger retryTimes = new AtomicInteger(0);
//
//
//    private boolean gray;
//
//    public boolean isGray() {
//        return gray;
//    }
//
//    public void setGray(boolean gray) {
//        this.gray = gray;
//    }
//
//    /**
//     * 构造函数
//     *
//     * @param protocol
//     * @param nettyCtx
//     * @param keepAlive
//     */
//    public GatewayContextNew(String protocol, ChannelHandlerContext nettyCtx, boolean keepAlive,
//                             GatewayRequest request, RuleNew rule) {
//        super(protocol, nettyCtx, keepAlive);
//        this.request = request;
//        this.rule = rule;
//    }
//
//    public GatewayContextNew() {
//        super();
//
//    }
//
//
//    public static class Builder {
//        private String protocol;
//        private ChannelHandlerContext nettyCtx;
//        private boolean keepAlive;
//        private GatewayRequest request;
//        private RuleNew rule;
//
//        public Builder() {
//
//        }
//
//        public Builder setProtocol(String protocol) {
//            this.protocol = protocol;
//            return this;
//        }
//
//        public Builder setNettyCtx(ChannelHandlerContext nettyCtx) {
//            this.nettyCtx = nettyCtx;
//            return this;
//        }
//
//        public Builder setKeepAlive(boolean keepAlive) {
//            this.keepAlive = keepAlive;
//            return this;
//        }
//
//        public Builder setRequest(GatewayRequest request) {
//            this.request = request;
//            return this;
//        }
//
//        public Builder setRule(RuleNew rule) {
//            this.rule = rule;
//            return this;
//        }
//
//        public GatewayContextNew build() {
//            return new GatewayContextNew(protocol, nettyCtx, keepAlive, request, rule);
//        }
//    }
//
//    @Override
//    public GatewayRequest getRequest() {
//        return request;
//    }
//
//
//    public void setRequest(GatewayRequest request) {
//        this.request = request;
//    }
//
//    @Override
//    public GatewayResponse getResponse() {
//        return response;
//    }
//
//    public void setResponse(Object response) {
//        this.response = (GatewayResponse) response;
//    }
//
//
//    public String getUniqueId() {
//        return request.getUniqueId();
//    }
//
//    /**
//     * 重写父类释放资源方法，用于正在释放资源
//     */
//    public void releaseRequest() {
//        if (requestReleased.compareAndSet(false, true)) {
//            ReferenceCountUtil.release(request.getFullHttpRequest());
//        }
//    }
//
//    /**
//     * 获取原始的请求对象
//     *
//     * @return
//     */
//    public GatewayRequest getOriginRequest() {
//        return request;
//    }
//
//
//    public AtomicInteger getRetryTimes() {
//        return retryTimes;
//    }
//
//
//}
