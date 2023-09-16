package com.qyh.gateway.core.netty;

import com.qyh.gateway.core.config.Config;
import com.qyh.gateway.core.container.LifeCycle;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import com.qyh.gateway.core.netty.process.NettyProcessor;
import utils.RemotingUtil;


import java.net.InetSocketAddress;

@Slf4j
public class NettyHttpServer implements LifeCycle {


    private final NettyProcessor nettyProcessor;

    private final Config config;

    private ServerBootstrap serverBootstrap;

    private EventLoopGroup eventLoopGroupBoss;

    private EventLoopGroup eventLoopGroupWorker;


    public NettyHttpServer(Config config, NettyProcessor nettyProcessor) {
        this.config = config;
        this.nettyProcessor = nettyProcessor;
        init();
    }


    //初始化
    @Override
    public void init() {
        this.serverBootstrap = new ServerBootstrap();
        //初始化线程池
        if (useEpoll()) {
            this.eventLoopGroupBoss = new EpollEventLoopGroup(config.getEventLoopGroupBossNum(),
                    new DefaultThreadFactory("com.qyh.gateway.core.netty-boss-nio"));
            this.eventLoopGroupWorker = new EpollEventLoopGroup(config.getEventLoopGroupWorkerNum(),
                    new DefaultThreadFactory("com.qyh.gateway.core.netty-woker-nio"));
        } else {
            this.eventLoopGroupBoss = new NioEventLoopGroup(config.getEventLoopGroupBossNum(),
                    new DefaultThreadFactory("com.qyh.gateway.core.netty-boss-nio"));
            this.eventLoopGroupWorker = new NioEventLoopGroup(config.getEventLoopGroupWorkerNum(),
                    new DefaultThreadFactory("com.qyh.gateway.core.netty-woker-nio"));
        }
    }


    //启动netty容器
    @Override
    public void start() {
        this.serverBootstrap
                .group(eventLoopGroupBoss, eventLoopGroupWorker)
                .channel(useEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)            //	sync + accept = backlog
                .option(ChannelOption.SO_REUSEADDR, true)    //	tcp端口重绑定
                .option(ChannelOption.SO_KEEPALIVE, false)    //  如果在两小时内没有数据通信的时候，TCP会自动发送一个活动探测数据报文
                .childOption(ChannelOption.TCP_NODELAY, true)   //	该参数的左右就是禁用Nagle算法，使用小数据传输时合并
                .childOption(ChannelOption.SO_SNDBUF, 65535)    //	设置发送数据缓冲区大小
                .childOption(ChannelOption.SO_RCVBUF, 65535)    //	设置接收数据缓冲区大小
                .localAddress(new InetSocketAddress(config.getPort()))
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(
                                new HttpServerCodec(), //http编解码处理器
                                new HttpObjectAggregator(config.getMaxContentLength()), //请求报文聚合成FullHttpRequest
                                new NettyHttpServerHandler(nettyProcessor),
                                new NettyServerConnectManagerHandler()//管理连接的生命周期。
                        );
                    }
                });

        try {
            this.serverBootstrap.bind().sync();
            log.info("netty服务器启动成功  端口 : {}", this.config.getPort());
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    //优雅停机
    @Override
    public void shutdown() {
        if (eventLoopGroupBoss != null) {
            eventLoopGroupBoss.shutdownGracefully();
        }

        if (eventLoopGroupWorker != null) {
            eventLoopGroupWorker.shutdownGracefully();
        }
    }


    //这个方法用于判断当前系统是否支持并且可以使用 Epoll I/O 模型。
    //
    //方法名：useEpoll()
    //返回值：boolean（是否使用 Epoll）
    //
    //代码解析：
    //1. 首先，通过调用 `RemotingUtil.isLinuxPlatform()` 方法判断当前系统是否为 Linux 系统。
    //2. 然后，通过调用 `Epoll.isAvailable()` 方法判断是否支持 Epoll I/O 模型。
    //3. 如果当前系统是 Linux 并且支持 Epoll，则返回 `true`，表示可以使用 Epoll。
    //4. 如果不满足上述条件，则返回 `false`，表示不能使用 Epoll。
    //
    //总结：这个方法用于判断当前系统是否支持并且可以使用 Epoll I/O 模型，以便在适当的情况下选择使用 Epoll 进行高性能的 I/O 操作。
    public boolean useEpoll() {
        return RemotingUtil.isLinuxPlatform()
                && Epoll.isAvailable();
    }


    public NettyProcessor getNettyProcessor() {
        return nettyProcessor;
    }

    public Config getConfig() {
        return config;
    }

    public ServerBootstrap getServerBootstrap() {
        return serverBootstrap;
    }

    public void setServerBootstrap(ServerBootstrap serverBootstrap) {
        this.serverBootstrap = serverBootstrap;
    }

    public EventLoopGroup getEventLoopGroupBoss() {
        return eventLoopGroupBoss;
    }

    public void setEventLoopGroupBoss(EventLoopGroup eventLoopGroupBoss) {
        this.eventLoopGroupBoss = eventLoopGroupBoss;
    }

    public EventLoopGroup getEventLoopGroupWorker() {
        return eventLoopGroupWorker;
    }

    public void setEventLoopGroupWorker(EventLoopGroup eventLoopGroupWorker) {
        this.eventLoopGroupWorker = eventLoopGroupWorker;
    }
}
