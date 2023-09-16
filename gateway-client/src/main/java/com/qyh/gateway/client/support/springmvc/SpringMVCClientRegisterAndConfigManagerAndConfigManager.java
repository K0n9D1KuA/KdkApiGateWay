package com.qyh.gateway.client.support.springmvc;

import com.qyh.gateway.client.core.*;
import com.qyh.gateway.client.support.AbstractClientRegisterAndConfigManager;
import config.*;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.RegExUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import utils.HashUtils;
import utils.JSONUtil;
import utils.NetUtils;
import utils.TimeUtil;

import java.util.*;
import java.util.regex.Pattern;

import static constants.BasicConst.COLON_SEPARATOR;
import static constants.GatewayConst.DEFAULT_WEIGHT;


@Slf4j
public class SpringMVCClientRegisterAndConfigManagerAndConfigManager extends AbstractClientRegisterAndConfigManager implements ApplicationListener<ApplicationEvent>, ApplicationContextAware {


    //配置文件相关
    @Autowired
    private ServerProperties serverProperties;

    private ApplicationContext applicationContext;


    //用来获取所有的接口方法的springmvc组件
    RequestMappingHandlerMapping mapping;


    //正则表达式 目的在于 将 abc/{variable1}/{variable2} 变为 abc/*/*
    private static final Pattern PATTERN = Pattern.compile("\\{(.*?)\\}");

    public String ASTERISK = "*";


    public SpringMVCClientRegisterAndConfigManagerAndConfigManager(ApiProperties apiProperties) {
        super(apiProperties);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        //监听 容器启动事件
        if (applicationEvent instanceof ApplicationStartedEvent) {
            try {
                if (getApiProperties().isRewrite()) {
                    //生成所有接口对应网关路由规则
                    generateAllUrlRules();
                }
                //注册服务
                registerService();
            } catch (Exception e) {
                log.error("doRegisterSpringMvc error", e);
                throw new RuntimeException(e);
            }
            log.info("服务已注册到注册中心 ，及该服务网关路由规则已成功写入配置中心");
        }
    }

    private void registerService() {

        String serviceName = getApiProperties().getServiceName();

        ServiceDefinition serviceDefinition = new ServiceDefinition();

        //构建serviceDefinition
        serviceDefinition.setUniqueId(serviceName);
        serviceDefinition.setServiceId(serviceName);
        serviceDefinition.setVersion("1.0.0");
        serviceDefinition.setProtocol("http");

        serviceDefinition.setEnable(true);
        //设置环境
        serviceDefinition.setEnvType(getApiProperties().getEnv());

        //服务实例
        ServiceInstance serviceInstance = new ServiceInstance();
        String localIp = NetUtils.getLocalIp();
        int port = serverProperties.getPort();
        String serviceInstanceId = localIp + COLON_SEPARATOR + port;
        String uniqueId = serviceDefinition.getUniqueId();
        String version = serviceDefinition.getVersion();

        serviceInstance.setServiceInstanceId(serviceInstanceId);
        serviceInstance.setUniqueId(uniqueId);
        serviceInstance.setIp(localIp);
        serviceInstance.setPort(port);
        serviceInstance.setRegisterTime(TimeUtil.currentTimeMillis());
        serviceInstance.setVersion(version);

        //默认权重
        serviceInstance.setWeight(DEFAULT_WEIGHT);
        //如果该服务是一个灰度服务
        if (getApiProperties().isGray()) {
            serviceInstance.setGray(true);
        }

        //注册
        register(serviceDefinition, serviceInstance);


        log.info("该服务的服务定义，服务实例已经注册到注册中心。");
    }

    private void generateAllUrlRules() {
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();

        Set<RuleNew> ruleNews = new HashSet<>();

        map.keySet().forEach(info -> {
            //去生成规则对象
            RuleNew ruleNew = doGenerateRule(info);
            ruleNews.add(ruleNew);
        });

        String jsonString = JSONUtil.toJSONString(ruleNews);

        //写入配置中心
        //文件名是 URL-RULES- + 服务名
        writeToConfigCenter("URL-RULES-" + getApiProperties().getServiceName(), getApiProperties().getEnv(), jsonString);

        log.info("已经将所有 url 对应的网关路由规则写入配置中心 {}", jsonString);

    }

