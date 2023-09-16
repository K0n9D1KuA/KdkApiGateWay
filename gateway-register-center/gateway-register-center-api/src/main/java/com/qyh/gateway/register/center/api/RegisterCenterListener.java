package com.qyh.gateway.register.center.api;


import config.ServiceDefinition;
import config.ServiceInstance;

import java.util.Set;


@FunctionalInterface
public interface RegisterCenterListener {

    //回调钩子函数
    void onChange(ServiceDefinition serviceDefinition,
                  Set<ServiceInstance> serviceInstanceSet);
}
