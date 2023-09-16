package com.qyh.gateway.core.filter.dynamic.flowControl.hot_param.distributed;

import com.qyh.gateway.core.filter.dynamic.flowControl.ControllerAspect;
import com.qyh.gateway.core.filter.dynamic.flowControl.RedisCountLimiter;
import com.qyh.gateway.core.filter.dynamic.flowControl.hot_param.AbstractHotParamController;
import com.qyh.gateway.core.filter.dynamic.flowControl.hot_param.HotParamInfoWrapper;

import static constants.FilterConst.FLOW_CTL_MODEL_DISTRIBUTED;

/**
 * @author K0n9D1KuA
 * @version 1.0
 * @description: 热点参数限流（分布式）
 * @email 3161788646@qq.com
 * @date 2023/9/16 13:39
 */
@ControllerAspect(type = FLOW_CTL_MODEL_DISTRIBUTED)
public class HotParamControllerDistributed extends AbstractHotParamController {


    private HotParamControllerDistributed() {

    }

    public static class SINGLETON_HOLDER {
        public static HotParamControllerDistributed SINGLETON = new HotParamControllerDistributed();
    }

    public static HotParamControllerDistributed getInstance() {
        return SINGLETON_HOLDER.SINGLETON;
    }


    @Override
    protected boolean canPass(HotParamInfoWrapper hotParamInfoWrapper) {
        String key = hotParamInfoWrapper.getKey();
        //这里的key还需要再拼装一个 paramValue
        //以此来做到不同的参数值 分别限流
        key = key + ":" + hotParamInfoWrapper.getParamValue();

        return RedisCountLimiter.canPass(key, hotParamInfoWrapper.getQps(), hotParamInfoWrapper.getWindowCount());
    }

}