    //为接口生成网关路由规则
    private RuleNew doGenerateRule(RequestMappingInfo info) {
        //获得接口
        String url = new ArrayList<>(info
                .getPatternsCondition()
                .getPatterns())
                .get(0);
        RuleNew ruleNew = new RuleNew();
        //获得当前服务名
        String serviceName = getApiProperties().getServiceName();
        ruleNew.setServiceName(serviceName);
        //UUID生成随机规则id
        ruleNew.setId(UUID.randomUUID().toString().replace("-", ""));
        //协议名称
        ruleNew.setProtocol("http");
        //生成规则名称 路径 + --ruleNew
        ruleNew.setName(url + "--ruleNew");
        //默认重试次数是5
        ruleNew.setRetryTimes(5);
        //为该接口生成动态的过滤器
        generateDynamicFilter(info, ruleNew);
        //设置路劲
        ruleNew.setPath(url);
        return ruleNew;
    }

    private void generateDynamicFilter(RequestMappingInfo info, RuleNew ruleNew) {

        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();

        //HandlerMethod里面包含了 接口所在类 + 接口所在方法
        HandlerMethod handlerMethod = map.get(info);

        // 检查是否有 @Limit 注解 据此生成 黑名单限制过滤器
        Limit limit = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), Limit.class);
        if (limit != null) {
            handleLimitFilter(limit, ruleNew);
        }

        // 检查是否有 @MOCK 注解 据此生成 MOCK模拟过滤器
        Mock mock = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), Mock.class);
        if (mock != null) {
            handleMockFilter(mock, ruleNew);
        }

        //检查是否有 @Gray 注解 据此生成 灰度发布过滤器
        Gray gray = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), Gray.class);
        if (gray != null) {
            handleGrayFilter(gray, ruleNew);
        }


        //检查是否制定了 @loadBalance注解 如果没有只当 默认负载均衡方式是 随机
        LoadBalance loadBalance = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), LoadBalance.class);
        if (loadBalance != null) {
            ruleNew.setLoadBalanceConfig(loadBalance.value());
        } else {
            ruleNew.setLoadBalanceConfig("Random");
        }

        //检查是否添加了 @RetryTimes注解
        RetryTimes retryTimes = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), RetryTimes.class);
        if (retryTimes != null) {
            ruleNew.setRetryTimes(retryTimes.value());
        }

        //检查是否添加了 热点参数限流注解
        HotParamControl hotParamControl = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), HotParamControl.class);
        if (hotParamControl != null) {
            handleHotParamControlFilter(hotParamControl, ruleNew);
        }
    }

    private void handleHotParamControlFilter(HotParamControl hotParamControl, RuleNew ruleNew) {
        String paramName = hotParamControl.paramName();
        int qps = hotParamControl.qps();
        int windowCount = hotParamControl.windowCount();
        RuleNew.DynamicFilterConfig hotParamLimitFilter = new RuleNew.DynamicFilterConfig();
        hotParamLimitFilter.setId("hot_param_control_filter");

        Map<String, String> configMap = new HashMap<>();
        configMap.put("paramName", paramName);
        configMap.put("qps", String.valueOf(qps));
        configMap.put("windowCount", String.valueOf(windowCount));
        configMap.put("control_type", hotParamControl.controlType());

        hotParamLimitFilter.setConfig(configMap);

        ruleNew.addDynamicFilterConfig(hotParamLimitFilter);
    }

    private void handleGrayFilter(Gray gray, RuleNew ruleNew) {
        int probability = gray.value();


        RuleNew.DynamicFilterConfig grayFilter = new RuleNew.DynamicFilterConfig();
        grayFilter.setId("gray_filter");

        Map<String, String> configMap = new HashMap<>();
        configMap.put("probability", String.valueOf(probability));
        grayFilter.setConfig(configMap);

        ruleNew.addDynamicFilterConfig(grayFilter);
    }


    private void handleMockFilter(Mock mock, RuleNew ruleNew) {
        String mockValue = mock.value();

        RuleNew.DynamicFilterConfig mockFilter = new RuleNew.DynamicFilterConfig();
        mockFilter.setId("mock_filter");

        Map<String, String> configMap = new HashMap<>();
        configMap.put("mock_value", mockValue);
        mockFilter.setConfig(configMap);

        ruleNew.addDynamicFilterConfig(mockFilter);
    }

    private void handleLimitFilter(Limit limit, RuleNew ruleNew) {
        LimitType[] value = limit.value();
        if (value != null) {
            for (LimitType limitType : value) {
                //如果需要做的是IP封禁
                if (limitType.equals(LimitType.IP)) {
                    //获得该url需要封禁的ip
                    String[] ips = limit.ipConfig();
                    handle(ips, ruleNew, "IP", "limit_filter");
                } else if (limitType.equals(LimitType.PROVINCE)) {
                    //如果需要做的是请求所在省份封禁
                    String[] provinces = limit.provinceConfig();
                    handle(provinces, ruleNew, "PROVINCE", "limit_filter");
                } else {
                    //TODO 等待扩展
                }
            }
        }
    }

    private void handle(String[] config, RuleNew ruleNew, String key, String filterId) {
        //尝试获得动态过滤器规则
        RuleNew.DynamicFilterConfig limitFilter = ruleNew.getDynamicFilterConfigById(filterId);

        //配置规则
        //我们value 使用 $来分割
        StringBuilder values = new StringBuilder();
        int len = config.length;
        for (int i = 0; i < len; i++) {
            String curValue = config[i];
            if (i == len - 1) {
                values.append(curValue);
            } else {
                values.append(curValue + ";");
            }
        }

        if (limitFilter == null) {
            limitFilter = new RuleNew.DynamicFilterConfig();
            //设置过滤器id
            limitFilter.setId(filterId);
            ruleNew.addDynamicFilterConfig(limitFilter);
        }

        Map<String, String> configMap = limitFilter.getConfig();
        if (configMap == null) {
            configMap = new HashMap<>();
            limitFilter.setConfig(configMap);
        }
        configMap.put(key, values.toString());
    }


    /**
     * @author: K0n9D1KuA
     * @description: 扫描该服务所有接口，将生成的短链写入配置中心
     * @date: 2023/9/6 12:59
     */
    private void generateShortLink() {


        Set<String> urlsToBeShortUrl = new HashSet<>();


        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();

        map.keySet().forEach(info -> {
            //HandlerMethod里面包含了 接口所在类 + 接口所在方法
            HandlerMethod handlerMethod = map.get(info);

            // 获取方法上边的注解 将 abc/{variable1}/{variable2} 变为 abc/*/*
            GenerateShortLink method = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), GenerateShortLink.class);

            if (method != null) {
                info
                        .getPatternsCondition()
                        .getPatterns().forEach(url ->
                                //将 abc/{variable1}/{variable2} 变为 abc/*/*
                                urlsToBeShortUrl.add(RegExUtils.replaceAll(url, PATTERN, ASTERISK)));
            }


            // 获取类上边的注解 将 abc/{variable1}/{variable2} 变为 abc/*/*
            GenerateShortLink controller = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), GenerateShortLink.class);

            if (controller != null) {
                info
                        .getPatternsCondition()
                        .getPatterns().forEach(url ->
                                urlsToBeShortUrl.add(RegExUtils.replaceAll(url, PATTERN, ASTERISK)));
            }


        });

        Set<String> shortUrls = new HashSet<>();

        Map<String, String> shortUrlToLongUrl = new HashMap<>();

        //模拟hash冲突
