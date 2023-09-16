package config;

import java.util.*;

public class RuleNew {


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
    private Set<DynamicFilterConfig> dynamicConfig = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuleNew ruleNew = (RuleNew) o;
        return ruleNew.id.equals(((RuleNew) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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
            DynamicFilterConfig that = (DynamicFilterConfig) o;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }


    public void addDynamicFilterConfig(DynamicFilterConfig dynamicFilterConfig) {
        this.dynamicConfig.add(dynamicFilterConfig);
    }

    public DynamicFilterConfig getDynamicFilterConfigById(String filterId) {
        for (DynamicFilterConfig dynamicFilterConfig : dynamicConfig) {
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

    public Set<DynamicFilterConfig> getDynamicConfig() {
        return dynamicConfig;
    }

    public void setDynamicConfig(Set<DynamicFilterConfig> dynamicConfig) {
        this.dynamicConfig = dynamicConfig;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
