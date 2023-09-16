package com.qyh.gateway.core.filter.dynamic;

import com.qyh.gateway.core.context.GatewayContext;
import com.qyh.gateway.core.filter.Filter;

import java.util.Map;

/**
 * @author K0n9D1KuA
 * @version 1.0
 * @description: 动态过滤器父类
 * 规定了一些公用的算法 , 从规则对象里面获得该动态过滤器的规则配置
 * @email 3161788646@qq.com
 * @date 2023/9/16 13:06
 */

public class AbstractDynamicFilter implements Filter {
    @Override
    public void doFilter(GatewayContext ctx) throws Exception {
        doFilter(getDynamicFilterConfig(ctx), ctx);
    }


    /**
     * @author: K0n9D1KuA
     * @description: 交给子类实现具体的逻辑
     * @param: config 该动态过滤器的规则配置
     * @param: context 领域上下文
     * @return:
     * @date: 2023/9/16 13:08
     */

    protected void doFilter(Map<String, String> config, GatewayContext context) {
        throw new UnsupportedOperationException();
    }

    /**
     * @author: K0n9D1KuA
     * @description: 从规则对象里面获得该动态过滤器的规则配置
     * @param: context 领域上下文
     * @return: java.util.Map<java.lang.String, java.lang.String>
     * @date: 2023/9/16 13:08
     */
    private Map<String, String> getDynamicFilterConfig(GatewayContext context) {
        return context
                .getRule()
                .getDynamicFilterConfigByFilterId(getId())
                .getConfig();
    }
}
