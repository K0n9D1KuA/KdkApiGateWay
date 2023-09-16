package com.qyh.gateway.core.filter.dynamic.flowControl.fail_fast;

import com.qyh.gateway.core.context.GatewayContext;
import com.qyh.gateway.core.filter.dynamic.flowControl.IController;

import java.util.Map;

public class AbstractFailFastController implements IController {

    public static final String CONFIG_KEY_OEN = "intervalInSecond";

    public static final String CONFIG_KEY_TWO = "sampleCount";


    @Override
    public boolean canPass(GatewayContext ctx, Map<String, String> config) {

        FailFastParamInfoWrapper failFastParamInfoWrapper = builtFailFastParamInfoWrapper(ctx, config);

        return canPass(failFastParamInfoWrapper);

    }

    protected boolean canPass(FailFastParamInfoWrapper failFastParamInfoWrapper) {
        throw new UnsupportedOperationException();
    }

    private FailFastParamInfoWrapper builtFailFastParamInfoWrapper(GatewayContext ctx, Map<String, String> config) {

        //要把一个周期划分为几个窗口？
        int sampleCount = Integer.parseInt(config.get(CONFIG_KEY_TWO));

        //一个周期的总时间 转化为s
        int intervalInSecond = Integer.parseInt(config.get(CONFIG_KEY_OEN));

        //时间转化成ms
        int intervalInMs = intervalInSecond * 1000;

        //一个窗口的长度 = 一个周期的长度 / 窗口个数
        int windowLengthInMs = (intervalInMs / sampleCount);

        FailFastParamInfoWrapper failFastParamInfoWrapper = new FailFastParamInfoWrapper();
        failFastParamInfoWrapper.setSampleCount(sampleCount);
        failFastParamInfoWrapper.setIntervalInMs(intervalInMs);
        failFastParamInfoWrapper.setIntervalInSecond(intervalInSecond);
        failFastParamInfoWrapper.setWindowLengthInMs(windowLengthInMs);
        String path = ctx.getRequest().getPath();
        String uniqueId = ctx.getUniqueId();
        failFastParamInfoWrapper.setKey(uniqueId + ";" + path);
        return failFastParamInfoWrapper;
    }


}
