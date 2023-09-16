package com.qyh.gateway.core.helper;

import com.qyh.gateway.core.context.GatewayContext;
import com.qyh.gateway.core.manager.DynamicConfigManager;
import com.qyh.gateway.core.request.GatewayRequest;
import com.qyh.gateway.core.response.GatewayResponse;
import enums.ResponseCode;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
public class HashShortLinkChecker {

    private static ConcurrentHashMap<String, String> shortAndOld;

    private static Set<String> longUrlSet;

    static {
        //从配置中心加载 短链
        ConcurrentHashMap<String, String> shortUrlToLongUrl = DynamicConfigManager.getInstance().getShortUrlToLongUrl();
        longUrlSet = new HashSet<>(shortUrlToLongUrl.values());
        shortAndOld = shortUrlToLongUrl;
    }

    /**
     * @return boolean 校验链接是否成功
     * @author: K0n9D1KuA
     * @description: 校验连接 长短链转化
     * @param: request 请求
     * @param: ctx netty 上下文
     * @date: 2023/9/5 11:36
     */
    public static boolean checkUrl(FullHttpRequest request, ChannelHandlerContext ctx) {

        //获得原始链接
        String originalUrl = request.getUri();


        //如果原始连接就是长连接 那么直接返回
        if (isLongUrl(originalUrl)) {
            return true;
        }


        //获得短链对应的长链
        String longPath = shortAndOld.get(originalUrl);


        //短链接
        if (longPath == null) {
            //说明该短链是无效的短链
            //告知前端路径不匹配
            GatewayRequest gatewayRequest = RequestHelper.doRequest(request, ctx);
            GatewayContext gatewayContext = new GatewayContext.Builder()
                    .setNettyCtx(ctx)
                    .setKeepAlive(HttpUtil.isKeepAlive(request))
                    .setRequest(gatewayRequest).build();
            gatewayContext.setResponse(GatewayResponse.buildGatewayResponse(ResponseCode.PATH_NO_MATCHED));
            gatewayContext.written();
            ResponseHelper.writeResponse(gatewayContext);
            log.error("{} , 是无效的短链....", originalUrl);
            gatewayContext.terminated();
            return false;
        } else {
            //替换为长链
            request.setUri(longPath);
            return true;
        }

    }


    /**
     * @author: K0n9D1KuA
     * @description: 校验该链接是原始链接
     * @param: originalUrl 原始链接
     * @return: boolean
     * @date: 2023/9/6 15:27
     */
    private static boolean isLongUrl(String originalUrl) {
        return longUrlSet.contains(originalUrl);
    }


}
