package com.qyh.gateway.core.filter.loadbalance;

import com.qyh.gateway.core.manager.DynamicConfigManager;
import config.ServiceInstance;
import exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import static enums.ResponseCode.SERVICE_INSTANCE_NOT_FOUND;


@Slf4j
public class AbstractLoadBalanceRule implements IGatewayLoadBalanceRule {

    private static ConcurrentHashMap<String, IGatewayLoadBalanceRule> serviceMap = new ConcurrentHashMap<>();


    @Override
    public ServiceInstance choose(String serviceId, boolean isGray) {
        //这里选择灰度服务
        Set<ServiceInstance> serviceInstanceSet = DynamicConfigManager.getInstance().getServiceInstanceByUniqueId(serviceId, isGray);
        if (serviceInstanceSet.isEmpty()) {
            log.warn("no instance available for : {}", serviceId);
            throw new NotFoundException(SERVICE_INSTANCE_NOT_FOUND);
        }
        List<ServiceInstance> serviceInstanceList = new ArrayList<>(serviceInstanceSet);
        return doChoose(serviceInstanceList);
    }

    @Override
    public IGatewayLoadBalanceRule getInstance(String serviceIdentity) {
        IGatewayLoadBalanceRule iGatewayLoadBalanceRule = serviceMap.get(serviceIdentity);
        if (iGatewayLoadBalanceRule == null) {
            iGatewayLoadBalanceRule = getObject();
            serviceMap.put(serviceIdentity, iGatewayLoadBalanceRule);
        }
        return iGatewayLoadBalanceRule;
    }


    @Override
    public boolean match(String type) {
        return this.getLoadBalanceType().equals(type);
    }

    // 下列方法都需要交给子类实现 模板方法

    protected IGatewayLoadBalanceRule getObject() {
        throw new UnsupportedOperationException();
    }


    protected ServiceInstance doChoose(List<ServiceInstance> serviceInstanceList) {
        throw new UnsupportedOperationException();
    }

    protected String getLoadBalanceType() {
        throw new UnsupportedOperationException();
    }
}
