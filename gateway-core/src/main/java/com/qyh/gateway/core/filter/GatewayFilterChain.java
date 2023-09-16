package com.qyh.gateway.core.filter;

import com.qyh.gateway.core.context.GatewayContext;

import lombok.extern.slf4j.Slf4j;


import java.util.ArrayList;
import java.util.List;

/**
 * @PROJECT_NAME: api-gateway
 * @DESCRIPTION: 过滤器链条类
 * @USER: WuYang
 * @DATE: 2023/3/12 19:59
 */
@Slf4j
public class GatewayFilterChain {

    private List<Filter> filters = new ArrayList<>();


    public GatewayFilterChain addFilter(Filter filter) {
        filters.add(filter);
        return this;
    }

    public GatewayFilterChain addFilterList(List<Filter> filter) {
        filters.addAll(filter);
        return this;
    }


    public GatewayContext doFilter(GatewayContext ctx) throws Exception {
        if (filters.isEmpty()) {
            return ctx;
        }
        try {
            for (Filter fl : filters) {
                //依次执行 doFilter方法
                fl.doFilter(ctx);
                if (ctx.isTerminated()) {
                    //如果已经终止了 说明前面的拦截器已经提前写回 不用再写了
                    break;
                }
            }
        } catch (Exception e) {
            log.error("执行过滤器发生异常,异常信息：{}", e.getMessage());
            throw e;
        }
        return ctx;
    }


}

