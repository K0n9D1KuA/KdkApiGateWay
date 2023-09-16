package com.qyh.gateway.register.center.nacos.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingMaintainFactory;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.Service;
import com.alibaba.nacos.api.naming.pojo.ServiceInfo;
import com.alibaba.nacos.common.executor.NameThreadFactory;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.qyh.gateway.register.center.api.RegisterCenter;
import com.qyh.gateway.register.center.api.RegisterCenterListener;
import config.ServiceDefinition;
import config.ServiceInstance;
import constants.GatewayConst;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;


@Slf4j
public class NacosRegisterCenter implements RegisterCenter {

    //一次订阅服务的pageSize
    public final static Integer PAGE_SIZE = 100;


    //注册中心地址
    private String registerAddress;

    //环境
    private String env;

    //=== nacos 相关api====
    //主要用于维护服务实例信息
    private NamingService namingService;

    //主要用于维护服务定义信息
    private NamingMaintainService namingMaintainService;

    //监听器列表
    private List<RegisterCenterListener> registerCenterListenerList = new CopyOnWriteArrayList<>();


    public NacosRegisterCenter(String registerAddress, String env) {
        this.registerAddress = registerAddress;
        this.env = env;
        this.init(registerAddress, env);
    }


    @Override
    public void init(String registerAddress, String env) {
        try {
            this.namingMaintainService = NamingMaintainFactory.createMaintainService(this.registerAddress);
            this.namingService = NamingFactory.createNamingService(this.registerAddress);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void register(ServiceDefinition serviceDefinition, ServiceInstance serviceInstance) {
        try {
            //构造nacos实例信息
            Instance nacosInstance = new Instance();
            //服务示例id
            nacosInstance.setInstanceId(serviceInstance.getServiceInstanceId());
            nacosInstance.setPort(serviceInstance.getPort());
            nacosInstance.setIp(serviceInstance.getIp());

            //设置元数据信息
            HashMap<String, String> mataDataMap = new HashMap<>();
            mataDataMap.put(GatewayConst.META_DATA_KEY /* meta */, JSON.toJSONString(serviceInstance)/* 服务实例信息 */);
            nacosInstance.setMetadata(mataDataMap);

            //注册
            namingService.registerInstance(serviceDefinition.getServiceId(), env, nacosInstance);

            //更新服务定义
            HashMap<String, String> definitionMataDataMap = new HashMap<>();
            definitionMataDataMap.put(GatewayConst.META_DATA_KEY /* meta */, JSON.toJSONString(serviceDefinition)/* 服务定义信息 */);


            namingMaintainService.updateService(serviceDefinition.getServiceId(), env, 0,
                    definitionMataDataMap);

            log.info("服务成功注册到注册中心! {} {}", serviceDefinition, serviceInstance);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deregister(ServiceDefinition serviceDefinition, ServiceInstance serviceInstance) {
        try {
            namingService.registerInstance(serviceDefinition.getServiceId(),
                    env, serviceInstance.getIp(), serviceInstance.getPort());
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void subscribeAllServices(RegisterCenterListener registerCenterListener) {
        registerCenterListenerList.add(registerCenterListener);

        doSubscribeAllServices();

        //需要一个定时器 来发现新的服务
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1, new NameThreadFactory("doSubscribeAllServicesThread") {
        });

        //开启定时器 每10s拉取一次服务
        scheduledExecutorService.scheduleWithFixedDelay(this::doSubscribeAllServices, 10, 10, TimeUnit.SECONDS);
    }


    private void doSubscribeAllServices() {
        try {
            //记重表  记录已经定义过的服务 防止重复订阅
            Set<String> subscribeService = namingService.getSubscribeServices().stream()
                    .map(ServiceInfo::getName).collect(Collectors.toSet());


            //分页
            int pageNo = 1;


            //分页从nacos拿到服务列表
            List<String> serviseList = namingService
                    .getServicesOfServer(pageNo, PAGE_SIZE, env).getData();

            while (CollectionUtils.isNotEmpty(serviseList)) {

                log.info("服务列表大小: {}", serviseList.size());
                System.out.println(serviseList);

                //遍历
                for (String service : serviseList) {
                    //如果已经订阅过了 那么直接跳过
                    if (subscribeService.contains(service)) {
                        continue;
                    }

                    //nacos事件监听器
                    EventListener eventListener = new NacosRegisterListener();
                    eventListener.onEvent(new NamingEvent(service, null));
                    namingService.subscribe(service, env, eventListener);
                    log.info("订阅服务 服务名:{} 服务环境:{}", service, env);
                }

                //获得下一页的服务
                serviseList = namingService
                        .getServicesOfServer(++pageNo, PAGE_SIZE, env).getData();
            }

        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

    //Nacos服务订阅后 回调的监听器
    public class NacosRegisterListener implements EventListener {

        @Override
        public void onEvent(Event event) {
            if (event instanceof NamingEvent) {
                NamingEvent namingEvent = (NamingEvent) event;
                String serviceName = namingEvent.getServiceName();

                try {
                    //获取服务定义信息
                    Service service = namingMaintainService.queryService(serviceName, env);
                    ServiceDefinition serviceDefinition = JSON.parseObject(service.getMetadata()
                            .get(GatewayConst.META_DATA_KEY), ServiceDefinition.class);

                    //获取服务实例信息
                    List<Instance> allInstances = namingService.getAllInstances(service.getName(), env);
                    Set<ServiceInstance> set = new HashSet<>();

                    for (Instance instance : allInstances) {
                        ServiceInstance serviceInstance = JSON.parseObject(instance.getMetadata()
                                .get(GatewayConst.META_DATA_KEY), ServiceInstance.class);
                        set.add(serviceInstance);
                    }


                    //调用我们自己的监听器
                    registerCenterListenerList
                            .forEach(l -> l.onChange(serviceDefinition, set));
                } catch (NacosException e) {
                    throw new RuntimeException(e);
                }


            }
        }
    }
}
