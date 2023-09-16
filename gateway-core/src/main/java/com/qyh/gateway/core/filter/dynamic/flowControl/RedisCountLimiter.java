package com.qyh.gateway.core.filter.dynamic.flowControl;

import com.qyh.gateway.core.redis.JedisUtil;
import enums.ResponseCode;
import exception.LimitException;
import lombok.extern.slf4j.Slf4j;


/**
 * @author K0n9D1KuA
 * @version 1.0
 * @description: 使用redis实现分布式限流
 * @email 3161788646@qq.com
 * @date 2023/9/4 20:15
 */

@Slf4j
public class RedisCountLimiter {

    protected static JedisUtil jedisUtil;

    static {
        jedisUtil = new JedisUtil();
    }


    private static final int SUCCESS_RESULT = 1;
    private static final int FAILED_RESULT = 0;

    /**
     * 执行限流
     *
     * @param key    限流key
     * @param qps  是 n秒 内允许 m个请求 中的 m
     * @param windowCount 是 n秒 内允许 m个请求 中的 n
     * @return
     */
    public static boolean canPass(String key, int qps, int windowCount) {
        try {
            Object object = jedisUtil.executeScript(key, qps, windowCount);
            if (object == null) {
                return true;
            }
            Long result = Long.valueOf(object.toString());
            if (FAILED_RESULT == result) {
                return false;
            }
        } catch (Exception e) {
            log.error("{} 进行分布式限流发生异常 ", key);
            throw new LimitException(ResponseCode.LIMIT_EXCEPTION);
        }
        return true;
    }


}
