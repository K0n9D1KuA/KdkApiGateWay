package com.qyh.gateway.core.filter.dynamic.flowControl;

import com.qyh.gateway.core.context.GatewayContext;
import com.qyh.gateway.core.filter.dynamic.flowControl.ControllerAspect;

import java.util.Map;


/**
 * @author K0n9D1KuA
 * @version 1.0
 * @description: 各种限流器的顶层抽象接口
 * @email 3161788646@qq.com
 * @date 2023/9/16 16:32
 */

public interface IController {


    boolean canPass(GatewayContext ctx, Map<String, String> config);


    default String getType() {
        ControllerAspect controllerAspect = this.getClass().getAnnotation(ControllerAspect.class);
        if (controllerAspect != null) {
            return controllerAspect.type();
        }

        return null;
    }
}
