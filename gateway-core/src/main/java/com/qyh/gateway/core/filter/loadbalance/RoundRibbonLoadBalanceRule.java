package com.qyh.gateway.core.filter.loadbalance;

import config.ServiceInstance;
import lombok.extern.slf4j.Slf4j;


import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @PROJECT_NAME: api-gateway
 * @DESCRIPTION: 负载均衡-轮询
 * @USER: WuYang
 * @DATE: 2023/3/12 22:13
 */
@Slf4j
public class RoundRibbonLoadBalanceRule extends AbstractLoadBalanceRule {


    final AtomicInteger position = new AtomicInteger(0);

    //    String LOAD_BALANCE_STRATEGY_RANDOM = "Random";
    //    String LOAD_BALANCE_STRATEGY_ROUND_ROBIN = "RoundRobin";
    public static final String BALANCE_TYPE = "RoundRobin";


    @Override
    protected ServiceInstance doChoose(List<ServiceInstance> serviceInstanceList) {
        int pos = position.getAndIncrement();
        int size = serviceInstanceList.size();
        int takeIndex = pos % size;
        return serviceInstanceList.get(takeIndex);
    }

    @Override
    protected IGatewayLoadBalanceRule getObject() {
        return this;
    }

    @Override
    public boolean match(String type) {
        return BALANCE_TYPE.equals(type);
    }

}
