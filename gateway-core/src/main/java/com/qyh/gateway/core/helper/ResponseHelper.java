package com.qyh.gateway.core.helper;

import constants.BasicConst;
import com.qyh.gateway.core.context.IContext;
import enums.ResponseCode;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.*;
import com.qyh.gateway.core.response.GatewayResponse;
import io.netty.util.ReferenceCountUtil;

import java.util.Objects;

/**
 * 响应的辅助类
 */
public class ResponseHelper {

    /**
     * 获取响应对象
     */
    public static FullHttpResponse getHttpResponse(ResponseCode responseCode) {
        GatewayResponse gatewayResponse = GatewayResponse.buildGatewayResponse(responseCode);
        DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.INTERNAL_SERVER_ERROR,
                Unpooled.wrappedBuffer(gatewayResponse.getContent().getBytes()));

        httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON + ";charset=utf-8");
        httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content().readableBytes());
        return httpResponse;
    }

    /**
     * 通过上下文对象和Response对象 构建FullHttpResponse
     * 该方法用于根据`GatewayResponse`对象获取一个完整的响应对象`FullHttpResponse`，并设置相应的内容和响应头。
     * <p>
     * 具体的实现逻辑如下：
     * <p>
     * 1. 首先，判断`gatewayResponse`对象中是否存在`futureResponse`，如果存在则表示响应是异步获取的，需要通过`gatewayResponse.getFutureResponse().getResponseBodyAsByteBuffer()`获取响应内容，并将其包装成`ByteBuf`对象。
     * 2. 如果不存在`futureResponse`，则判断`gatewayResponse`的`content`是否为空，如果不为空，则将其转换成`ByteBuf`对象。
     * 3. 如果`content`为空，则创建一个空白的`ByteBuf`对象。
     * 4. 如果不存在`futureResponse`，则创建一个`DefaultFullHttpResponse`对象，使用`HTTP_1_1`版本和`gatewayResponse`中的`httpResponseStatus`作为参数，并设置响应内容和响应头。
     * a. 将`gatewayResponse`中的`responseHeaders`添加到响应头中。
     * b. 将`gatewayResponse`中的`extraResponseHeaders`添加到响应头中。
     * c. 设置`Content-Length`响应头，值为`httpResponse.content().readableBytes()`。
     * d. 返回该`DefaultFullHttpResponse`对象。
     * 5. 如果存在`futureResponse`，则将`gatewayResponse`中的`extraResponseHeaders`添加到`futureResponse`的头部中。
     * a. 创建一个`DefaultFullHttpResponse`对象，使用`HTTP_1_1`版本和`gatewayResponse`中的`futureResponse`的`statusCode`作为参数，并设置响应内容和响应头。
     * b. 将`futureResponse`的头部添加到响应头中。
     * c. 返回该`DefaultFullHttpResponse`对象。
     * <p>
     * 总结来说，该方法根据`GatewayResponse`对象的情况，构建一个完整的`FullHttpResponse`对象，并设置相应的内容和响应头。
     */
    private static FullHttpResponse getHttpResponse(IContext ctx, GatewayResponse gatewayResponse) {
        ByteBuf content;
        //首先，判断`gatewayResponse`对象中是否存在`futureResponse`，如果存在则表示响应是异步获取的，需要通过`gatewayResponse.getFutureResponse().getResponseBodyAsByteBuffer()`获取响应内容，并将其包装成`ByteBuf`对象。
        if (Objects.nonNull(gatewayResponse.getFutureResponse())) {
            content = Unpooled.wrappedBuffer(gatewayResponse.getFutureResponse()
                    .getResponseBodyAsByteBuffer());
        }
        //如果不存在`futureResponse`，则判断`gatewayResponse`的`content`是否为空，如果不为空，则将其转换成`ByteBuf`对象。
        else if (gatewayResponse.getContent() != null) {
            content = Unpooled.wrappedBuffer(gatewayResponse.getContent().getBytes());
        } else {
            //如果`content`为空，则创建一个空白的`ByteBuf`对象。
            content = Unpooled.wrappedBuffer(BasicConst.BLANK_SEPARATOR_1.getBytes());
        }

        if (Objects.isNull(gatewayResponse.getFutureResponse())) {
            //4. 如果不存在`futureResponse`，则创建一个`DefaultFullHttpResponse`对象，使用`HTTP_1_1`版本和`gatewayResponse`中的`httpResponseStatus`作为参数，并设置响应内容和响应头。
            //	 *    a. 将`gatewayResponse`中的`responseHeaders`添加到响应头中。
            //	 *    b. 将`gatewayResponse`中的`extraResponseHeaders`添加到响应头中。
            //	 *    c. 设置`Content-Length`响应头，值为`httpResponse.content().readableBytes()`。
            //	 *    d. 返回该`DefaultFullHttpResponse`对象。
            DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    gatewayResponse.getHttpResponseStatus(),
                    content);
            httpResponse.headers().add(gatewayResponse.getResponseHeaders());
            httpResponse.headers().add(gatewayResponse.getExtraResponseHeaders());
            httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content().readableBytes());
            return httpResponse;
        } else {
            gatewayResponse.getFutureResponse().getHeaders().add(gatewayResponse.getExtraResponseHeaders());

            DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.valueOf(gatewayResponse.getFutureResponse().getStatusCode()),
                    content);
            httpResponse.headers().add(gatewayResponse.getFutureResponse().getHeaders());
            return httpResponse;
        }
    }


    /**
     * 这是一个用于写回响应信息的方法，以下是对该方法的分析：
     * a. 使用ResponseHelper.getHttpResponse(com.qyh.gateway.core.context, (GatewayResponse)com.qyh.gateway.core.context.getResponse())构建响应对象，并将其转换为FullHttpResponse。
     * b. 如果不需要保持长连接（!com.qyh.gateway.core.context.isKeepAlive()），则通过context.getNettyCtx().writeAndFlush(httpResponse)方法将响应对象写回，并在写回完成后关闭连接。
     * c. 如果需要保持长连接，则通过httpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)设置响应头，表示保持连接。
     * d. 最后，通过context.completed()设置写回状态为COMPLETED，表示写回操作已完成。
     * <p>
     * 如果没有写回响应信息，但已经完成处理（com.qyh.gateway.core.context.isCompleted()为true），则调用context.invokeCompletedCallBack()方法执行完成回调操作。
     */
    public static void writeResponse(IContext context) {

        //首先，该方法会释放请求资源，调用context.releaseRequest()释放请求资源。
        context.releaseRequest();

        if (context.isWritten()) {
            //	1：第一步构建响应对象，并写回数据
            FullHttpResponse httpResponse = ResponseHelper.getHttpResponse(context, (GatewayResponse) context.getResponse());
            if (!context.isKeepAlive()) {
                context.getNettyCtx()
                        .writeAndFlush(httpResponse).addListener(ChannelFutureListener.CLOSE);
            }
            //	长连接：
            else {
                httpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                context.getNettyCtx().writeAndFlush(httpResponse);
            }
            //	2:	设置写回结束状态为： COMPLETED
            context.completed();
        } else if (context.isCompleted()) {
            context.invokeCompletedCallBack();
        }
    }


}
