package com.qyh.gateway.core.filter;

import com.qyh.gateway.core.context.GatewayContext;
import com.qyh.gateway.core.helper.ResponseHelper;
import com.qyh.gateway.core.response.GatewayResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author K0n9D1KuA
 * @version 1.0
 * @description: 实现该接口的过滤器，具有可中断请求的功能
 * 也就是说，该过滤器可以直接中断请求，该请求不再经过后面的过滤器
 * 直接返回给前端
 * @email 3161788646@qq.com
 * @date 2023/9/16 13:02
 */


public interface Interruptible {
    /**
     * @author: K0n9D1KuA
     * @description: 将消息直接写回客户端
     * @param: ctx 核心上下文领域模型
     * @param: content 需要写回的内容
     * @return:
     * @date: 2023/9/5 13:58
     */
    default void writeToClient(GatewayContext ctx, String content) {
        ctx.setResponse(GatewayResponse.buildGatewayResponse(content));
        ctx.written();
        ResponseHelper.writeResponse(ctx);
        //切换为终止状态
        ctx.terminated();
    }
}
