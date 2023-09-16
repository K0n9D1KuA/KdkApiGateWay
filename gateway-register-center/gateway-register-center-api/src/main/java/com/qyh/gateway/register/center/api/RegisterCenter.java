package com.qyh.gateway.register.center.api;

import config.ServiceDefinition;
import config.ServiceInstance;


public interface RegisterCenter {


    /**
     * 初始化
     *
     * @param registerAddress 注册中心地址
     * @param env             环境
     */
    void init(String registerAddress, String env);

    /**
     * 注册·
     *
     * @param serviceDefinition 服务定义
     * @param serviceInstance   服务实例
     */
    void register(ServiceDefinition serviceDefinition, ServiceInstance serviceInstance);

    /**
     * 注销
     *
     * @param serviceDefinition 服务定义
     * @param serviceInstance   服务实例
     */
    void deregister(ServiceDefinition serviceDefinition, ServiceInstance serviceInstance);

    /**
     * 订阅所有服务变更
     *
     * @param registerCenterListener 监听服务变更的监听器
     */
    void subscribeAllServices(RegisterCenterListener registerCenterListener);
}
