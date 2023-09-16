package com.qyh.gateway.client.support.springmvc;


import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Gray {

    /**
     * 多少的概率会访问到该灰度发布接口
     * 如果value为 1024 那么就是 1/1024的概率使用灰度发布接口
     */
    int value() default 1024;
}
