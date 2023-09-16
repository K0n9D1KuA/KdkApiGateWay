package com.qyh.gateway.client.support;

import com.qyh.gateway.client.core.ApiProperties;
import com.qyh.gateway.config.center.api.ConfigCenter;
import com.qyh.gateway.config.center.impl.NaocsConfigCenter;
import com.qyh.gateway.register.center.api.RegisterCenter;
import com.qyh.gateway.register.center.nacos.impl.NacosRegisterCenter;
import config.ServiceDefinition;
import config.ServiceInstance;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractClientRegisterAndConfigManager {
    @Getter
    private ApiProperties apiProperties;

    //注册中心客户端
    private RegisterCenter registerCenter;

    //配置中心客户端
    private ConfigCenter configCenter;

    protected AbstractClientRegisterAndConfigManager(ApiProperties apiProperties) {
        this.apiProperties = apiProperties;

        this.registerCenter = new NacosRegisterCenter(apiProperties.getRegisterAddress(), apiProperties.getEnv());

        //初始化注册中心管理器
        registerCenter.init(apiProperties.getRegisterAddress(), apiProperties.getEnv());


        //初始化配置中心
        this.configCenter = new NaocsConfigCenter();
        configCenter.init(apiProperties.getRegisterAddress(), apiProperties.getEnv());
    }


    /**
     * @author: K0n9D1KuA
     * @description: 注册服务实例和服务定义
     * @param: serviceDefinition 服务定义
     * @param: serviceInstance 服务实例
     * @date: 2023/9/6 13:53
     */
    protected void register(ServiceDefinition serviceDefinition, ServiceInstance serviceInstance) {
        registerCenter.register(serviceDefinition, serviceInstance);
    }


    /**
     * @author: K0n9D1KuA
     * @description: 将 content 写入 dataId的远程配置文件
     * @param: dataId
     * @param: env
     * @param: content
     * @date: 2023/9/6 13:55
     */
    protected void writeToConfigCenter(String dataId, String env, String content) {
        configCenter.writeToConfigCenter(dataId, env, content);
    }


}
