package com.qyh.gateway.core.filter.dynamic.limitation.factory;

import com.qyh.gateway.core.filter.dynamic.limitation.limitRule.ILimitRule;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author K0n9D1KuA
 * @version 1.0
 * @description: 限制器产生工厂
 * @email 3161788646@qq.com
 * @date 2023/9/15 16:24
 */

public class LimitRuleFactory implements ILimitRuleFactory {

    private final ConcurrentHashMap<String, ILimitRule> ruleMap = new ConcurrentHashMap<>();

    public static volatile LimitRuleFactory SINGLE_INSTANCE;

    private LimitRuleFactory() {
        //SPI初始化所有黑名单限制器
        ServiceLoader<ILimitRule> serviceLoader = ServiceLoader.load(ILimitRule.class);
        Iterator<ILimitRule> iterator = serviceLoader.iterator();
        while (iterator.hasNext()) {
            ILimitRule limitRule = iterator.next();
            ruleMap.put(limitRule.getType(), limitRule);
        }
    }

    //实现单例
    public static ILimitRuleFactory getInstance() {
        if (SINGLE_INSTANCE == null) {
            synchronized (LimitRuleFactory.class) {
                if (SINGLE_INSTANCE == null) {
                    SINGLE_INSTANCE = new LimitRuleFactory();
                }
            }
        }
        return SINGLE_INSTANCE;
    }

    @Override
    public ILimitRule getLimitRuleByType(String type) {
        return ruleMap.get(type);
    }
}
