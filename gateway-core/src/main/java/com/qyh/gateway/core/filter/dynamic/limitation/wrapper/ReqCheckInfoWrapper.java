package com.qyh.gateway.core.filter.dynamic.limitation.wrapper;

/**
 * @author K0n9D1KuA
 * @version 1.0
 * @description: 请求信息包装对象，
 * 里面封装例如请求ip / 请求所属省份
 * 黑名单限制其将会通过该对象拿去相关信息做出校验
 * @email 3161788646@qq.com
 * @date 2023/9/15 16:44
 */

public class ReqCheckInfoWrapper {


    //请求所属地
    private String province;
    private String ip;

    //请求ip
    public String getIp() {
        return ip;
    }


    //等待扩展

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}
