package com.qyh.gateway.core.netty;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import utils.RemotingHelper;


/**
 * 这个类是一个 Netty 服务器连接管理器的处理器，用于管理连接的生命周期。
 *
 * 主要功能：
 * 1. 在不同的连接生命周期事件中，记录相关日志并执行相应的操作。
 * 2. 处理空闲状态事件，当连接一段时间没有收到或发送任何数据时，关闭连接。
 * 3. 处理异常事件，当连接处理过程中出现异常时，关闭连接。
 *
 * 具体方法解析：
 * 1. `channelRegistered(ChannelHandlerContext ctx)` 方法：当 Channel 注册到 EventLoop 并且能够处理 I/O 时调用。记录远程地址并输出日志。
 * 2. `channelUnregistered(ChannelHandlerContext ctx)` 方法：当 Channel 从 EventLoop 注销并且无法处理任何 I/O 时调用。记录远程地址并输出日志。
 * 3. `channelActive(ChannelHandlerContext ctx)` 方法：当 Channel 处于活动状态时被调用，可以接收和发送数据。记录远程地址并输出日志。
 * 4. `channelInactive(ChannelHandlerContext ctx)` 方法：当 Channel 不再是活动状态且不再连接远程节点时被调用。记录远程地址并输出日志。
 * 5. `userEventTriggered(ChannelHandlerContext ctx, Object evt)` 方法：当触发用户自定义事件时被调用。在这里主要处理空闲状态事件，当一段时间没有收到或发送任何数据时，关闭连接。
 * 6. `exceptionCaught(ChannelHandlerContext ctx, Throwable cause)` 方法：当 ChannelHandler 在处理过程中出现异常时调用。记录远程地址和异常信息，并关闭连接。
 *
 * 总结：这个类是一个 Netty 服务器连接管理器的处理器，负责管理连接的生命周期，包括记录日志、处理空闲状态和异常事件，并执行相应的操作。
 */
@Slf4j
public class NettyServerConnectManagerHandler extends ChannelDuplexHandler {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        //当Channel注册到它的EventLoop并且能够处理I/O时调用
        final String remoteAddr = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
        log.debug("NETTY SERVER PIPLINE: channelRegistered {}", remoteAddr);
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        //当Channel从它的EventLoop中注销并且无法处理任何I/O时调用
        final String remoteAddr = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
        log.debug("NETTY SERVER PIPLINE: channelUnregistered {}", remoteAddr);
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //当Channel处理于活动状态时被调用，可以接收与发送数据
        final String remoteAddr = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
        log.debug("NETTY SERVER PIPLINE: channelActive {}", remoteAddr);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //不再是活动状态且不再连接它的远程节点时被调用
        final String remoteAddr = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
        log.debug("NETTY SERVER PIPLINE: channelInactive {}", remoteAddr);
        super.channelInactive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //当ChannelInboundHandler.fireUserEventTriggered()方法被调用时触发
        if(evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent)evt;
            if(event.state().equals(IdleState.ALL_IDLE)) { //有一段时间没有收到或发送任何数据
                final String remoteAddr = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
                log.warn("NETTY SERVER PIPLINE: userEventTriggered: IDLE {}", remoteAddr);
                ctx.channel().close();
            }
        }
        ctx.fireUserEventTriggered(evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        //当ChannelHandler在处理过程中出现异常时调用
        final String remoteAddr = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
        log.warn("NETTY SERVER PIPLINE: remoteAddr： {}, exceptionCaught {}", remoteAddr, cause);
        ctx.channel().close();
    }

}
