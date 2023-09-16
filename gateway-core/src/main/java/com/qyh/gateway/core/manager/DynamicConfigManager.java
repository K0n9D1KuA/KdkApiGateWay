package com.qyh.gateway.core.manager;

import config.Rule;
import config.ServiceDefinition;
import config.ServiceInstance;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 动态服务缓存配置管理类
 */
public class DynamicConfigManager {

    //	服务的定义集合：uniqueId代表服务的唯一标识
    private ConcurrentHashMap<String /* uniqueId */ , ServiceDefinition> serviceDefinitionMap = new ConcurrentHashMap<>();

    //	服务的实例集合：uniqueId与一对服务实例对应
    private ConcurrentHashMap<String /* uniqueId */ , Set<ServiceInstance>> serviceInstanceMap = new ConcurrentHashMap<>();

    //	规则集合
    private ConcurrentHashMap<String /* ruleId */ , Rule> ruleMap = new ConcurrentHashMap<>();

    //路径以及规则集合
    private ConcurrentHashMap<String /* 路径 */ , Rule> pathRuleMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String /* 服务名 */ , List<Rule>> serviceRuleMap = new ConcurrentHashMap<>();

    //存放所有的短链链接和长链链接对应关系
    private ConcurrentHashMap<String, String> shortUrlToLongUrl;


    public ConcurrentHashMap<String, String> getShortUrlToLongUrl() {
        return shortUrlToLongUrl;
    }

    public void setShortUrlToLongUrl(ConcurrentHashMap<String, String> shortUrlToLongUrl) {
        this.shortUrlToLongUrl = shortUrlToLongUrl;
    }

    private DynamicConfigManager() {
    }

    private static class SingletonHolder {
        private static final DynamicConfigManager INSTANCE = new DynamicConfigManager();
    }


    /***************** 	对服务定义缓存进行操作的系列方法 	***************/

    public static DynamicConfigManager getInstance() {
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

    /***************** 	对服务实例缓存进行操作的系列方法 	***************/

    public Set<ServiceInstance> getServiceInstanceByUniqueId(String uniqueId, boolean gray) {
        Set<ServiceInstance> serviceInstances = serviceInstanceMap.get(uniqueId);
        if (CollectionUtils.isEmpty(serviceInstances)) {
            return Collections.emptySet();
        }

        //过滤出灰度服务
        Set<ServiceInstance> grayServiceInstances = serviceInstances.stream().filter(serviceInstance -> Boolean.valueOf(gray).equals(serviceInstance.isGray())).collect(Collectors.toSet());
        return grayServiceInstances;
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

    public void putRule(String ruleId, Rule rule) {
        ruleMap.put(ruleId, rule);
    }

    public void putAllRule(List<Rule> ruleList) {
        ConcurrentHashMap<String, Rule> newRuleMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, Rule> newPathMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, List<Rule>> newServiceMap = new ConcurrentHashMap<>();
        for (Rule rule : ruleList) {
            newRuleMap.put(rule.getId(), rule);
            List<Rule> rules = newServiceMap.get(rule.getServiceName());
            if (rules == null) {
                rules = new ArrayList<>();
            }
            rules.add(rule);
            newServiceMap.put(rule.getServiceName(), rules);
            newPathMap.put(rule.getServiceName() + "." + rule.getPath(), rule);
        }
        ruleMap = newRuleMap;
        pathRuleMap = newPathMap;
        serviceRuleMap = newServiceMap;
    }

    public Rule getRule(String ruleId) {
        return ruleMap.get(ruleId);
    }

    public void removeRule(String ruleId) {
        ruleMap.remove(ruleId);
    }

    public ConcurrentHashMap<String, Rule> getRuleMap() {
        return ruleMap;
    }

    public Rule getRuleByPath(String path) {
        return pathRuleMap.get(path);
    }

    public List<Rule> getRuleByServiceId(String serviceId) {
        return serviceRuleMap.get(serviceId);
    }
}
