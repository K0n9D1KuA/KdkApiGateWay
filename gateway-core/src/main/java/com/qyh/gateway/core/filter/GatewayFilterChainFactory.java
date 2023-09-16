package com.qyh.gateway.core.filter;

import com.qyh.gateway.core.context.GatewayContext;


import com.qyh.gateway.core.filter.loadbalance.LoadBalanceFilter;
import com.qyh.gateway.core.filter.router.RouterFilter;

import config.Rule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @PROJECT_NAME: api-gateway
 * @DESCRIPTION: 过滤器工厂实现类
 * @USER: WuYang
 * @DATE: 2023/3/12 20:05
 */
@Slf4j
public class GatewayFilterChainFactory implements FilterFactory {

    private static class SingletonInstance {
        private static final GatewayFilterChainFactory INSTANCE = new GatewayFilterChainFactory();
    }

    public static GatewayFilterChainFactory getInstance() {
        return SingletonInstance.INSTANCE;
    }


    private Map<String, Filter> processorFilterIdMap = new ConcurrentHashMap<>();

    public GatewayFilterChainFactory() {
        //JAVA SPI
        ServiceLoader<Filter> serviceLoader = ServiceLoader.load(Filter.class);

        //迭代器
        Iterator<Filter> iterator = serviceLoader.iterator();
        while (iterator.hasNext()) {
            Filter filter = iterator.next();
            FilterAspect annotation = filter.getClass().getAnnotation(FilterAspect.class);
            log.info("过滤器加载成功 类型:{}, 过滤器标识:{},过滤器名称:{},过滤器顺序:{}", filter.getClass(),
                    annotation.id(), annotation.name(), annotation.order());
            if (annotation != null) {
                //添加到过滤集合
                String filterId = annotation.id();
                if (StringUtils.isEmpty(filterId)) {
                    filterId = filter.getClass().getName();
                }
                processorFilterIdMap.put(filterId, filter);
            }
        }
        log.info("所有过滤器已经成功加载 ! ");
    }





    /*
     * @author: K0n9D1KuA
     * @description: 根据核心上下文领域对象 构造起规则对应的过滤器链条
     * @param: ctx 核心上下文领域对象
     * @return: com.qyh.gateway.core.filter.GatewayFilterChain 过滤器链条
     * @date: 2023/9/4 22:18
     */

    @Override
    public GatewayFilterChain buildFilterChain(GatewayContext ctx) throws Exception {

        GatewayFilterChain gatewayFilterChain = new GatewayFilterChain();


        //todo 构建固定的前部分过滤器
        //暂时还没想到哪些是前部分过滤器
        buildBeforeFilters(gatewayFilterChain);

        //todo 组装动态过滤器 动态过滤器是每个接口生成的路由规则所指定的
        //mock模拟 ， 限流  ， 灰度发布 ， 黑名单限制这些都是
        //他们的执行顺序是 黑名单限制 -> mock模拟 -> 限流 -> 灰度发布
        buildDynamicFilters(gatewayFilterChain, ctx);


        //todo 构建固定的后部分处理器
        //负载均衡过滤器 路由过滤器 这些都是每个接口所必备的过滤器
        buildAfterFilters(gatewayFilterChain);


        return gatewayFilterChain;
    }

    private void buildDynamicFilters(GatewayFilterChain gatewayFilterChain, GatewayContext ctx) {
        Rule rule = ctx.getRule();
        Set<Rule.DynamicFilterConfig> dynamicConfig = rule.getDynamicConfig();

        //如果没有动态过滤器
        if (CollectionUtils.isEmpty(dynamicConfig)) {
            return;
        }

        List<Filter> filters = new ArrayList<>();

        //装配动态过滤器
        for (Rule.DynamicFilterConfig dynamicFilterConfig : dynamicConfig) {
            String id = dynamicFilterConfig.getId();
            Filter filterById = getFilterById(id);
            filters.add(filterById);
        }

        //将这些动态过滤器排序
        filters.sort(Comparator.comparingInt(Filter::getOrder));

        //添加到拦截器链
        gatewayFilterChain.addFilterList(filters);
    }


    private void buildAfterFilters(GatewayFilterChain gatewayFilterChain) {
        //todo 负载均衡过滤器
        gatewayFilterChain.addFilter(new LoadBalanceFilter());

        //todo 最后添加路由过滤器
        gatewayFilterChain.addFilter(new RouterFilter());

    }

    private void buildBeforeFilters(GatewayFilterChain gatewayFilterChain) {
    }


    @Override
    public Filter getFilterById(String filterId) {
        return processorFilterIdMap.get(filterId);
    }
}
