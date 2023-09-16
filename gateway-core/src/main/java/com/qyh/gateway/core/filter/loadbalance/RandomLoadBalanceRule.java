package com.qyh.gateway.core.filter.loadbalance;

import com.qyh.gateway.core.context.GatewayContext;
import com.qyh.gateway.core.manager.DynamicConfigManager;
import config.ServiceInstance;
import exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.ConcurrentException;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import static enums.ResponseCode.SERVICE_INSTANCE_NOT_FOUND;


/**
 * @author K0n9D1KuA
 * @version 1.0
 * @description: 随机负载均衡策略
 * @email 3161788646@qq.com
 * @date 2023/9/4 11:15
 */

@Slf4j
public class RandomLoadBalanceRule extends AbstractLoadBalanceRule {

    public static final String BALANCE_TYPE = "Random";



    @Override
    protected ServiceInstance doChoose(List<ServiceInstance> serviceInstanceList) {
        int size = serviceInstanceList.size();
        int randomIndex = ThreadLocalRandom.current().nextInt(size);
        return serviceInstanceList.get(randomIndex);
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
