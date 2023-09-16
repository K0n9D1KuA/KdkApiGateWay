package com.qyh.gateway.client.support.springmvc;


import java.lang.annotation.*;

import static constants.FilterConst.FLOW_CTL_MODEL_SINGLETON;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HotParamControl {

    //需要限制的热点参数名称
    String paramName();

    //统计窗口大小
    //例如1s内允许的qps为5
    int windowCount();

    //一个窗口内允许的qps
    //例如1s内允许的qps为5
    int qps();


    //热点参数限流的类型 单机 / 分布式
    //默认单机限流
    String controlType() default FLOW_CTL_MODEL_SINGLETON;
}
