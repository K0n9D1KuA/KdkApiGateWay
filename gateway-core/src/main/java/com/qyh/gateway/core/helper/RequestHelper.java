package com.qyh.gateway.core.helper;


import com.qyh.gateway.core.manager.DynamicConfigManager;
import com.qyh.gateway.core.manager.DynamicConfigManagerNew;
import com.qyh.gateway.core.response.GatewayResponse;
import config.*;
import constants.BasicConst;
import constants.GatewayConst;
import constants.GatewayProtocol;
import com.qyh.gateway.core.context.GatewayContext;
import enums.ResponseCode;
import exception.ResponseException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import com.qyh.gateway.core.request.GatewayRequest;


import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static enums.ResponseCode.PATH_NO_MATCHED;


@Slf4j
public class RequestHelper {


    private static final ConcurrentHashMap<String, String> shortAndOld = new ConcurrentHashMap<>();

    private static final Set<String> longUrlSet = new HashSet<>();


    public static GatewayContext doContext(FullHttpRequest request, ChannelHandlerContext ctx) {

        //	构建请求对象GatewayRequest
        GatewayRequest gateWayRequest = doRequest(request, ctx);

        //	根据请求对象里的uniqueId，获取资源服务信息(也就是服务定义信息)
        ServiceDefinition serviceDefinition = DynamicConfigManager
                .getInstance()
                .getServiceDefinition(gateWayRequest.getUniqueId());


        //根据请求对象获取规则
        Rule rule = getRule(gateWayRequest, gateWayRequest.getUniqueId());

        return new GatewayContext(serviceDefinition.getProtocol(), ctx, HttpUtil.isKeepAlive(request), gateWayRequest, rule);
    }


    /**
     * 获取Rule对象
     *
     * @param gateWayRequest 请求对象
     * @return
     */
    private static Rule getRule(GatewayRequest gateWayRequest, String serviceId) {
        String key = serviceId + "." + gateWayRequest.getPath();
        Rule rule = DynamicConfigManager.getInstance().getRuleByPath(key);

        if (rule != null) {
            return rule;
        }
        return null;
    }




    /**
     * 构建Request请求对象
     */
    protected static GatewayRequest doRequest(FullHttpRequest fullHttpRequest, ChannelHandlerContext ctx) {

        //请求头
        HttpHeaders headers = fullHttpRequest.headers();

        //	从header头获取必须要传入的关键属性 uniqueId
        String uniqueId = headers.get(GatewayConst.UNIQUE_ID);

        String host = headers.get(HttpHeaderNames.HOST);
        HttpMethod method = fullHttpRequest.method();
        String uri = fullHttpRequest.uri();
        String clientIp = getClientIp(ctx, fullHttpRequest);
        String contentType = HttpUtil.getMimeType(fullHttpRequest) == null ? null : HttpUtil.getMimeType(fullHttpRequest).toString();
        Charset charset = HttpUtil.getCharset(fullHttpRequest, StandardCharsets.UTF_8);

        return new GatewayRequest(uniqueId,
                charset,
                clientIp,
                host,
                uri,
                method,
                contentType,
                headers,
                fullHttpRequest);
    }

    /**
     * 获取客户端ip
     */
    private static String getClientIp(ChannelHandlerContext ctx, FullHttpRequest request) {
        String xForwardedValue = request.headers().get(BasicConst.HTTP_FORWARD_SEPARATOR);

        String clientIp = null;
        if (StringUtils.isNotEmpty(xForwardedValue)) {
            List<String> values = Arrays.asList(xForwardedValue.split(", "));
            if (values.size() >= 1 && StringUtils.isNotBlank(values.get(0))) {
                clientIp = values.get(0);
            }
        }
        if (clientIp == null) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
            clientIp = inetSocketAddress.getAddress().getHostAddress();
        }
        return clientIp;
    }

}
