package com.qyh.gateway.core.filter.dynamic.flowControl.hot_param.single;

import com.qyh.gateway.core.filter.dynamic.flowControl.ControllerAspect;
import com.qyh.gateway.core.filter.dynamic.flowControl.hot_param.AbstractHotParamController;
import com.qyh.gateway.core.filter.dynamic.flowControl.hot_param.HotParamInfoWrapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static constants.FilterConst.FLOW_CTL_MODEL_SINGLETON;

/**
 * @author K0n9D1KuA
 * @version 1.0
 * @description: 热点参数限流器(单机限流)
 * @email 3161788646@qq.com
 * @date 2023/9/16 1:19
 */
@Slf4j
@ControllerAspect(type = FLOW_CTL_MODEL_SINGLETON)
public class HotParamControllerSingle extends AbstractHotParamController {


    //key -> 服务名 + url + 热点参数名( 目的就是为了防止重复
    //value -> 该热点参数对应的令牌桶
    private final Map<String, HotParamCounterHolder> PARAM_COUNTER_HOLDER_MAP = new ConcurrentHashMap<>();

    //懒汉单例
    private static class SINGLETON_HOLDER {
        private static final HotParamControllerSingle SINGLETON = new HotParamControllerSingle();
    }

    public static HotParamControllerSingle getInstance() {
        return SINGLETON_HOLDER.SINGLETON;
    }


    @Override
    protected boolean canPass(HotParamInfoWrapper hotParamInfoWrapper) {
        //如果令牌桶不存在 那么创建 这里是线程安全的
        PARAM_COUNTER_HOLDER_MAP.putIfAbsent(hotParamInfoWrapper.getKey(), new HotParamCounterHolder(1, hotParamInfoWrapper.getWindowCount(), hotParamInfoWrapper.getQps()));

        //调用令牌桶里面的canPass 检查是否限流通过
        return PARAM_COUNTER_HOLDER_MAP
                .get(hotParamInfoWrapper.getKey())
                .canPass(hotParamInfoWrapper.getParamValue());
    }
}
