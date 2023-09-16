package com.qyh.gateway.core.filter.dynamic.mock;


import com.qyh.gateway.core.context.GatewayContext;
import com.qyh.gateway.core.filter.dynamic.AbstractDynamicFilter;
import com.qyh.gateway.core.filter.FilterAspect;
import com.qyh.gateway.core.filter.Interruptible;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static constants.FilterConst.*;

@Slf4j
@FilterAspect(id = MOCK_FILTER_ID,
        name = MOCK_FILTER_NAME,
        order = MOCK_FILTER_ORDER)
/**
 * @description: MOCK模拟过滤器
 * @email 3161788646@qq.com
 * @author K0n9D1KuA
 * @date 2023/9/15 17:10
 * @version 1.0
 */

public class MockFilter extends AbstractDynamicFilter implements Interruptible {


    public static final String KEY = "mock_value";

    @Override
    protected void doFilter(Map<String, String> config, GatewayContext context) {
        //获得mock值
        String mockValue = config.get(KEY);
        //写回前端
        writeToClient(context, mockValue);
    }
}
