package com.qyh.gateway.core.filter.dynamic.flowControl.fail_fast.single;

import com.qyh.gateway.core.context.GatewayContext;
import com.qyh.gateway.core.filter.dynamic.flowControl.ControllerAspect;
import com.qyh.gateway.core.filter.dynamic.flowControl.IController;
import com.qyh.gateway.core.filter.dynamic.flowControl.fail_fast.AbstractFailFastController;
import com.qyh.gateway.core.filter.dynamic.flowControl.fail_fast.FailFastParamInfoWrapper;
import com.qyh.gateway.core.filter.dynamic.flowControl.fail_fast.single.ArrayMetric;
import com.sun.jnlp.IntegrationServiceNSBImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static constants.FilterConst.FLOW_CTL_MODEL_SINGLETON;


/**
 * @author K0n9D1KuA
 * @version 1.0
 * @description: 快速失败限流器（采用滑动窗口算法 , 单机版)
 * @email 3161788646@qq.com
 * @date 2023/9/16 16:33
 */


@ControllerAspect(type = FLOW_CTL_MODEL_SINGLETON)
public class FailFastControllerSingle extends AbstractFailFastController {

    private final Map<String, ArrayMetric> keyToArrayMetric = new ConcurrentHashMap<>();

    //实现单例
    private FailFastControllerSingle() {
    }

    public static FailFastControllerSingle SINGLETON;

    public static FailFastControllerSingle getInstance() {
        if (SINGLETON == null) {
            synchronized (FailFastControllerSingle.class) {
                if (SINGLETON == null) {
                    SINGLETON = new FailFastControllerSingle();
                }
            }
        }
        return SINGLETON;
    }

    @Override
    protected boolean canPass(FailFastParamInfoWrapper failFastParamInfoWrapper) {
        String key = failFastParamInfoWrapper.getKey();
        keyToArrayMetric.putIfAbsent(key, new ArrayMetric());
        return false;
    }
}
