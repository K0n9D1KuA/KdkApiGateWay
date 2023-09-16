package com.qyh.gateway.core.filter.dynamic.limitation.limitRule;

import com.qyh.gateway.core.filter.dynamic.limitation.wrapper.ReqCheckInfoWrapper;
import com.qyh.gateway.core.filter.dynamic.limitation.factory.LimitRuleFactory;

import java.util.Map;

/**
 * @author K0n9D1KuA
 * @version 1.0
 * @description: 限制器组合器 -- 组合模式的体现
 * 会找到所有该接口对应的限制器，并调用canPass方法,如果任意一个不通过，
 * 那么该请求就不会通过
 * @email 3161788646@qq.com
 * @date 2023/9/15 16:25
 */

public class LimitRuleComposite implements ILimitRule {


    public volatile static LimitRuleComposite SINGLETON;

    private LimitRuleComposite() {

    }

    //单例模式
    public static LimitRuleComposite getInstance() {
        if (SINGLETON == null) {
            synchronized (LimitRuleComposite.class) {
                if (SINGLETON == null) {
                    SINGLETON = new LimitRuleComposite();
                }
            }
        }
        return SINGLETON;
    }

    public boolean canPass(Map<String, String> configs, ReqCheckInfoWrapper reqCheckInfoWrapper) {
        for (String type : configs.keySet()) {
            if (!LimitRuleFactory.getInstance().getLimitRuleByType(type).canPass(configs, reqCheckInfoWrapper)) {
                //校验没有通过
                return false;
            }
        }
        //校验通过了
        return true;
    }


}
