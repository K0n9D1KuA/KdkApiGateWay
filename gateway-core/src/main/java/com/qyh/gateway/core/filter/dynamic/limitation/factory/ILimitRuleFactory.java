package com.qyh.gateway.core.filter.dynamic.limitation.factory;

import com.qyh.gateway.core.filter.dynamic.limitation.limitRule.ILimitRule;

/**
 * @author K0n9D1KuA
 * @version 1.0
 * @description: 限制器产生工厂
 * @email 3161788646@qq.com
 * @date 2023/9/15 16:24
 */

public interface ILimitRuleFactory {
    ILimitRule getLimitRuleByType(String type);
}
