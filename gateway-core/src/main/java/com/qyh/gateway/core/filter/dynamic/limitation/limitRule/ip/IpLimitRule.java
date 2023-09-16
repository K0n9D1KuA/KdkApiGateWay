package com.qyh.gateway.core.filter.dynamic.limitation.limitRule.ip;


import com.qyh.gateway.core.filter.dynamic.limitation.limitRule.ILimitRule;
import com.qyh.gateway.core.filter.dynamic.limitation.limitRule.LimitRuleAspect;
import com.qyh.gateway.core.filter.dynamic.limitation.wrapper.ReqCheckInfoWrapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Map;


/**
 * @author K0n9D1KuA
 * @version 1.0
 * @description: IP黑名单限制器
 * @email 3161788646@qq.com
 * @date 2023/9/15 16:27
 */

@LimitRuleAspect(
        type = "IP"
)
@Slf4j
public class IpLimitRule implements ILimitRule {

    public static final String IP_LIMIT_KEY = "IP";
    public static final String PADDING = ";";


    //127.0.0.1.1$192.168.1.1.2

    @Override
    public boolean canPass(Map<String, String> configs, ReqCheckInfoWrapper reqCheckInfoWrapper) {
        //127.0.0.1.1$192.168.1.1.2
        //根据 $ 切割 获得限制的ip
        String[] ips = configs.get(getType()).split(PADDING);

        //请求ip
        String clientIp = reqCheckInfoWrapper.getIp();
        boolean contains = Arrays.asList(ips).contains(clientIp);
        //说明收到了限制
        if (contains) {
            log.info("请求 ip 为 {} , 的用户是ip黑名单里面的用户，受到限制", clientIp);
            return false;
        }

        return true;
    }
}
