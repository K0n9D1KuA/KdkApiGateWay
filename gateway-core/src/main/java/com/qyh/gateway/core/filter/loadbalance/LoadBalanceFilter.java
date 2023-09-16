package com.qyh.gateway.core.filter.loadbalance;

import com.qyh.gateway.core.context.GatewayContext;
import com.qyh.gateway.core.filter.Filter;
import com.qyh.gateway.core.filter.FilterAspect;
import com.qyh.gateway.core.request.GatewayRequest;
import config.Rule;
import config.ServiceInstance;
import exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;


import java.util.*;

import static constants.FilterConst.*;
import static enums.ResponseCode.SERVICE_INSTANCE_NOT_FOUND;

/**
 * @PROJECT_NAME: api-gateway
 * @DESCRIPTION: 负载均衡过滤器
 * @USER: WuYang
 * @DATE: 2023/3/12 22:02
 */
@Slf4j
@FilterAspect(id = LOAD_BALANCE_FILTER_ID,
        name = LOAD_BALANCE_FILTER_NAME,
        order = LOAD_BALANCE_FILTER_ORDER)
public class LoadBalanceFilter implements Filter {

    @Override
    public void doFilter(GatewayContext ctx) {
        String serviceId = ctx.getUniqueId();
        IGatewayLoadBalanceRule gatewayLoadBalanceRule = getLoadBalanceRule(ctx);
        ServiceInstance serviceInstance = gatewayLoadBalanceRule.choose(serviceId, ctx.isGray());
        log.info("ip : {} , port : {}", serviceInstance.getIp(), serviceInstance.getPort());
        GatewayRequest request = ctx.getRequest();
        if (serviceInstance != null && request != null) {
            String host = serviceInstance.getIp() + ":" + serviceInstance.getPort();
            request.setModifyHost(host);
        } else {
            log.warn("No instance available for :{}", serviceId);
            throw new NotFoundException(SERVICE_INSTANCE_NOT_FOUND);
        }
    }


    /**
     * @author: K0n9D1KuA
     * @description: 获取负载均衡器
     * @param: ctx 核心领域上下文
     * @return: com.qyh.gateway.core.filter.loadbalance.IGatewayLoadBalanceRule 负载均衡器
     * @date: 2023/9/4 11:24
     */
    public IGatewayLoadBalanceRule getLoadBalanceRule(GatewayContext ctx) {

        //获得规则对象
        Rule configRule = ctx.getRule();
        if (configRule == null) {
            return null;
        }
        //获得负载均衡 过滤器配置
        String type = configRule.getLoadBalanceConfig();

        //获得负载均衡过滤器配置 选择 随机 / 轮询 ？
        ServiceLoader<IGatewayLoadBalanceRule> serviceLoader = ServiceLoader.load(IGatewayLoadBalanceRule.class);
        Iterator<IGatewayLoadBalanceRule> iterator = serviceLoader.iterator();
        while (iterator.hasNext()) {
            IGatewayLoadBalanceRule iGatewayLoadBalanceRule = iterator.next();
            if (iGatewayLoadBalanceRule.match(type)) {
                return iGatewayLoadBalanceRule.getInstance(ctx.getUniqueId() + type);
            }
        }

        return null;
    }
}
