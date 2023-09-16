package com.qyh.gateway.client.core;

/**
 * @author K0n9D1KuA
 * @version 1.0
 * @description: 下游服务的协议类型
 * @email 3161788646@qq.com
 * @date 2023/9/3 14:26
 */

public enum ApiProtocol {
    HTTP("http", "http协议"),
    DUBBO("dubbo", "dubbo协议");

    private String code;

    private String desc;

    ApiProtocol(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
