package com.qyh.gateway.core;


import com.alibaba.fastjson.JSON;
import com.qyh.gateway.config.center.api.ConfigCenter;
import com.qyh.gateway.config.center.impl.NaocsConfigCenter;
import com.qyh.gateway.core.config.Config;
import com.qyh.gateway.core.config.ConfigLoader;
import com.qyh.gateway.core.manager.DynamicConfigManager;
import com.qyh.gateway.register.center.nacos.impl.NacosRegisterCenter;
import config.ServiceDefinition;
import config.ServiceInstance;
import com.qyh.gateway.core.container.Container;
import com.qyh.gateway.register.center.api.RegisterCenter;
import lombok.extern.slf4j.Slf4j;
import utils.NetUtils;
import utils.TimeUtil;

import java.util.HashMap;

import static constants.BasicConst.COLON_SEPARATOR;


/**
 * API网关启动类
 */

@Slf4j

public class Bootstrap {

    public static void main(String[] args) {

        //加载网关核心静态配置
        Config config = ConfigLoader.getInstance().load(args);

        //配置中心相关
        configRegisterAndSubscribe(config);

        //启动容器
        Container container = new Container(config);
        container.start();

        //将该服务注册到注册中心上
        //订阅服务变更
        //服务优雅关机
        //收到kill信号时调用
        registerAndSubAndShutdown(config, container);
    }


    private static ServiceInstance buildGatewayServiceInstance(Config config) {
        //获得本地ip
        String localIp = NetUtils.getLocalIp();
        //获得配置文件里面的端口
        int port = config.getPort();

        ServiceInstance serviceInstance = new ServiceInstance();
        //服务实例ID localhost:8080  即 ip:端口
        serviceInstance.setServiceInstanceId(localIp + COLON_SEPARATOR + port);
        serviceInstance.setIp(localIp);
        serviceInstance.setPort(port);
        //设置注册时间
        serviceInstance.setRegisterTime(TimeUtil.currentTimeMillis());

        return serviceInstance;
    }

    private static ServiceDefinition buildGatewayServiceDefinition(Config config) {
        ServiceDefinition serviceDefinition = new ServiceDefinition();

        serviceDefinition.setInvokerMap(new HashMap<>());
        serviceDefinition.setUniqueId(config.getApplicationName());
        serviceDefinition.setServiceId(config.getApplicationName());
        serviceDefinition.setEnvType(config.getEnv());
        return serviceDefinition;
    }


    /**
     * @author: K0n9D1KuA
     * @description: 将网关服务注册到注册中心 并且订阅各个下游服务器的服务列表更新变化
     * @param: config 网关配置信息
     * @param: container
     * @return:
     * @date: 2023/9/15 23:12
     */
    private static void registerAndSubAndShutdown(Config config, Container container) {


        //连接注册中心，将注册中心的实例加载到本地
        RegisterCenter registerCenter = new NacosRegisterCenter(config.getRegistryAddress(), config.getEnv());

        //创建服务定义
        ServiceDefinition serviceDefinition = buildGatewayServiceDefinition(config);

        //创建服务实例
        ServiceInstance serviceInstance = buildGatewayServiceInstance(config);

        //将该服务注册到nacos上
        registerCenter.register(serviceDefinition, serviceInstance);

        //服务订阅
        registerCenter.subscribeAllServices((changeServiceDefinition, changeServiceSet) -> {
            //更新服务定义和实例的缓存
            log.info("服务发生变化: 服务名: {} 服务实例列表: {}", serviceDefinition.getUniqueId(),
                    JSON.toJSON(changeServiceSet));
            DynamicConfigManager manager = DynamicConfigManager.getInstance();
            manager.changeServiceInstances(changeServiceDefinition.getUniqueId(), changeServiceSet);
            manager.changeServiceDefinition(changeServiceDefinition.getUniqueId(), changeServiceDefinition);
            System.out.println("服务更新啦" + manager.getServiceDefinitionMap());
        });


        //服务优雅关机
        //收到kill信号时调用
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                registerCenter.deregister(buildGatewayServiceDefinition(config),
                        buildGatewayServiceInstance(config));

                //关闭 netty
                //关闭 netty-client
                container.shutdown();
            }
        });
    }

    /**
     * @author: K0n9D1KuA
     * @description: 从配置中心拉取各个下游服务器的网关路由规则
     * @param: config 网关配置信息
     * @date: 2023/9/15 23:11
     */
    private static void configRegisterAndSubscribe(Config config) {


        //配置中心管理器初始化，连接配置中心
        ConfigCenter configCenter = new NaocsConfigCenter();
        configCenter.init(config.getRegistryAddress(), config.getEnv(), config.getConfigNames());


        //监听各个下游服务器的网关路由规则的更新 删除 更改....
        configCenter.subscribeRulesChange(rules -> DynamicConfigManager.getInstance()
                .putAllRule(rules));


//        //监听短链的新增、修改、删除
//        configCenter.subscribeShortUrlsChangeL(map -> {
//            DynamicConfigManager.getInstance().setShortUrlToLongUrl(map);
//        });

    }

}
