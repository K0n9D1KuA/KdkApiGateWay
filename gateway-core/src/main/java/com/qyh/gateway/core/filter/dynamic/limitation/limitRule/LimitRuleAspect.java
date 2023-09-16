package com.qyh.gateway.core.filter.dynamic.limitation.limitRule;


import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface LimitRuleAspect {

    /**
     * 校验器类型
     *
     * @return
     */
    String type();


}
