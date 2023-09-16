package com.qyh.gateway.core.container;

import com.qyh.gateway.core.config.Config;
import lombok.extern.slf4j.Slf4j;
import com.qyh.gateway.core.netty.NettyHttpClient;
import com.qyh.gateway.core.netty.NettyHttpServer;
import com.qyh.gateway.core.netty.process.NettyProcessor;

@Slf4j
public class Container implements LifeCycle {
    private final Config config;


    private NettyHttpServer nettyHttpServer;

    private NettyHttpClient nettyHttpClient;

    private NettyProcessor nettyProcessor;

    public Container(Config config) {
        this.config = config;
        init();
    }


    //初始化
    @Override
    public void init() {
        this.nettyProcessor = new NettyProcessor();

        this.nettyHttpServer = new NettyHttpServer(config, nettyProcessor);

        this.nettyHttpClient = new NettyHttpClient(config, nettyHttpServer.getEventLoopGroupWorker());
    }

    @Override
    public void start() {
        //启动netty容器
        nettyHttpServer.start();

        //初始化httpClient
        nettyHttpClient.start();
    }

    @Override
    public void shutdown() {
        nettyHttpServer.shutdown();
        nettyHttpClient.shutdown();
    }
}
