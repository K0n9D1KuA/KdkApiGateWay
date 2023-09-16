package com.qyh.gateway.core.filter.router;

import com.qyh.gateway.core.config.ConfigLoader;
import com.qyh.gateway.core.context.GatewayContext;
import com.qyh.gateway.core.filter.Filter;
import com.qyh.gateway.core.filter.FilterAspect;
import com.qyh.gateway.core.helper.AsyncHttpHelper;
import com.qyh.gateway.core.helper.ResponseHelper;
import com.qyh.gateway.core.response.GatewayResponse;
import enums.ResponseCode;
import exception.ConnectException;
import exception.ResponseException;

import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

import static constants.FilterConst.*;


/**
 * @author K0n9D1KuA
 * @version 1.0
 * @description: 路由过滤器
 * @email 3161788646@qq.com
 * @date 2023/9/4 11:44
 */

@Slf4j
@FilterAspect(id = ROUTER_FILTER_ID,
        name = ROUTER_FILTER_NAME,
        order = ROUTER_FILTER_ORDER)
public class RouterFilter implements Filter {
    private static Logger accessLog = LoggerFactory.getLogger("accessLog");

    @Override
    public void doFilter(GatewayContext gatewayContext) throws Exception {
        Request request = gatewayContext.getRequest().build();
        CompletableFuture<Response> future = AsyncHttpHelper.getInstance().executeRequest(request);

        boolean whenComplete = ConfigLoader.getConfig().isWhenComplete();

        if (whenComplete) {
            future.whenComplete((response, throwable) -> {
                complete(request, response, throwable, gatewayContext);
            });
        } else {
            future.whenCompleteAsync((response, throwable) -> {
                complete(request, response, throwable, gatewayContext);
            });
        }
    }

    private void complete(Request request,
                          Response response,
                          Throwable throwable,
                          GatewayContext gatewayContext) {
        gatewayContext.releaseRequest();

        //获得当前请求已经请求的次数
        int alreadyRetryTimes = gatewayContext.getRetryTimes().get();

        //获得规则里面设置的重试次数
        int time = gatewayContext.getRule().getRetryTimes();

        if ((throwable instanceof TimeoutException || throwable instanceof IOException) && alreadyRetryTimes < time) {
            //进行重试
            doRetry(gatewayContext);
            return;
        }

        try {
            if (Objects.nonNull(throwable)) {
                String url = request.getUrl();
                if (throwable instanceof TimeoutException) {
                    log.warn("complete time out {}", url);
                    gatewayContext.setThrowable(new ResponseException(ResponseCode.REQUEST_TIMEOUT));
                    gatewayContext.setResponse(GatewayResponse.buildGatewayResponse(ResponseCode.REQUEST_TIMEOUT));
                } else {
                    gatewayContext.setThrowable(new ConnectException(throwable,
                            gatewayContext.getUniqueId(),
                            url, ResponseCode.HTTP_RESPONSE_ERROR));
                    gatewayContext.setResponse(GatewayResponse.buildGatewayResponse(ResponseCode.HTTP_RESPONSE_ERROR));
                }
            } else {
                gatewayContext.setResponse(GatewayResponse.buildGatewayResponse(response));
            }
        } catch (Throwable t) {
            gatewayContext.setThrowable(new ResponseException(ResponseCode.INTERNAL_ERROR));
            gatewayContext.setResponse(GatewayResponse.buildGatewayResponse(ResponseCode.INTERNAL_ERROR));
            log.error("complete error", t);
        } finally {
            gatewayContext.written();
            ResponseHelper.writeResponse(gatewayContext);

            //打印日志......
            accessLog.info("{} {} {} {} {} {} {}",
                    System.currentTimeMillis() - gatewayContext.getRequest().getBeginTime(),
                    gatewayContext.getRequest().getClientIp(),
                    gatewayContext.getRequest().getUniqueId(),
                    gatewayContext.getRequest().getMethod(),
                    gatewayContext.getRequest().getPath(),
                    gatewayContext.getResponse().getHttpResponseStatus().code(),
                    gatewayContext.getResponse().getFutureResponse().getResponseBodyAsBytes().length);
        }
    }

    private void doRetry(GatewayContext gatewayContext) {
        log.info("进行重试，重试次数为 : {}", gatewayContext.getRetryTimes().incrementAndGet());
        try {
            doFilter(gatewayContext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
