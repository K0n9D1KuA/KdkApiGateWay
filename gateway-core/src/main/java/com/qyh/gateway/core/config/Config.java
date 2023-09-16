package com.qyh.gateway.core.config;


public class Config {

    //网关默认端口
    private int port = 8888;

    private String configNames;


    //网关服务名称
    private String applicationName = "api-gateway";

    //注册中心地址
    private String registryAddress = "127.0.0.1:8848";

    //环境
    private String env = "dev";

    //com.qyh.gateway.core.netty 相关配置

    // boss线程组线程数 只用来处理 客户端连接事件
    private int eventLoopGroupBossNum = 1;

    //worker线程组线程数  用来处理客户端 读写事件
    private int eventLoopGroupWorkerNum = Runtime.getRuntime().availableProcessors();

    private int maxContentLength = 64 * 1024 * 1024;

    //默认单异步模式
    private boolean whenComplete = true;

    //	Http Async 参数选项：

    //	连接超时时间
    private int httpConnectTimeout = 30 * 1000;

    //	请求超时时间 设置为3s
    private int httpRequestTimeout = 3 * 1000;

    //	客户端请求重试次数
    private int httpMaxRequestRetry = 2;

    //	客户端请求最大连接数
    private int httpMaxConnections = 10000;

    //	客户端每个地址支持的最大连接数
    private int httpConnectionsPerHost = 8000;

    //	客户端空闲连接超时时间, 默认60秒
    private int httpPooledConnectionIdleTimeout = 60 * 1000;

    private String bufferType = "parallel";

    private int bufferSize = 1024 * 16;

    private int processThread = Runtime.getRuntime().availableProcessors();

    private String waitStrategy = "blocking";


    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getRegistryAddress() {
        return registryAddress;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public int getEventLoopGroupBossNum() {
        return eventLoopGroupBossNum;
    }

    public void setEventLoopGroupBossNum(int eventLoopGroupBossNum) {
        this.eventLoopGroupBossNum = eventLoopGroupBossNum;
    }

    public int getEventLoopGroupWorkerNum() {
        return eventLoopGroupWorkerNum;
    }

    public void setEventLoopGroupWorkerNum(int eventLoopGroupWorkerNum) {
        this.eventLoopGroupWorkerNum = eventLoopGroupWorkerNum;
    }

    public int getMaxContentLength() {
        return maxContentLength;
    }

    public void setMaxContentLength(int maxContentLength) {
        this.maxContentLength = maxContentLength;
    }

    public boolean isWhenComplete() {
        return whenComplete;
    }

    public void setWhenComplete(boolean whenComplete) {
        this.whenComplete = whenComplete;
    }

    public int getHttpConnectTimeout() {
        return httpConnectTimeout;
    }

    public void setHttpConnectTimeout(int httpConnectTimeout) {
        this.httpConnectTimeout = httpConnectTimeout;
    }

    public int getHttpRequestTimeout() {
        return httpRequestTimeout;
    }

    public void setHttpRequestTimeout(int httpRequestTimeout) {
        this.httpRequestTimeout = httpRequestTimeout;
    }

    public int getHttpMaxRequestRetry() {
        return httpMaxRequestRetry;
    }

    public void setHttpMaxRequestRetry(int httpMaxRequestRetry) {
        this.httpMaxRequestRetry = httpMaxRequestRetry;
    }

    public int getHttpMaxConnections() {
        return httpMaxConnections;
    }

    public void setHttpMaxConnections(int httpMaxConnections) {
        this.httpMaxConnections = httpMaxConnections;
    }

    public int getHttpConnectionsPerHost() {
        return httpConnectionsPerHost;
    }

    public void setHttpConnectionsPerHost(int httpConnectionsPerHost) {
        this.httpConnectionsPerHost = httpConnectionsPerHost;
    }

    public int getHttpPooledConnectionIdleTimeout() {
        return httpPooledConnectionIdleTimeout;
    }

    public void setHttpPooledConnectionIdleTimeout(int httpPooledConnectionIdleTimeout) {
        this.httpPooledConnectionIdleTimeout = httpPooledConnectionIdleTimeout;
    }

    public String getBufferType() {
        return bufferType;
    }

    public void setBufferType(String bufferType) {
        this.bufferType = bufferType;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public int getProcessThread() {
        return processThread;
    }

    public void setProcessThread(int processThread) {
        this.processThread = processThread;
    }

    public String getWaitStrategy() {
        return waitStrategy;
    }

    public void setWaitStrategy(String waitStrategy) {
        this.waitStrategy = waitStrategy;
    }

    public String getConfigNames() {
        return configNames;
    }

    public void setConfigNames(String configNames) {
        this.configNames = configNames;
    }
}
