package com.qyh.gateway.config.center.api;

/**
 * @author K0n9D1KuA
 * @version 1.0
 * @description: 配置中心顶层接口
 * @email 3161788646@qq.com
 * @date 2023/9/14 11:16
 */

public interface ConfigCenter {

    //初始化方法
    void init(String serverAddr, String env, String configNames);

    void init(String serverAddr, String env);

    //监听规则变化
    //RulesChangeListener listener 监听器

    void subscribeRulesChange(RulesChangeListener listener);


    void subscribeShortUrlsChangeL(ShortUrlsChangeListener listener);


    //将content写到nacos配置中心 dataId文件名 env环境
    void writeToConfigCenter(String dataId, String env, String content);


    void subscribeNewRulesChange(NewRulesChangeListener newRulesChangeListener);

}

