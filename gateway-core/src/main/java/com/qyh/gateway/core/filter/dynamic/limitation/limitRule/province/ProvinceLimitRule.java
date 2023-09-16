package com.qyh.gateway.core.filter.dynamic.limitation.limitRule.province;


import com.qyh.gateway.core.filter.dynamic.limitation.limitRule.ILimitRule;
import com.qyh.gateway.core.filter.dynamic.limitation.limitRule.LimitRuleAspect;
import com.qyh.gateway.core.filter.dynamic.limitation.wrapper.ReqCheckInfoWrapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Map;

@LimitRuleAspect(
        type = "PROVINCE"
)
@Slf4j
public class ProvinceLimitRule implements ILimitRule {

    public static final String PADDING = ";";

    @Override
    public boolean canPass(Map<String, String> configs, ReqCheckInfoWrapper reqCheckInfoWrapper) {
        //黑名单限制省份
        String[] forbiddenProvinces = configs.get(getType()).split(PADDING);
        //请求所属省份
        String province = reqCheckInfoWrapper.getProvince();
        //检查是否包含
        boolean contains = Arrays.asList(forbiddenProvinces).contains(province);
        //说明收到了限制
        if (contains) {
            log.info("请求 省份 为 {} , 的用户是请求省份黑名单里面的用户，受到限制", province);
            return false;
        }
        return true;
    }
}
