package com.qyh.gateway.core.manager;

import config.Rule;
import config.RuleNew;
import config.ServiceDefinition;
import config.ServiceInstance;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 动态服务缓存配置管理类
 */
public class DynamicConfigManagerNew {

    //	服务的定义集合：uniqueId代表服务的唯一标识
    private ConcurrentHashMap<String /* uniqueId */ , ServiceDefinition> serviceDefinitionMap = new ConcurrentHashMap<>();

    //	服务的实例集合：uniqueId与一对服务实例对应
    private ConcurrentHashMap<String /* uniqueId */ , Set<ServiceInstance>> serviceInstanceMap = new ConcurrentHashMap<>();

    //	规则集合
    private ConcurrentHashMap<String /* ruleId */ , RuleNew> ruleMap = new ConcurrentHashMap<>();

    //路径以及规则集合
    private ConcurrentHashMap<String /* 路径 */ , RuleNew> pathRuleMap = new ConcurrentHashMap<>();


    //存放所有的短链链接和长链链接对应关系
    private ConcurrentHashMap<String, String> shortUrlToLongUrl;


    private DynamicConfigManagerNew() {

    }

    //懒汉单例模式
    private static class SingletonHolder {
        private static final DynamicConfigManagerNew INSTANCE = new DynamicConfigManagerNew();
    }


    public static DynamicConfigManagerNew getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void changeServiceDefinition(String uniqueId,
                                        ServiceDefinition serviceDefinition) {
        serviceDefinitionMap.put(uniqueId, serviceDefinition);
    }

    public ServiceDefinition getServiceDefinition(String uniqueId) {
        return serviceDefinitionMap.get(uniqueId);
    }

    public void removeServiceDefinition(String uniqueId) {
        serviceDefinitionMap.remove(uniqueId);
    }

    public ConcurrentHashMap<String, ServiceDefinition> getServiceDefinitionMap() {
        return serviceDefinitionMap;
    }


    /**
     * 根据服务名获得对应的服务实例列表
     */
    public Set<ServiceInstance> getServiceInstanceByUniqueId(String serviceName, boolean gray) {
        Set<ServiceInstance> serviceInstances = serviceInstanceMap.get(serviceName);
        if (CollectionUtils.isEmpty(serviceInstances)) {
            return Collections.emptySet();
        }
        //过滤出灰度服务
        return serviceInstances.stream()
                .filter(serviceInstance -> Boolean.valueOf(gray).equals(serviceInstance.isGray()))
                .collect(Collectors.toSet());

    }

    public void changeServiceInstances(String uniqueId, ServiceInstance serviceInstance) {
        Set<ServiceInstance> set = serviceInstanceMap.get(uniqueId);
        set.add(serviceInstance);
    }

    public void changeServiceInstances(String uniqueId, Set<ServiceInstance> serviceInstanceSet) {
        serviceInstanceMap.put(uniqueId, serviceInstanceSet);
    }

    public void updateServiceInstance(String uniqueId, ServiceInstance serviceInstance) {
        Set<ServiceInstance> set = serviceInstanceMap.get(uniqueId);
        Iterator<ServiceInstance> it = set.iterator();
        while (it.hasNext()) {
            ServiceInstance is = it.next();
            if (is.getServiceInstanceId().equals(serviceInstance.getServiceInstanceId())) {
                it.remove();
                break;
            }
        }
        set.add(serviceInstance);
    }

    public void removeServiceInstance(String uniqueId, String serviceInstanceId) {
        Set<ServiceInstance> set = serviceInstanceMap.get(uniqueId);
        Iterator<ServiceInstance> it = set.iterator();
        while (it.hasNext()) {
            ServiceInstance is = it.next();
            if (is.getServiceInstanceId().equals(serviceInstanceId)) {
                it.remove();
                break;
            }
        }
    }

    public void removeServiceInstancesByUniqueId(String uniqueId) {
        serviceInstanceMap.remove(uniqueId);
    }


    /***************** 	对规则缓存进行操作的系列方法 	***************/

    public void putRule(String ruleId, RuleNew rule) {
        ruleMap.put(ruleId, rule);
    }

    public void putAllRule(List<RuleNew> ruleList) {
        ConcurrentHashMap<String, RuleNew> newRuleMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, RuleNew> newPathMap = new ConcurrentHashMap<>();
        for (RuleNew rule : ruleList) {
            String serviceName = rule.getServiceName();
            newRuleMap.put(rule.getId(), rule);
            newPathMap.put(serviceName + "." + rule.getPath(), rule);
        }
        ruleMap = newRuleMap;
        pathRuleMap = newPathMap;
    }

    public RuleNew getRuleByPathAndServiceName(String pathAndServiceName) {
        return pathRuleMap.get(pathAndServiceName);
    }

}
