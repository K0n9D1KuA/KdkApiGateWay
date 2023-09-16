package com.qyh.gateway.core.filter.dynamic.flowControl.hot_param.single;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


public class HotParamCounterHolder {

    //令牌桶
    private final Map<String, AtomicLong> tokenCounters;

    //记录上一次请求的时间戳
    private final Map<String, AtomicLong> timeCounters;

    //需要的令牌数 一般一次请求需要的令牌数为1
    private final int requireCount;
    //统计窗口大小
    private final int windowCount;
    //一个统计窗口允许的最大qps
    private final int qps;


    public HotParamCounterHolder(int requireCount, int windowCount, int qps) {
        this.requireCount = requireCount;
        this.qps = qps;
        this.windowCount = windowCount;
        tokenCounters = new ConcurrentHashMap<>();
        timeCounters = new ConcurrentHashMap<>();
    }

    public boolean canPass(String paramValue) {

        //如果需要的令牌数已经超过了最大令牌限制 那么一定不可以
        if (requireCount > qps) {
            return false;
        }

        while (true) {
            //获得当前时间戳
            long currentTimeMillis = System.currentTimeMillis();

            //最近一次请求时间
            //putIfAbsent可以保证只有一个线程返回null
            AtomicLong lastAddTokenTime = timeCounters.putIfAbsent(paramValue, new AtomicLong(currentTimeMillis));

            if (lastAddTokenTime == null) {
                //说明这个请求是第一次来 那么直接放行
                //同时需要更新剩余令牌
                tokenCounters.putIfAbsent(paramValue, new AtomicLong(qps - requireCount));
                return true;
            }

            //lastAddTokenTime != null
            //说明这个请求已经发起过
            //获得距离上次请求时间通过的时间
            long passTime = currentTimeMillis - lastAddTokenTime.get();

            if (passTime > windowCount * 1000L) {
                //说明已经很久没有人访问了
                //因为已经很久没有人访问了 那么我们可以直接生成5个令牌 然后扣减令牌数
                AtomicLong oldTokens = tokenCounters.putIfAbsent(paramValue, new AtomicLong(qps - requireCount));
                if (oldTokens == null) {
                    //表示是第一次来 健壮性判断
                    lastAddTokenTime.set(currentTimeMillis);
                    return true;
                } else {
                    long oldToken = oldTokens.get();
                    //这段时间应该生成多少个令牌？
                    //假设我们规定统计 统计窗口时长为1s
                    //此时已经过去了 3s
                    //那么应该生成15个（3000 * 5） / (1000 * 1)
                    long toAddTokenCount = (passTime * qps) / (windowCount * 1000L);

                    //我们规定 新生成的count不能超过 2 * qps
                    long newTokenCount = toAddTokenCount + oldToken > qps * 2L ? (qps - requireCount)
                            : (oldToken + toAddTokenCount - requireCount);

                    if (newTokenCount < 0) {
                        return false;
                    }

                    if (oldTokens.compareAndSet(oldToken, newTokenCount)) {
                        //表示修改成功
                        lastAddTokenTime.set(currentTimeMillis);
                        return true;
                    }
                    //让出cpu使用权
                    Thread.yield();
                }
            } else {
                //说明访问较为频繁
                AtomicLong oldTokens = tokenCounters.get(paramValue);
                if (oldTokens != null) {
                    long oldToken = oldTokens.get();
                    if (oldToken - requireCount >= 0) {
                        //cas修改 保证线程安全
                        if (oldTokens.compareAndSet(oldToken, oldToken - requireCount)) {
                            return true;
                        }
                    } else {
                        //已经不能访问了
                        return false;
                    }
                }
            }
        }
    }
}


