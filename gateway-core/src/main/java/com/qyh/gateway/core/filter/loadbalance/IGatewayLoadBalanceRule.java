package com.qyh.gateway.core.filter.loadbalance;


import config.ServiceInstance;

/**
 * 负载均衡顶级接口
 */
public interface IGatewayLoadBalanceRule {


    /**
     * 通过服务ID拿到对应的服务实
     */
    ServiceInstance choose(String serviceId , boolean isGray);

    IGatewayLoadBalanceRule getInstance(String serviceIdentity);

    boolean match(String type);
}
