package constants;

/**
 * 过滤器常量类
 */
public interface FilterConst {


    String GRAY_FILTER_ID = "gray_filter";
    String GRAY_FILTER_NAME = "gray_filter";
    int GRAY_FILTER_ORDER = 5;


    String LIMIT_FILTER_ID = "limit_filter";
    String LIMIT_FILTER_NAME = "limit_filter";
    int LIMIT_FILTER_ORDER = 3;


    String MOCK_FILTER_ID = "mock_filter";
    String MOCK_FILTER_NAME = "mock_filter";
    int MOCK_FILTER_ORDER = 4;


    String LOAD_BALANCE_FILTER_ID = "load_balancer_filter";
    String LOAD_BALANCE_FILTER_NAME = "load_balancer_filter";
    int LOAD_BALANCE_FILTER_ORDER = 100;

    String LOAD_BALANCE_KEY = "load_balancer";
    String LOAD_BALANCE_STRATEGY_RANDOM = "Random";
    String LOAD_BALANCE_STRATEGY_ROUND_ROBIN = "RoundRobin";


    String ROUTER_FILTER_ID = "router_filter";
    String ROUTER_FILTER_NAME = "router_filter";
    int ROUTER_FILTER_ORDER = Integer.MAX_VALUE;


    String FLOW_CTL_FILTER_ID = "flow_ctl_filter";
    String FLOW_CTL_FILTER_NAME = "flow_ctl_filter";
    int FLOW_CTL_FILTER_ORDER = 50;


    //分布式限流
    String FLOW_CTL_MODEL_DISTRIBUTED = "Distributed";

    //单机限流
    String FLOW_CTL_MODEL_SINGLETON = "Singleton";


}
