package com.qyh.gateway.core.filter.dynamic.gray;


import com.qyh.gateway.core.context.GatewayContext;
import com.qyh.gateway.core.filter.dynamic.AbstractDynamicFilter;
import com.qyh.gateway.core.filter.FilterAspect;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static constants.FilterConst.*;

@Slf4j
@FilterAspect(id = GRAY_FILTER_ID,
        name = GRAY_FILTER_NAME,
        order = GRAY_FILTER_ORDER)
public class GrayFilter extends AbstractDynamicFilter {


    public static final String GRAY = "true";

    public static final String CONFIG_KEY = "probability";

    public static final String HEAD_KEY = "gray";

    @Override
    protected void doFilter(Map<String, String> config, GatewayContext context) {
        //从请求头里面拿 是否是请求的灰度服务
        String isGary = context.getRequest().getHeaders().get(HEAD_KEY);

        if (GRAY.equals(isGary)) {
            context.setGray(true);
            return;
        }

        //获取规则该路径对应的规则对象指定的概率值
        String configValue = config.get(CONFIG_KEY);
        int probability = Integer.parseInt(configValue);


        //获得用户ip的hashcode
        //然后对该hashcode 进行 取模  概率是由下游服务器自己指定
        //如果没有指定 默认就是 1/1024
        int SIZE_MASK = probability - 1;
        int res = context.getRequest().getClientIp().hashCode() & SIZE_MASK;


        //概率大概是 1/1024
        context.setGray(res == 1);
    }
}
