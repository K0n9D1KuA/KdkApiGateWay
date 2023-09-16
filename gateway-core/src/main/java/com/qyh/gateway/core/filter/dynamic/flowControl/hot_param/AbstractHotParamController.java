package com.qyh.gateway.core.filter.dynamic.flowControl.hot_param;

import com.qyh.gateway.core.context.GatewayContext;
import com.qyh.gateway.core.filter.dynamic.flowControl.IController;


import java.util.Map;

public class AbstractHotParamController implements IController {

    public static final String SEPARATOR = ":";

    public static final String CONFIG_KEY_OEN = "qps";

    public static final String CONFIG_KEY_TWO = "windowCount";

    public static final String CONFIG_KEY_THREE = "paramName";

    public static final String FILTER_ID = "hot_param_limit_filter";


    @Override
    public boolean canPass(GatewayContext ctx, Map<String, String> config) {
        HotParamInfoWrapper hotParamInfoWrapper = buildParamInfoWrapper(ctx, config);
        return canPass(hotParamInfoWrapper);
    }

    private HotParamInfoWrapper buildParamInfoWrapper(GatewayContext ctx, Map<String, String> config) {
        HotParamInfoWrapper hotParamInfoWrapper = new HotParamInfoWrapper();
        hotParamInfoWrapper.setQps(Integer.parseInt(config.get(CONFIG_KEY_OEN)));
        hotParamInfoWrapper.setParamName(config.get(CONFIG_KEY_THREE));
        hotParamInfoWrapper.setParamValue(ctx.getRequest().getQueryParametersMultiple(hotParamInfoWrapper.getParamName()).get(0));
        hotParamInfoWrapper.setWindowCount(Integer.parseInt(config.get(CONFIG_KEY_TWO)));
        hotParamInfoWrapper.setKey(buildKey(ctx));
        return hotParamInfoWrapper;
    }

    protected boolean canPass(HotParamInfoWrapper hotParamInfoWrapper) {
        throw new UnsupportedOperationException();
    }


    private String buildKey(GatewayContext context) {
        String paramName = context
                .getRule()
                .getDynamicFilterConfigByFilterId(FILTER_ID)
                .getConfig()
                .get(CONFIG_KEY_THREE);
        return context.getUniqueId() + SEPARATOR + context.getRequest().getPath() + SEPARATOR + paramName;
    }

}
