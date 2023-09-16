package com.qyh.gateway.core.filter.dynamic.limitation.limitRule;

import com.qyh.gateway.core.filter.dynamic.limitation.wrapper.ReqCheckInfoWrapper;

import java.util.Map;

public interface ILimitRule {
    boolean canPass(Map<String, String> configs, ReqCheckInfoWrapper reqCheckInfoWrapper);

    default String getType() {
        LimitRuleAspect limitRuleAspect = this.getClass().getAnnotation(LimitRuleAspect.class);
        if (limitRuleAspect != null) {
            return limitRuleAspect.type();
        }
        return this.getClass().getName();
    }

}
