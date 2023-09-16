package config;


import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author K0n9D1KuA
 * @version 1.0
 * @description: 规则对象
 * @email 3161788646@qq.com
 * @date 2023/9/2 23:13
 */

public class Rule implements Comparable<Rule>, Serializable {


    /**
     * 规则ID，全局唯一 , 可以用来判断是否是一个规则
     */
    private String id;

    /**
     * 规则名称
     */
    private String name;

    /**
     * 接口路径
     */
    private String path;

    /**
     * 协议
     */
    private String protocol;

    /**
     * 下游服务服务名
     */
    private String serviceName;


    /**
     * 超时重试重试次数
     */
    private int retryTimes;


    /**
     * 负载均衡配置
     */
    private String loadBalanceConfig;


    /**
     * 动态过滤器规则集合
     */
    private Set<Rule.DynamicFilterConfig> dynamicConfig = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rule rule = (Rule) o;
        return Objects.equals(id, rule.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Rule o) {
        return this.id.compareTo(o.id);
    }

    public DynamicFilterConfig getDynamicFilterConfigByFilterId(String limitFilterId) {
        for (Rule.DynamicFilterConfig dynamicFilterConfig : dynamicConfig) {
            if (limitFilterId.equals(dynamicFilterConfig.getId())) {
                return dynamicFilterConfig;
            }
        }

        //没有找到
        return null;
    }

    /**
     * 动态过滤器配置
     */
    public static class DynamicFilterConfig {


        //用来标识是哪个动态过滤器
        private String id;


        //这是一个json串 用来标识该动态过滤器的配置
        private Map<String, String> config;


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Map<String, String> getConfig() {
            return config;
        }

        public void setConfig(Map<String, String> config) {
            this.config = config;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Rule.DynamicFilterConfig that = (Rule.DynamicFilterConfig) o;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }


    public void addDynamicFilterConfig(Rule.DynamicFilterConfig dynamicFilterConfig) {
        this.dynamicConfig.add(dynamicFilterConfig);
    }

    public Rule.DynamicFilterConfig getDynamicFilterConfigById(String filterId) {
        for (Rule.DynamicFilterConfig dynamicFilterConfig : dynamicConfig) {
            if (dynamicFilterConfig.getId().equals(filterId)) {
                return dynamicFilterConfig;
            }
        }
        //没有找到
        return null;
    }


    //get / set


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }


    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public String getLoadBalanceConfig() {
        return loadBalanceConfig;
    }

    public void setLoadBalanceConfig(String loadBalanceConfig) {
        this.loadBalanceConfig = loadBalanceConfig;
    }

    public Set<Rule.DynamicFilterConfig> getDynamicConfig() {
        return dynamicConfig;
    }

    public void setDynamicConfig(Set<Rule.DynamicFilterConfig> dynamicConfig) {
        this.dynamicConfig = dynamicConfig;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
