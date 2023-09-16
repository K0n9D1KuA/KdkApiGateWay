package com.qyh.gateway.core.filter.dynamic.flowControl.fail_fast.single;

import java.util.concurrent.atomic.AtomicLong;

public class WindowWrap {
    //该窗口的起始时间
    private long windowStart;
    //窗口的总长
    private final long windowLengthInMs;
    //该窗口里面的值 qps
    private AtomicLong value;
    //构造方法

    public WindowWrap(long windowStart, long windowLengthInMs, AtomicLong value) {
        this.windowStart = windowStart;
        this.windowLengthInMs = windowLengthInMs;
        this.value = value;
    }


    //get set

    public Long getWindowStart() {
        return windowStart;
    }

    public void setWindowStart(Long windowStart) {
        this.windowStart = windowStart;
    }

    public Long getWindowLengthInMs() {
        return windowLengthInMs;
    }

    public void setWindowStart(long windowStart) {
        this.windowStart = windowStart;
    }

    public AtomicLong getValue() {
        return value;
    }

    public void setValue(AtomicLong value) {
        this.value = value;
    }
}
