package com.qyh.gateway.core.filter.dynamic.flowControl.fail_fast.filter;


import com.qyh.gateway.core.context.GatewayContext;
import com.qyh.gateway.core.filter.Interruptible;
import com.qyh.gateway.core.filter.dynamic.AbstractDynamicFilter;

import java.util.Map;

/**
 * @author K0n9D1KuA
 * @version 1.0
 * @description: 直接失败限流过滤器
 * 如果被限流了 那么就会直接失败
 * @email 3161788646@qq.com
 * @date 2023/9/16 16:24
 */

public class FailFastControlFilter extends AbstractDynamicFilter implements Interruptible {


    @Override
    protected void doFilter(Map<String, String> config, GatewayContext context) {

    }
}
