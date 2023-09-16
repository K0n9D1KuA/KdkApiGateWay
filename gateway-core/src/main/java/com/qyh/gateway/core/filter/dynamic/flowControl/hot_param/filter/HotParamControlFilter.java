package com.qyh.gateway.core.filter.dynamic.flowControl.hot_param.filter;

import com.qyh.gateway.core.context.GatewayContext;
import com.qyh.gateway.core.filter.FilterAspect;
import com.qyh.gateway.core.filter.Interruptible;
import com.qyh.gateway.core.filter.dynamic.AbstractDynamicFilter;
import com.qyh.gateway.core.filter.dynamic.flowControl.hot_param.factory.HotParamControllerFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author K0n9D1KuA
 * @version 1.0
 * @description: 热点参数限流过滤器
 * @email 3161788646@qq.com
 * @date 2023/9/16 0:42
 */
@FilterAspect(id = "hot_param_control_filter",
        name = "hot_param_control_filter",
        order = 0
)
@Slf4j
public class HotParamControlFilter extends AbstractDynamicFilter implements Interruptible {

    public static final String MSG = "你的请求过于频繁 , 请稍后再试!";

    public static final String CONTROL_TYPE = "control_type";

    @Override
    protected void doFilter(Map<String, String> config, GatewayContext context) {
        String controlType = config.get(CONTROL_TYPE);
        log.info("进行 {} 方式限流", controlType);
        boolean canPass = HotParamControllerFactory
                .getHotParamControllerByType(controlType)
                .canPass(context, config);
        if (!canPass) {
            //被限流了
            log.info("请求 : {} , 被热点参数限流 ", context.getRequest().getUri());
            writeToClient(context, MSG);
        }
    }

}
