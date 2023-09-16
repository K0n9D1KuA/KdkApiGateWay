package com.qyh.gateway.core.filter.dynamic.limitation;


import com.qyh.gateway.core.context.GatewayContext;
import com.qyh.gateway.core.filter.dynamic.AbstractDynamicFilter;
import com.qyh.gateway.core.filter.FilterAspect;
import com.qyh.gateway.core.filter.Interruptible;
import com.qyh.gateway.core.filter.dynamic.limitation.limitRule.LimitRuleComposite;
import com.qyh.gateway.core.filter.dynamic.limitation.wrapper.ReqCheckInfoWrapper;
import lombok.extern.slf4j.Slf4j;

import static constants.FilterConst.*;


import java.util.Map;

/**
 * @author K0n9D1KuA
 * @version 1.0
 * @description: 黑名单限制器
 * @email 3161788646@qq.com
 * @date 2023/9/15 16:50
 */

@Slf4j
@FilterAspect(id = LIMIT_FILTER_ID,
        name = LIMIT_FILTER_NAME,
        order = LIMIT_FILTER_ORDER)
public class LimitationFilter extends AbstractDynamicFilter implements Interruptible {

    public static final String MSG = "你的请求已遭到限制，请勿恶意请求!";

    public static final String HEADER_KEY = "province";

    @Override
    protected void doFilter(Map<String, String> config, GatewayContext context) {
        //构建参数校验包装对象
        ReqCheckInfoWrapper reqCheckInfoWrapper = buildReqToCheck(context);
        //校验该请求是否能通过
        boolean canPass = LimitRuleComposite.getInstance().canPass(config, reqCheckInfoWrapper);
        //该请求属于黑名单限制
        if (!canPass) {
            //该请求遭到限制
            writeToClient(context, MSG);
        }
    }

    /**
     * @author: K0n9D1KuA
     * @description: 构建请求参数校验包装对象，方便黑名单限制器进行校验
     * @param: ctx 核心领域上下文
     * @return: com.qyh.gateway.core.filter.dynamic.wrapper.limitation.ReqCheckInfoWrapper
     * @date: 2023/9/15 16:44
     */
    private ReqCheckInfoWrapper buildReqToCheck(GatewayContext ctx) {
        ReqCheckInfoWrapper reqCheckInfoWrapper = new ReqCheckInfoWrapper();

        //设置请求ip
        reqCheckInfoWrapper.setIp(ctx.getRequest().getClientIp());

        //设置请求ip所属地
        String clientProvince = ctx.getRequest().getHeaders().get(HEADER_KEY);
        reqCheckInfoWrapper.setProvince(clientProvince);

        return reqCheckInfoWrapper;
    }


}
