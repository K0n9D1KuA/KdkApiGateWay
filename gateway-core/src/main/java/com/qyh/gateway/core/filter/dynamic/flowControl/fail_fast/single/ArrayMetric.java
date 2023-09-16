package com.qyh.gateway.core.filter.dynamic.flowControl.fail_fast.single;


/**
 * @author K0n9D1KuA
 * @version 1.0
 * @description: 滑动窗口算法
 * @email 3161788646@qq.com
 * @date 2023/9/16 15:29
 */

public class ArrayMetric {





    //滑动窗口
    private LeapArray data;

    public void addPass(int count) {
        WindowWrap windowWrap = data.currentWindow();
        windowWrap.getValue().incrementAndGet();
    }


    boolean canPass(int acquireCount, int maxCount) {
        int curCount = data.values();
        if (acquireCount + curCount > maxCount) {
            return false;
        }
        return true;
    }
}
