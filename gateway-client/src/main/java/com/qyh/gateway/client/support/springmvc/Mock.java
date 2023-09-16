package com.qyh.gateway.client.support.springmvc;


import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mock {

    /**
     * 接口mock的返回值
     */

    String value();
}
