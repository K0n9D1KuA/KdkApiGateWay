package com.qyh.gateway.client.support.springmvc;


import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Limit {

    /**
     * 限制类型 例如IP
     */

    LimitType[] value();





    /**
     * 限制哪些省份的请求
     */

    String[] provinceConfig() default "";



    /**
     * 限制哪些IP
     */


    String[] ipConfig() default "";
}
