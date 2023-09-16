package com.qyh.gateway.client.core;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "api")
public class ApiProperties {

    //配置中心及其注册中心地址
    private String registerAddress;

    //环境
    private String env = "dev";

    //是否是灰度服务
    private boolean gray;

    //写入配置中心的文件名
    private String dataId;

    //服务名
    private String serviceName;


    //是否重写网关路由规则
    private boolean rewrite;
}
