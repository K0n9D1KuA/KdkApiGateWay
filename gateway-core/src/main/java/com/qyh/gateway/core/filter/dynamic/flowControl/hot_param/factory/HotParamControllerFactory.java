package com.qyh.gateway.core.filter.dynamic.flowControl.hot_param.factory;

import com.qyh.gateway.core.filter.dynamic.flowControl.IController;
import com.qyh.gateway.core.filter.dynamic.flowControl.hot_param.distributed.HotParamControllerDistributed;
import com.qyh.gateway.core.filter.dynamic.flowControl.hot_param.single.HotParamControllerSingle;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HotParamControllerFactory {

    private static final Map<String, IController> hotParamControllerMap = new ConcurrentHashMap<>();


    static {
        //这里偷个懒 不用spi  直接new了 😊
        IController single = HotParamControllerSingle.getInstance();
        IController distributed = HotParamControllerDistributed.getInstance();
        hotParamControllerMap.put(single.getType(), single);
        hotParamControllerMap.put(distributed.getType(), distributed);
    }


    public static IController getHotParamControllerByType(String type) {
        return hotParamControllerMap.get(type);
    }


}
