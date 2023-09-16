package com.qyh.gateway.core.filter;


import com.qyh.gateway.core.context.GatewayContext;

/**
 * 工厂接口
 */
public interface FilterFactory {

    /**
     * 构建过滤器链条
     * @param ctx
     * @return
     * @throws Exception
     */
    GatewayFilterChain buildFilterChain(GatewayContext ctx) throws Exception;

    /**
     * 通过过滤器ID获取过滤器
     * @param filterId
     * @return
     * @param <T>
     * @throws Exception
     */
    <T> T getFilterById(String filterId) throws Exception;


}
