package com.qyh.gateway.core.filter.dynamic.flowControl.fail_fast;

public class FailFastParamInfoWrapper {

    //一个窗口的总时间长度
    private long windowLengthInMs;
    //要把一个周期划分为几个窗口？
    private int sampleCount;
    //一个周期的总时间
    private double intervalInMs;
    //一个周期的总时间 转化为s
    private int intervalInSecond;
    //限流哪个接口 key
    //key的规则是 服务名+path
    private String key;


    public long getWindowLengthInMs() {
        return windowLengthInMs;
    }

    public void setWindowLengthInMs(long windowLengthInMs) {
        this.windowLengthInMs = windowLengthInMs;
    }

    public int getSampleCount() {
        return sampleCount;
    }

    public void setSampleCount(int sampleCount) {
        this.sampleCount = sampleCount;
    }

    public double getIntervalInMs() {
        return intervalInMs;
    }

    public void setIntervalInMs(double intervalInMs) {
        this.intervalInMs = intervalInMs;
    }

    public int getIntervalInSecond() {
        return intervalInSecond;
    }

    public void setIntervalInSecond(int intervalInSecond) {
        this.intervalInSecond = intervalInSecond;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
