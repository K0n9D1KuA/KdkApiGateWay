package com.qyh.gateway.config.center.api;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@FunctionalInterface
public interface ShortUrlsChangeListener {
    void onChangeEvent(ConcurrentHashMap<String, String> shortUrlToLongUrl);
}
