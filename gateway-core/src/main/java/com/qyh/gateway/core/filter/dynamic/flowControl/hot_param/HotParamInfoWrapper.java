package com.qyh.gateway.core.filter.dynamic.flowControl.hot_param;

public class HotParamInfoWrapper {

    private int qps;
    private int windowCount;
    private String paramName;
    private String paramValue;
    private String key;


    public int getQps() {
        return qps;
    }

    public void setQps(int qps) {
        this.qps = qps;
    }

    public int getWindowCount() {
        return windowCount;
    }

    public void setWindowCount(int windowCount) {
        this.windowCount = windowCount;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
