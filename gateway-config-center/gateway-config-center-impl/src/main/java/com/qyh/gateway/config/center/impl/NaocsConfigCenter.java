package com.qyh.gateway.config.center.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.qyh.gateway.config.center.api.ConfigCenter;
import com.qyh.gateway.config.center.api.NewRulesChangeListener;
import com.qyh.gateway.config.center.api.RulesChangeListener;
import com.qyh.gateway.config.center.api.ShortUrlsChangeListener;
import config.Rule;
import config.RuleNew;
import exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import utils.JSONUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

@Slf4j
public class NaocsConfigCenter implements ConfigCenter {

    //配置名
    private static final String DATA_ID_GATEWAY = "api-gateway";

    private static final String DATA_ID_SHORT_URL = "short_urls";

    private static final String DATA_ID_RULE = "URL-RULES-HDU-SERVER";


    //服务地址
    private String serverAddr;

    //环境
    private String env;

    //nacos相关api
    private ConfigService configService;

    //需要读取的配置文件列表 以 , 分割
    private String configName;


    @Override
    public void init(String serverAddr, String env, String configName) {
        this.serverAddr = serverAddr;
        this.env = env;
        this.configName = configName;

        try {
            configService = NacosFactory.createConfigService(serverAddr);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init(String serverAddr, String env) {
        this.serverAddr = serverAddr;
        this.env = env;
        try {
            configService = NacosFactory.createConfigService(serverAddr);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @author: K0n9D1KuA
     * @description: 加载各个下游服务上传的网关路由规则 并且订阅其更新
     * @param: listener
     * @return:
     * @date: 2023/9/15 22:49
     */
    @Override
    public void subscribeRulesChange(RulesChangeListener listener) {
        try {
            String[] configNames = this.configName.split(",");
            if (configNames.length == 0) {
                throw new RuntimeException("config cant be empty");
            } else {
                for (String configName : configNames) {

                    log.info("开始加载文件名为 : {} 的网关路由规则", configName);

                    //获得网关路由规则信息
                    String config = configService.getConfig(configName, env, 5000);

                    log.info("文件名为: {} 的网关路由规则加载完毕 , {}", configName, config);

                    //反序列化
                    List<Rule> rules = JSON.parseArray(config, Rule.class);

                    //触发监听
                    listener.onRulesChange(rules);

                    //添加网关路由规则信息变更监听
                    configService.addListener(DATA_ID_GATEWAY, env, new Listener() {
                        @Override
                        public Executor getExecutor() {
                            return null;
                        }

                        @Override
                        public void receiveConfigInfo(String configInfo) {
                            log.info("网关路由规则发生改变: {}", configInfo);
                            List<Rule> rules = JSON.parseObject(configInfo).getJSONArray("rules").toJavaList(Rule.class);
                            listener.onRulesChange(rules);
                        }
                    });
                }
            }
        } catch (NacosException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void subscribeShortUrlsChangeL(ShortUrlsChangeListener listener) {

        try {
            //初始化通知
            String config = configService.getConfig(DATA_ID_SHORT_URL, env, 5000);

            log.info("short url config from nacos: {}", config);
            //反序列化为map
            ConcurrentHashMap<String, String> map = JSON.parseObject(config, ConcurrentHashMap.class);
            //触发监听
            listener.onChangeEvent(map);

            //监听 rules的动态变化
            configService.addListener(DATA_ID_GATEWAY, env, new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String configInfo) {
                    log.info("short url config from nacos: {}", configInfo);
                    //反序列化为map
                    ConcurrentHashMap<String, String> map = JSON.parseObject(config, ConcurrentHashMap.class);
                    //触发监听
                    listener.onChangeEvent(map);
                }
            });

        } catch (NacosException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * @author: K0n9D1KuA
     * @description: 将内容写到nacos配置中心
     * @param: dataId
     * @param: env
     * @param: content
     * @return:
     * @date: 2023/9/6 12:51
     */
    @Override
    public void writeToConfigCenter(String dataId, String env, String content) {
        try {
            configService.publishConfig(dataId, env, content);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void subscribeNewRulesChange(NewRulesChangeListener newRulesChangeListener) {
        try {
            //初始化通知
            String config = configService.getConfig(DATA_ID_RULE, env, 5000);

            log.info("short url config from nacos: {}", config);

            //反序列化为规则列表
            List<RuleNew> ruleNews = JSONUtil.parseToList(config, RuleNew.class);


            //钩子函数
            newRulesChangeListener.onRulesChange(ruleNews);


            //监听 rules的动态变化
            configService.addListener(DATA_ID_RULE, env, new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String configInfo) {
                    log.info("short url config from nacos: {}", configInfo);
                    //反序列化为规则列表
                    List<RuleNew> ruleNews = JSON.parseObject(config, ArrayList.class);
                    //触发监听
                    newRulesChangeListener.onRulesChange(ruleNews);
                }
            });

        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }
}
