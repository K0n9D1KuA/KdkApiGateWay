package com.qyh.gateway.client.support.springmvc;


import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RetryTimes {
    //超时重试次数
    int value();
}