//        List<String> newUrlsToBeShortUrls = Arrays.asList("/http-server/ping", "/http-server/ping");
        for (String urlToBeShortUrl : urlsToBeShortUrl) {
            String shortUrl = HashUtils.longUrlToShortUrl(urlToBeShortUrl);
            if (!shortUrls.contains(shortUrl)) {
                shortUrlToLongUrl.put(shortUrl, urlToBeShortUrl);
                shortUrls.add(shortUrl);
            } else {
                //说明出现了哈希冲突
                processHashCrash(shortUrlToLongUrl, shortUrls, urlToBeShortUrl);
            }
        }
        //获得到了所有需要生成短链的连接

        //写入配置中心
        String shortUrlAndLongUrl = JSONUtil.toJSONString(shortUrlToLongUrl);
        String dataId = getApiProperties().getDataId();
        String env = getApiProperties().getEnv();
        writeToConfigCenter(dataId, env, JSONUtil.toJSONString(shortUrlToLongUrl));
        log.info("该服务所有链接生成的短链已经成功写入远程配置中心,dataId : {} , env : {} , content : {} ", dataId, env, shortUrlAndLongUrl);
    }


    /**
     * @author: K0n9D1KuA
     * @description: 处理长链转化成短链的hash冲突
     * 处理规则 给长链加上特殊的分隔符 然后再重新生成短链
     * @param: shortUrlToLongUrl 长短链对应关系
     * @param: shortUrls 已经生成过的短链集合
     * @param: longUrl 待生成短链的长链
     * @return:
     * @date: 2023/9/6 13:33
     */
    private void processHashCrash(Map<String, String> shortUrlToLongUrl, Set<String> shortUrls, String longUrl) {
        //首先给原始的url加上特殊的分隔符
        longUrl = longUrl + ":" + "PADDING";
        //再重新生成url
        String shortUrl = HashUtils.longUrlToShortUrl(longUrl);
        if (!shortUrls.contains(shortUrl)) {
            shortUrlToLongUrl.put(shortUrl, longUrl);
            shortUrls.add(shortUrl);
        } else {
            //递归处理
            processHashCrash(shortUrlToLongUrl, shortUrls, longUrl);
        }
    }
}

