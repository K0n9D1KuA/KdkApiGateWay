package com.qyh.gateway.client.core;


import java.lang.annotation.*;


/**
 * @author K0n9D1KuA
 * @version 1.0
 * @description: 服务定义
 * @email 3161788646@qq.com
 * @date 2023/9/3 14:25
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiService {
    String serviceId();

    String version() default "1.0.0";

    ApiProtocol protocol();

    String patternPath();
}
